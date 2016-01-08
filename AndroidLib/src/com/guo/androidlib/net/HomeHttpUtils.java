package com.guo.androidlib.net;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import com.guo.androidlib.db.HomeSqliteDatabase;
import com.guo.androidlib.db.HomeSqliteManage;
import com.guo.androidlib.http.HttpConstanst;
import com.guo.androidlib.net.callback.HomeDownFileCallBack;
import com.guo.androidlib.net.callback.HomeHttpResponseCallBack;
import com.guo.androidlib.net.entity.HomeDownFileTable;
import com.lidroid.xutils.http.client.entity.GZipDecompressingEntity;

public class HomeHttpUtils {
	private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
	private static final String ENCODING_GZIP = "gzip";
	private static HomeHttpUtils mHomeHttpUtils;
	
	private DefaultHttpClient mHttpClient;
	
	private HomeExecutors mHomeHttpExecutors = new HomeExecutors();//http请求线程池
	private HomeExecutors mHomeDownExecutors;
	private HomeSqliteDatabase downFileDataBase;

	
	public static HomeHttpUtils getInstance(){
		if(mHomeHttpUtils == null){
			mHomeHttpUtils = new HomeHttpUtils();
		}
		return mHomeHttpUtils;
	}
	
	private HomeHttpUtils() {
		HttpParams httpParames = new BasicHttpParams();

		ConnManagerParams.setTimeout(httpParames,
				HomeHttpConstanst.HTTP_TIME_OUT);
		HttpConnectionParams.setSoTimeout(httpParames,
				HomeHttpConstanst.HTTP_TIME_OUT);// 设置等待数据超时时间
		HttpConnectionParams.setConnectionTimeout(httpParames,
				HomeHttpConstanst.HTTP_TIME_OUT);// 设置请求超时

		/**
		 * 1、MaxtTotal是整个池子的大小； 2、DefaultMaxPerRoute是根据连接到的主机对MaxTotal的一个细分；比如：
		 * MaxtTotal=400 DefaultMaxPerRoute=200
		 * 而我只连接到http://sishuok.com时，到这个主机的并发最多只有200；而不是400；
		 * 而我连接到http://sishuok.com 和 http://qq.com时，到每个主机的并发最多只有200；
		 * 即加起来是400（但不能超过400）；所以起作用的设置是DefaultMaxPerRoute。
		 */
		ConnManagerParams.setMaxConnectionsPerRoute(httpParames,
				new ConnPerRouteBean(10));
		ConnManagerParams.setMaxTotalConnections(httpParames, 10);// 设置整个连接池最大连接数

		HttpConnectionParams.setStaleCheckingEnabled(httpParames, true);// 在提交请求之前
																		// 测试连接是否可用
		HttpConnectionParams.setTcpNoDelay(httpParames, true);// 是否不延迟
		HttpConnectionParams.setSocketBufferSize(httpParames, 1024 * 8);// socket缓存
		HttpProtocolParams.setVersion(httpParames, HttpVersion.HTTP_1_1);// http版本

		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		schemeRegistry.register(new Scheme("https", HomeDefaultSSLSocket
				.getSocketFactory(), 443));

		mHttpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(
				httpParames, schemeRegistry), httpParames);
		mHttpClient.setHttpRequestRetryHandler(new HomeHttpRequestRetryHandler(
				3));// 重复请求三次

		// request header gzip 压缩
		mHttpClient.addRequestInterceptor(new HttpRequestInterceptor() {
			@Override
			public void process(org.apache.http.HttpRequest httpRequest,
					HttpContext httpContext) throws HttpException, IOException {
				if (!httpRequest.containsHeader(HEADER_ACCEPT_ENCODING)) {
					httpRequest
							.addHeader(HEADER_ACCEPT_ENCODING, ENCODING_GZIP);
				}
			}
		});

