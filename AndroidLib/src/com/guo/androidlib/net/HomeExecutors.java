package com.guo.androidlib.net;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;

public class HomeExecutors implements Executor {
	private final static int EXECUTOR_CORE_POOL_SIZE = 5;
	private int MAX_INUM_POOL_SIZE = 32;
	private final int keepAliveTime = 1;
	@SuppressLint("NewApi")
	private BlockingQueue<Runnable> workQueue = new LinkedBlockingDeque<Runnable>();
	

	private ThreadFactory threadFactory = new ThreadFactory() {
		@Override
		public Thread newThread(Runnable r) {
			// TODO Auto-generated method stub
			Thread thread = new Thread(r);
			return thread;
		}
	};
	private ThreadPoolExecutor mThreadPoolExecutor;
	
	public HomeExecutors() {
		// TODO Auto-generated constructor stub
		this(EXECUTOR_CORE_POOL_SIZE,null);
	}
	
	public HomeExecutors(int threadSize,BlockingQueue<Runnable> blockingQueue) {
		// TODO Auto-generated constructor stub
		if(blockingQueue != null){
			this.workQueue = blockingQueue;
		}
		mThreadPoolExecutor = new ThreadPoolExecutor(threadSize, MAX_INUM_POOL_SIZE, keepAliveTime, TimeUnit.SECONDS, workQueue,threadFactory);
	}
	
	public void setMaxNumPoolSize(int maxNumPoolSize){
		MAX_INUM_POOL_SIZE = maxNumPoolSize;
		mThreadPoolExecutor.setMaximumPoolSize(MAX_INUM_POOL_SIZE);
	}
	
	@Override
	public void execute(Runnable command) {
		// TODO Auto-generated method stub
		mThreadPoolExecutor.execute(command);
	}
}
