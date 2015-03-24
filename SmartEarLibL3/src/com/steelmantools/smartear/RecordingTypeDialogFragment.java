package com.steelmantools.smartear;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class RecordingTypeDialogFragment extends DialogFragment {

	private Button audioButton;
	private Button videoButton;

	public static RecordingTypeDialogFragment newInstance() {
		RecordingTypeDialogFragment fragment = new RecordingTypeDialogFragment();
		Bundle bundle = new Bundle();
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater li = LayoutInflater.from(getActivity());
		return new AlertDialog.Builder(getActivity()).setView(doCreateView(li, null, null)).setTitle(R.string.select_a_recording_type)
				.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
					}
				}).create();
	}

	public View doCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_recording_type, container);

		audioButton = (Button) view.findViewById(R.id.audioButton);
		audioButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((MainActivity) RecordingTypeDialogFragment.this.getActivity()).startRecordingAudio();
				RecordingTypeDialogFragment.this.dismiss();
			}
		});
		videoButton = (Button) view.findViewById(R.id.videoButton);
		videoButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((MainActivity) RecordingTypeDialogFragment.this.getActivity()).startRecordingVideo();
				RecordingTypeDialogFragment.this.dismiss();
			}
		});

		return view;
	}

}
