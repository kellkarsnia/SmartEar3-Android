package com.steelmantools.smartear;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

public class AnalogMeterFragment extends NumericalMeterFragment {

	private ImageView needle;
	private float previousRotation = 0;
	private static final long ANIMATION_TIME = 110; // milliseconds, determines how 'lively' the needle is
	private static final boolean SMOOTHING = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View root = inflater.inflate(R.layout.fragment_analog_meter, null);
		needle = (ImageView) root.findViewById(R.id.mainScreenNeedle);

		return setup(root);
	}

	@Override
	public void consumeReading(int decibel, int average, int maximum, boolean actual) {
		super.consumeReading(decibel, average, maximum, actual);
		positionNeedle(decibel); // always move the needle
	}

	/**
	 * 120db = 180 degrees, 60db = 90 degrees, So: db*1.5 = degrees of rotation
	 * 
	 * Could just use: needle.setRotation(rotationDegrees); but required api 11
	 */
	//@SuppressWarnings("unused")
	private void positionNeedle(int decibel) {
		Animation current = needle.getAnimation();
		if (!SMOOTHING || (SMOOTHING && (current == null || current.hasEnded()))) {
			float rotationDegrees = (float) (decibel * 1.5);
			RotateAnimation animation = new RotateAnimation(previousRotation, rotationDegrees, Animation.RELATIVE_TO_SELF,
					1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
			animation.setInterpolator(new LinearInterpolator());
			animation.setDuration(ANIMATION_TIME);
			animation.setFillAfter(true);
			needle.startAnimation(animation);
			
			previousRotation = rotationDegrees;
		}
	}
}
