package com.steelmantools.smartear.model;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.steelmantools.smartear.R;

public class Channel {

	private static final List<Channel> DEFAULT_CHANNELS = new ArrayList<Channel>();
	static {
		DEFAULT_CHANNELS.add(new Channel(1, R.color.graph_red));
		DEFAULT_CHANNELS.add(new Channel(2, R.color.graph_green));
		DEFAULT_CHANNELS.add(new Channel(3, R.color.graph_white));
		DEFAULT_CHANNELS.add(new Channel(4, R.color.graph_pink));
		DEFAULT_CHANNELS.add(new Channel(5, R.color.graph_blue));
		DEFAULT_CHANNELS.add(new Channel(6, R.color.graph_yellow));
	}
	
	public static final int CHANNEL_1_FREQUENCY = 5000;
	public static final int CHANNEL_2_FREQUENCY = 6000;
	public static final int CHANNEL_3_FREQUENCY = 7000;
	public static final int CHANNEL_4_FREQUENCY = 8000;
	public static final int CHANNEL_5_FREQUENCY = 9000;
	public static final int CHANNEL_6_FREQUENCY = 10000;
	
	public static List<Channel> getDefaulChannels() {
		List<Channel> temp = new ArrayList<Channel>();
		for (Channel ch : Channel.DEFAULT_CHANNELS) {
			temp.add(new Channel(ch));
		}
		return temp;
	}	

	private Integer number;
	private String partName;
	private String locationName;
	private int colorResource;
	private int frequency;

	public Channel(Integer number, int colorResource) {
		this.number = number;
		this.colorResource = colorResource;
		this.partName = "";
		this.locationName = "";
		frequency = (number + 4) * 1000; //same as using constants above
	}

	public Channel(Channel ch) {
		this.colorResource = ch.getColorResource();
		this.locationName = ch.getLocationName();
		this.number = ch.getNumber();
		this.partName = ch.getPartName();
		this.frequency = ch.getFrequency();
	}

	public String getName() {
		if (locationName != null && !"".equals(locationName)) {
			return partName + " - " + locationName;
		}
		return partName;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public String getPartName() {
		return partName;
	}

	public void setPartName(String partName) {
		this.partName = partName;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public int getColorResource() {
		return colorResource;
	}
	
	public int getAndroidColor(Context context) {
		return context.getResources().getColor(colorResource);
	}

	public void setColorResource(int colorResource) {
		this.colorResource = colorResource;
	}

	public int getFrequency() {
		return frequency;
	}

}
