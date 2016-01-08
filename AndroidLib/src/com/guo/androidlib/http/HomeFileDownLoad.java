package com.guo.androidlib.http;

import java.io.File;

import com.guo.androidlib.util.GHLog;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

public class HomeFileDownLoad {
	private static HomeFileDownLoad homeFileDownLoad;

	public static HomeFileDownLoad getInstance() {
		if (homeFileDownLoad == null) {
			homeFileDownLoad = new HomeFileDownLoad();
		}
		return homeFileDownLoad;
	}

	/**
	 * 
	 * @param downUrl
	 *            �ļ����ص�ַ
	 * @param savePath
	 *            ���ر���·��
	 */
	public HttpHandler<File> singleThreadDownFile(String downUrl,
			String savePath, final HomeDownLoadListener downLoadListener) {
		HttpUtils http = new HttpUtils();
		HttpHandler<File> handler = http.download(downUrl, savePath, false, // ���Ŀ���ļ����ڣ�����δ��ɵĲ��ּ������ء���������֧��RANGEʱ���������ء�
				false, // ��������󷵻���Ϣ�л�ȡ���ļ�����������ɺ��Զ���������
				new RequestCallBack<File>() {
					@Override
					public void onStart() {
						GHLog.gHLog("onstart down");
					}

					@Override
					public void onLoading(long total, long current,
							boolean isUploading) {
						GHLog.gHLog("onLoading" + total + "current:" + current);
						downLoadListener.downProgress(
								HomeDownLoadListener.HOMEDOWNLOADING, total,
								current, "");
					}

					@Override
					public void onSuccess(ResponseInfo<File> responseInfo) {
						File file = responseInfo.result;
						GHLog.gHLog("onSuccess" + file.getName());
						downLoadListener.downProgress(
								HomeDownLoadListener.HOMEDOWNLOAD_SUCCESS,
								responseInfo.contentLength,
								responseInfo.contentLength,
								file.getAbsolutePath());
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						String message = error.getMessage();
						GHLog.gHLog(GHLog.LOG_ERROR, "message:" + message
								+ "onFailure" + msg, msg);
					}
				});
		return handler;
	}
}
