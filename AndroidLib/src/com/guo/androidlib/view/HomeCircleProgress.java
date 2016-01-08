package com.guo.androidlib.view;

import com.guo.androidlib.util.GHLog;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class HomeCircleProgress extends View {

	private Paint arcPaint;
	private int paintStrokeWidth = 4;
	private RectF oval;
	private int progress = 0;
	
	
	private void init(){
		arcPaint = new Paint();
        arcPaint.setAntiAlias(true);
        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setStrokeWidth(paintStrokeWidth);
        arcPaint.setColor(Color.rgb(46, 164, 242));
        
        oval = new RectF();
        oval.left = paintStrokeWidth / 2;
		oval.top = paintStrokeWidth / 2;
	}
	
	public HomeCircleProgress(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public HomeCircleProgress(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public HomeCircleProgress(Context context) {
		super(context);
		init();
	}

	public void setPaint(Paint paint){
		arcPaint = paint;
	}
	
	
	public void setProgress(int progress){
		this.progress = (int) (progress * 3.6f);
		GHLog.gHLog("setProgress");
		postInvalidate();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		oval.bottom = getHeight() - paintStrokeWidth / 2;
		oval.right = getWidth() - paintStrokeWidth / 2;
		GHLog.gHLog("onDraw"+progress);
		canvas.drawArc(oval , 270, progress, false, arcPaint);
	}
}
