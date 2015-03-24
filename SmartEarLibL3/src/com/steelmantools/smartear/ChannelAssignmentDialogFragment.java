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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.steelmantools.smartear.model.Channel;

public class ChannelAssignmentDialogFragment extends DialogFragment {

	private final static String CHANNEL_POSITION = "CHANNEL_POSITION";

	private Spinner spinner;
	private ChannelNamesAdapter adapter;
	private int position;
	private String selectedPartName;
	private String selectedLocation;
	private Channel originalChannel;

	public static ChannelAssignmentDialogFragment newInstance(int position) {
		ChannelAssignmentDialogFragment fragment = new ChannelAssignmentDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(CHANNEL_POSITION, position);
		fragment.setArguments(bundle);
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		position = getArguments().getInt(CHANNEL_POSITION);
		originalChannel = GlobalSettings.getChannels().get(position);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater li = LayoutInflater.from(getActivity());
		return new AlertDialog.Builder(getActivity()).setView(doCreateView(li, null, null)).setTitle(getTitle())
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						((ChannelAssignmentActivity) getActivity()).doPositiveClick();
					}
				}).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						GlobalSettings.getChannels().set(position, originalChannel);
						((ChannelAssignmentActivity) getActivity()).doNegativeClick();
					}
				}).create();
	}

	// @Override
	// public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	// return doCreateView(inflater, container, savedInstanceState);
	// }

	public View doCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_channel_assignment, container);

		spinner = (Spinner) view.findViewById(R.id.channelSpinner);
		
		refreshUI();

		OnClickListener buttonListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isDefaultSelectedPartName()) {
					Toast.makeText(ChannelAssignmentDialogFragment.this.getActivity(), "Please select a part first", Toast.LENGTH_SHORT).show();
				} else {
					selectedLocation = ((Button) v).getText().toString();
					GlobalSettings.getChannels().get(position).setLocationName(selectedLocation);
					getDialog().setTitle(getTitle());
				}
			}
		};
		
		Channel channel = GlobalSettings.getChannels().get(position);
		int channelColor = channel.getAndroidColor(view.getContext());
		((Button) view.findViewById(R.id.channelFront)).setTextColor(channelColor);
		((Button) view.findViewById(R.id.channelLeftFront)).setTextColor(channelColor);
		((Button) view.findViewById(R.id.channelRightFront)).setTextColor(channelColor);
		((Button) view.findViewById(R.id.channelRear)).setTextColor(channelColor);
		((Button) view.findViewById(R.id.channelLeftRear)).setTextColor(channelColor);
		((Button) view.findViewById(R.id.channelRightRear)).setTextColor(channelColor);

		((Button) view.findViewById(R.id.channelFront)).setOnClickListener(buttonListener);
		((Button) view.findViewById(R.id.channelLeftFront)).setOnClickListener(buttonListener);
		((Button) view.findViewById(R.id.channelRightFront)).setOnClickListener(buttonListener);
		((Button) view.findViewById(R.id.channelRear)).setOnClickListener(buttonListener);
		((Button) view.findViewById(R.id.channelLeftRear)).setOnClickListener(buttonListener);
		((Button) view.findViewById(R.id.channelRightRear)).setOnClickListener(buttonListener);

		return view;
	}

	private String getTitle() {
		String title;
		Channel channel = GlobalSettings.getChannels().get(position);
		if (isDefaultSelectedPartName() && selectedLocation == null) {
			title = "Assign channel " + channel.getNumber() + " a name";
		} else {
			title = channel.getName();
		}
		return title;
	}

	private boolean isDefaultSelectedPartName() {
		return (selectedPartName == null || "".equals(selectedPartName) || selectedPartName.equals(adapter.getItem(0).toString()));
	}

	public void refreshUI() {
		adapter = new ChannelNamesAdapter(this.getActivity(), this);

		spinner.setAdapter(adapter);
		Channel channel = GlobalSettings.getChannels().get(position);
		if (channel.getPartName() != null && !"".equals(channel.getPartName())) {
			spinner.setSelection(adapter.getPosition(channel.getPartName()));
		}
		
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int index, long id) {
				selectedPartName = adapter.getItem(index).toString();
				if (isDefaultSelectedPartName()) {
					selectedPartName = null;
					GlobalSettings.getChannels().get(position).setPartName("");
					GlobalSettings.getChannels().get(position).setLocationName("");
					getDialog().setTitle(getTitle());
				} else if (ChannelNameHelper.getOtherString().equals(selectedPartName)) {
					((ChannelAssignmentActivity)getActivity()).createNewChannelSelected();
					ChannelAssignmentDialogFragment.this.dismiss();
				} else {
					GlobalSettings.getChannels().get(position).setPartName(selectedPartName);
					getDialog().setTitle(getTitle());
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}
}
