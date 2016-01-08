package com.guo.androidlib.net;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.guo.androidlib.db.HomeSqliteDatabase;
import com.guo.androidlib.db.HomeWhereEntity;
import com.guo.androidlib.db.entity.HomeTableEntity;
import com.guo.androidlib.net.callback.HomeDownFileCallBack;
import com.guo.androidlib.net.entity.HomeDownFileTable;
import com.guo.androidlib.util.GHLog;

public class HomeDownFileRunnable implements Runnable {
	private DefaultHttpClient mHttpClient;
	private HomeDownFileCallBack mDownFileCallBack;
	private String mDownUrlPath;
	private String mFilePath;
	private HomeExecutors mHomeDownExecutors;
	private long mTotalSize;
	private HomeDownFileTable homeDownFileTable = new HomeDownFileTable();
	private long downProgress;

	public HomeDownFileRunnable(DefaultHttpClient httpClient,
			String downUrlPath, HomeExecutors homeExecutors,
			HomeDownFileCallBack downFileCallBack) {
		mHttpClient = httpClient;
		mDownFileCallBack = downFileCallBack;
		mDownUrlPath = downUrlPath;
		mHomeDownExecutors = homeExecutors;
		homeDownFileTable.setPrimaryKeys("downURL");
	}

	public void setSaveFilePath(String filePath) {
		mFilePath = filePath;
	}

	private void saveEmptyRecord(HomeSqliteDatabase downFileDatabase){
		homeDownFileTable = new HomeDownFileTable();
		homeDownFileTable.setDownURL(mDownUrlPath);
		homeDownFileTable.setTotalSize(mTotalSize);
		homeDownFileTable.setSaveFilePath(mFilePath);
		downFileDatabase.save(homeDownFileTable);
	}
	
