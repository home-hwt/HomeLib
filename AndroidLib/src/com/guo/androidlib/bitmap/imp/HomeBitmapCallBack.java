package com.guo.androidlib.bitmap.imp;

import android.graphics.Bitmap;
import android.view.View;

public interface HomeBitmapCallBack<T extends View> {
	public void loadStart(T view,String url);
	public void loadFail(T view,String url);
	public void loadingCallBack(T view,String url,long total ,long progress);
	public void loadComplete(T view , String url , Bitmap bmp);
}
