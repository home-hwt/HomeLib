package com.guo.androidlib.bitmap;

import java.io.IOException;

import android.graphics.Bitmap;
import android.view.View;

import com.guo.androidlib.bitmap.imp.HomeBitmapCallBack;
import com.guo.androidlib.util.GHLog;

public class HomeBitmapRunnable<T extends View> implements Runnable,
		Comparable<HomeBitmapRunnable<View>> {
	private int priority;
	private T bitView;
	private String downUrl;
	private HomeBitmapCallBack<T> callBack;
	private HomeBmpDisplayConfig displayConfig;

	public HomeBitmapRunnable(String url, T view, int priority,
			HomeBmpDisplayConfig displayConfig, HomeBitmapCallBack<T> callBack) {
		bitView = view;
		this.priority = priority;
		this.downUrl = url;
		this.callBack = callBack;
		if (displayConfig == null) {
			displayConfig = new HomeBmpDisplayConfig();
			displayConfig.compress = 100;
			displayConfig.requestWidth = view.getMeasuredWidth();
			displayConfig.requestHeight = view.getMeasuredHeight();
		}
		this.displayConfig = displayConfig;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		HomeBitmapDownLoad homeBitmapDownLoad = new HomeBitmapDownLoad();
		int times = 1;
		if (callBack != null) {
			callBack.loadStart(bitView, downUrl);
		}
		String fileName = HomeMD5.getInstance().generate(downUrl);
		HomeBitmapUtil bitmapUtil = HomeBitmapUtil.getInstance();

		DiskLruCache diskLruCache = HomeBitmapUtil.getInstance()
				.getDiskLruCache();
		if (diskLruCache != null) {
			try {
				Bitmap bitmap = bitmapUtil.getBitmapForDiskCache(fileName,
						displayConfig);
				if (callBack != null && bitmap != null) {
					callBack.loadComplete(bitView, downUrl, bitmap);
				}
				GHLog.gHLog("get bitmap from cache:" + downUrl);
				if (bitmap != null)
					return;// 拿到缓存的
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		while (!homeBitmapDownLoad.downLoad(downUrl, fileName, callBack)) {
			times++;
			if (times > 3) {// 失败重复3次
				if (callBack != null) {
					callBack.loadFail(bitView, downUrl);
				}
				break;
			}
		}

		if (times <= 3) {// 下载成功
			try {
				diskLruCache.flush();
				Bitmap bitmap = bitmapUtil.getBitmapForDiskCache(fileName,
						displayConfig);
				if (callBack != null && bitmap != null) {
					callBack.loadComplete(bitView, downUrl, bitmap);
				}
				if (bitmap != null)
					return;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			callBack.loadFail(bitView, downUrl);
		}
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	@Override
	public int compareTo(HomeBitmapRunnable<View> another) {
		// TODO Auto-generated method stub
		return this.priority - another.priority >= 0 ? 1 : -1;
	}
}
