package com.guo.androidlib.bitmap;

import java.io.File;

import android.content.Context;

import com.guo.androidlib.file.FileUtil;
import com.guo.androidlib.util.GHLog;


public class HomeConfig {
	//private Bitmap loadingBitmap;
	//private Bitmap loadFailBitmap;
	private int version = 1;
	private int cacheSize = 50*1024*1024;
	private File fileCachePath ;
	
	private static HomeConfig homeConfig;
	
	private HomeConfig(){
	}

	public File getFileCachePath() {
		return fileCachePath;
	}
	
	public void setFileCachePath(Context context) {
		FileUtil fileUtil = FileUtil.getInstance();
		if(fileUtil.getExternalStorageState()){
			fileCachePath = context.getExternalCacheDir();
		}else{
			fileCachePath = context.getCacheDir();
		}
		GHLog.gHLog("fileCachePath:"+fileCachePath);
	}

	public static HomeConfig getInstance(){
		if(homeConfig == null){
			homeConfig = new HomeConfig();
		}
		return homeConfig;
	}
	/*
	public Bitmap getLoadingBitmap() {
		return loadingBitmap;
	}
	public void setLoadingBitmap(Bitmap loadingBitmap) {
		this.loadingBitmap = loadingBitmap;
	}
	public Bitmap getLoadFailBitmap() {
		return loadFailBitmap;
	}
	public void setLoadFailBitmap(Bitmap loadFailBitmap) {
		this.loadFailBitmap = loadFailBitmap;
	}*/
	
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public int getCacheSize() {
		return cacheSize;
	}
	public void setCacheSize(int cacheSize) {
		this.cacheSize = cacheSize;
	}
	
	
	
}