package com.steelmantools.smartear.audio;

import java.io.Serializable;

public class AudioState implements Serializable {
	private static final long serialVersionUID = -7236633546519879329L;

	public boolean analogActive;
	public boolean autoActive;
	public boolean microphoneActive;
	public int samplingRate;
	public int offset;
	public int volumePercentage;

	public AudioState() {
		analogActive = true;
		microphoneActive = true;
		autoActive = false;
		samplingRate = 300;
		offset = 0;
		volumePercentage = 50;
	}
}
