package com.steelmantools.smartear.audio;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.media.audiofx.Equalizer;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.steelmantools.smartear.GlobalSettings;
import com.steelmantools.smartear.MainActivity;
import com.steelmantools.smartear.model.Channel;
import com.steelmantools.smartear.util.LimitedQueue;
import com.steelmantools.smartear.video.VideoRecorder2;

public class AudioService extends Service {

	public static final String LOG_TAG = AudioService.class.getSimpleName();

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM_dd_yyyy_HH_mm_ss", Locale.US);

	private static final int AUDIO_ENCODING_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

	private static boolean ACTIVE = false; // Can toggle to start-stop buffer read
	private static boolean PAUSE = false;
	private static boolean RECORDING = false; // Can toggle to start-stop recording
	private static boolean AUDIO_THREAD_RUNNING = false;
	private List<File> rawFiles = new ArrayList<File>();
	private Map<Long, Integer> channelStates = new LinkedHashMap<Long, Integer>();
	
	//DBPCMAdapter DBPCMAdapter;

	public static final int SAMPLE_RATE = 44100;
	public static String AUDIO_FILE_EXTENSION = "wav";
	@SuppressLint("UseSparseArrays")
	private static Map<Integer, short[]> TONE_CACHE = new HashMap<Integer, short[]>();
	static {
		AudioHelper.getTone(Channel.CHANNEL_1_FREQUENCY);
		AudioHelper.getTone(Channel.CHANNEL_2_FREQUENCY);
		AudioHelper.getTone(Channel.CHANNEL_3_FREQUENCY);
		AudioHelper.getTone(Channel.CHANNEL_4_FREQUENCY);
		AudioHelper.getTone(Channel.CHANNEL_5_FREQUENCY);
		AudioHelper.getTone(Channel.CHANNEL_6_FREQUENCY);
	}

	public static final String OUTPUT_PATH;
	static {
		File musicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
		OUTPUT_PATH = musicDir.getAbsolutePath() + "/SmartEar";
	}

	public class AudioServiceBinder extends Binder {
		public AudioService getService() {
			return AudioService.this;
		}
	}

	private final IBinder mBinder = new AudioServiceBinder();

	private Handler handler;
	private Equalizer equalizer;
	private AudioTrack audioOutput;
	private AudioRecord audioInput;
	private int inBufferSize;
	private int outBufferSize;

	private int channelFrequency = 0;
	private AudioConsumer audioConsumer;
	private long lastUpdateTime;

	private Object dataMutex = new Object();
	private int maxDb;
	private LimitedQueue<Integer> historicalData;
	private int lastOffset;

	private long recordingStartTime;
	private long lastPauseTime;
	private long totalPauseTime;

	// when true ensure that sampling does not restart, the video encoders restarts sampling when done processing
	public boolean videoProcessing = false;

