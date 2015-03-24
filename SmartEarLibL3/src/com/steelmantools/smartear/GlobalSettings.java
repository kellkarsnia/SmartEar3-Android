package com.steelmantools.smartear;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.steelmantools.smartear.model.Channel;

import android.content.Context;
import android.media.AudioManager;

public class GlobalSettings {

	private static int appPhase;
	
	public static boolean isPhase1() {
		return appPhase == 1;
	}
	public static boolean isPhase2() {
		return appPhase == 2;
	}
	public static boolean isPhase3() {
		return appPhase == 3;
	}

	private static int offset; // -120 to 120
	private static int refreshRate; // 100 to 500
	private static Map<Short, Short> equalizerBands;
	private static int autoActive;
	private static boolean microphoneActive;
	private static boolean headphonesConnected = false;
	private static List<Channel> channels;
	private static int activeChannelIndex;
	private static String isConnectToBT;
	private static String btDevice;
	private static String btDeviceAddr;
	private static String btDeviceAddrCh2;
	private static String btDeviceAddrCh3;
	private static String btDeviceAddrCh4;
	private static String btDeviceAddrCh5;
	private static String btDeviceAddrCh6;
	private static String randomString;
	private static String recordingOnCheck = "START";
	private static String recordingPausedCheck = "START";
	
	private static String devicesCheck;
	
	private static int activeBTChannelIndex;
	private static boolean recordingVideo;

	private static final int DEFAULT_OFFSET = 0;
//	private static final int DEFAULT_REFRESH_RATE = 300;
	//Gary change, added by KK_031114_0910
	private static final int DEFAULT_REFRESH_RATE = 100;
	private static final int DEFAULT_VOLUME = 50; // percentage
	private static final boolean DEFAULT_MICROPHONE_ACTIVE = true;
	private static final int DEFAULT_ACTIVE_CHANNEL = 0;
	private static final boolean DEFAULT_RECORDING_VIDEO = false;

	// These are wild guesses because this isn't a good solution
	private static int ANALOG_MIC_DB_OFFSET = 10;
	private static int ANALOG_CLAMP_DB_OFFSET = 22;
	public static int[] CHANNEL_TONES = { 5000, 5200, 5375, 5550, 5720, 5970 };

	private static Map<Short, Short> originalEqualizerBands;

	public static boolean IS_INIT = false;
	
	private static boolean lineGraph = true;
	
	public static void init(Context context) {
		IS_INIT = true;
		originalEqualizerBands = new HashMap<Short, Short>();
		resetMicrophoneActive();
		resetOffset();
		resetRefreshRate();
		resetVolume(context);
		resetEqualizer();
		resetAutoActive();
		resetChannels();
		resetActiveChannelIndex();
		resetRecordingVideo();
		
		// set the appPhase
		String appName = context.getResources().getString(R.string.app_name);
		appPhase = Integer.valueOf("" + appName.charAt(appName.length()-1));
	}

	public static int getConnectorDbOffset() {
		return microphoneActive ? ANALOG_MIC_DB_OFFSET : ANALOG_CLAMP_DB_OFFSET;
	}

	public static void resetAll(Context context) {
		GlobalSettings.resetOffset();
		GlobalSettings.resetRefreshRate();
		GlobalSettings.resetVolume(context);
		GlobalSettings.resetEqualizer();
	}

	public static void resetChannels() {
		GlobalSettings.setChannels(Channel.getDefaulChannels());
	}

	public static int resetOffset() {
		offset = DEFAULT_OFFSET;
		return offset;
	}

	public static int resetRefreshRate() {
		refreshRate = DEFAULT_REFRESH_RATE;
		return refreshRate;
	}

	public static int resetVolume(Context context) {
		setVolume(DEFAULT_VOLUME, context);
		return getVolume(context);
	}

	public static Map<Short, Short> resetEqualizer() {
		equalizerBands = new HashMap<Short, Short>(originalEqualizerBands);
		return equalizerBands;
	}

	public static int resetAutoActive() {
		autoActive = 0;
		return autoActive;
	}

