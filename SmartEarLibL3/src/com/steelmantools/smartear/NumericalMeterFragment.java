package com.steelmantools.smartear;

import java.nio.ShortBuffer;

import com.steelmantools.smartear.audio.AudioConsumer;

import android.media.audiofx.Equalizer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class NumericalMeterFragment extends Fragment implements AudioConsumer {

	protected TextView gaugeDecibels;
	protected TextView gaugeRefreshRate;
	protected TextView average;
	protected TextView maximum;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		return setup(inflater.inflate(R.layout.fragment_numerical_meter, null));
	}
	
	protected View setup(View root) {
		gaugeDecibels = (TextView) root.findViewById(R.id.mainScreenGaugeDecibels);
		gaugeRefreshRate = (TextView) root.findViewById(R.id.mainScreenGaugeRefreshRate);
		average = (TextView) root.findViewById(R.id.mainScreenAverage);
		maximum = (TextView) root.findViewById(R.id.mainScreenPeak);
		
		setRefreshRate(GlobalSettings.getRefreshRate());
		
		return root;
	}

	@Override
	public void consumeReading(int decibel, int average, int maximum, boolean actual) {
		if (actual) {
			gaugeDecibels.setText(Integer.toString(decibel) + "dB");
			this.average.setText(Integer.toString(average));
			this.maximum.setText(Integer.toString(maximum));
		}
	}
	
	@Override
	public void bindEqualizer(Equalizer equalizer) {
	}
	
	protected void setRefreshRate(int refreshRate) {
		gaugeRefreshRate.setText(Integer.toString(refreshRate) + "ms");
	}

	@Override
	public void consumeReadingBuffer(ShortBuffer buffer) {
		// do nothing
	}


}