	@Override
	public void onCreate() {
		Log.d(LOG_TAG, "Creating audio service");
		
//		DBPCMAdapter=new DBPCMAdapter(this);
//		DBPCMAdapter=DBPCMAdapter.open();
		
		String isRecOnInPause = "ON";
		if(isRecOnInPause.equals(GlobalSettings.getIsRecordingOn())){
			//ACTIVE = true;
			//beginSampling(false);
//			ACTIVE = false;
			Log.d(LOG_TAG, "AUDIOSERVICE ONCREATE ACTIVE=TRUE");
		} else{
			lastUpdateTime = 0;
			rawFiles = new ArrayList<File>();
			resetHistoricalData();
			beginSampling(false);
		}

//		lastUpdateTime = 0;
//		rawFiles = new ArrayList<File>();
//		//String isRecOnInPause = "ON";
//		resetHistoricalData();
//		beginSampling(false);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		handler = new Handler();
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		String isRecOnInPause = "ON";
		if(isRecOnInPause.equals(GlobalSettings.getIsRecordingOn())){
//			Log.d(LOG_TAG, "AUDIOSERVICE ONDESTROY NOT Stopping audio service");
			ACTIVE = true;
			Log.d(LOG_TAG, "AUDIOSERVICE ONDESTROY NOT Stopping audio service, ACTIVE=TRUE");
		} else {
			Log.d(LOG_TAG, "Stopping audio service");
			ACTIVE = false;
			runWhenSamplingFinishes(true, new Runnable() {
				@Override
				public void run() {
					equalizer.release();
					audioInput.release();
					audioOutput.release();
				}
			});
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	/**
	 * Gets a File for saving to the local music directory.
	 */
	private File getFile(final Date now, final String suffix) {
		File rootDir = new File(OUTPUT_PATH);
		rootDir.mkdirs();
		int activeChannel = GlobalSettings.getActiveChannel().getNumber();
		return new File(rootDir, "CH:" + activeChannel + "_" + GlobalSettings.getRandomString() + "_" + "smartear_" + dateFormat.format(now) + "." + suffix);
	}
	
    String randomString(int len) 
    {
	    final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	    Random r = new Random();
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ ) 
           sb.append( alphabet.charAt( r.nextInt(alphabet.length()) ) );
        return sb.toString();
    }

	/**
	 * Removes and resets temporary files used for raw audio data.
	 */
//	private void removeRawFiles() {
//		for (File f : rawFiles) {
//			Log.e(LOG_TAG, "removeRawFiles FIRED");
//			f.delete();
//		}
//		rawFiles = new ArrayList<File>();
//	}
	public void removeRawFiles() {
		for (File f : rawFiles) {
			Log.e(LOG_TAG, "removeRawFiles FIRED");
			f.delete();
		}
		rawFiles = new ArrayList<File>();
	}
	
	private void resetChannelChangeTimers() {
		recordingStartTime = 0;
		lastPauseTime = 0;
		totalPauseTime = 0;
		channelStates = new HashMap<Long, Integer>();
		Log.e(LOG_TAG, "resetChannelChangeTimers FIRED");
	}

	/**
	 * Resets historical data in a thread safe manner.
	 */
//	private void resetHistoricalData() {
//		synchronized (dataMutex) {
//			historicalData = new LimitedQueue<Integer>(100);
//			Log.e(LOG_TAG, "resetHistoricalData FIRED");
//		}
//	}
	public void resetHistoricalData() {
		synchronized (dataMutex) {
			historicalData = new LimitedQueue<Integer>(100);
			Log.e(LOG_TAG, "resetHistoricalData FIRED");
		}
	}

	/**
	 * Converts the current set of raw audio files into an mp4. Uses the current MainActivity heavily during the process.
	 * 
	 * @param rawFiles
	 *            - the files contains the audio data to record. Can be more than one because the user may have paused.
	 * 
	 * @throws IOException
	 */
	private void rawToMp4(final List<File> rawFiles, final Map<Long, Integer> channelStates) throws IOException {
		Thread t = new Thread() {
			public void run() {
				try {
					VideoRecorder2 rec = new VideoRecorder2(MainActivity.mainActivityRef, rawFiles, channelStates);
					rec.createVideo();
					AudioService.this.rawFiles = new ArrayList<File>();
				} catch (Exception e) {
					Log.e(LOG_TAG, "Error", e);
				}
			}
		};
		t.start();
	}

	/**
	 * Causes the current sampling loop to stop.
	 * 
	 * @param pause
	 *            - true if the user is only pausing the sampling loop. Should be false unless the user is recording.
	 */
	private void stop(boolean pause) {
		PAUSE = pause;
		ACTIVE = false;
	}

	/**
	 * Utility method to execute a Runnable when the current sampling loop has completed.
	 * 
	 * @param synchronous
	 *            - when true the created Thread will be run on this Thread. Always use false unless you want to block the current thread.
	 * @param callback
	 *            - The Runnable to execute when sampling exits
	 */
	private void runWhenSamplingFinishes(final boolean synchronous, final Runnable callback) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (AUDIO_THREAD_RUNNING) { // Wait until current sampling stops
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						Log.e(LOG_TAG, "Error", e);
					}
				}
				callback.run();
			}
		});
		if (synchronous) {
			thread.run();
		} else {
			thread.start();
		}
	}

	/**
	 * Initializes audio streams and buffers using the current sample rate then begins sampling. This method loop continuously until stopped using the .stop
	 * method which toggles a boolean telling loop to exit.
	 */
	private void beginSampling(boolean recording) {
		ACTIVE = true;
		RECORDING = recording;

		inBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AUDIO_ENCODING_FORMAT);
		outBufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AUDIO_ENCODING_FORMAT);
		audioInput = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AUDIO_ENCODING_FORMAT, inBufferSize);
		audioOutput = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AUDIO_ENCODING_FORMAT, outBufferSize,
				AudioTrack.MODE_STREAM);
