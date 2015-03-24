package com.steelmantools.smartear;

import java.util.Locale;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class ChannelNameDialogFragment extends DialogFragment {

	private final static String CHANNEL_POSITION = "CHANNEL_POSITION";
	
	private int position;
	private EditText editText;
	
	public static ChannelNameDialogFragment newInstance(int position) {
		ChannelNameDialogFragment fragment = new ChannelNameDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(CHANNEL_POSITION, position);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		position = getArguments().getInt(CHANNEL_POSITION);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater li = LayoutInflater.from(getActivity());
		return new AlertDialog.Builder(getActivity()).setView(doCreateView(li, null, null)).setTitle(R.string.new_part_name)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						String value = ChannelNameDialogFragment.this.editText.getText().toString();
						if (value != null && !"".equals(value)) {
							ChannelNameHelper.addChannelName(getActivity(), value);
							GlobalSettings.getChannels().get(position).setPartName(value.toUpperCase(Locale.US));
						}
						((ChannelAssignmentActivity) getActivity()).showChannelDialog();
						ChannelNameDialogFragment.this.dismiss();
					}
				}).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						((ChannelAssignmentActivity) getActivity()).showChannelDialog();
						ChannelNameDialogFragment.this.dismiss();
					}
				}).create();
	}

	public View doCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_new_channel_name, container);
		editText = (EditText) view.findViewById(R.id.channelNameEditText);
		editText.requestFocus();
		return view;
	}

}
