package com.guo.androidlib.net.entity;

import com.guo.androidlib.db.entity.HomeTableEntity;

public class HomeDownFileTable extends HomeTableEntity {
	//id     downURL    thread_one----five	  totalSize   saveFilePath downState
	public String downURL;
	public long totalSize;
	public String saveFilePath;//文件保存路径
	public int downState;//文件下载状态   1下载完成   其它下载未完成
	public long downProgress;//下载进度
	public long thread_0;
	public long thread_1;
	public long thread_2;
	public long thread_3;
	public long thread_4;
	
	
	public HomeDownFileTable(){
		primaryKeys = "downURL" ;
	}
	
	public String getDownURL() {
		return downURL;
	}
	public void setDownURL(String downURL) {
		this.downURL = downURL;
	}
	public long getTotalSize() {
		return totalSize;
	}
	public void setTotalSize(long totalSize) {
		this.totalSize = totalSize;
	}
	public String getSaveFilePath() {
		return saveFilePath;
	}
	public void setSaveFilePath(String saveFilePath) {
		this.saveFilePath = saveFilePath;
	}
	public int getDownState() {
		return downState;
	}
	public void setDownState(int downState) {
		this.downState = downState;
	}
	
	public long getDownProgress() {
		return downProgress;
	}
	public void setDownProgress(long downProgress) {
		this.downProgress = downProgress;
	}
	public long getThread_1() {
		return thread_1;
	}
	public void setThread_1(long thread_1) {
		this.thread_1 = thread_1;
	}
	public long getThread_2() {
		return thread_2;
	}
	public void setThread_2(long thread_2) {
		this.thread_2 = thread_2;
	}
	public long getThread_3() {
		return thread_3;
	}
	public void setThread_3(long thread_3) {
		this.thread_3 = thread_3;
	}
	public long getThread_4() {
		return thread_4;
	}
	public void setThread_4(long thread_4) {
		this.thread_4 = thread_4;
	}
	public long getThread_0() {
		return thread_0;
	}
	public void setThread_0(long thread_0) {
		this.thread_0 = thread_0;
	}
	
}
