package com.guo.androidlib.test;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.guo.androidlib.http.HomeDownLoadListener;
import com.guo.androidlib.http.HomeFileDownLoad;
import com.guo.androidlib.view.HomeCircleProgress;
import com.guo.androidlib.view.VideoTextureView;
import com.xingchen.mhly.you49.R;

public class HomeListAdapter extends BaseAdapter {

	private Context mContext;
	private List<VideoItem> mResourceData;

	public HomeListAdapter(Context context, List<VideoItem> data) {
		mContext = context;
		mResourceData = data;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mResourceData.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mResourceData.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(R.layout.h_video_list_item, null);
		TextView titleTv = (TextView) view.findViewById(R.id.h_video_tilte_tv);
		final VideoTextureView textureView = (VideoTextureView) view
				.findViewById(R.id.h_video_textureview);
		final VideoItem videoItem = mResourceData.get(position);
		titleTv.setText(videoItem.getTitle());
		ImageView playIv = (ImageView) view.findViewById(R.id.h_play_iv);
		final HomeCircleProgress hcp = (HomeCircleProgress) view.findViewById(R.id.h_download_view);
		playIv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(mContext, "play :" + position, Toast.LENGTH_LONG)
						.show();
				HomeFileDownLoad.getInstance().singleThreadDownFile(videoItem.getDownLink(), videoItem.getSavePath(), new HomeDownLoadListener() {
					@Override
					public void downProgress(int downState, long total, long progress,
							String filePathOrErrorMsg) {
						// TODO Auto-generated method stub
						switch (downState) {
						case HomeDownLoadListener.HOMEDOWNLOADING:
							hcp.setProgress((int) (progress*100.0f/total));
							break;
						case HOMEDOWNLOAD_SUCCESS:
							textureView.prepare(mContext, filePathOrErrorMsg);
							break;
						default:
							break;
						}
					}
				});
			}
		});

		return view;
	}
}
