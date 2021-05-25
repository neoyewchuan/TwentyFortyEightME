package com.thefatoldman.twentyfortyeightme;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class SwipeListener implements GestureDetector.OnGestureListener {

    private GestureDetector detector;
    private SwipeCallback callback;

    public SwipeListener(Context context, SwipeCallback callback)   {
        this.callback = callback;
        detector = new GestureDetector(context, this);
    }

    public void onTouchEvent(MotionEvent e) {
        detector.onTouchEvent(e);
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float velocityX, float velocityY) {
        if (Math.abs(velocityX) > Math.abs(velocityY))  {
            // Horizontal movement
            if (velocityX > 0)  {
                // moving Right
                callback.onSwipe(SwipeCallback.Direction.RIGHT);
            } else {
                // moving left
                callback.onSwipe(SwipeCallback.Direction.LEFT);
            }
        } else {
            // Vertical movement
            if (velocityY > 0)  {
                // moving down
                callback.onSwipe(SwipeCallback.Direction.DOWN);
            } else {
                // moving up
                callback.onSwipe(SwipeCallback.Direction.UP);
            }
        }
        return false;
    }
}
