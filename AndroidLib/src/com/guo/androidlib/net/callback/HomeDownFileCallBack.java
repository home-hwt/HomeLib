package com.guo.androidlib.net.callback;

import com.guo.androidlib.net.HomeResponseInfo;

public interface HomeDownFileCallBack {
	public void onProgress(long progress,long total);
	public void onSuccess(HomeResponseInfo responseInfo);
	public void onFail(String errorMsg);
}
