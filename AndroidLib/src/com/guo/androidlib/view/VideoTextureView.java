package com.guo.androidlib.view;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;

import com.guo.androidlib.util.GHLog;

/**
 * SDK >= 14
 * @author Home
 *
 */
@SuppressLint("NewApi")
public class VideoTextureView extends TextureView implements
		TextureView.SurfaceTextureListener, OnCompletionListener,
		OnPreparedListener {
	private Surface mSurface;// ²¥·ÅMediaPlayer²ã
	private MediaPlayer mMediaPlayer;
	private MediaState mCurrentMediaPlayerState;// ²¥·Å×´Ì¬

	private void init(Context context) {
		setSurfaceTextureListener(this);
		mMediaPlayer = new MediaPlayer();
	}

	public VideoTextureView(Context context) {
		// TODO Auto-generated constructor stub
		super(context);
		init(context);
	}

	public VideoTextureView(Context context, AttributeSet attrs) {
		// TODO Auto-generated constructor stub
		super(context, attrs);
		init(context);
	}

	public void prepare(Context context, String path) {
		if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
			mMediaPlayer.reset();
			// mMediaPlayer.release();
		}
		setCurrentPlayerState(MediaState.PREPARE);
		mMediaPlayer.setOnCompletionListener(this);
		mMediaPlayer.setOnPreparedListener(this);
		mMediaPlayer.setSurface(mSurface);
		mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		try {
			mMediaPlayer.setDataSource(path);
			mMediaPlayer.prepareAsync();
			mMediaPlayer
					.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {
						@Override
						public void onBufferingUpdate(MediaPlayer mp,
								int percent) {
							// TODO Auto-generated method stub
							mMediaPlayer.start();
						}
					});

		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			GHLog.gHLog(GHLog.LOG_ERROR,
					"IllegalArgumentException:" + e.getMessage());
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			GHLog.gHLog(GHLog.LOG_ERROR, "SecurityException:" + e.getMessage());
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			GHLog.gHLog(GHLog.LOG_ERROR,
					"IllegalStateException:" + e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			GHLog.gHLog(GHLog.LOG_ERROR, "IOException:" + e.getMessage());
		}
	}

	public void prepare(Context context, int id) {
		if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
			mMediaPlayer.reset();
		}
		setCurrentPlayerState(MediaState.PREPARE);
		mMediaPlayer = MediaPlayer.create(context, id);
		mMediaPlayer.setSurface(mSurface);
		// mMediaPlayer.setLooping(true);
		mMediaPlayer.setOnCompletionListener(this);
		mMediaPlayer.setOnPreparedListener(this);
		mMediaPlayer.start();
	}

	// ²¥·Å
	public void play() {
		if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
			mCurrentMediaPlayerState = MediaState.PLAY;
			mMediaPlayer.start();
		}
	}

	// ÔÝÍ£
	public void pause() {
		if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
			mCurrentMediaPlayerState = MediaState.PAUSE;
			mMediaPlayer.pause();
		}
	}

	public void reset() {
		mCurrentMediaPlayerState = MediaState.RESET;
		mMediaPlayer.reset();
	}

	// ²¥·Å×´Ì¬ÉèÖÃ
	public void setCurrentPlayerState(MediaState mediaState) {
		mCurrentMediaPlayerState = mediaState;
	}

	public MediaState getCurrentPlayerState() {
		return mCurrentMediaPlayerState;
	}

	@Override
	public void onSurfaceTextureAvailable(SurfaceTexture surface, int width,
			int height) {
		// TODO Auto-generated method stub
		mSurface = new Surface(surface);
		GHLog.gHLog("onSurfaceTextureAvailable");
	}

	@Override
	public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width,
			int height) {
		// TODO Auto-generated method stub
		GHLog.gHLog("onSurfaceTextureSizeChanged");
	}

	@Override
	public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
		// TODO Auto-generated method stub
		GHLog.gHLog("onSurfaceTextureDestroyed");
		return false;
	}

	@Override
	public void onSurfaceTextureUpdated(SurfaceTexture surface) {
		// TODO Auto-generated method stub
		GHLog.gHLog("onSurfaceTextureUpdated");
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub
		GHLog.gHLog("onCompletion");
		mCurrentMediaPlayerState = MediaState.COMPLETE;
		mp.release();
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		// TODO Auto-generated method stub
		GHLog.gHLog("onPrepared");
		// play();
	}

	public static enum MediaState {
		RESET, PREPARE, COMPLETE, PLAY, PAUSE;
	}
}
