package com.laonstory.airportbeacon;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings.Secure;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.wizturn.sdk.WizTurnDelegate;
import com.wizturn.sdk.WizTurnManager;
import com.wizturn.sdk.WizTurnProximityState;
import com.wizturn.sdk.baseclass.IWizTurnController;
import com.wizturn.sdk.entity.WizTurnBeacons;

@SuppressLint("UseSparseArrays")
/*
 * Beacon service class
 * Handle beacon messages
 */
public class BeaconService extends Service 
{
	/*
	//Estimote's API Beacon manager
	private BeaconManager beaconManager;
	//Estimote's API list of beacons
	private ArrayList<Region> regionlist;
	
	private NotificationManager notificationManager;*/
	
	boolean connecting=false;
	ArrayList<Integer> done;
	Stack<Integer> toConnect;
	
	
	int delayhour;
	int delayminute;
	
	SharedPreferences settings;
	SharedPreferences.Editor editor;
	
	LocalBroadcastManager broadcaster;
	
	public BeaconService() {
	}
	
	private Socket socket;
	int SERVERPORT = 4187;
	String SERVER_IP;
	
	public WizTurnManager _wizturnMgr;
	private WizTurnDelegate _wtDelegate = new WizTurnDelegate() {

		int lastminor=-1;
		@Override
		public void onGetDeviceList(IWizTurnController arg0,
				List<WizTurnBeacons> beaconlist) {
			if(!beaconlist.isEmpty()&&socket!=null)
			{
				if(beaconlist.get(0).getMajor()==8300&&beaconlist.get(0).getMinor()!=lastminor)
				{
					lastminor=beaconlist.get(0).getMinor();
					int major=beaconlist.get(0).getMajor();
					try {
						PrintWriter out;
						out = new PrintWriter(new BufferedWriter(
						new OutputStreamWriter(socket.getOutputStream())),true);
						out.println(major);
						out.println(lastminor);
						out.println(Secure.getString(getContentResolver(),
                                Secure.ANDROID_ID) );
						Log.i("Minor sent", ""+lastminor);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
		}

		@Override
		public void onGetProximity(IWizTurnController arg0,
				WizTurnProximityState arg1) {
			
		}

		@Override
		public void onGetRSSI(IWizTurnController arg0, List<String> arg1,
				List<Integer> arg2) {
			
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();
		/*
		beaconManager = new BeaconManager(this);
		regionlist = new ArrayList<Region>();
		regionlist.add(new Region("Main", null, null, null));
		beaconManager.setRangingListener(new RangingListener() {
			int lastminor=-1;
			@Override
			public void onBeaconsDiscovered(Region arg0, List<Beacon> beaconlist) {
			if(!beaconlist.isEmpty()&&socket!=null)
			{
				if(beaconlist.get(0).getMajor()==8300&&beaconlist.get(0).getMinor()!=lastminor)
				{
					lastminor=beaconlist.get(0).getMinor();
					int major=beaconlist.get(0).getMajor();
					try {
						PrintWriter out;
						out = new PrintWriter(new BufferedWriter(
						new OutputStreamWriter(socket.getOutputStream())),true);
						out.println(major);
						out.println(lastminor);
						out.println(Secure.getString(getContentResolver(),
                                Secure.ANDROID_ID) );
						Log.i("Minor sent", ""+lastminor);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
		}
	});*/
		_wizturnMgr = WizTurnManager.sharedInstance(this);
		// Check if device supports BLE.
		if (!_wizturnMgr.hasBluetooth()) {
			Toast.makeText(this, "Device does not have Bluetooth Low Energy", Toast.LENGTH_LONG).show();
			return;
		}
		// If BLE is not enabled, let user enable it.
		if (!_wizturnMgr.isBluetoothEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivity(enableBtIntent);
		} else {
			//Wizturn Scan Start
			_wizturnMgr.setInitController();
			_wizturnMgr.setWizTurnDelegate(_wtDelegate);
			_wizturnMgr.startController();
		}
		/*
		//start beacon manager
		beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
		      @Override
		      public void onServiceReady() {
		        try {
		        	for (Region region : regionlist) {
					beaconManager.startRanging(region);
		        	}
				} catch (RemoteException e) {
					e.printStackTrace();
				}
		      }
		    });
		*/
		broadcaster=LocalBroadcastManager.getInstance(this);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		Bundle bundle = intent.getExtras();
		SERVER_IP=bundle.getString("ip");
		
		new Thread(new ClientThread()).start();
		return START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (_wizturnMgr.isStarted()) {
			// WizTurnMgr Destroy
			_wizturnMgr.destroy();
		}
	}
	
	class ClientThread implements Runnable {
		        @Override
		        public void run() {
		            try {
		                InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
		                socket = new Socket(serverAddr, SERVERPORT);
		                Intent i = new Intent("Beacon").putExtra("connected", "connected");
		                sendBroadcast(i);
		                Log.i("message","ok!");
		            } catch (UnknownHostException e1) {
		                e1.printStackTrace();
		                Intent i = new Intent("Beacon").putExtra("fail", "fail");
		                sendBroadcast(i);
		                Log.i("message","fail!");
		                BeaconService.this.stopSelf();
		            } catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Intent i = new Intent("Beacon").putExtra("fail", "fail");
		                sendBroadcast(i);
		                Log.i("message","fail!");
		                BeaconService.this.stopSelf();
					}
		        }
		    }


}

