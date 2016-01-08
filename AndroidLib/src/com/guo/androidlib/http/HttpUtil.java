package com.guo.androidlib.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.guo.androidlib.util.GHLog;

public class HttpUtil {
	private static HttpUtil httpUtil;

	private String mEncodeFormat = "UTF-8";

	private HttpUtil() {
	};

	public static HttpUtil getInstance() {
		if (httpUtil == null) {
			httpUtil = new HttpUtil();
		}
		return httpUtil;
	}

	public void setEncoding(String encodeFormat) {
		mEncodeFormat = encodeFormat;
	}

	public String getEncodeFormat() {
		return mEncodeFormat;
	}

	public HashMap<String, String> httpPostRequest(String requestUrl,
			String parames) {
		HashMap<String, String> responseMap = new HashMap<String, String>();
		responseMap.put(HttpConstanst.RESPONSE_CODE_KEY,
				HttpConstanst.RESPONSE_FAIL);
		HttpURLConnection conn = null;
		OutputStream os = null;
		try {
			URL url = new URL(requestUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("Post");
			conn.setConnectTimeout(15000);
			os = conn.getOutputStream();
			
			parames = URLEncoder.encode(parames, getEncodeFormat());
			os.write(new Byte(parames));
			os.flush();
			int responseCode = conn.getResponseCode();
			if (HttpURLConnection.HTTP_OK == responseCode) {// 成功
				String responseMessage = conn.getResponseMessage();
				responseMap.put(HttpConstanst.RESPONSE_CODE_KEY,
						HttpConstanst.RESPONSE_SUCCESS);
				responseMap
						.put(HttpConstanst.RESPONSE_MSG_KEY, responseMessage);
			} else {// 失败
				responseMap.put(HttpConstanst.RESPONSE_MSG_KEY,
						"response code:" + conn.getResponseCode());
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMap.put(HttpConstanst.RESPONSE_MSG_KEY, e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMap.put(HttpConstanst.RESPONSE_MSG_KEY, e.getMessage());
		} finally {
			if (conn != null) {
				conn.disconnect();
			}

			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return responseMap;
	}

	public HashMap<String, String> httpGetRequest(String requestUrl) {
		HashMap<String, String> responseMap = new HashMap<String, String>();
		responseMap.put(HttpConstanst.RESPONSE_CODE_KEY,
				HttpConstanst.RESPONSE_FAIL);
		HttpURLConnection conn = null;
		InputStream is = null;
		try {
			requestUrl = URLEncoder.encode(requestUrl, getEncodeFormat());
			URL url = new URL(requestUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(15000);
			conn.connect();
			is = conn.getInputStream();
			int responseCode = conn.getResponseCode();
			if (HttpURLConnection.HTTP_OK == responseCode) {// 成功
				byte[] buffer = new byte[1024 * 1024];
				int length = 0;
				StringBuffer sb = new StringBuffer();
				while ((length = is.read(buffer)) > 0) {
					sb.append(new String(buffer, 0, length));
				}
				responseMap.put(HttpConstanst.RESPONSE_CODE_KEY,
						HttpConstanst.RESPONSE_SUCCESS);
				responseMap.put(HttpConstanst.RESPONSE_MSG_KEY, sb.toString());
				GHLog.gHLog("response:" + sb);
			} else {
				responseMap.put(HttpConstanst.RESPONSE_MSG_KEY, "responseCode:"
						+ responseCode);
			}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			GHLog.gHLog(GHLog.LOG_ERROR, e.getMessage());
			responseMap.put(HttpConstanst.RESPONSE_MSG_KEY, e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			GHLog.gHLog(GHLog.LOG_ERROR, e.getMessage());
			responseMap.put(HttpConstanst.RESPONSE_MSG_KEY, e.getMessage());
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return responseMap;
	}

	/**
	 * 检查当前网络是否可用
	 * 
	 * @param context
	 * @return
	 */

	public boolean isNetworkAvailable(Context context) {
		// 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager == null) {
			return false;
		} else {
			// 获取NetworkInfo对象
			NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
			if (networkInfo != null && networkInfo.length > 0) {
				for (int i = 0; i < networkInfo.length; i++) {
					GHLog.gHLog(i + "===状态===" + networkInfo[i].getState());
					GHLog.gHLog(i + "===类型===" + networkInfo[i].getTypeName());
					// 判断当前网络状态是否为连接状态
					if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
