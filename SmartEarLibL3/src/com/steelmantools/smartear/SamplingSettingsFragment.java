package com.steelmantools.smartear;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.steelmantools.smartear.FullScreenGraphActivity.SettingsMenuInitializer;
import com.steelmantools.smartear.widgets.VerticalSeekBar;

public class SamplingSettingsFragment extends Fragment implements OnSeekBarChangeListener, OnClickListener{

	private TextView refreshRateText;
	private TextView refreshRateQuantityText;
	private ImageView refreshRateQuantityUp;
	private ImageView refreshRateQuantityDown;
	
	private TextView offsetText;
	private TextView offsetQuantityText;
	private ImageView offsetQuantityUp;
	private ImageView offsetQuantityDown;
	
	private TextView volumeText;
	private TextView volumeQuantityText;
	private SeekBar volumeSeekBar;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view;
		
		if (GlobalSettings.isPhase3()) {
			view = inflater.inflate(R.layout.fragment_sample_settings_2, container, false);
			new SettingsMenuInitializer(this.getActivity(), view);
		} else {
			view = inflater.inflate(R.layout.fragment_sample_settings, container, false);
		}
		
		refreshRateText = (TextView) view.findViewById(R.id.settingsRefreshRate);
		refreshRateQuantityText = (TextView) view.findViewById(R.id.settingsRefreshRateQuantity);
		refreshRateQuantityUp = (ImageView) view.findViewById(R.id.settingsRefreshRateButtonUp);
		refreshRateQuantityDown = (ImageView) view.findViewById(R.id.settingsRefreshRateButtonDown);
		
		offsetText = (TextView) view.findViewById(R.id.settingsOffset);
		offsetQuantityText = (TextView) view.findViewById(R.id.settingsOffsetQuantity);
		offsetQuantityUp = (ImageView) view.findViewById(R.id.settingsOffsetButtonUp);
		offsetQuantityDown = (ImageView) view.findViewById(R.id.settingsOffsetButtonDown);
		
		volumeText = (TextView) view.findViewById(R.id.settingsVolume);
		volumeQuantityText = (TextView) view.findViewById(R.id.settingsVolumeQuantity);
		volumeSeekBar = (VerticalSeekBar) view.findViewById(R.id.settingsVolumeSeek);
		
		refreshRateText.setOnClickListener(this);
		refreshRateQuantityUp.setOnClickListener(this);
		refreshRateQuantityDown.setOnClickListener(this);
		
		offsetText.setOnClickListener(this);
		offsetQuantityUp.setOnClickListener(this);
		offsetQuantityDown.setOnClickListener(this);
		
		volumeText.setOnClickListener(this);
		volumeSeekBar.setOnSeekBarChangeListener(this);
				
		return view;
	}

	public void refreshUI() {
		refreshRateQuantityText.setText(GlobalSettings.getRefreshRateString());
		offsetQuantityText.setText(GlobalSettings.getOffsetString());
		volumeQuantityText.setText(GlobalSettings.getVolumeString(getActivity()));
		volumeSeekBar.setProgress(GlobalSettings.getVolume(getActivity()));
		((MainActivity)getActivity()).setRefreshRate(GlobalSettings.getRefreshRate());
	}
	
	@Override
	public void onResume() {
		super.onResume();
		refreshUI();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		((MainActivity) getActivity()).settingsDismissed();
	}
		
	@Override
	public void onProgressChanged(SeekBar v, int progress, boolean isUser) {
		GlobalSettings.setVolume(progress, getActivity());
		volumeQuantityText.setText(GlobalSettings.getVolumeString(getActivity()));
	}
	
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	
	}
	
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
	
	}
	
	public void onClick(View v) {
		if (v.getId() == refreshRateText.getId()) {
			GlobalSettings.resetRefreshRate();
		} else if (v.getId() == refreshRateQuantityUp.getId()) {
			GlobalSettings.incrementRefreshRate();
		} else if (v.getId() == refreshRateQuantityDown.getId()) {
			GlobalSettings.decrementRefreshRate();
		} else if (v.getId() == offsetText.getId()) {
			GlobalSettings.resetOffset();
		} else if (v.getId() == offsetQuantityUp.getId()) {
			GlobalSettings.incrementOffset();
		} else if (v.getId() == offsetQuantityDown.getId()) {
			GlobalSettings.decrementOffset();
		} else if (v.getId() == volumeText.getId()) {
			GlobalSettings.resetVolume(getActivity());
		}
		refreshUI();
	}
}
