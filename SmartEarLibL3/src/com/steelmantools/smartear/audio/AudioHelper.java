package com.steelmantools.smartear.audio;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import android.annotation.SuppressLint;
import android.util.Log;

import com.steelmantools.smartear.GlobalSettings;
import com.steelmantools.smartear.model.Channel;

public class AudioHelper {
	
	public static final String LOG_TAG = AudioHelper.class.getSimpleName();
    private static String[] filesList = null;
	
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM_dd_yyyy_HH_mm_ss", Locale.US);
	
	@SuppressLint("UseSparseArrays")
	private static Map<Integer, short[]> TONE_CACHE = new HashMap<Integer, short[]>();
	static {
		getTone(Channel.CHANNEL_1_FREQUENCY);
		getTone(Channel.CHANNEL_2_FREQUENCY);
		getTone(Channel.CHANNEL_3_FREQUENCY);
		getTone(Channel.CHANNEL_4_FREQUENCY);
		getTone(Channel.CHANNEL_5_FREQUENCY);
		getTone(Channel.CHANNEL_6_FREQUENCY);
	}
	
	public static int calculateDecibel(double amplitude, double refAmplitude) {
		int decibel = (int) (20 * Math.log10(Math.abs(amplitude) / refAmplitude));
		// Apply offset and ensure within limits
		decibel += GlobalSettings.getNetOffsets();
		if (decibel < 0) {
			decibel = 0;
		}
		if (decibel > 120) {
			decibel = 120;
		}
		return decibel;
	}
	
	/**
	 * Gets a short array representing a tone of the given frequency.
	 * 
	 * @param freqOfTone
	 * @return
	 */
	public static short[] getTone(int freqOfTone) {
		if (TONE_CACHE.containsKey(Integer.valueOf(freqOfTone))) {
			return TONE_CACHE.get(freqOfTone);
		}
		int numSamples = 30;
		// fill out the array
		double[] sample = new double[numSamples];
		for (int i = 0; i < numSamples; ++i) {
			sample[i] = Math.sin(2 * Math.PI * i / (AudioService.SAMPLE_RATE / freqOfTone));
		}
 
		// convert to 16 bit pcm sound array
		// assumes the sample buffer is normalized.
		short[] generatedSnd = new short[numSamples * 2];
		int idx = 0;
		for (final double dVal : sample) {
			// scale to maximum amplitude
			final short val = (short) ((dVal * 32767));
			// in 16 bit wav PCM, first byte is the low order byte
			generatedSnd[idx++] = (byte) (val & 0x00ff);
			generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);

		}
		TONE_CACHE.put(Integer.valueOf(freqOfTone), generatedSnd);
		return generatedSnd;
	}
	
	
	public static void rawToWave(final List<File> rawFiles, final File waveFile) throws IOException {
		int totalLength = 0;
		

//		filesList = new String[rawFiles.size()]; 
//		for (int i = 0; i<rawFiles.size(); i++){
//			
//		}
		
		
		for (File rawFile : rawFiles) {
			totalLength += (int) rawFile.length();
			Log.d(LOG_TAG, "rawToWave, rawFile.length(): " + totalLength);
		}
		byte[] rawData = new byte[totalLength];
		int offset = 0;
		for (File rawFile : rawFiles) {
			DataInputStream input = null;
			try {
				input = new DataInputStream(new FileInputStream(rawFile));
				input.read(rawData, offset, (int) rawFile.length());
				offset += rawFile.length();
				Log.d(LOG_TAG, "rawToWave, offset: " + offset);
			} finally {
				if (input != null) {
					input.close();
				}
			}
		}

		DataOutputStream output = null;
		try {
			output = new DataOutputStream(new FileOutputStream(waveFile));
			// WAVE header
			// see http://ccrma.stanford.edu/courses/422/projects/WaveFormat/
			writeString(output, "RIFF"); // chunk id
			writeInt(output, 36 + rawData.length); // chunk size
			writeString(output, "WAVE"); // format
			writeString(output, "fmt "); // subchunk 1 id
			writeInt(output, 16); // subchunk 1 size
			writeShort(output, (short) 1); // audio format (1 = PCM)
			writeShort(output, (short) 1); // number of channels
			writeInt(output, AudioService.SAMPLE_RATE); // sample rate
			writeInt(output, AudioService.SAMPLE_RATE * 2); // byte rate
			writeShort(output, (short) 2); // block align
			writeShort(output, (short) 16); // bits per sample
			writeString(output, "data"); // subchunk 2 id
			writeInt(output, rawData.length); // subchunk 2 size
			// Audio data (conversion big endian -> little endian)
			short[] shorts = new short[rawData.length / 2];
			ByteBuffer.wrap(rawData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
			ByteBuffer bytes = ByteBuffer.allocate(shorts.length * 2);
			for (short s : shorts) {
				bytes.putShort(s);
			}
			output.write(bytes.array());
		} finally {
			if (output != null) {
				output.close();
			}
		}
	}

	public static File getFile(final Date now, final String suffix) {
		File rootDir = new File(AudioService.OUTPUT_PATH);
		rootDir.mkdirs();

		return new File(rootDir, "smartear_" + dateFormat.format(now) + "." + suffix);
	}

	public static void writeInt(final DataOutputStream output, final int value) throws IOException {
		output.write(value >> 0);
		output.write(value >> 8);
		output.write(value >> 16);
		output.write(value >> 24);
	}

	public static void writeShort(final DataOutputStream output, final short value) throws IOException {
		output.write(value >> 0);
		output.write(value >> 8);
	}

	public static void writeString(final DataOutputStream output, final String value) throws IOException {
		for (int i = 0; i < value.length(); i++) {
			output.write(value.charAt(i));
		}
	}	
}
