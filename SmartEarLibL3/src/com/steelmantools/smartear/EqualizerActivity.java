package com.steelmantools.smartear;

import java.util.HashMap;
import java.util.Map;

import android.media.audiofx.Equalizer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.steelmantools.smartear.widgets.VerticalSeekBar;

public class EqualizerActivity extends GraphingActivity {

	public static final String LOG_TAG = EqualizerActivity.class.getSimpleName();

	private ImageView equalizerReset;
	private Button okayButton;
	private Button cancelButton;
	private LinearLayout equalizerLayout;
	private LayoutInflater layoutInflater;
	private Map<Short, Short> previousEqualizerBands;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_equalizer_screen);

		previousEqualizerBands = new HashMap<Short, Short>(GlobalSettings.getEqualizerBands());
		
		layoutInflater = LayoutInflater.from(this);
		equalizerLayout = (LinearLayout) findViewById(R.id.equalizerLayout);

		equalizerReset = (ImageView) findViewById(R.id.equalizerEqualizerReset);
		equalizerReset.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				GlobalSettings.resetEqualizer();
				setupEqualizerAndUI();
			}
		});

		okayButton = (Button) findViewById(R.id.okayButton);
		okayButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		cancelButton = (Button) findViewById(R.id.cancelButton);
		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				GlobalSettings.setEqualizerBands(previousEqualizerBands);
				onBackPressed();
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void bindEqualizer(Equalizer equalizer) {
		super.bindEqualizer(equalizer);
		setupEqualizerAndUI();
	}

	private void setupEqualizerAndUI() {
		if (equalizer == null) {
			Log.e(LOG_TAG, "No equalizer found");
			return;
		}
		short bands = equalizer.getNumberOfBands();
		equalizerLayout.setWeightSum(bands);
		equalizerLayout.removeAllViews();

		final short minEQLevel = equalizer.getBandLevelRange()[0];
		final short maxEQLevel = equalizer.getBandLevelRange()[1];
		final int progressMax = maxEQLevel - minEQLevel;
		
		for (short i = 0; i < bands; i++) {
			final short band = i;

			RelativeLayout root = (RelativeLayout) layoutInflater.inflate(R.layout.partial_equalizer_bar, null);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0,
					ViewGroup.LayoutParams.MATCH_PARENT);
			layoutParams.weight = 1;
			root.setLayoutParams(layoutParams);
			
			TextView freqTextView = (TextView) root.findViewById(R.id.freqTextView);
			freqTextView.setText((equalizer.getCenterFreq(band) / 1000) + " Hz");

			VerticalSeekBar bar = (VerticalSeekBar) root.findViewById(R.id.equalizerSeek);
			bar.setMax(progressMax);
			bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
					short actualBandValue = (short) (progress + minEQLevel);
					equalizer.setBandLevel(band, actualBandValue);
					GlobalSettings.setEqualizerBand(band, actualBandValue);
				}

				public void onStartTrackingTouch(SeekBar seekBar) {
				}

				public void onStopTrackingTouch(SeekBar seekBar) {
				}
			});
			Short bandValue = GlobalSettings.getEqualizerBand(band);
			if (bandValue == null) {
				bandValue = equalizer.getBandLevel(band);
				GlobalSettings.setEqualizerBand(band, bandValue);
			}
			bar.setProgress(bandValue + progressMax/2);

			equalizerLayout.addView(root);
		}

	}

}
