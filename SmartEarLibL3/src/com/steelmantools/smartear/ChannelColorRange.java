package com.steelmantools.smartear;

public class ChannelColorRange {

	private double lowerX;
	private double upperX;
	private int color;
	
	public ChannelColorRange(double lowerX, int color) {
		this.lowerX = lowerX;
		this.upperX = -1.0;
		this.color = color;
	}
	
	public void setUpperX(double upperX) {
		this.upperX = upperX;
	}
	
	public boolean isInRange(double x) {
		if (upperX == -1.0) {
			return x > lowerX;
		}
		return x > lowerX && x <= upperX;
	}
	
	public int getColor() {
		return color;
	}
	
	public int getColorIfInRange(double x) {
		if (isInRange(x)) {
			return color;
		}
		return -1;
	}
}
