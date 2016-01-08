package com.guo.androidlib.bitmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

import com.guo.androidlib.bitmap.imp.HomeBitmapCallBack;
import com.guo.androidlib.net.HomeExecutors;

public class HomeBitmapManage {
	private HomeExecutors bmpHomeExecutors;
	private List<String>  startingTaskList = new ArrayList<String>();
	private HashMap<String, Runnable> waitTaskMap = new HashMap<String, Runnable>();
	
	/**
	 * @param version 版本diskCache
	 * @param cacheSize diskCache缓存大小
	 */
	public HomeBitmapManage(Context context,int version,int cacheSize) {
		HomeConfig homeConfig = HomeConfig.getInstance();
		homeConfig.setFileCachePath(context);
		homeConfig.setVersion(version);
		homeConfig.setCacheSize(cacheSize);
		bmpHomeExecutors = new HomeExecutors(5,
				new PriorityBlockingQueue<Runnable>());
	}
	
	/*
	public void setLoadingBitmap(String filePath){
		HomeConfig.getInstance().setLoadingBitmap(BitmapFactory.decodeFile(filePath));
	}
	
	public void setLoadingBitmap(Context context,int drawableId){
		if(context == null)throw new NullPointerException("context is null");
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawableId);
		HomeConfig.getInstance().setLoadingBitmap(bitmap);
	}
	
	public void setLoadFailBitmap(String filePath){
		HomeConfig.getInstance().setLoadingBitmap(BitmapFactory.decodeFile(filePath));
	}
	
	public void setLoadFailBitmap(Context context,int drawableId){
		if(context == null)throw new NullPointerException("context is null");
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawableId);
		HomeConfig.getInstance().setLoadFailBitmap(bitmap);
	}*/
	
	public <T extends View> void display(T view, String bmpUrl, int priority,
			final HomeBitmapCallBack<T> bitmapCallBack,
			HomeBmpDisplayConfig displayConfig) {
		boolean isStartingTask = startingTaskList.contains(bmpUrl);
		boolean hasWaitTask = waitTaskMap.containsKey(bmpUrl);
		
		if(isStartingTask) return ;//任务正在运行
		if(hasWaitTask) return ;//任务已经在等待列表了
		
		HomeBitmapRunnable<T> homeBitmapRunnable = new HomeBitmapRunnable<T>(
				bmpUrl, view, priority, displayConfig, new HomeBitmapCallBack<T>() {
					@Override
					public void loadStart(T view, String url) {
						// TODO Auto-generated method stub
						if(bitmapCallBack != null){
							bitmapCallBack.loadStart(view, url);
						}
					}

					@Override
					public void loadFail(T view, String url) {
						// TODO Auto-generated method stub
						if(bitmapCallBack != null){
							bitmapCallBack.loadFail(view, url);
						}
						startNewTask(url);
					}

					@Override
					public void loadingCallBack(T view, String url,
							long total, long progress) {
						// TODO Auto-generated method stub
						if(bitmapCallBack != null){
							bitmapCallBack.loadingCallBack(view, url, total, progress);
						}
					}

					@Override
					public void loadComplete(T view, String url, Bitmap bmp) {
						// TODO Auto-generated method stub
						if(bitmapCallBack != null){
							bitmapCallBack.loadComplete(view, url, bmp);
						}
						startNewTask(url);
					}
					
					
					private void startNewTask(String url){
						startingTaskList.remove(url);
						int size = waitTaskMap.size();
						if(size > 0){
							Set<String> keySet = waitTaskMap.keySet();
							Iterator<String> iterator = keySet.iterator();
							String downUrl = iterator.next();
							Runnable runnable = waitTaskMap.get(downUrl);
							bmpHomeExecutors.execute(runnable);
							startingTaskList.add(downUrl);
							waitTaskMap.remove(downUrl);
						}
					}
				});
		if(startingTaskList.size() < 8){
			bmpHomeExecutors.execute(homeBitmapRunnable);
			startingTaskList.add(bmpUrl);
		}else{
			waitTaskMap.put(bmpUrl, homeBitmapRunnable);
		}
	}
}