//		audioOutput = new AudioTrack(AudioManager.STREAM_VOICE_CALL, SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AUDIO_ENCODING_FORMAT, outBufferSize,
//				AudioTrack.MODE_STREAM);

		// set equalizer and individual bands from GlobalSettings
		equalizer = new Equalizer(0, audioOutput.getAudioSessionId());
		equalizer.setEnabled(true);
		short bands = equalizer.getNumberOfBands();
		for (short i = 0; i < bands; i++) {
			final Short band = i;
			Short bandValue = GlobalSettings.getEqualizerBand(band);
			if (GlobalSettings.getEqualizerBand(band) != null) {
				equalizer.setBandLevel(band, bandValue);
			}
		}

		if (audioConsumer != null) {
			audioConsumer.bindEqualizer(equalizer);
		}

		Thread t = new Thread() {
			public void run() {
				AUDIO_THREAD_RUNNING = true;
				Log.d(LOG_TAG, "Entered record loop");

				if (audioOutput.getState() != AudioTrack.STATE_INITIALIZED || audioInput.getState() != AudioTrack.STATE_INITIALIZED) {
					Log.d(LOG_TAG, "Can't start. Race condition?");
//					audioInput.startRecording();
//					Log.d(LOG_TAG, "FORCE START ON Race condition?");
				} else {
					try {
						try {
							audioOutput.play();
							Log.d(LOG_TAG, "AUDIOSERVICE audioOutput.play() in beginsampling FIRED");
						} catch (Exception e) {
							Log.e(LOG_TAG, "Failed to start playback");
							return;
						}
						try {
							audioInput.startRecording();
						} catch (Exception e) {
							Log.e(LOG_TAG, "Failed to start recording");
							audioOutput.stop();
                            audioOutput.release();
                            Log.e(LOG_TAG, "audioOutput.release() FIRED catch on startRecording");
							return;
						}

						startAudioProcessingLoop();

						try {
							audioOutput.stop();
                            audioOutput.release();
                            Log.e(LOG_TAG, "audioOutput.release() FIRED");
						} catch (Exception e) {
							Log.e(LOG_TAG, "Can't stop playback", e);
						}
						try {
							audioInput.stop();
                            audioInput.release();
                            Log.e(LOG_TAG, "audioInput.release() FIRED");
						} catch (Exception e) {
							Log.e(LOG_TAG, "Can't stop recording", e);
						}
					} catch (Exception e) {
						Log.d(LOG_TAG, "Error somewhere in record loop.", e);
					}
				}
				Log.d(LOG_TAG, "Record loop finished");
				AUDIO_THREAD_RUNNING = false;
			}
		};
		t.start();
	}

	/**
	 * This is the main recording loop which does playback and sampling.
	 */
	private void startAudioProcessingLoop() {
		short buffer[] = new short[inBufferSize];
		DataOutputStream fileOutput = null;
		File rawFile = null;
		Date now = null;
		try {
			if (RECORDING) { // creates an audio file
				long nowMillis = System.currentTimeMillis();
				if (recordingStartTime == 0) {
					recordingStartTime = nowMillis;
					totalPauseTime = 0;
					lastPauseTime = 0;
					Log.d(LOG_TAG, "recordingStartTime: " + recordingStartTime);
				} else { //resuming from a pause
					totalPauseTime = totalPauseTime + (nowMillis - lastPauseTime);
					Log.d(LOG_TAG, "recordingStartTime > 0, resuming from pause");
				}
				
				channelStates.put(nowMillis - recordingStartTime - totalPauseTime, GlobalSettings.getActiveChannelIndex());
				now = new Date();
				rawFile = getFile(now, "raw");
				fileOutput = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(rawFile)));
				Log.d(LOG_TAG, "Recording to " + rawFile.getPath());
			}

			int channelSwitchingCounter = 0;
			int channelSwitchingPriorVolume = 50;
			while (ACTIVE) {
				int readSize = audioInput.read(buffer, 0, buffer.length); // read audio
				// if (RECORDING) {
				// ShortBuffer shortBuffer = ShortBuffer.wrap(buffer.clone(), 0, readSize);
				// audioConsumer.consumeReadingBuffer(shortBuffer);
				// }
				// Samsung sample app method for getting an amplitude. Uses RMS
				double sum = 0;
				for (int i = 0; i < readSize; i++) {
					sum += buffer[i] * buffer[i];
					if (RECORDING && fileOutput != null) { // write to file
						fileOutput.writeShort(buffer[i]);
					}
				}
				if (channelFrequency > 0) {
					publishReading(120);
				} else if (readSize > 0) {
					int decibel = AudioHelper.calculateDecibel(Math.sqrt(sum / readSize), 1);
					publishReading(decibel);
				}

				if (channelFrequency > 0) {
					// set max volume and turn off equalizer first time through
					if (channelSwitchingCounter == 0) {
						equalizer.setEnabled(true);
						channelSwitchingPriorVolume = GlobalSettings.getVolume(getApplicationContext());
						GlobalSettings.setVolume(100, getApplicationContext());
					}
					channelSwitchingCounter += 1;
					short[] tone = AudioHelper.getTone(channelFrequency);
					audioOutput.write(tone, 0, tone.length);

					// reset volume and turn on equalizer again
					if (channelSwitchingCounter > 5) {
						channelSwitchingCounter = 0;
						channelFrequency = 0; // reset so next loop we don't play sound
						equalizer.setEnabled(true);
						GlobalSettings.setVolume(channelSwitchingPriorVolume, getApplicationContext());
					}
				} else if (GlobalSettings.isHeadphonesConnected()) {
					audioOutput.write(buffer, 0, readSize); // echo audio to headphones
				}
			}
		} catch (Exception e) {
			Log.e(LOG_TAG, "Audio sampling error", e);
		} finally { // close the file output to complete writing
			if (fileOutput != null) {
				try {
					fileOutput.flush();
				} catch (IOException e) {
					Log.e(LOG_TAG, "Audio file flush error", e);
				} finally {
					try {
						fileOutput.close();
					} catch (IOException e) {
						Log.e(LOG_TAG, "Audio file close error", e);
					}
				}
			}
			if (PAUSE) {
				PAUSE = false;
				rawFiles.add(rawFile);
				Log.d(LOG_TAG,"AYDIOSERVICE if(PAUSE) rawFile added to Files FIRED");
				lastPauseTime = System.currentTimeMillis();
				try {
					AudioHelper.rawToWave(rawFiles, getFile(now, AudioService.AUDIO_FILE_EXTENSION));
					Log.d(LOG_TAG,"AYDIOSERVICE if(PAUSE) RAWTOWAVE FIRED");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String isRecOnCheck = "ON";
//				try{
				if(isRecOnCheck.equals(GlobalSettings.getIsRecordingOn())){
					String didGet = "YES";
					String pauseTime = Long.toString(lastPauseTime);
					String newRawFileString = rawFile.toString();
					Log.d("AYDIOSERVICE newRawFileString: ", newRawFileString);
					Log.d("AYDIOSERVICE pauseTime: ", pauseTime);
					//DBPCMAdapter.insertEntry(newRawFileString);
					//AudioHelper.rawToWave(rawFiles, getFile(now, AudioService.AUDIO_FILE_EXTENSION));
				}
			} else if (rawFile != null) {
				try {
					String isRecOnCheck = "ON";
					rawFiles.add(rawFile);
					if (GlobalSettings.isRecordingVideo()) {
						videoProcessing = true;
						rawToMp4(rawFiles, channelStates);
//					} else {
//						AudioHelper.rawToWave(rawFiles, getFile(now, AudioService.AUDIO_FILE_EXTENSION));
//						removeRawFiles();
//						Log.d(LOG_TAG,"AYDIOSERVICE removeRawFiles in startAudioProcessingLoop FIRED");
//						resetChannelChangeTimers();
//						Log.d(LOG_TAG,"AYDIOSERVICE resetChannelChangeTimers in startAudioProcessingLoop FIRED");
//					}
//					} else if(isRecOnCheck.equals(GlobalSettings.getIsRecordingOn())){
//						Log.d(LOG_TAG,"AYDIOSERVICE isRecOnCheck.equals(GlobalSettings.getIsRecordingOn()) FIRED");
					} else{
						AudioHelper.rawToWave(rawFiles, getFile(now, AudioService.AUDIO_FILE_EXTENSION));
						//DBPCMAdapter.deleteDatabase();
						removeRawFiles();
						Log.d(LOG_TAG,"AYDIOSERVICE removeRawFiles in startAudioProcessingLoop FIRED");
						resetChannelChangeTimers();
						Log.d(LOG_TAG,"AYDIOSERVICE resetChannelChangeTimers in startAudioProcessingLoop FIRED");
					}
				} catch (IOException e) {
					handler.post(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(getApplicationContext(), "Recording failed", Toast.LENGTH_SHORT).show();
						}
					});
				}
			}
		}
		ACTIVE = false;
		Log.d(LOG_TAG, "Finished recording");
	}

	/**
	 * Publishes a decibel reading to the set AudioConsumer (if one is set).
	 * 
	 * @param decibel
	 *            - the most recent dB reading
	 */
	private void publishReading(int decibel) {
		long currentTime = System.currentTimeMillis();
		boolean actual = true; // whether this reading "counts" toward totals and published results
								// when false the reading can still be useful for visual elements

		if (lastUpdateTime + GlobalSettings.getRefreshRate() <= currentTime) {
			lastUpdateTime = currentTime;
			int currentOffset = GlobalSettings.getNetOffsets();
			if (lastOffset != currentOffset) {
				lastOffset = currentOffset;
				resetHistoricalData();
				maxDb = 0;
			}

			if (decibel < 120) {
				if (decibel > maxDb) {
					maxDb = decibel;
				}
				synchronized (dataMutex) {
					historicalData.add(decibel);
				}
			}
		} else {
			actual = false;
		}

		if (audioConsumer != null) {
			audioConsumer.consumeReading(decibel, getAverageDB(), maxDb, actual);
		}
	}

	/********************************************************************************
	 ******************************************************************************** 
	 * Public api
	 ******************************************************************************** 
	 *******************************************************************************/

	/**
	 * Adds a hook to which this class reports audio activity including dB readings.
	 * 
	 * @param audioConsumer
	 *            - the object the receive updates
	 */
	public void setAudioConsumer(AudioConsumer audioConsumer) {
		this.audioConsumer = audioConsumer;
		if (audioConsumer != null && equalizer != null) {
			audioConsumer.bindEqualizer(equalizer);
		}
	}

	/**
	 * Restarts sampling in recording state where raw audio is store for post processing into wav or mp4.
	 */
	public void startRecording() {
		if (!RECORDING) {
			stop(false);
			runWhenSamplingFinishes(false, new Runnable() {
				@Override
				public void run() {
					beginSampling(true);
				}
			});
		}
	}

	/**
	 * Pause or un-pauses recording, and thus sampling as well. Pausing means to store the current raw audio files so that they can be prepended to future
	 * results for processing.
	 */
	public void togglePauseRecording() {
		String recPausedStart = "PAUSED";
		if (RECORDING) {
			stop(true);
			handler.post(new Runnable() {
				@Override
				public void run() {
					String recPaused = "PAUSED";
					GlobalSettings.setIsRecordingPaused(recPaused);
					Toast.makeText(getApplicationContext(), "Pausing recording", Toast.LENGTH_SHORT).show();
				}
			});
			runWhenSamplingFinishes(false, new Runnable() {
				@Override
				public void run() {
					beginSampling(false);
				}
			});

//		} else {
//			startRecording();
//			handler.post(new Runnable() {
//				@Override
//				public void run() {
//					Toast.makeText(getApplicationContext(), "Resuming recording", Toast.LENGTH_SHORT).show();
//				}
//			});
//		}
		} else if(recPausedStart.equals(GlobalSettings.getIsRecordingPaused())){
			startRecording();
			handler.post(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(getApplicationContext(), "Resuming recording", Toast.LENGTH_SHORT).show();
				}
			});
		}
	}

	/**
	 * Stops recording audio. In the case that a video is being processed it's the responsibility of the client to begin sampling once processing of the video
	 * is complete.
	 * 
	 * @param restartSampling
	 */
	public void stopRecording(final boolean restartSampling) {
		if (RECORDING) {
			stop(false);
			runWhenSamplingFinishes(false, new Runnable() {
				@Override
				public void run() {
					if (restartSampling && !videoProcessing) {
						beginSampling(false);
					}
				}
			});
		}
	}

	/**
	 * Gets the average dB over the last 100 readings.
	 * 
	 * @return the average dB rating
	 */
	public int getAverageDB() {
		int total = 0;
		synchronized (dataMutex) {
			for (Integer i : historicalData) {
				total += i;
			}
			if (historicalData.size() == 0) {
				return (int) total;
			}
			return (int) total / historicalData.size();
		}
	}

	/**
	 * Switches audio channels
	 * 
	 * @param frequency
	 *            - the frequency to switch to. Take from Channel constants.
	 */
	public void changeChannel(Channel channel) {
		this.channelFrequency = channel.getFrequency(); // causes this channel to be output in the recording loop
		if (ACTIVE && !PAUSE && RECORDING) {
			channelStates.put(System.currentTimeMillis() - recordingStartTime - totalPauseTime, channel.getNumber() - 1);
		}
	}

	/**
	 * Tells this service to restart now that video processing is complete.
	 */
	public void finishedVideoProcessing() {
		removeRawFiles();
		resetChannelChangeTimers();
		videoProcessing = false;
		if (!AUDIO_THREAD_RUNNING) {
			beginSampling(false);
		}
	}
}
