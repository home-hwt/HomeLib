package com.guo.androidlib.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

public class GHDevice {
	private static GHDevice ghDevice;

	public static GHDevice getInstance() {
		if (ghDevice == null) {
			ghDevice = new GHDevice();
		}
		return ghDevice;
	}

	/**
	 * @return [1]- [2]
	 */
	public String[] getCpuInfo() {
		String str1 = "/proc/cpuinfo";
		String str2 = "";
		String[] cpuInfo = { "", "" }; // 
		String[] arrayOfString;
		try {
			FileReader fr = new FileReader(str1);
			BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
			str2 = localBufferedReader.readLine();
			arrayOfString = str2.split("\\s+");
			for (int i = 2; i < arrayOfString.length; i++) {
				cpuInfo[0] = cpuInfo[0] + arrayOfString[i] + " ";
			}
			str2 = localBufferedReader.readLine();
			arrayOfString = str2.split("\\s+");
			cpuInfo[1] += arrayOfString[2];
			localBufferedReader.close();
		} catch (IOException e) {
		}
		return cpuInfo;
	}

	/**
	 * @param context
	 * @return  ip
	 * */
	public String getMacAddress(Context context) {
		String result = "";
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		result = wifiInfo.getMacAddress();
		return result;
	}
	
	/**
	 * Returns the unique device ID
	 * @param context
	 * @return
	 */
	public String getIMEI(Context context){
		TelephonyManager mTm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = mTm.getDeviceId();
		return imei;
	}
	
	/**
	 * µç»°ºÅÂë
	 * @param context
	 * @return
	 */
	public String getPhomeNum(Context context){
		TelephonyManager mTm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String phoneNum = mTm.getLine1Number();
		return phoneNum;
	}
	
	/**
	 * Returns the unique subscriber ID
	 * @param context
	 * @return
	 */
	public String getIMSI(Context context){
		TelephonyManager mTm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return mTm.getSubscriberId();
	}
}
