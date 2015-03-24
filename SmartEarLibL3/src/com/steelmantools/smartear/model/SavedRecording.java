package com.steelmantools.smartear.model;

import java.io.File;
import java.io.FilenameFilter;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import com.steelmantools.smartear.R;
import com.steelmantools.smartear.audio.AudioService;
import com.steelmantools.smartear.logging.Log;
import com.steelmantools.smartear.video.VideoRecorder;

public class SavedRecording implements Serializable {

	private static final long serialVersionUID = 4151484177052696702L;

	private static final String LOG_TAG = SavedRecording.class.getSimpleName();
	private static final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a", Locale.US);
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

	public static List<SavedRecording> getAllRecordings() {
		List<SavedRecording> result = new ArrayList<SavedRecording>();

		File videoRootDir = new File(VideoRecorder.VIDEO_OUTPUT_PATH);
		File[] videoFiles = videoRootDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				return filename.endsWith(VideoRecorder.VIDEO_FILE_EXTENSION);
			}
		});
		if (videoFiles != null) {
			for (int i = 0; i < videoFiles.length; i++) {
				result.add(new SavedRecording(videoFiles[i]));
			}
		}
		
		File audioRootDir = new File(AudioService.OUTPUT_PATH);
		File[] audioFiles = audioRootDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				return filename.endsWith(".wav");
			}
		});
		if (audioFiles != null) {
			for (int i = 0; i < audioFiles.length; i++) {
				result.add(new SavedRecording(audioFiles[i]));
			}
		}
		
		Collections.sort(result, new Comparator<SavedRecording>() {
			@Override
			public int compare(SavedRecording lhs, SavedRecording rhs) {
				if (lhs == null && rhs == null) {
					return 0;
				}
				if (lhs == null) {
					return -1;
				}
				if (rhs == null) {
					return 1;
				}
				return rhs.getLastModified().compareTo(lhs.getLastModified());
			}
		});
		return result;
	}

	private File file;
	private Date lastModified;
	private String creationTime;
	private String creationDate;

	public SavedRecording(String path) {
		file = new File(path);
		lastModified = new Date(file.lastModified());
		creationTime = timeFormat.format(lastModified);
		creationDate = dateFormat.format(lastModified);
	}

	public SavedRecording(File file) {
		this.file = file;
		lastModified = new Date(file.lastModified());
		creationTime = timeFormat.format(lastModified);
		creationDate = dateFormat.format(lastModified);
	}

	public Date getLastModified() {
		return lastModified;
	}

	public boolean delete() {
		try {
			return file.delete();
		} catch (Exception e) {
			Log.e(LOG_TAG, "Failed to delete file", e);
			return false;
		}
	}

	public File getFile() {
		return file;
	}

	public String getLength(Context context) {
		try {
			MediaPlayer mp = MediaPlayer.create(context, Uri.fromFile(file));
			int duration = mp.getDuration();
			mp.reset();
			mp.release();
			return String.format("%d m %d s", TimeUnit.MILLISECONDS.toMinutes(duration),
					TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)), Locale.US);
		} catch (Exception e) {
			return "";
		}
	}

	public String getCreationTime() {
		return creationTime;
	}

	public String getCreationDate() {
		return creationDate;
	}

	public boolean isVideo() {
		int i = file.getName().lastIndexOf('.');
		String extension = (i > 0) ? file.getName().substring(i+1) : "";
		return extension.equals(VideoRecorder.VIDEO_FILE_EXTENSION);
	}
	
	public String getRecordingTypeText(Context context) {
		return isVideo() ? context.getString(R.string.video) : context.getString(R.string.audio);
	}

}
