package com.laonstory.airportbeacon.map;

import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.example.airportbeacon.R;
import com.laonstory.airportbeacon.BeaconService;

public class MapView extends View {

	private static final int INVALID_POINTER_ID = -1;
	
	private float mPosX;
    private float mPosY;

    private float mLastTouchX;
    private float mLastTouchY;
    private float mLastGestureX;
    private float mLastGestureY;
    private int mActivePointerId = INVALID_POINTER_ID;

    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;
    
    Bitmap map;
    Bitmap beacon;
    Bitmap arrow;
    
    
	
    public MapView(Context context) {
          super(context);
          mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
          map = BitmapFactory.decodeResource(getResources(), R.drawable.map);
          beacon = BitmapFactory.decodeResource(getResources(), R.drawable.beacon);
          arrow = BitmapFactory.decodeResource(getResources(), R.drawable.arrow);
    }
    
    private int beacons[]={};
    
    
    private float angle=0;
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();

        canvas.translate(mPosX, mPosY);

        /*
        if (mScaleDetector.isInProgress()) {
            canvas.scale(mScaleFactor, mScaleFactor, mScaleDetector.getFocusX(), mScaleDetector.getFocusY());
        }
        else{
            canvas.scale(mScaleFactor, mScaleFactor, mLastGestureX, mLastGestureY);
        }*/
        canvas.drawBitmap(arrow,0,0,null);
        super.onDraw(canvas);
        canvas.restore();
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // Let the ScaleGestureDetector inspect all events.
        mScaleDetector.onTouchEvent(ev);

        final int action = MotionEventCompat.getActionMasked(ev); 
        
        switch (action) { 
        case MotionEvent.ACTION_DOWN: {
            final int pointerIndex = MotionEventCompat.getActionIndex(ev); 
            final float x = MotionEventCompat.getX(ev, pointerIndex); 
            final float y = MotionEventCompat.getY(ev, pointerIndex); 
                
            // Remember where we started (for dragging)
            mLastTouchX = x;
            mLastTouchY = y;
            // Save the ID of this pointer (for dragging)
            mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
            break;
        }
                
        case MotionEvent.ACTION_MOVE: {
            // Find the index of the active pointer and fetch its position
            final int pointerIndex = 
                    MotionEventCompat.findPointerIndex(ev, mActivePointerId);  
                
            final float x = MotionEventCompat.getX(ev, pointerIndex);
            final float y = MotionEventCompat.getY(ev, pointerIndex);
                
            // Calculate the distance moved
            final float dx = x - mLastTouchX;
            final float dy = y - mLastTouchY;

            mPosX += dx;
            mPosY += dy;

            invalidate();

            // Remember this touch position for the next move event
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
                
            final int pointerIndex = MotionEventCompat.getActionIndex(ev); 
            final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex); 

            if (pointerId == mActivePointerId) {
                // This was our active pointer going up. Choose a new
                // active pointer and adjust accordingly.
                final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                mLastTouchX = MotionEventCompat.getX(ev, newPointerIndex); 
                mLastTouchY = MotionEventCompat.getY(ev, newPointerIndex); 
                mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
            }
            break;
        }
        }       
        return true;
    }
    
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));

            invalidate();
            return true;
        }
    }

}
