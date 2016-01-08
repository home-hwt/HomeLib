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
	
	private HomeExecutors mHomeHttpExecutors = new HomeExecutors();//http�����̳߳�
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
				HomeHttpConstanst.HTTP_TIME_OUT);// ���õȴ����ݳ�ʱʱ��
		HttpConnectionParams.setConnectionTimeout(httpParames,
				HomeHttpConstanst.HTTP_TIME_OUT);// ��������ʱ

		/**
		 * 1��MaxtTotal���������ӵĴ�С�� 2��DefaultMaxPerRoute�Ǹ������ӵ���������MaxTotal��һ��ϸ�֣����磺
		 * MaxtTotal=400 DefaultMaxPerRoute=200
		 * ����ֻ���ӵ�http://sishuok.comʱ������������Ĳ������ֻ��200��������400��
		 * �������ӵ�http://sishuok.com �� http://qq.comʱ����ÿ�������Ĳ������ֻ��200��
		 * ����������400�������ܳ���400�������������õ�������DefaultMaxPerRoute��
		 */
		ConnManagerParams.setMaxConnectionsPerRoute(httpParames,
				new ConnPerRouteBean(10));
		ConnManagerParams.setMaxTotalConnections(httpParames, 10);// �����������ӳ����������

		HttpConnectionParams.setStaleCheckingEnabled(httpParames, true);// ���ύ����֮ǰ
																		// ���������Ƿ����
		HttpConnectionParams.setTcpNoDelay(httpParames, true);// �Ƿ��ӳ�
		HttpConnectionParams.setSocketBufferSize(httpParames, 1024 * 8);// socket����
		HttpProtocolParams.setVersion(httpParames, HttpVersion.HTTP_1_1);// http�汾

		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		schemeRegistry.register(new Scheme("https", HomeDefaultSSLSocket
				.getSocketFactory(), 443));

		mHttpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(
				httpParames, schemeRegistry), httpParames);
		mHttpClient.setHttpRequestRetryHandler(new HomeHttpRequestRetryHandler(
				3));// �ظ���������

		// request header gzip ѹ��
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

		// response gzip ��ѹ
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
	 * @param urlPath �����ַ
	 * @param httpResponseCallBack ������Ӧ�ص�
	 */
	public void sendGetRequest(final String urlPath,HomeHttpResponseCallBack httpResponseCallBack){
		sendGetRequest(urlPath, HTTP.UTF_8, httpResponseCallBack);
	}
	
	/**
	 * get request
	 * @param urlPath �����ַ
	 * @param charSet ��Ӧ����
	 * @param httpResponseCallBack ������Ӧ�ص�
	 */
	public void sendGetRequest(final String urlPath,String charSet,HomeHttpResponseCallBack httpResponseCallBack){
		HttpUriRequest httpUriRequest = new HttpGet(urlPath);
		HomeHttpRunnable homeHttpRunnable = new HomeHttpRunnable(mHttpClient, httpUriRequest, httpResponseCallBack);
		homeHttpRunnable.setResponseCharSet(charSet);
		mHomeHttpExecutors.execute(homeHttpRunnable);
	}
	
	/**
	 * post request
	 * @param urlPath �����ַ
	 * @param paramList post ����
	 * @param httpResponseCallBack ������Ӧ�ص�
	 */
	public void sendPostRequest(final String urlPath,List<NameValuePair> paramList,HomeHttpResponseCallBack httpResponseCallBack){
		sendPostRequest(urlPath, HTTP.UTF_8, paramList, httpResponseCallBack);
	}
	
	/**
	 * post request
	 * @param urlPath �����ַ
	 * @param charSet response ����
	 * @param paramList post ����
	 * @param httpResponseCallBack ������Ӧ�ص�
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
	
	//��ʼ������
	private void initDwonFileConfig(){
		mHomeDownExecutors = new HomeExecutors(5,null);//�����̳߳�
		mHomeDownExecutors.setMaxNumPoolSize(100);//����߳���Ϊ25
		
		HomeDownFileConfig homeDownFileConfig = HomeDownFileConfig.getInstance();
		downFileDataBase = HomeSqliteManage.getInstance().createOrOpenDatabase(homeDownFileConfig.getDefaultFilePath(),homeDownFileConfig.getDbName());
		HomeDownFileTable homeDownFileTable = new HomeDownFileTable();
		homeDownFileTable.setPrimaryKeys("downURL");
		downFileDataBase.createTable(homeDownFileTable);//�������
	}
	
	public void downFileRequest(final String downURLPath,final HomeDownFileCallBack downFileCallBack){
		downFileRequest(downURLPath, null, downFileCallBack);
	}
	
	/**
	 * @param downURLPath  ���ص�ַ
	 * @param filePath �����ļ�����·��
	 * @param downFileCallBack ���ػص�֪ͨ
	 */
	public void downFileRequest(final String downURLPath,String filePath,final HomeDownFileCallBack downFileCallBack){
		if(mHomeDownExecutors == null){
			initDwonFileConfig();
		}
		
		if(filePath == null){
			HomeDownFileConfig homeDownFileConfig = HomeDownFileConfig.getInstance();
			String fileDir = homeDownFileConfig.getDefaultFilePath();//����Ŀ¼
			String extensionName = downURLPath.substring(downURLPath.lastIndexOf(".")+1);
			filePath = new File(fileDir, System.currentTimeMillis() + "." + extensionName).getAbsolutePath();
		}
		
		HomeDownFileRunnable downFileRunnable = new HomeDownFileRunnable(mHttpClient, downURLPath, mHomeDownExecutors, downFileCallBack);
		downFileRunnable.setSaveFilePath(filePath);
		mHomeHttpExecutors.execute(downFileRunnable);
	}
}
