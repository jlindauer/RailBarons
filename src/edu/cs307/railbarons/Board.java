package edu.cs307.railbarons;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.WindowManager;

public class Board extends SurfaceView implements Callback {
	private Paint _boardPaint;
	private GameThread _thread;
	private Context context;
//	private ScaleGestureDetector mScaleDetector;
	private GestureDetector mGestureDetector;
	private float mScaleFactor = 1.f;
//	private Matrix matrix;
	private int height;
	private int width;
	
	//for touch events
	private static final int INVALID_POINTER_ID = -1;
	private float mPosX;
	private float mPosY;
	private float mLastTouchX;
	private float mLastTouchY;
	//private float xFocus = 0.0f;
	//private float yFocus = 0.0f;
	private int mActivePointerId = INVALID_POINTER_ID;
	private float boundX;
	private float boundY;

	public Board(Context con, AttributeSet attrs) {
		super(con, attrs);
		context = con;
		
		getHolder().addCallback(this);
		_thread = new GameThread(this, this.getHolder());
		_boardPaint = new Paint();
		_boardPaint.setColor(Color.WHITE);
		_boardPaint.setTextSize(30);
//		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
		mLastTouchX = mLastTouchY = 0;
		Renderer.initialize(context);
		WindowManager wm = (WindowManager) con.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		mGestureDetector = new GestureDetector(context, new MyDoubleTapListener());
		Point size = new Point();
		display.getSize(size);
		width = size.x;
		height = size.y;
		mPosX = -(3195 - width);
		mPosY = -300; // that's the location of boston
		mLastTouchX = -mPosX;
		mLastTouchY = -mPosY;
		boundX = -mPosX;
		boundY = -mPosY; 
		
	}
	
	public float bWidth() {
		return width;
	}
	
	public float bHeight() {
		return height;
	}
	
	public PointF currLocation() {
		return new PointF(boundX,boundY);
	}
	
	//API 12 and down does now support display.getSize(Point)
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2) @SuppressLint("DrawAllocation") 
	@Override
	public void onDraw(Canvas c) {
		long time = System.currentTimeMillis();
		_boardPaint.setColor(Color.BLACK);
		c.drawPaint(_boardPaint);
		//transposes the canvas matrix by scale and focuses zoom in 
		//with xFocus and yFocus
//		c.scale(mScaleFactor, mScaleFactor, xFocus, yFocus);
		
		
		if (boundX < 0) {
			boundX = 0;
			mPosX = 0;
		}
		
		if (boundY < 0) {
			boundY = 0;
			mPosY = 0;
		}
		int tempX = 3195 - width;
		if (boundX >= tempX) {
			boundX = tempX;
			mPosX = -tempX;
		}
		
		int tempY = 2048 - height;
		if (boundY >= tempY) {
			boundY = tempY;
			mPosY = -tempY;
		}
		
		c.save();
		c.translate(mPosX, mPosY);
		
		Renderer.drawLayers(c, _boardPaint, this);
		
		time = System.currentTimeMillis() - time;
	}
	
	
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		//Thread.currentThread();
		GameThread.interrupted();
		if(_thread.getState() == Thread.State.TERMINATED)
		{
			_thread = new GameThread(this, this.getHolder());
			_thread.setRunning(true);
			_thread.start();
		}
		if (_thread.getState() == Thread.State.NEW)
		{
			_thread.setRunning(true);
			_thread.start();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		_thread.setRunning(false);
		Thread.currentThread().interrupt();
	}

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // Let the ScaleGestureDetector inspect all events.
//        mScaleDetector.onTouchEvent(ev);
    	mGestureDetector.onTouchEvent(ev);

        final int action = ev.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
	        case MotionEvent.ACTION_DOWN: {
	            final float x = ev.getX();
	            final float y = ev.getY();     
	            mLastTouchX = x;
	            mLastTouchY = y;
	            mActivePointerId = ev.getPointerId(0);
	            break;
	        }
	
	        case MotionEvent.ACTION_MOVE: {
	            final int pointerIndex = ev.findPointerIndex(mActivePointerId);
	            final float x = ev.getX(pointerIndex);
	            final float y = ev.getY(pointerIndex);
	
	            // Only move if the ScaleGestureDetector isn't processing a gesture.
//	            if (!mScaleDetector.isInProgress()) {
	            final float dx = x - mLastTouchX;
	            final float dy = y - mLastTouchY;
	            boundX += (mLastTouchX -x);
	            boundY += (mLastTouchY - y);
	            mPosX += (dx/mScaleFactor); 
	            mPosY += (dy/mScaleFactor);
	
	            invalidate();
//	            }
	            mLastTouchX = x;
	            mLastTouchY = y;
	
	            break;
	        }
	
	        case MotionEvent.ACTION_UP: {
	            mActivePointerId = INVALID_POINTER_ID;
	            break;
	        }
	
	        case MotionEvent.ACTION_CANCEL: {
	            mActivePointerId = INVALID_POINTER_ID;
	            break;
	        }
	
	        case MotionEvent.ACTION_POINTER_UP: {
	            final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) 
	                    >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
	            final int pointerId = ev.getPointerId(pointerIndex);
	            if (pointerId == mActivePointerId) {
	                // This was our active pointer going up. Choose a new
	                // active pointer and adjust accordingly.
	                final int newPointerIndex = pointerIndex == 0 ? 1 : 0;              
	                mLastTouchX = ev.getX(newPointerIndex);
	                mLastTouchY = ev.getY(newPointerIndex);
	                mActivePointerId = ev.getPointerId(newPointerIndex);
	            }
	            break;
	        }
        }
        return true;
    }
    
    private class MyDoubleTapListener implements OnDoubleTapListener, android.view.GestureDetector.OnGestureListener {
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			return false;
		}

		@Override
		public boolean onDoubleTapEvent(MotionEvent e) {
			return false;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			float pts[] = new float[2];
			
			pts[0] = e.getX() - mPosX;
			pts[1] = e.getY() - mPosY;
			
			Point tap = new Point((int)pts[0], (int)pts[1]);
			
			Renderer.eventHandler(tap);
			
 			return false;
		}

		@Override
		public boolean onDown(MotionEvent e) {
			return false;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {			
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {
			
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			return false;
		}
    	
    }

//    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
//        @Override
//        public boolean onScale(ScaleGestureDetector detector) {
//            mScaleFactor *= detector.getScaleFactor();
//
//            // Don't let the object get too small or too large.
//            mScaleFactor = Math.max(1f, Math.min(mScaleFactor, 5.0f)); // don't touch this
//            xFocus = detector.getFocusX();
//            yFocus = detector.getFocusY();
//
//            invalidate();
//            return true;
//        }
//    }
//    
//    public Canvas scaleWithOffset(Canvas c) {
//    	float offset = .75f;
//    	float withOffset = mScaleFactor - offset;
//    	c.scale(withOffset, withOffset, xFocus, yFocus);
//    	return c;
//    }
}
