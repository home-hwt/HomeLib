package com.guo.androidlib;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.guo.androidlib.bitmap.HomeBitmapManage;
import com.guo.androidlib.bitmap.HomeBmpDisplayConfig;
import com.guo.androidlib.bitmap.imp.HomeBitmapCallBack;
import com.guo.androidlib.db.HomeSqliteDatabase;
import com.guo.androidlib.db.HomeSqliteManage;
import com.guo.androidlib.db.HomeWhereEntity;
import com.guo.androidlib.db.entity.HomeTableEntity;
import com.guo.androidlib.http.HomeDownLoadListener;
import com.guo.androidlib.http.HomeFileDownLoad;
import com.guo.androidlib.net.HomeHttpUtils;
import com.guo.androidlib.net.HomeResponseInfo;
import com.guo.androidlib.net.callback.HomeDownFileCallBack;
import com.guo.androidlib.test.DbTablePerson;
import com.guo.androidlib.test.HomeListAdapter;
import com.guo.androidlib.test.VideoItem;
import com.guo.androidlib.util.GHDevice;
import com.guo.androidlib.util.GHLog;
import com.guo.androidlib.view.ArrowDownloadButton;
import com.guo.androidlib.view.HomeCircleProgress;
import com.xingchen.mhly.you49.R;

public class MainActivity extends Activity implements OnClickListener {

	private ListView mTestListView;
	private ArrowDownloadButton mDownProgressView;
	private HomeCircleProgress mCircleProgressView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		
		String[] cpuInfo = GHDevice.getInstance().getCpuInfo();
		for (int i = 0; i < cpuInfo.length; i++) {
			GHLog.gHLog("cpuInfo"+cpuInfo[i]);
		}
	}

	private void initView(){
		mDownProgressView = (ArrowDownloadButton)findViewById(R.id.h_down_progress_v);
		mDownProgressView.setOnClickListener(this);
		
		mCircleProgressView = (HomeCircleProgress) findViewById(R.id.h_download_view);
		mCircleProgressView.setOnClickListener(this);
		
		mTestListView = (ListView) findViewById(R.id.listView1);
		
		List<VideoItem> data = new ArrayList<VideoItem>();
		for (int i = 0; i < 50; i++) {
			//data.add("videa-"+i);
			VideoItem videoItem = new VideoItem();
			videoItem.setTitle("video-"+i);
			videoItem.setDownLink("http://qiubai-video.qiushibaike.com/J9Z9NPK7VCMWEIQL.mp4");
			
			File sdCardFile = Environment.getExternalStorageDirectory();
			File file = new File(sdCardFile,"homeJoke");
			file = new File(file, videoItem.getDownLink().substring(videoItem.getDownLink().lastIndexOf("/")+1));
			videoItem.setSavePath(file.getAbsolutePath());
			data.add(videoItem);
		}

		HomeListAdapter homeListAdapter = new HomeListAdapter(this, data);
		mTestListView.setAdapter(homeListAdapter);
		
		/*
		mTestListView.setOnScrollListener(new OnScrollListener() {
			private int mScrollState = OnScrollListener.SCROLL_STATE_IDLE;
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				GHLog.gHLog("onScrollStateChanged scrollState"+scrollState);
				mScrollState = scrollState;
				view.getFirstVisiblePosition();
				int childCount = mTestListView.getChildCount();
				GHLog.gHLog("childCount"+childCount);
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				GHLog.gHLog("onScrollStateChanged firstVisibleItem"+firstVisibleItem+"visibleItemCount"+visibleItemCount+"totalItemCount"+totalItemCount);
				if(mScrollState == OnScrollListener.SCROLL_STATE_IDLE){
					View childAt = view.getChildAt(0);
					Object itemAtPosition = mTestListView.getItemAtPosition(firstVisibleItem);
					Object item = view.getAdapter().getItem(firstVisibleItem);
					GHLog.gHLog(GHLog.LOG_ERROR, itemAtPosition+"item"+childAt+"visible:"+ (childAt!=null?childAt.getVisibility():-1));
				}
			}
		});
		*/
		
		
		ImageView firstImage = (ImageView) findViewById(R.id.imageView1);
		ImageView secondImage = (ImageView) findViewById(R.id.imageView2);
		
		HomeBitmapManage bitmapManage = new HomeBitmapManage(this,1,30*1024*1024);
		HomeBitmapCallBack<ImageView> bitmapCallBack = new HomeBitmapCallBack<ImageView>() {

			@Override
			public void loadStart(ImageView view, String url) {
				// TODO Auto-generated method stub
				GHLog.gHLog("loadStart"+url);
			}

			@Override
			public void loadFail(ImageView view, String url) {
				// TODO Auto-generated method stub
				GHLog.gHLog("loadFail"+url);
			}

			@Override
			public void loadingCallBack(ImageView view, String url, long total,
					long progress) {
				// TODO Auto-generated method stub
				GHLog.gHLog("loadingCallBack"+progress);
			}

			@Override
			public void loadComplete(final ImageView view, String url, final Bitmap bmp) {
				// TODO Auto-generated method stub
				view.post(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						view.setImageBitmap(bmp);
					}
				});
			}
		};
		String bmpUrl = "http://code4app.qiniudn.com/photo/51fca7546803faeb67000001_1.png";
		//bmpUrl = "http://www.apkbus.com/data/attachment/portal/201511/18/143017jo67umn1xzgaj116.jpg";
		//bmpUrl = "http://www.apkbus.com/data/attachment/forum/201511/20/121319ccacmq5lmkk7mcce.png";
		HomeBmpDisplayConfig displayConfig = new HomeBmpDisplayConfig();
		displayConfig.compress = 60;
		bitmapManage.display(firstImage, bmpUrl, 0, bitmapCallBack , displayConfig);
		
