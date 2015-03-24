package com.steelmantools.smartear.audio;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.googlecode.javacv.FFmpegFrameRecorder;
import com.googlecode.javacv.FrameRecorder.Exception;
import com.googlecode.javacv.cpp.opencv_core;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.steelmantools.smartear.GlobalSettings;
import com.steelmantools.smartear.MainActivity;
import com.steelmantools.smartear.audio.AudioHelper;
import com.steelmantools.smartear.audio.AudioService;

public class AudioServiceDb {

	private static final String LOG_TAG = AudioServiceDb.class.getSimpleName();

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM_dd_yyyy_HH_mm_ss", Locale.US);
	public static final String VIDEO_OUTPUT_PATH;
	public static final String PICTURE_OUTPUT_PATH;
	public static final String VIDEO_FILE_EXTENSION = "mp4";

	static {
		File videoDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
		VIDEO_OUTPUT_PATH = videoDir.getAbsolutePath() + "/SmartEar";
		File picDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		PICTURE_OUTPUT_PATH = picDir.getAbsolutePath() + "/SmartEar";
	}

	private static final int sampleAudioRateInHz = AudioService.SAMPLE_RATE;
	private static final int imageWidth = 320;
	private static final int imageHeight = 400;
	private static final int frameRate = 60;

	private final FFmpegFrameRecorder recorder;
	private final List<File> rawFiles;
	private final Map<Long, Integer> channelStates;
	private final int audioShortsPerFrame;
	@SuppressWarnings("unused")
	private final int audioBytesPerFrame;

	private MainActivity activity;
	private int lastDecibelIndex;
	private short[] rawShorts;
	private List<Long> channelTimes;
	private List<Integer> channelIndices;

	/**
	 * 
	 * @param activity
	 *            - the calling activity
	 * @param rawFiles
	 *            - the raw audio files to encode
	 * @param channelStates
	 *            - the channels at the given times during recording
	 */
	public AudioServiceDb(MainActivity activity, List<File> rawFiles, Map<Long, Integer> channelStates) {
		this.activity = activity;
		this.rawFiles = rawFiles;
		this.channelStates = channelStates;
		channelTimes = new ArrayList<Long>();
		channelIndices = new ArrayList<Integer>();
		for (Map.Entry<Long, Integer> entry : this.channelStates.entrySet()) {
			channelTimes.add(entry.getKey());
			channelIndices.add(entry.getValue());
			int channel = entry.getValue().intValue() + 1;
			Log.d(LOG_TAG, "Channel is " + channel + " at " + entry.getKey() + " ms");
		}

		audioShortsPerFrame = sampleAudioRateInHz / frameRate;
		audioBytesPerFrame = audioShortsPerFrame * 2;

		recorder = new FFmpegFrameRecorder(getVideoFile(new Date(), VIDEO_FILE_EXTENSION), imageWidth, imageHeight, 1);
		recorder.setFormat(VIDEO_FILE_EXTENSION);
		recorder.setAudioChannels(1);
		recorder.setSampleRate(sampleAudioRateInHz);
		recorder.setFrameRate(frameRate);
	}

	/**
	 * Helper method for getting a unique video file output path.
	 */
	private File getVideoFile(final Date now, final String suffix) {
		File rootDir = new File(VIDEO_OUTPUT_PATH);
		rootDir.mkdirs();
		return new File(rootDir, "smartear_" + dateFormat.format(now) + "." + suffix);
	}

