package com.guo.androidlib.test;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.guo.androidlib.bitmap.HomeBitmapManage;
import com.guo.androidlib.bitmap.imp.HomeBitmapCallBack;
import com.xingchen.mhly.you49.R;

public class BitmapListSample extends BaseAdapter {

	private List<String> data;
	private Context context;
	private HomeBitmapManage bitmapManage;

	private Bitmap loadingBitmap;
	private Bitmap loadingFail;

	public BitmapListSample(Context context, List<String> data) {
		this.context = context;
		this.data = data;
		bitmapManage = new HomeBitmapManage(context, 1, 30 * 1024 * 1024);
		loadingBitmap = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.loading_start);
		loadingFail = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.loading_fail);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder vh = null;
		if (convertView == null) {
			vh = new ViewHolder();
			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(R.layout.bitmap_list_item, null);
			vh.bitIv = (ImageView) convertView.findViewById(R.id.imageView1);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		String bmpUrl = data.get(position);
		bitmapManage.display(vh.bitIv, bmpUrl, 0,
				new HomeBitmapCallBack<ImageView>() {
					@Override
					public void loadStart(final ImageView view, String url) {
						// TODO Auto-generated method stub
						view.post(new Runnable() {
							@Override
							public void run() {
								view.setImageBitmap(loadingBitmap);
							}
						});
					}

					@Override
					public void loadFail(final ImageView view, String url) {
						// TODO Auto-generated method stub
						view.post(new Runnable() {
							@Override
							public void run() {
								view.setImageBitmap(loadingFail);
							}
						});
					}

					@Override
					public void loadingCallBack(ImageView view, String url,
							long total, long progress) {
						// TODO Auto-generated method stub

					}

					@Override
					public void loadComplete(final ImageView view, String url,
							final Bitmap bmp) {
						// TODO Auto-generated method stub
						view.post(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								view.setImageBitmap(bmp);
							}
						});
					}
				}, null);
		return convertView;
	}

	private static class ViewHolder {
		public ImageView bitIv;
	}
}
