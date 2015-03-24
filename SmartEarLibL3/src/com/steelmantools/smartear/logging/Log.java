package com.steelmantools.smartear.logging;

import java.util.HashSet;
import java.util.Set;

import com.steelmantools.smartear.SplashScreenActivity;

/**
 * Turn off logging statements for selected class. 
 */
public class Log {

	private static Set<String> disabledLogs = new HashSet<String>();
	static {
		disabledLogs.add(SplashScreenActivity.LOG_TAG);
	}
	
	private static boolean enabled(String tag) {
		if (disabledLogs.contains(tag)) {
			return false;
		}
		return true;
	}
	
	public static void d(String tag, String msg) {
		if (enabled(tag)) {
			android.util.Log.d(tag, msg);
		}
	}
	
	public static void d(String tag, String msg, Throwable tr) {
		if (enabled(tag)) {
			android.util.Log.d(tag, msg, tr);
		}
	}
	
	public static void e(String tag, String msg) {
//		if (enabled(tag)) {
			android.util.Log.e(tag, msg);
//		}
	}

	public static void e(String tag, String msg, Throwable tr) {
//		if (enabled(tag)) {
			android.util.Log.e(tag, msg, tr);
//		}
	}
		
	public static void i(String tag, String msg) {
		if (enabled(tag)) {
			android.util.Log.i(tag, msg);
		}
	}
	
	public static void i(String tag, String msg, Throwable tr) {
		if (enabled(tag)) {
			android.util.Log.i(tag, msg, tr);
		}
	}
	
	public static void v(String tag, String msg) {
		if (enabled(tag)) {
			android.util.Log.v(tag, msg);
		}
	}
	
	public static void v(String tag, String msg, Throwable tr) {
		if (enabled(tag)) {
			android.util.Log.v(tag, msg, tr);
		}
	}
	
	public static void w(String tag, String msg) {
		if (enabled(tag)) {
			android.util.Log.w(tag, msg);
		}
	}
	
	public static void w(String tag, String msg, Throwable tr) {
		if (enabled(tag)) {
			android.util.Log.w(tag, msg, tr);
		}
	}
	
	public static void w(String tag, Throwable tr) {
		if (enabled(tag)) {
			android.util.Log.w(tag, tr);
		}
	}
	
	public static void wtf(String tag, String msg) {
//		if (enabled(tag)) {
			android.util.Log.wtf(tag, msg);
//		}
	}
	
	public static void wtf(String tag, String msg, Throwable tr) {
//		if (enabled(tag)) {
			android.util.Log.wtf(tag, msg, tr);
//		}
	}
	
	public static void wtf(String tag, Throwable tr) {
//		if (enabled(tag)) {
			android.util.Log.wtf(tag, tr);
//		}
	}
	
}