	/**
	 * Creates and mp4 video from the given raw audio files. Updates the MainActivity graphview and pull image data from it's view cache. Does this frame by
	 * frame while manually calculating then feeding decibel readings to the graph.
	 * 
	 * @throws IOException
	 */
	public void createVideo() throws IOException {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				Log.d(LOG_TAG, "Starting video encoding for " + rawFiles.size() + " raw files");
				activity.startedProcessingRecording();
				if (rawFiles.size() == 0) {
					Log.e(LOG_TAG, "ERROR: NO DATA TO VIDEO ENCODE");
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(activity, "Failed to process recording", Toast.LENGTH_SHORT).show();
						}
					});
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
					//activity.finishedProcessingRecording();
					return;
				}
				try {
					recorder.start();
				} catch (Exception e) {
					Log.e(LOG_TAG, "Failed to start recording", e);
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(activity, "Failed to start recording", Toast.LENGTH_SHORT).show();
						}
					});
					return;
				}

				int totalLength = 0;
				for (File rawFile : rawFiles) {
					totalLength += (int) rawFile.length();
				}
				Log.d(LOG_TAG, "Raw files total size is  " + totalLength);
				byte[] rawBytes = new byte[totalLength];
				int offset = 0;
				
				for (File rawFile : rawFiles) {
					DataInputStream input = null;
					try {
						input = new DataInputStream(new FileInputStream(rawFile));
						input.read(rawBytes, offset, (int) rawFile.length());
						offset += rawFile.length();
					} catch (IOException e) {
						Log.e(LOG_TAG, "Error", e);
					} finally {
						if (input != null) {
							try {
								input.close();
							} catch (IOException e) {
								Log.e(LOG_TAG, "Error", e);
							}
						}
					}
				}
				rawShorts = new short[rawBytes.length / 2];
				ByteBuffer.wrap(rawBytes).asShortBuffer().get(rawShorts);

				double lastUpdateTime = 0;
				int currentFrame = 0;
				lastDecibelIndex = 0;
				
				int channelStateIndex = 0;
				long channelTime = channelTimes.get(channelStateIndex);
				
				for (int i = 0; i < rawShorts.length; i += audioShortsPerFrame) {
					if (i >= rawShorts.length) {
						break;
					}
					int shortCount = audioShortsPerFrame;
					if (i + audioShortsPerFrame > rawShorts.length) {
						shortCount = rawShorts.length - i;
					}

					double currentTime = (currentFrame * 1.0 / frameRate) * 1000;
					if (currentTime > channelTime) {
						channelStateIndex += 1;
						if (channelStateIndex < channelTimes.size()) {
							GlobalSettings.setActiveChannelIndex(channelIndices.get(channelStateIndex));
							activity.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									activity.updateChannelView();
								}
							});
							channelTime = channelTimes.get(channelStateIndex);
						} else {
							channelTime = Long.MAX_VALUE;
						}
					}
					boolean updateUiFrame;
					if (currentTime == 0) {
						updateUiFrame = true;
					} else {
						updateUiFrame = currentTime - lastUpdateTime > GlobalSettings.getRefreshRate();
					}
					if (updateUiFrame) {
						lastUpdateTime = currentTime;
					}
					currentFrame += 1;

					Log.d(LOG_TAG, "Processing frame " + currentFrame);
					processDataSegment(i, shortCount, updateUiFrame);
				}

				try {
					recorder.stop();
					recorder.release();
				} catch (Exception e) {
					Log.e(LOG_TAG, "Failed to stop recording");
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(activity, "Failed to stop recording", Toast.LENGTH_SHORT).show();
						}
					});
				}
				for (File f : rawFiles) {
					f.delete();
				}
				IplImage.deallocateReferences();
				//activity.finishedProcessingRecording();
				Log.d(LOG_TAG, "Finished video encoding");
			}
		});
		t.start();
	}

	/**
	 * Recording at 44100 16-bit PCM we have 88100 bytes or 44100 shorts per second. Keep a count of a the bytes processed and record an image for every frame.
	 * At 30 frames per second this means every 1470th short we should output a frame.
	 * 
	 * ShortBuffers of length 'audioShortsPerFrame'. ie one ShortBuffer cause one video frame to be recorded.
	 * 
	 * @param buffer
	 */
	private void processDataSegment(int i, int shortCount, boolean updateUiFrame) {
		try {
			if (updateUiFrame) {
				Log.d(LOG_TAG, "Updating display");
				updateGraph(i + shortCount);
				lastDecibelIndex = i + shortCount;
			}
			//Bitmap originalBitmap = activity.graphParentLayout.getDrawingCache(true);
			//Bitmap b = Bitmap.createScaledBitmap(originalBitmap, imageWidth, imageHeight, true);
			//IplImage image = IplImage.create(imageWidth, imageHeight, opencv_core.IPL_DEPTH_8U, 4);
			//b.copyPixelsToBuffer(image.getByteBuffer());
			//recorder.record(image);
			recorder.record(ShortBuffer.wrap(rawShorts, i, shortCount));
			//image.release();
		} catch (Exception e) {
			Log.e(LOG_TAG, "Error recording audio", e);
		}
	}

	/**
	 * Updates the MainActivity view prior to grabbing the view caching. Pulls data from the rawShorts array starting at the lastDecibelIndex.
	 * 
	 * @param upperRange
	 *            - the upper index of the rawShorts array to use.
	 */
	public void updateGraph(int upperRange) {
		double sum = 0;
		for (int i = lastDecibelIndex; i < upperRange; i++) {
			sum += rawShorts[i] * rawShorts[i];
		}
		int decibel = AudioHelper.calculateDecibel(Math.sqrt(sum / (upperRange - lastDecibelIndex)), 1);
		activity.consumeReading(decibel, 0, 0, true);
	}

}
