package com.guo.androidlib;

import java.util.ArrayList;
import java.util.List;

import com.guo.androidlib.test.BitmapListSample;
import com.xingchen.mhly.you49.R;

import android.app.Activity;
import android.widget.ListView;

public class BitmapSample extends Activity {
	private ListView bitmapList = null;
	private void initData(){
		List<String> data = new ArrayList<String>();
		
		for (int i = 0; i < 1; i++) {
			data.add("http://code4app.qiniudn.com/photo/51fca7546803faeb67000001_1.png");
			data.add("http://res.javaapk.com/wp-content/uploads/2015/11/QQ%E6%88%AA%E5%9B%BE20151126211046.jpg");
			data.add("http://res.javaapk.com/wp-content/uploads/2015/11/QQ%E6%88%AA%E5%9B%BE20151126205154.jpg");
			data.add("http://res.javaapk.com/wp-content/uploads/2015/11/%E6%88%AA%E5%9B%BE20141126-7.png");
			data.add("http://res.javaapk.com/wp-content/uploads/2015/11/QQ%E6%88%AA%E5%9B%BE20151102210534.png");
			data.add("http://res.javaapk.com/wp-content/uploads/2015/11/QQ%E6%88%AA%E5%9B%BE20151103163912.png");
			data.add("http://res.javaapk.com/wp-content/uploads/2015/11/QQ%E6%88%AA%E5%9B%BE20151103105249.png");
			data.add("http://res.javaapk.com/wp-content/uploads/2015/10/QQ%E6%88%AA%E5%9B%BE20151023204943.png");
			data.add("http://res.javaapk.com/wp-content/uploads/2015/10/QQ%E6%88%AA%E5%9B%BE20151022150642.png");
			data.add("http://res.javaapk.com/wp-content/uploads/2015/10/QQ%E6%88%AA%E5%9B%BE20151016091938.png");
		}
		bitmapList.setAdapter(new BitmapListSample(this, data));
	}
	
	private void initView(){
		bitmapList = (ListView) findViewById(R.id.bitmap_sample_list);
		initData();
	}
	
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bitmap_sample);
		initView();
	};
	
	
	
}