	public static boolean resetMicrophoneActive() {
		microphoneActive = DEFAULT_MICROPHONE_ACTIVE;
		return microphoneActive;
	}

	public static int resetActiveChannelIndex() {
		activeChannelIndex = DEFAULT_ACTIVE_CHANNEL;
		return activeChannelIndex;
	}

	public static boolean resetRecordingVideo() {
		recordingVideo = DEFAULT_RECORDING_VIDEO;
		return recordingVideo;
	}	
	
	public static int getAutoActive() {
		return autoActive;
	}

	public static void setAutoActive(int currentAverage) {
		if (currentAverage > 5) {
			currentAverage = currentAverage - 5;
		}
		GlobalSettings.autoActive = -1 * currentAverage;
	}

	public static int getOffset() {
		return offset;
	}

	public static String getOffsetString() {
		return offset + "dB";
	}

	public static int getRefreshRate() {
		return refreshRate;
	}

	public static String getRefreshRateString() {
		return refreshRate + "ms";
	}

	public static void setEqualizerBand(Short band, Short value) {
		equalizerBands.put(band, value);
		if (!originalEqualizerBands.containsKey(band)) {
			originalEqualizerBands.put(band, value);
		}
	}

	public static Short getEqualizerBand(Short band) {
		return equalizerBands.get(band);
	}

	public static Map<Short, Short> getEqualizerBands() {
		return equalizerBands;
	}

	public static void setEqualizerBands(Map<Short, Short> equalizerBands) {
		GlobalSettings.equalizerBands = equalizerBands;
	}

	public static int getVolume(Context context) {
		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		return currentVolume * 100 / maxVolume;
	}

	public static String getVolumeString(Context context) {
//		return getVolume(context) + "%";
		return "";
	}

	public static void setVolume(int percentageVolume, Context context) {
		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		int newVolume = percentageVolume * maxVolume / 100;
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, 0);
	}

	public static int incrementRefreshRate() {
		if (refreshRate != 500) {
			refreshRate = refreshRate + 25;
		}
		return refreshRate;
	}

	public static int decrementRefreshRate() {
		if (refreshRate != 100) {
			refreshRate = refreshRate - 25;
		}
		return refreshRate;
	}

	public static int incrementOffset() {
		if (offset != 120) {
			offset = offset + 5;
		}
		return offset;
	}