	private void deleteInvalidRecord(HomeSqliteDatabase downFileDatabase,HomeWhereEntity whereEntity){
		downFileDatabase.delete(homeDownFileTable.getClass(), whereEntity);
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			HttpGet httpGet = new HttpGet(mDownUrlPath);
			HttpResponse response = mHttpClient.execute(httpGet);
			mTotalSize = response.getEntity().getContentLength();
			GHLog.gHLog("AllcontentLength" + mTotalSize);
			int threadCount = 3;
			if (mTotalSize > 50 * 1024 * 1024) {// 大于50M就开启五个线程
				threadCount = 5;
			} else if (mTotalSize < 50 * 1024) {// 小于50K单线程下载
				threadCount = 1;
			}

			final File file = new File(mFilePath);
			if (file.exists()) {
				file.createNewFile();
			}
			long singleDownSize = mTotalSize / threadCount;
			// id downURL thread_one----five totalSize saveFilePath downState
			final HomeSqliteDatabase downFileDatabase = HomeHttpUtils
					.getInstance().getDownFileDatabase();

			final HomeWhereEntity whereEntity = HomeWhereEntity.newInstance();
			whereEntity.add("downURL", "=", mDownUrlPath);
			List<HomeTableEntity> query = downFileDatabase.query(
					homeDownFileTable, whereEntity);
			if (query.size() > 0) {
				homeDownFileTable = (HomeDownFileTable) query.get(0);
				int downState = homeDownFileTable.getDownState();
				long totalSize = homeDownFileTable.getTotalSize();// 之前保存的
				String saveFilePath = homeDownFileTable.getSaveFilePath();
				boolean isSaveFileExists = false;
				if (saveFilePath != null) {
					File saveFile = new File(saveFilePath);
					isSaveFileExists = saveFile.exists();
				}
				
				if(!isSaveFileExists){//文件不存在
					deleteInvalidRecord(downFileDatabase,whereEntity);
					saveEmptyRecord(downFileDatabase);
				}else{
					long preDownProgress = homeDownFileTable.getDownProgress();
					mDownFileCallBack.onProgress(preDownProgress, totalSize);
					if (downState == 1 && totalSize == mTotalSize && isSaveFileExists) {// 下载完成
						HomeResponseInfo homeResponseInfo = new HomeResponseInfo();
						homeResponseInfo.setFileName(file.getName());
						homeResponseInfo.setSaveFilePath(file.getAbsolutePath());
						homeResponseInfo.setLength(mTotalSize);
						mDownFileCallBack.onProgress(preDownProgress, totalSize);
						mDownFileCallBack.onSuccess(homeResponseInfo);
						return;
					}
				} 
			} else {
				saveEmptyRecord(downFileDatabase);
			}
			
			downProgress = homeDownFileTable.getDownProgress();

			// downFileDatabase.beginTransaction();
			final String[] threadIds = new String[threadCount];// 线程编号
			// 所有分支下载进度监听
			HomeDownFileBranchCallBack downFileBranchCallBack = new HomeDownFileBranchCallBack() {
				private HashMap<String, Long> progressMap = new HashMap<String, Long>();
				@Override
				public void onSuccess(String threadId) {
					// TODO Auto-generated method stub
					if (downProgress >= mTotalSize) {
						HomeResponseInfo homeResponseInfo = new HomeResponseInfo();
						homeResponseInfo.setFileName(file.getName());
						homeResponseInfo
								.setSaveFilePath(file.getAbsolutePath());
						homeResponseInfo.setLength(mTotalSize);
						mDownFileCallBack.onSuccess(homeResponseInfo);
						homeDownFileTable.setDownState(1);
						downFileDatabase.update(homeDownFileTable, whereEntity);
					}
				}

				// 更新对应线程下载进度
				private void updateThreadIdProgress(String threadId,
						long progress) {
					for (int i = 0; i < threadIds.length; i++) {
						if (threadId != null
								&& threadId.equalsIgnoreCase(threadIds[i])) {
							switch (i) {
							case 0:
								homeDownFileTable.setThread_0(progress);
								break;
							case 1:
								homeDownFileTable.setThread_1(progress);
								break;
							case 2:
								homeDownFileTable.setThread_2(progress);
								break;
							case 3:
								homeDownFileTable.setThread_3(progress);
								break;
							case 4:
								homeDownFileTable.setThread_4(progress);
								break;
							}
						}
					}
				}

				@Override
				public synchronized void onProgress(String threadId,
						long progress) {
					// TODO Auto-generated method stub
					GHLog.gHLog("threadId" + threadId + "progress:" + progress);
					Long longValue = progressMap.get(threadId);
					Long progressValue = longValue == null ? 0 : longValue;
					long addProgress = progress - progressValue;
					downProgress += addProgress;
					progressMap.put(threadId, progress);
					mDownFileCallBack.onProgress(downProgress, mTotalSize);
					// 更新数据库
					homeDownFileTable.setDownProgress(downProgress);
					updateThreadIdProgress(threadId, progress);
					downFileDatabase.update(homeDownFileTable, whereEntity);
				}

				@Override
				public void onFail(String threadId, String errorMsg) {
					// TODO Auto-generated method stub
					file.delete();
					mDownFileCallBack.onFail(errorMsg);
				}
			};

			for (int i = 0; i < threadCount; i++) {
				HttpGet downHttpGet = new HttpGet(mDownUrlPath);
				HomeDownFileBranchRunnable homeDownFileRunnable = new HomeDownFileBranchRunnable(
						mHttpClient, mFilePath, downHttpGet,
						downFileBranchCallBack);
				threadIds[i] = "Thread_" + i;
				long threadProgress = 0;
				try {
					Method method = homeDownFileTable.getClass().getMethod(
							"get" + threadIds[i]);
					threadProgress = (Long) method.invoke(homeDownFileTable);
					GHLog.gHLog("threadProgress:" + threadProgress);
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				long startPos = 0;
				long endPos = 0;
				if (i == threadCount - 1) {// 最后一项
					startPos = i * singleDownSize + 1 + threadProgress;
					endPos = mTotalSize;
				} else if (i == 0) {
					startPos = 0 + threadProgress;
					endPos = (i + 1) * singleDownSize;
				} else {
					startPos = i * singleDownSize + 1 + threadProgress;
					endPos = (i + 1) * singleDownSize;
				}
				GHLog.gHLog("startPos:" + startPos + "endPos:" + endPos);
				if (startPos < endPos) {
					homeDownFileRunnable
							.setRant(threadIds[i], startPos, endPos);
					mHomeDownExecutors.execute(homeDownFileRunnable);
				}
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public interface HomeDownFileBranchCallBack {
		/**
		 * @param threadId
		 * @param progress
		 *            增加的下载进度
		 */
		public void onProgress(String threadId, long progress);

		public void onSuccess(String threadId);

		public void onFail(String threadId, String errorMsg);
	}
}
