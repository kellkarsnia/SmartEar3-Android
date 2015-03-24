package com.steelmantools.smartear;

import java.io.File;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.steelmantools.smartear.model.SavedRecording;
import com.steelmantools.smartear.audio.AudioService;
import com.steelmantools.smartear.video.VideoRecorder2;

public class EditRecordingDialogFragment extends DialogFragment {

	private final static String RECORDING = "RECORDING";
		
	private EditText name;
	private TextView recordedDate;
	private TextView recordedLength;

	private SavedRecording recording;

	public static EditRecordingDialogFragment newInstance(SavedRecording recording) {
		EditRecordingDialogFragment fragment = new EditRecordingDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable(RECORDING, recording);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		recording = (SavedRecording) getArguments().getSerializable(RECORDING);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater li = LayoutInflater.from(getActivity());
		return new AlertDialog.Builder(getActivity()).setView(doCreateView(li, null, null)).setTitle(R.string.edit_recording)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// rename the file if the name is different
						String fileExt = (recording.isVideo()) ? VideoRecorder2.VIDEO_FILE_EXTENSION : AudioService.AUDIO_FILE_EXTENSION;	
						File newFile = new File(recording.getFile().getParent(), name.getText().toString() + "." + fileExt);
						boolean success = recording.getFile().renameTo(newFile);
						if (!success) {
							getActivity().runOnUiThread(new Runnable() {
								@Override
								public void run() {
									Toast.makeText(getActivity(), "Renaming failed", Toast.LENGTH_SHORT).show();
								}
							});
						}
						MediaScannerConnection.scanFile(getActivity(), new String[] { newFile.toString() }, null,
								new MediaScannerConnection.OnScanCompletedListener() {
									public void onScanCompleted(String path, Uri uri) {
										Log.i("ExternalStorage", "Scanned " + path + ":");
									}
								});
						MediaScannerConnection.scanFile(getActivity(), new String[] { recording.getFile().toString() }, null,
								new MediaScannerConnection.OnScanCompletedListener() {
									public void onScanCompleted(String path, Uri uri) {
										Log.i("ExternalStorage", "Scanned " + path + ":");
									}
								});
						((SavedRecordingsActivity) getActivity()).refreshAdapter();
					}
				}).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
					}
				}).create();
	}

	public View doCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_edit_recording, container);

		name = (EditText) view.findViewById(R.id.name);
		recordedDate = (TextView) view.findViewById(R.id.recordedDate);
		recordedLength = (TextView) view.findViewById(R.id.recordedLength);
		
		String filename = recording.getFile().getName();
        filename = filename.substring(0, filename.lastIndexOf("."));
		
		name.setText(filename);
        recordedDate.setText(recording.getCreationDate());
		recordedLength.setText(recording.getLength(getActivity()));	
		return view;
	}

}