//		displayConfig.requestHeight = 60;
//		displayConfig.requestWidth = 80;
		//bitmapManage.display(secondImage, bmpUrl, 0, bitmapCallBack, displayConfig );
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	public void mainLayoutViewClick(View view){
		switch (view.getId()) {
		case R.id.id_http_request_bt:
			String downUrl = "http://qiubai-video.qiushibaike.com/J9Z9NPK7VCMWEIQL.mp4";
			File sdCardFile = Environment.getExternalStorageDirectory();
			File file = new File(sdCardFile,"homeJoke");
			file = new File(file, downUrl.substring(downUrl.lastIndexOf("/")+1));
			HomeFileDownLoad.getInstance().singleThreadDownFile(downUrl, file.getAbsolutePath(),new HomeDownLoadListener() {
				@Override
				public void downProgress(int downState, long total, long progress,String filePathOrErrorMsg) {
					// TODO Auto-generated method stub
					switch (downState) {
					case HomeDownLoadListener.HOMEDOWNLOADING:
						int s = (int) (progress*1.0f/total*100);
						GHLog.gHLog("s:"+s);
						mDownProgressView.setProgress(s);
						break;
					case HOMEDOWNLOAD_SUCCESS:
						
						break;
					default:
						break;
					}
				}
			});
			break;
		case R.id.id_op_db_bt:
			HomeSqliteDatabase sqlDb = HomeSqliteManage.getInstance().createOrOpenDatabase(this, "home");
			DbTablePerson tableEntity = new DbTablePerson();
			
			//删除
			//sqlDb.dropTable(DbTablePerson.class);
				
			//插入
			tableEntity.setAge(10);
			tableEntity.setName("zhangsan");
			//tableEntity.setPrimaryKeys("ID AUTOINCREMENT");
			sqlDb.save(tableEntity);
				
			//查询
			HomeWhereEntity where = HomeWhereEntity.newInstance();
			where.add("age", "=", 10);
			List<HomeTableEntity> query = sqlDb.query(tableEntity, where);
			Log.e("querey", query.size()+"size");
			
		
			sqlDb.alterTable(tableEntity);
			
			break;
		default:
			break;
		}
	}

	
	int count = 0;
	int progress = 0;
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
	}
	
	public void opThreadPool(View v){
		HomeHttpUtils httpUtils = HomeHttpUtils.getInstance();
		//get 请求
//		httpUtils.sendGetRequest("http://cdn.javaapk.com/B3D25DFFF779B4E47B6BA6C5894ED3EC.rar", new HomeHttpResponseCallBack() {
//			@Override
//			public void httpResponseCallBack(String code, String responseContent) {
//				// TODO Auto-generated method stub
//				GHLog.gHLog("response:"+responseContent);
//				if(HttpConstanst.RESPONSE_SUCCESS.equalsIgnoreCase(code)){//请求成功
//				}else{//失败
//				}
//			}
//		});
		
		
		/**
		 * 文件下载
		 */
		File sdFile = Environment.getExternalStorageDirectory();
		File file = new File(sdFile,"BCD051FFF09E84388EAC2380DA383CD2.apk");
		file.delete();
		httpUtils.downFileRequest("http://119.147.254.64/dd.myapp.com/16891/BCD051FFF09E84388EAC2380DA383CD2.apk?mkey=563c4c07827c31fb&f=f24&fsname=com.ishugui_1.3.18.10353_1010318.apk&asr=02f1&p=.apk",file.getAbsolutePath(),new HomeDownFileCallBack() {
			@Override
			public void onSuccess(HomeResponseInfo response) {
				// TODO Auto-generated method stub
				GHLog.gHLog("downFileRequest:success");
			}
			
			@Override
			public void onProgress(long progress, long total) {
				// TODO Auto-generated method stub
				GHLog.gHLog("onProgress" + progress +" total:"+total);
			}
			
			@Override
			public void onFail(String errorMsg) {
				// TODO Auto-generated method stub
				GHLog.gHLog(GHLog.LOG_ERROR, errorMsg);
			}
		});
	}
	
	
	
	public void intent2Bitmap(View view){
		startActivity(new Intent(this, BitmapSample.class));
	}
}
