package com.guo.androidlib.receiver;

import com.guo.androidlib.http.HttpUtil;
import com.guo.androidlib.util.GHLog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NetBroadCastReciver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		boolean isEnable = HttpUtil.getInstance().isNetworkAvailable(context);
		GHLog.gHLog("network state:"+isEnable);
	}
}
