package com.guo.androidlib.net;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import com.guo.androidlib.net.HomeDownFileRunnable.HomeDownFileBranchCallBack;
import com.guo.androidlib.util.GHLog;

public class HomeDownFileBranchRunnable implements Runnable {
	private DefaultHttpClient mHttpClient;
	private HttpUriRequest mHttpUriRequest;
	private HttpContext mHttpContext;
	private HomeDownFileBranchCallBack mDownFileCallBack;
	private String mFilePath ;
	private long mStartPos ;
	private String currentThreadId;
	
	
	public HomeDownFileBranchRunnable(DefaultHttpClient httpClient,String filePath,HttpUriRequest httpUriRequest ,HomeDownFileBranchCallBack downFileCallBack) {
		mHttpClient = httpClient;
		mHttpUriRequest = httpUriRequest;
		mDownFileCallBack = downFileCallBack;
		mFilePath = filePath;
		mHttpContext = new BasicHttpContext();
	}

	public void setHttpContext(HttpContext context){
		mHttpContext = context;
	}
	/**
	 * 
	 * @param start 下载起始位置
	 * @param end 下载结束为止
	 */
	public void setRant(String threadId,long startPos,long endPos){
		//method.addRequestHeader("Range", "bytes=500-");
		currentThreadId = threadId;
		mStartPos = startPos;
		GHLog.gHLog("startPos"+startPos +"endPos:"+endPos +"downLength:"+(endPos - startPos));
		mHttpUriRequest.addHeader("Range", "bytes=" + startPos + "-" + endPos);
	}
	
	public void requestAbort(){
		mHttpUriRequest.abort();
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		GHLog.gHLog(GHLog.LOG_ERROR,Thread.currentThread().getName()+"start:"+mHttpUriRequest.getURI());
		StringBuilder sb = new StringBuilder();
		RandomAccessFile raf = null;
		try {
			HttpResponse response = mHttpClient.execute(mHttpUriRequest, mHttpContext);
			int statusCode = response.getStatusLine().getStatusCode();
			if(statusCode < 300){//成功
				raf = new RandomAccessFile(new File(mFilePath), "rwd");
				raf.seek(mStartPos);//从某个位置写入
				
				long startTime = System.currentTimeMillis()/1000;
				HttpEntity entity = response.getEntity();
				long contentLength = entity.getContentLength();
				GHLog.gHLog("contentLength:"+contentLength);
				
				InputStream is = entity.getContent();
				byte[] b = new byte[8*1024];
				int length = 0;
				long downLength = 0;//下载进度
				while((length = is.read(b)) > 0){
					raf.write(b, 0, length);
					downLength += length;
					mDownFileCallBack.onProgress(currentThreadId,downLength);
				}
				mDownFileCallBack.onSuccess(currentThreadId);
				GHLog.gHLog("read content use time:" + (System.currentTimeMillis()/1000 - startTime));
			}else{
				mDownFileCallBack.onFail(currentThreadId,response.getStatusLine().toString()+"");
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mDownFileCallBack.onFail(currentThreadId, e.getMessage()+"");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mDownFileCallBack.onFail(currentThreadId, e.getMessage()+"");
		} finally {
			if(raf != null){
				try {
					raf.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			GHLog.gHLog("end:"+mHttpUriRequest.getURI().getPath());
			GHLog.gHLog(sb.toString());
		}
	}
}
