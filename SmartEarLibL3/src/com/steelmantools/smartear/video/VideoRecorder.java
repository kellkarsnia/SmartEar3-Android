package com.steelmantools.smartear.video;

import java.io.File;
import java.nio.ShortBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.googlecode.javacv.FFmpegFrameRecorder;
import com.googlecode.javacv.FrameRecorder.Exception;
import com.googlecode.javacv.cpp.opencv_core;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.steelmantools.smartear.MainActivity;

/**
 * No longer used. See VideoRecorder2 which post processes audio data in mp4 format.
 */
@Deprecated
public class VideoRecorder {

	private static final String LOG_TAG = VideoRecorder.class.getSimpleName();

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM_dd_yyyy_HH_mm_ss", Locale.US);
	public static final String VIDEO_OUTPUT_PATH;
	public static final String PICTURE_OUTPUT_PATH;
	public static final String VIDEO_FILE_EXTENSION = "mp4";
	private static boolean recording = false;
	static {
		File videoDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
		VIDEO_OUTPUT_PATH = videoDir.getAbsolutePath() + "/SmartEar";
		File picDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		PICTURE_OUTPUT_PATH = picDir.getAbsolutePath() + "/SmartEar";
	}

	private static final int sampleAudioRateInHz = 44100;
	private static final int imageWidth = 320;
	private static final int imageHeight = 240;
	private static final int frameRate = 30;

	private MainActivity activity;
	final FFmpegFrameRecorder recorder;

	public VideoRecorder(MainActivity activity) {
		this.activity = activity;

		recorder = new FFmpegFrameRecorder(getVideoFile(new Date(), VIDEO_FILE_EXTENSION), imageWidth, imageHeight, 1);
		recorder.setFormat(VIDEO_FILE_EXTENSION);
		recorder.setAudioChannels(1);
		recorder.setSampleRate(sampleAudioRateInHz);
		recorder.setFrameRate(frameRate);
	}

	private File getVideoFile(final Date now, final String suffix) {
		File rootDir = new File(VIDEO_OUTPUT_PATH);
		rootDir.mkdirs();
		return new File(rootDir, "smartear_" + dateFormat.format(now) + "." + suffix);
	}

	/**
	 * Used to sotre
	 * @param now
	 * @param suffix
	 * @return
	 */
	@SuppressWarnings("unused")
	private File getPictureFile(final Date now, final String suffix) {
		File rootDir = new File(PICTURE_OUTPUT_PATH);
		rootDir.mkdirs();
		return new File(rootDir, "recording_temp." + suffix);
	}

	public void start() {
		try {
			recorder.start();
		} catch (Exception e) {
			Log.e(LOG_TAG, "Failed to start recording", e);
			Toast.makeText(activity, "Failed to start recording", Toast.LENGTH_SHORT).show();
			return;
		}
		Thread t = new Thread(new Runnable() {
			@Override
			/**
			 * For reference from https://code.google.com/p/javacv/issues/detail?id=67:
			 * 
			Android Bitmap's format and IplImage's format matching is very important.

			IplImage image = IplImage.create(width, height, IPL_DEPTH_8U, 4);
			IplImage _3image = IplImage.create(width, height, IPL_DEPTH_8U, 3);
			IplImage _1image = IplImage.create(width, height, IPL_DEPTH_8U, 1);
			Bitmap bitmap = Bitmap.createBitma(width, height, Bitmap.Config.ARGB_8888);

			1. iplimage -> bitmap
			bitmap.copyPixelsFromBuffer(image.getByteBuffer());

			2. bitmap -> iplimage
			bitmap.copyPixelsToBuffer(image.getByteBuffer());

			3. iplimage(4channel) -> iplimage(3channel or 1channel)
			cvCvtColor(image, _3image, CV_BGRA2BGR);
			cvCvtColor(_3image, _1image, CV_RGB2GRAY);
			 */
			public void run() {
				android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_DISPLAY);

				recording = true;
				View view = activity.graphParentLayout;
				// File picFile = getPictureFile(new Date(), "jpg");
				while (recording) {
					try {
						Bitmap b = view.getDrawingCache(); // could use this to avoid the file system and be awesome --> b.copyPixelsToBuffer(dst);
						// b.compress(CompressFormat.JPEG, 20, new FileOutputStream(picFile));
						IplImage image = IplImage.create(b.getWidth(), b.getHeight(), opencv_core.IPL_DEPTH_8U, 4);
						b.copyPixelsToBuffer(image.getByteBuffer());
						recorder.record(image);
						// recorder.record(opencv_highgui.cvLoadImage(picFile.getAbsolutePath()));
					} catch (Exception e) {
						Log.e(LOG_TAG, "Error adding recording image", e);
					}
					// catch (FileNotFoundException e) {
					// Log.e(LOG_TAG, "Error compressing image", e);
					// }
				}
				try {
					recorder.stop();
					recorder.release();
				} catch (Exception e) {
					Log.e(LOG_TAG, "Failed to stop recording");
					Toast.makeText(activity, "Failed to stop recording", Toast.LENGTH_SHORT).show();
				}
			}
		});
		t.start();
	}

	public void stop() {
		recording = false;
	}

	public void consumeReadingBuffer(ShortBuffer buffer) {
		if (recording) {
			try {
				recorder.record(buffer);
			} catch (Exception e) {
				Log.e(LOG_TAG, "Error recording audio", e);
			}
		}
	}
}