		// response gzip 解压
		mHttpClient.addResponseInterceptor(new HttpResponseInterceptor() {
			@Override
			public void process(HttpResponse response, HttpContext httpContext)
					throws org.apache.http.HttpException, IOException {
				final HttpEntity entity = response.getEntity();
				if (entity == null) {
					return;
				}
				final Header encoding = entity.getContentEncoding();
				if (encoding != null) {
					for (HeaderElement element : encoding.getElements()) {
						if (element.getName().equalsIgnoreCase(ENCODING_GZIP)) {
							response.setEntity(new GZipDecompressingEntity(
									response.getEntity()));
							return;
						}
					}
				}
			}
		});
	}
	
	private void androidHttpUrlConnect(String url){
		try {
			HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
			urlConnection.connect();
			//urlConnection.setRequestProperty(field, newValue)
			InputStream is = urlConnection.getInputStream();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public HomeSqliteDatabase getDownFileDatabase(){
		return downFileDataBase;
	}
	
	/**
	 * get request
	 * @param urlPath 请求地址
	 * @param httpResponseCallBack 请求响应回调
	 */
	public void sendGetRequest(final String urlPath,HomeHttpResponseCallBack httpResponseCallBack){
		sendGetRequest(urlPath, HTTP.UTF_8, httpResponseCallBack);
	}
	
	/**
	 * get request
	 * @param urlPath 请求地址
	 * @param charSet 响应编码
	 * @param httpResponseCallBack 请求响应回调
	 */
	public void sendGetRequest(final String urlPath,String charSet,HomeHttpResponseCallBack httpResponseCallBack){
		HttpUriRequest httpUriRequest = new HttpGet(urlPath);
		HomeHttpRunnable homeHttpRunnable = new HomeHttpRunnable(mHttpClient, httpUriRequest, httpResponseCallBack);
		homeHttpRunnable.setResponseCharSet(charSet);
		mHomeHttpExecutors.execute(homeHttpRunnable);
	}
	
	/**
	 * post request
	 * @param urlPath 请求地址
	 * @param paramList post 参数
	 * @param httpResponseCallBack 请求响应回调
	 */
	public void sendPostRequest(final String urlPath,List<NameValuePair> paramList,HomeHttpResponseCallBack httpResponseCallBack){
		sendPostRequest(urlPath, HTTP.UTF_8, paramList, httpResponseCallBack);
	}
	
	/**
	 * post request
	 * @param urlPath 请求地址
	 * @param charSet response 编码
	 * @param paramList post 参数
	 * @param httpResponseCallBack 请求响应回调
	 */
	public void sendPostRequest(final String urlPath,String charSet,List<NameValuePair> paramList,HomeHttpResponseCallBack httpResponseCallBack){
		HttpPost httpPost = new HttpPost(urlPath);
		//List<NameValuePair> paramList = new ArrayList<NameValuePair>(); 
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(paramList,HTTP.UTF_8));
			HomeHttpRunnable homeHttpRunnable = new HomeHttpRunnable(mHttpClient, httpPost, httpResponseCallBack);
			homeHttpRunnable.setResponseCharSet(charSet);
			mHomeHttpExecutors.execute(homeHttpRunnable);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			httpResponseCallBack.httpResponseCallBack(HttpConstanst.RESPONSE_FAIL, e.getMessage()+"");
		} 
	}
	
	//初始化下载
	private void initDwonFileConfig(){
		mHomeDownExecutors = new HomeExecutors(5,null);//下载线程池
		mHomeDownExecutors.setMaxNumPoolSize(100);//最大线程数为25
		
		HomeDownFileConfig homeDownFileConfig = HomeDownFileConfig.getInstance();
		downFileDataBase = HomeSqliteManage.getInstance().createOrOpenDatabase(homeDownFileConfig.getDefaultFilePath(),homeDownFileConfig.getDbName());
		HomeDownFileTable homeDownFileTable = new HomeDownFileTable();
		homeDownFileTable.setPrimaryKeys("downURL");
		downFileDataBase.createTable(homeDownFileTable);//创建表格
	}
	
	public void downFileRequest(final String downURLPath,final HomeDownFileCallBack downFileCallBack){
		downFileRequest(downURLPath, null, downFileCallBack);
	}
	
	/**
	 * @param downURLPath  下载地址
	 * @param filePath 下载文件保存路径
	 * @param downFileCallBack 下载回调通知
	 */
	public void downFileRequest(final String downURLPath,String filePath,final HomeDownFileCallBack downFileCallBack){
		if(mHomeDownExecutors == null){
			initDwonFileConfig();
		}
		
		if(filePath == null){
			HomeDownFileConfig homeDownFileConfig = HomeDownFileConfig.getInstance();
			String fileDir = homeDownFileConfig.getDefaultFilePath();//保存目录
			String extensionName = downURLPath.substring(downURLPath.lastIndexOf(".")+1);
			filePath = new File(fileDir, System.currentTimeMillis() + "." + extensionName).getAbsolutePath();
		}
		
		HomeDownFileRunnable downFileRunnable = new HomeDownFileRunnable(mHttpClient, downURLPath, mHomeDownExecutors, downFileCallBack);
		downFileRunnable.setSaveFilePath(filePath);
		mHomeHttpExecutors.execute(downFileRunnable);
	}
}
