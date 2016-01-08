package com.guo.androidlib.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import com.guo.androidlib.util.GHLog;

public class HomeListView extends ListView {

	public HomeListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public HomeListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		// TODO Auto-generated method stub
		super.onScrollChanged(l, t, oldl, oldt);
		//GHLog.gHLog("onScrollChanged  left:"+l+"  top:"+t);
	}
	
	@Override
	public void scrollBy(int x, int y) {
		// TODO Auto-generated method stub
		super.scrollBy(x, y);
		GHLog.gHLog("scrollBy x:"+x+"  y:"+y);
	}
	
	@TargetApi(19)
	@Override
	public void scrollListBy(int y) {
		// TODO Auto-generated method stub
		super.scrollListBy(y);
		GHLog.gHLog("scrollListBy y:"+y);
	}
	
	@Override
	public void scrollTo(int x, int y) {
		// TODO Auto-generated method stub
		super.scrollTo(x, y);
		GHLog.gHLog("scrollTo x:"+x+"  y:"+y);
	}
}
