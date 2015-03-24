package com.steelmantools.smartear;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class ChannelNameHelper {

	private final static String USER_CHANNEL_NAMES = "USER_CHANNEL_NAMES";
	
	private static String DELIMITER = "#@#";

	private static List<String> stringToList(String s) {
		return new ArrayList<String>(Arrays.asList(s.split(DELIMITER)));
	}

	private static String listToString(List<String> list) {
		StringBuilder sb = new StringBuilder();
		for (String s : list) {
			sb.append(s).append(DELIMITER);
		}
		if (sb.length() >= DELIMITER.length()) {
			return sb.substring(0, sb.length() - DELIMITER.length());
		}
		return sb.toString();
	}
	
	public static List<String> getChannelNames(Activity activity) {
		SharedPreferences pref = activity.getPreferences(Context.MODE_PRIVATE);
		String optionsString = pref.getString(ChannelNameHelper.USER_CHANNEL_NAMES, null);
		List<String> optionsList;
		if (optionsString == null) {
			optionsList = Arrays.asList(activity.getResources().getStringArray(R.array.channel_names));
			pref.edit().putString(ChannelNameHelper.USER_CHANNEL_NAMES, listToString(optionsList)).commit();
		} else {
			optionsList = stringToList(optionsString);
		}
		return optionsList;
	}
	
	public static void addChannelName(Activity activity, String s) {
		s = s.toUpperCase(Locale.US);
		
		SharedPreferences pref = activity.getPreferences(Context.MODE_PRIVATE);
		List<String> currentList = getChannelNames(activity);
		if (currentList.contains(s)) {
			return;
		}
		
		String first = currentList.remove(0);
		String last = currentList.remove(currentList.size()-1);
		
		currentList.add(s);
		Collections.sort(currentList);
		currentList.add(last);
		currentList.add(0, first);
		
		pref.edit().putString(USER_CHANNEL_NAMES, listToString(currentList)).apply();
	}
	
	public static void removeChannelName(Activity activity, String s) {
		s = s.toUpperCase(Locale.US);		
		SharedPreferences pref = activity.getPreferences(Context.MODE_PRIVATE);
		String optionsString = pref.getString(USER_CHANNEL_NAMES, null);
		List<String> currentList = stringToList(optionsString);
		currentList.remove(s);
		pref.edit().putString(USER_CHANNEL_NAMES, listToString(currentList)).apply();
	}

	public static Object getOtherString() {
		return "OTHER (SPECIFY)";
	}

}
