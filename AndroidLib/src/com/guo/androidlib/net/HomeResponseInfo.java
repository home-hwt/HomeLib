package com.guo.androidlib.net;
/**
 * 
 * @author Home
 * 文件下载成功返回信息类
 */
public class HomeResponseInfo {
	/**
	 * 保存文件名
	 */
	private String fileName;
	/**
	 * 文件保存路径
	 */
	private String saveFilePath;
	/**
	 * 下载大小
	 */
	private long length;
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getSaveFilePath() {
		return saveFilePath;
	}
	public void setSaveFilePath(String saveFilePath) {
		this.saveFilePath = saveFilePath;
	}
	public long getLength() {
		return length;
	}
	public void setLength(long length) {
		this.length = length;
	}
	
	 
}
