package com.guo.androidlib.bitmap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;

import com.guo.androidlib.bitmap.DiskLruCache.Snapshot;
import com.guo.androidlib.util.GHLog;

public class HomeBitmapUtil {
	private static HomeBitmapUtil homeBitmapUtil;
	private DiskLruCache diskLruCache;

	private HomeBitmapUtil() {
	}

	public static HomeBitmapUtil getInstance() {
		if (homeBitmapUtil == null) {
			homeBitmapUtil = new HomeBitmapUtil();
		}
		return homeBitmapUtil;
	}

	public synchronized DiskLruCache getDiskLruCache() {
		if (diskLruCache == null) {
			try {
				GHLog.gHLog("getDiskLruCache");
				HomeConfig config = HomeConfig.getInstance();
				diskLruCache = DiskLruCache.open(config.getFileCachePath(),
						config.getVersion(), 1, config.getCacheSize());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				GHLog.gHLog(GHLog.LOG_ERROR, e.getMessage() + "");
			}
		}
		return diskLruCache;
	}

	private int bitmapCompressRatio(int originWidth, int originHeigh,
			int reqWidth, int reqHeigh) {
		GHLog.gHLog("originWidth:" + originWidth + "originHeigh:" + originHeigh
				+ "reqWidth:" + reqWidth + "reqHeigh:" + reqHeigh);
		int ratio = 1;
		if (reqWidth > 0 && reqHeigh > 0) {
			float widthRatio = originWidth / reqWidth;
			float heighRatio = originHeigh / reqHeigh;
			ratio = (int) (widthRatio > heighRatio ? widthRatio : heighRatio);
		}
		return ratio;
	}

	public Bitmap createBitmap(InputStream is,
			HomeBmpDisplayConfig displayConfig) {
		Options options = new BitmapFactory.Options();
		options.inSampleSize = bitmapCompressRatio(options.outWidth,
				options.outHeight, displayConfig.requestWidth,
				displayConfig.requestHeight);
		options.inJustDecodeBounds = false;
		BitmapFactory.decodeStream(is, null, options);
		return null;
	}

	public Bitmap getBitmapForDiskCache(String fileName,
			HomeBmpDisplayConfig displayConfig) throws IOException {
		Snapshot snapshot = diskLruCache.get(fileName);
		if (snapshot == null) {
			return null;
		}
		InputStream inputStream = snapshot.getInputStream(0);
		Bitmap bitmap = null;
		
		Options options = new BitmapFactory.Options();
		options.inSampleSize = bitmapCompressRatio(options.outWidth, options.outHeight, displayConfig.requestWidth, displayConfig.requestHeight);
		options.inJustDecodeBounds = true;
		if (inputStream != null) {
			bitmap = BitmapFactory.decodeStream(inputStream,null,options);
			inputStream.close();
		}
		snapshot.close();

		snapshot = diskLruCache.get(fileName);
		if(snapshot == null) return null;
		
		options.inJustDecodeBounds = false;
		inputStream = snapshot.getInputStream(0);
		if (inputStream != null) {
			bitmap = BitmapFactory.decodeStream(inputStream,null,options);
			inputStream.close();
		}
		snapshot.close();
		if (bitmap != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitmap.compress(displayConfig.compressFormat,
					displayConfig.compress, baos);
			bitmap = BitmapFactory.decodeByteArray(baos.toByteArray(), 0,
					baos.size());
			baos.close();
		} else {
			GHLog.gHLog("bitmap is null");
		}
		return bitmap;
	}

	public Bitmap createBitmap(File imageFile,
			HomeBmpDisplayConfig bmpDisplayConfig) {
		Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);

		options.inSampleSize = bitmapCompressRatio(options.outWidth,
				options.outHeight, bmpDisplayConfig.requestWidth,
				bmpDisplayConfig.requestHeight);
		GHLog.gHLog("inSampleSize:" + imageFile.length());
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(),
				options);
		ByteArrayOutputStream baos = null;
		baos = new ByteArrayOutputStream();
		if (bitmap != null) {
			bitmap.compress(bmpDisplayConfig.compressFormat,
					bmpDisplayConfig.compress, baos);
		} else {
			GHLog.gHLog("bitmap is null" + imageFile);
		}
		try {
			if (baos != null)
				baos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		bitmap = BitmapFactory.decodeByteArray(baos.toByteArray(), 0,
				baos.size());
		return bitmap;
	}
}
