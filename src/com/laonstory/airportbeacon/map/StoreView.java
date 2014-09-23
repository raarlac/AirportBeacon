package com.laonstory.airportbeacon.map;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;

import com.example.airportbeacon.R;

public class StoreView extends ImageView {

	int windowwidth;
    int windowheight;
    
    int major;
    int targetx;
    int targety;

	boolean moving=false;
	public StoreView(final MapActivity context, int major) {
		super(context);
		this.major=major;
		setBackground(getResources().getDrawable(R.drawable.store));
		setLayoutParams(new FrameLayout.LayoutParams(100, 100));
		
		setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                  LayoutParams layoutParams = (LayoutParams) getLayoutParams();
                  switch (event.getAction()) {
                  case MotionEvent.ACTION_DOWN:
                	  context.hidebuttons();
                	  	context.showbuttons(StoreView.this);
                         break;
                  case MotionEvent.ACTION_MOVE:
                	  if(moving)
                	  {
                         int x_cord = (int) event.getRawX();
                         int y_cord = (int) event.getRawY();

                         layoutParams.leftMargin = x_cord - 25;
                         layoutParams.topMargin = y_cord - 75;

                         setLayoutParams(layoutParams);
                         break;
                	  }
                  case MotionEvent.ACTION_UP:
                	  moving=false;
                       break;
                  default:
                         break;
                  }
                  return true;
            }
     });
	}


}
