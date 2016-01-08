package com.guo.androidlib.net;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.guo.androidlib.http.HttpConstanst;
import com.guo.androidlib.net.callback.HomeHttpResponseCallBack;
import com.guo.androidlib.util.GHLog;

public class HomeHttpRunnable implements Runnable {
	private DefaultHttpClient mHttpClient;
	private HttpUriRequest mHttpUriRequest;
	private HttpContext mHttpContext;
	private HomeHttpResponseCallBack mHttpResponseCallBack;
	private String reponseEncode = HTTP.UTF_8;
	
	public HomeHttpRunnable(DefaultHttpClient httpClient,HttpUriRequest httpUriRequest,HomeHttpResponseCallBack httpResponseCallBack) {
		mHttpClient = httpClient;
		mHttpUriRequest = httpUriRequest;
		mHttpResponseCallBack = httpResponseCallBack;
		mHttpContext = new BasicHttpContext();
	}

	public void setHttpContext(HttpContext context){
		mHttpContext = context;
	}
	
	public void setResponseCharSet(String reponseEncode){
		this.reponseEncode = reponseEncode;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		GHLog.gHLog("start:"+mHttpUriRequest.getURI());
		String responseCode = HttpConstanst.RESPONSE_FAIL;
		StringBuilder sb = new StringBuilder();
		try {
			HttpResponse response = mHttpClient.execute(mHttpUriRequest, mHttpContext);
			int statusCode = response.getStatusLine().getStatusCode();
			if(statusCode < 300){//³É¹¦
				responseCode = HttpConstanst.RESPONSE_SUCCESS;
				long startTime = System.currentTimeMillis()/1000;
				sb.append(EntityUtils.toString(response.getEntity(),reponseEncode));
				GHLog.gHLog("read content use time:" + (System.currentTimeMillis()/1000 - startTime));
			}else{
				sb.append("Error Response: "  
                        + response.getStatusLine().toString());
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			sb.append(e.getMessage()+"");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			sb.append(e.getMessage()+"");
		} finally {
			GHLog.gHLog("end:"+mHttpUriRequest.getURI().getPath());
			mHttpResponseCallBack.httpResponseCallBack(responseCode, sb.toString());
		}
	}

}
