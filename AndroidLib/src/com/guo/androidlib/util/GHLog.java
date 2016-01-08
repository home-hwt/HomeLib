package com.guo.androidlib.util;

import android.util.Log;

public class GHLog {
	public final static int  LOG_VERBOSE = 0;
	public final static int  LOG_DEBUG = 1;
	public final static int  LOG_INFO = 2;
	public final static int  LOG_WARN = 3;
	public final static int  LOG_ERROR = 4;
	private final static String TAG = "GH_LOG"; 
	private static boolean isShowLog = true;
	
	public void setIsShowLog(boolean isShow){
		isShowLog = isShow;
	}
	/**
	 * 
	 * @param logLevel log级别
	 * @param tag log tag
	 * @param logMsg log信息
	 */
	public static void gHLog(int logLevel,String tag,String logMsg){
		if(!isShowLog) return;
		switch (logLevel) {
		case LOG_VERBOSE:
			Log.v(tag, logMsg);
			break;
		case LOG_DEBUG:
			Log.d(tag, logMsg);
			break;
		case LOG_INFO:
			Log.i(tag, logMsg);
			break;
		case LOG_WARN:
			Log.w(tag, logMsg);
			break;
		case LOG_ERROR:
			Log.e(tag, logMsg);
			break;
		default:
			break;
		}
	}
	/**
	 * 
	 * @param logLevel
	 * @param logMsg
	 */
	public static void gHLog(int logLevel,String logMsg){
		gHLog(logLevel, TAG, logMsg);
	}
	
	/**
	 * 默认级别为INFO
	 * @param logMsg
	 */
	public static void gHLog(String logMsg){
		gHLog(LOG_INFO, TAG, logMsg);
	}
}