//	public static int decrementOffset() {
//		if (offset != 120) {
//			offset = offset - 5;
//		}
//		return offset;
//	}
	
	//Gary change, added by KK_031114_0910
	public static int decrementOffset() {
		if (offset != -120) {
			offset = offset - 5;
		}
		return offset;
	}

	public static boolean isMicrophoneActive() {
		return microphoneActive;
	}

	public static void setMicrophoneActive(boolean microphoneActive) {
		GlobalSettings.microphoneActive = microphoneActive;
	}

	public static int getNetOffsets() {
		int result = getOffset() + getAutoActive();
		result += isMicrophoneActive() ? ANALOG_MIC_DB_OFFSET : ANALOG_CLAMP_DB_OFFSET;
		return result;
	}

	public static boolean isHeadphonesConnected() {
		return headphonesConnected;
	}

	public static void setHeadphonesConnected(boolean headphonesConnected) {
		GlobalSettings.headphonesConnected = headphonesConnected;
	}

	public static List<Channel> getChannels() {
		return channels;
	}

	public static void setChannels(List<Channel> channels) {
		GlobalSettings.channels = channels;
	}

	public static int getActiveChannelIndex() {
		return activeChannelIndex;
	}
	
	public static int getActiveBTChannelIndex() {
		return activeBTChannelIndex;
	}

	public static void setActiveChannelIndex(int activeChannelIndex) {
		GlobalSettings.activeChannelIndex = activeChannelIndex;
	}
	
	public static void setBTDevice(String btDevice) {
		GlobalSettings.btDevice = btDevice;
	}
	
	public static String getBTDevice() {
		return btDevice;
	}
	
	public static void setDeviceCheck(String devicesCheck) {
		GlobalSettings.devicesCheck = devicesCheck;
	}
	
	public static String getDeviceCheck() {
		return devicesCheck;
	}
	
	public static void setIsRecordingOn(String recordingOnCheck) {
		GlobalSettings.recordingOnCheck = recordingOnCheck;
	}
	
	public static String getIsRecordingOn() {
		return recordingOnCheck;
	}
	
	public static void setIsRecordingPaused(String recordingPausedCheck) {
		GlobalSettings.recordingPausedCheck = recordingPausedCheck;
	}
	
	public static String getIsRecordingPaused() {
		return recordingPausedCheck;
	}
	
	public static void setRandomString(String randomString) {
		GlobalSettings.randomString = randomString;
	}
	
	public static String getRandomString() {
		return randomString;
	}
	
	public static void setBTDeviceChannelOne(String btDevice) {
		GlobalSettings.btDevice = btDevice;
	}
	
	public static String getBTDeviceChannelOne() {
		return btDevice;
	}
	
	public static void setIsConnectedToBT(String isConnectToBT) {
		GlobalSettings.isConnectToBT = isConnectToBT;
	}
	
	public static String getIsConnectedToBT() {
		return isConnectToBT;
	}
	
	public static void setBTDeviceChannelTwo(String btDevice) {
		GlobalSettings.btDevice = btDevice;
	}
	
	public static String getBTDeviceAddrChannelOne() {
		return btDeviceAddr;
	}
	
	public static void setBTDeviceAddrChannelOne(String btDeviceAddr) {
		GlobalSettings.btDeviceAddr = btDeviceAddr;
	}
	
	public static String getBTDeviceAddrChannelTwo() {
		return btDeviceAddrCh2;
	}
	
	public static void setBTDeviceAddrChannelTwo(String btDeviceAddrCh2) {
		GlobalSettings.btDeviceAddrCh2 = btDeviceAddrCh2;
	}
	
	public static String getBTDeviceAddrChannelThree() {
		return btDeviceAddrCh3;
	}
	
	public static void setBTDeviceAddrChannelThree(String btDeviceAddrCh3) {
		GlobalSettings.btDeviceAddrCh3 = btDeviceAddrCh3;
	}
	
	public static String getBTDeviceAddrChannelFour() {
		return btDeviceAddrCh4;
	}
	
	public static void setBTDeviceAddrChannelFour(String btDeviceAddrCh4) {
		GlobalSettings.btDeviceAddrCh4 = btDeviceAddrCh4;
	}
	
	public static String getBTDeviceAddrChannelFive() {
		return btDeviceAddrCh5;
	}
	
	public static void setBTDeviceAddrChannelFive(String btDeviceAddrCh5) {
		GlobalSettings.btDeviceAddrCh5 = btDeviceAddrCh5;
	}
	
	public static String getBTDeviceAddrChannelSix() {
		return btDeviceAddrCh6;
	}
	
	public static void setBTDeviceAddrChannelSix(String btDeviceAddrCh2) {
		GlobalSettings.btDeviceAddrCh6 = btDeviceAddrCh2;
	}
	
	public static String getBTDeviceChannelTwo() {
		return btDevice;
	}
	
	public static void setActiveBTChannelIndex(int activeBTChannelIndex) {
		GlobalSettings.activeBTChannelIndex = activeBTChannelIndex;
	}

	public static Channel getActiveChannel() {
		return channels.get(activeChannelIndex);
	}

	public static void moveToNextChannel() {
		if (getActiveChannelIndex() + 1 >= channels.size()) {
			setActiveChannelIndex(0);
		} else {
			setActiveChannelIndex(getActiveChannelIndex() + 1);
		}
	}
	
	public static boolean isRecordingVideo() {
		return recordingVideo;
	}
	
	public static void setRecordingVideo(boolean recordingVideo) {
		GlobalSettings.recordingVideo = recordingVideo;
	}
	
	public static void setLineGraph(boolean lineGraph) {
		GlobalSettings.lineGraph = lineGraph;
	}

	public static boolean isLineGraph() {
		return lineGraph;
	}

}
