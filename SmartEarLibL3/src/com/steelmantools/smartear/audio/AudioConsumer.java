package com.steelmantools.smartear.audio;

import java.nio.ShortBuffer;

import android.media.audiofx.Equalizer;

public interface AudioConsumer {

	void consumeReading(int decibel, int average, int maximum, boolean actual);
	void consumeReadingBuffer(ShortBuffer buffer);
	void bindEqualizer(Equalizer equalizer);
	
}
