package com.guo.androidlib.http;

public interface HomeDownLoadListener {
	/**
	 * 正在下载
	 */
	public static int HOMEDOWNLOADING = 0;
	
	/**
	 * 下载成功
	 */
	public static int HOMEDOWNLOAD_SUCCESS = 1;
	
	/**
	 * 下载失败
	 */
	public static int HOMEDOWNLOAD_FAIL = -1;
	
	/**
	 * 
	 * @param downState  下载状态，包含正在下载，下载完成，下载失败
	 * @param total	下载文件总大小
	 * @param progress 下载进度
	 * @param filePathOrErrorMsg 下载成功的文件保存路径或下载失败的错误信息
	 */
	public void downProgress(int downState,long total,long progress,String filePathOrErrorMsg);
}
