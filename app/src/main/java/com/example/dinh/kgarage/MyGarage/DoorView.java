package com.example.dinh.kgarage.MyGarage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.dinh.kgarage.R;
import com.example.dinh.kgarage.MyGarage.SimpleGestureFilter;

public class DoorView extends View{



	public DoorView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mContext = context;
	
	}
	
	public DoorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	
	}
	
	public DoorView(Context context) {
		super(context);
		mContext = context;
	}
	
	
	
//	private float mWidth = 320;
//	private float mHeight = 320;
	private Context mContext;
	private float mWidthScreen = 320;
	private float mHeightScreen = 480;
	private float mDistanceMove = 320;
	private float mPercent = 100;
	
	private boolean isOpen = false;
//	private float mScale = 1;
//	private RectF mRect1;
	private Paint mPaint1;
	
	Bitmap mDoor;
	Bitmap alien;
//	public boolean isBusy = false;
	
	public void init(){
		
//		mSimpleGestureFilter;
		mTimeToSleep = (int)getTimeToSleep(10);
		mSimpleGestureFilter = new SimpleGestureFilter(mContext);
		mSimpleGestureFilter.setOnListener(new SimpleGestureFilter.SimpleGestureListener() {
			
			@Override
			public void onSwipe(int direction) {
				switch (direction) {

				case SimpleGestureFilter.SWIPE_RIGHT:
//					str = "Swipe Right";
					break;
				case SimpleGestureFilter.SWIPE_LEFT:
//					str = "Swipe Left";
					break;
				case SimpleGestureFilter.SWIPE_DOWN:{
//					str = "Swipe Down";
					close();
					break;
				}case SimpleGestureFilter.SWIPE_UP:{
//					str = "Swipe Up";
					open();
					break;
				}
				}
			}
			
			@Override
			public void onDoubleTap() {
				
				
			}
		});
//		mRect1 = new RectF(0, 0, mWidth, mHeight);
		mPaint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint1.setColor(Color.CYAN);
		
//		mScale = mWidthScreen/mWidth;
		if(alien == null){
			Resources res = getResources();
			alien = LruCacheListImage.decodeSampledBitmapFromResource(res, R.drawable.garage,(int)mWidthScreen	,(int)mWidthScreen);
		}
		
		if(mDoor == null){
			Resources res = getResources();
			mDoor = LruCacheListImage.decodeSampledBitmapFromResource(res, R.drawable.door,(int)mWidthScreen	,(int)mWidthScreen);
		}
		
		
	}
	
	public void setGarage(Bitmap b){
	
		Matrix m = new Matrix();
		m.setRectToRect(new RectF(0, 0, b.getWidth(), b.getHeight()), new RectF(0, 0, mWidthScreen, mWidthScreen), Matrix.ScaleToFit.CENTER);
		alien  = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);;
		postInvalidate();
	}
	
	
	
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		mWidthScreen = MeasureSpec.getSize(widthMeasureSpec);
		mHeightScreen = MeasureSpec.getSize(heightMeasureSpec);
		
		if(mWidthScreen > mHeightScreen){
			mWidthScreen = mHeightScreen;
		}
		
		init();
		
	    this.setMeasuredDimension((int)mWidthScreen,(int) mWidthScreen);
	    
	};
	
	@Override
	public void draw(Canvas canvas) {
		
		canvas.save();
		if(alien != null){
			canvas.drawBitmap(alien, 0, 0, mPaint1);
		}
		
		if(mDoor != null){
			canvas.drawBitmap(mDoor, 0,- mWidthScreen + mWidthScreen*mPercent/100 , mPaint1);
		}
		canvas.restore();
		super.draw(canvas);
	}



	
	
	public void setOpen(boolean open){
		
		if(open){
			mDistanceMove =10;
			isOpen = true;
		}else{
			mDistanceMove = 320;
			isOpen = false;
			
		}
//        int d = (int)mDistanceMove;
		 mPercent =  mDistanceMove*100/320;
		postInvalidate();
	}
	
	public void open(){
//		if(isBusy){
//			return;
//		}
//		if(isOpen){
//			return;
//		}
		
		isOpen = true;
		if(mOpenDoor != null){
			mOpenDoor.cancel();
			mOpenDoor = null;
		}
		mOpenDoor = new OpenDoor(true);
		mOpenDoor.start();
		
		if(mDoorViewListener != null){
			mDoorViewListener.DoorStarOpen();
		}
	}


    public void stop(){
        if(mOpenDoor != null){
            mOpenDoor.cancel();
            mOpenDoor = null;
        }
    }

	public void close(){
//		if(isBusy){
//			return;
//		}
//
//		if(!isOpen){
//			return;
//		}
		
		isOpen = false;
		if(mOpenDoor != null){
			mOpenDoor.cancel();
			mOpenDoor = null;
		}
		
		mOpenDoor = new OpenDoor(false);
		mOpenDoor.start();



		if(mDoorViewListener != null){
			mDoorViewListener.DoorStarClose();
		}
	}
	
	OpenDoor mOpenDoor = null;
	class OpenDoor extends Thread{
		
		private boolean mOpen;
		private boolean isExit;
		
		public OpenDoor(boolean status) {
			mOpen = status;
		}
		
		public void cancel(){
			isExit = true;
		}
		
		@Override
		public void run() {
//			if(mOpen){
//				mDistanceMove = 320;
//			}else{
//				mDistanceMove = 0;
//			}
			isExit = false;
//			isBusy = true;
			while(!isExit){
				if(mOpen){
					mDistanceMove -= 1;
					if(mDistanceMove < 10){
						break;
					}
				}else{
					mDistanceMove += 1;
					if(mDistanceMove > 320){
						break;
					}
				}
				 mPercent = mDistanceMove*100/320;
				 
//				mRect1 = new RectF(0, 0, mWidth, mDistanceMove);
				postInvalidate();
				try {
					sleep(mTimeToSleep);
				} catch (InterruptedException e) {
					
				}
			}
//			isBusy = false;
			if(mDoorViewListener != null){
				mDoorViewListener.ActionFinished();
			}
		}
	}

	private int mTimeToSleep = 100;
	private float getTimeToSleep(float second){
		return second*1000/320;
	}


	
	SimpleGestureFilter mSimpleGestureFilter;

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
//		mSimpleGestureFilter.onTouchEvent(ev);
		return true;
	}
	
	DoorViewListener mDoorViewListener;
	public void setDoorViewListener(DoorViewListener listener){
		mDoorViewListener = listener;
	}
	public interface DoorViewListener{
		public void DoorStarOpen();
		public void DoorStarClose();
		public void ActionFinished();
	}
}
