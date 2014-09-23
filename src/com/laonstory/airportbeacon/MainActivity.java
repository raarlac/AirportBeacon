package com.laonstory.airportbeacon;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.example.airportbeacon.R;

public class MainActivity extends Activity {

	
	Button button;
	EditText iptext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		button= (Button) findViewById(R.id.button1);
		iptext= (EditText) findViewById(R.id.editText1);		
		
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(iptext.getText()!=null)
				{
					Intent serviceIntent = new Intent(MainActivity.this, BeaconService.class);
					serviceIntent.putExtra("ip", iptext.getText().toString());
					startService(serviceIntent);
					iptext.setEnabled(false);
					button.setEnabled(false);
					button.setText("Connecting...");
				}
			}
		});
		
		//setBluetooth(true);
	}
	
	 private BroadcastReceiver mReceiver;
		@Override
		protected void onResume() {
	        mReceiver = new BroadcastReceiver() {
	            @Override
	            public void onReceive(Context context, Intent intent) {
	            	if(intent.hasExtra("connected"))
	            	{
	            		button.setText("Connected!");
	            		
	            	}
	            	if(intent.hasExtra("fail"))
	            	{
	            		button.setText("Failed! Try again");
	            		iptext.setEnabled(true);
						button.setEnabled(true);
	            		
	            	}
	            }
	        };
	        IntentFilter intentFilter = new IntentFilter("Beacon");
	        this.registerReceiver(mReceiver, intentFilter);
			super.onResume();
		}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public static boolean setBluetooth(boolean enable) {
	    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	    boolean isEnabled = bluetoothAdapter.isEnabled();
	    if (enable && !isEnabled) {
	        return bluetoothAdapter.enable(); 
	    }
	    else if(!enable && isEnabled) {
	        return bluetoothAdapter.disable();
	    }
	    return true;
	}

	

}
