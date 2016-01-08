package com.guo.androidlib.bitmap;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.view.View;

import com.guo.androidlib.bitmap.DiskLruCache.Editor;
import com.guo.androidlib.bitmap.imp.HomeBitmapCallBack;
import com.guo.androidlib.util.GHLog;

public class HomeBitmapDownLoad {
	/**
	 * 
	 * @param url 下载地址
	 * @return 是否下载成功
	 */
	public synchronized <T extends View> boolean downLoad(String url,String fileName,HomeBitmapCallBack<T> bitmapCallBack) {
		GHLog.gHLog("down:"+url);
		boolean requestState = true;
		HttpURLConnection httpURLConnection = null;
		InputStream is = null;
		Editor edit = null;
		DiskLruCache diskLruCache = null;
		try {
			httpURLConnection = (HttpURLConnection) new URL(url)
					.openConnection();
			httpURLConnection.connect();
			is = httpURLConnection.getInputStream();
			int contentLength = httpURLConnection.getContentLength();
			int responseCode = httpURLConnection.getResponseCode();
			if (HttpURLConnection.HTTP_OK == responseCode) {// 请求成功
				diskLruCache = HomeBitmapUtil.getInstance().getDiskLruCache();
				edit = diskLruCache.edit(fileName);
				OutputStream outputStream = edit.newOutputStream(0);
				byte[] data = new byte[1024 * 8];
				int length = 0;
				long downLength = 0;
				while ((length = is.read(data)) > 0) {
					outputStream.write(data, 0, length);
					downLength += length;
					if(bitmapCallBack != null){
						bitmapCallBack.loadingCallBack(null, url, contentLength, downLength);
					}
				}
				//outputStream.flush();
				//outputStream.close();
				//diskLruCache.flush();
			}else{
				requestState = false;
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			requestState = false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			requestState = false;
		} finally {
			try {
				if (is != null) {
					is.close();
				}
				if(edit != null){
					edit.commit();
				}
				if(diskLruCache != null){
					diskLruCache.flush();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return requestState;
	}
}
