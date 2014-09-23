package com.laonstory.airportbeacon.map;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.FrameLayout.LayoutParams;

import com.example.airportbeacon.R;

public class MapActivity extends Activity {

	StoreView storeView;
	
	Button move,setmajor,settarget,newstore;
	TextView textView;
	ImageView targetaim;
	
	boolean target;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_game);
        
        final FrameLayout frameLayout=(FrameLayout) findViewById(R.id.FrameLayout1);
        frameLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				hidebuttons();
			}
		});
        targetaim=(ImageView)findViewById(R.id.imageView1);
        targetaim.setVisibility(View.INVISIBLE);
        frameLayout.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(target)
				{
					switch (event.getAction()) {
	                case MotionEvent.ACTION_DOWN:
	                	int x_cord = (int) event.getRawX();
	                    int y_cord = (int) event.getRawY();
	                    
	                    LayoutParams layoutParams = (LayoutParams) targetaim.getLayoutParams();
	                    layoutParams.leftMargin = x_cord-50;
                        layoutParams.topMargin = y_cord-50;
                        targetaim.setLayoutParams(layoutParams);
                        
	                    selected.targetx=x_cord;
	                    selected.targety=y_cord;
	                       break;
	                case MotionEvent.ACTION_UP:
	                	target=false;
	               	default:
	                       break;
					}
				}
				return false;
			}
		});
        
        newstore=(Button)findViewById(R.id.button1);
        newstore.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EditText editText=(EditText)findViewById(R.id.editText1);
				if(editText.getText().toString()!="")
				{
					storeView = new StoreView(MapActivity.this,Integer.parseInt(editText.getText().toString()));
			        frameLayout.addView(storeView, 0);
			        hidebuttons();
			        showbuttons(storeView);
				}
			}
		});
        move=(Button)findViewById(R.id.button4);
        move.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(selected!=null)
					selected.moving=true;
			}
		});
        setmajor=(Button)findViewById(R.id.button2);
        setmajor.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EditText editText=(EditText)findViewById(R.id.editText1);
				if(editText.getText().toString()!="")
				{
					StoreView temp=selected;
					selected.major=Integer.parseInt(editText.getText().toString());
					hidebuttons();
					showbuttons(temp);
				}
			}
		});
        settarget=(Button)findViewById(R.id.button3);
        settarget.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				target=true;
			}
		});
        textView=(TextView)findViewById(R.id.textView1);
        
        move.setVisibility(View.INVISIBLE);
        setmajor.setVisibility(View.INVISIBLE);
        settarget.setVisibility(View.INVISIBLE);
    }
    
    StoreView selected;
    public void showbuttons(StoreView storeView) {
    	move.setVisibility(View.VISIBLE);
        setmajor.setVisibility(View.VISIBLE);
        settarget.setVisibility(View.VISIBLE);
        selected=storeView;
        selected.setBackground(getResources().getDrawable(R.drawable.storeselected));
        textView.setText("Major: "+selected.major);
        targetaim.setVisibility(View.VISIBLE);
        
        LayoutParams layoutParams = (LayoutParams) targetaim.getLayoutParams();
        layoutParams.leftMargin = selected.targetx-50;
        layoutParams.topMargin = selected.targety-50;
        targetaim.setLayoutParams(layoutParams);
	}
    
    public void hidebuttons() {
    	move.setVisibility(View.INVISIBLE);
        setmajor.setVisibility(View.INVISIBLE);
        settarget.setVisibility(View.INVISIBLE);
        if(selected!=null)selected.setBackground(getResources().getDrawable(R.drawable.store));
        selected=null;
        textView.setText("");
        targetaim.setVisibility(View.INVISIBLE);
	} 
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.game, menu);
		return true;
	}

}
