package com.steelmantools.smartear;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.steelmantools.smartear.audio.AudioService;
import com.steelmantools.smartear.db.DBAdapter;
import com.steelmantools.smartear.db.DBDeviceAdapter;

//public class BluetoothSelectActivity extends Activity implements BluetoothBroadcastReceiver.Callback, BluetoothA2DPRequester.Callback {
public class BluetoothSelectActivity extends FragmentActivity implements BluetoothBroadcastReceiver.Callback, BluetoothA2DPRequester.Callback {
    private static final String TAG = "BluetoothActivity";

    /**
     * This is the name of the device to connect to. You can replace this with the name of
     * your device.
     */
    private static final String HTC_MEDIA = "HTC Car A100 V2.4B";
    private static final String SG3_MEDIA = "RN52-1283";
    private static final String SG3_MEDIAMIC = "RN52-1288";
    String SELECTED_DEVICE;
    List<String> discoveredArrayList = new ArrayList<String>();

    /**
     * Local reference to the device's BluetoothAdapter
     */
    private BluetoothAdapter mAdapter;
    private BluetoothHeadset mBluetoothHeadset;
    BluetoothDevice mBluetoothDevice;
    private static final int REQUEST_CONNECT_DEVICE = 1;
	protected AudioService audioService;
    
    DBAdapter DBAdapter;
    DBDeviceAdapter DBDeviceAdapter;
    
	// Define Service Listener of BluetoothProfile
	private BluetoothProfile.ServiceListener mProfileListener = new BluetoothProfile.ServiceListener() {
			public void onServiceConnected(int profile, BluetoothProfile proxy) {
				if (profile == BluetoothProfile.HEADSET) {
					mBluetoothHeadset = (BluetoothHeadset) proxy;
					List<BluetoothDevice> pairedDevices = mBluetoothHeadset.getConnectedDevices();
				    // If there are paired devices
				    if (pairedDevices.size() > 0) {
				    	//startSCO();
				    	for (BluetoothDevice device : pairedDevices) {
				    		Log.e(TAG, "BT Device :"+device.getName()+ " , BD_ADDR:" + device.getAddress());       //Print out Headset name  
				    		device.getAddress();
				    		//currentDeviceAddr = device.getAddress();
				    		mBluetoothHeadset.isAudioConnected(device);
				    	}
				    } else {
				    	//Toast.makeText(BluetoothSelectActivity.this, "Could not find a connected Headset In BTSELECTACT, please connect a headset", Toast.LENGTH_LONG).show();
				        return;
				    }
				}
			}	
			public void onServiceDisconnected(int profile) {
				if (profile == BluetoothProfile.HEADSET) {
					mBluetoothHeadset = null;
				}
			}
	};

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
		DBAdapter=new DBAdapter(this);
		DBAdapter=DBAdapter.open();
		
		DBDeviceAdapter=new DBDeviceAdapter(this);
		DBDeviceAdapter=DBDeviceAdapter.open();
        
        if(mBluetoothHeadset != null){
        	onBluetoothDisconnected();
        }
        
//        onBluetoothDisconnected();

        //Store a local reference to the BluetoothAdapter
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        
//        //Already connected, skip the rest
//        if (mAdapter.isEnabled()) {
//            onBluetoothConnected();
//            return;
//        }
//
//        //Check if we're allowed to enable Bluetooth. If so, listen for a
//        //successful enabling
//        if (mAdapter.enable()) {
//            BluetoothBroadcastReceiver.register(this, this);
//        } else {
//            Log.e(TAG, "Unable to enable Bluetooth. Is Airplane Mode enabled?");
//        }
        
      //ListPairedDevices();
//      Intent connectIntent = new Intent(BluetoothSelectActivity.this, DeviceListActivity.class);
//      startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
      startFragment();
    }
    
    public void startFragment(){
		FragmentManager fm = BluetoothSelectActivity.this.getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Fragment prev = fm.findFragmentByTag("channelSelection");
		if (prev != null) {
			ft.remove(prev);
		}
		ft.addToBackStack(null);
		ChannelSelectionDialogFragment newFragment = ChannelSelectionDialogFragment.newInstance();
		newFragment.show(ft, "channelSelection");
    }
    
    public void startMain(){
//      Intent connectIntent = new Intent(BluetoothSelectActivity.this, DeviceListActivity.class);
//      startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
    	
    	mAdapter.cancelDiscovery();
        //String mDeviceInfo = ((TextView) mView).getText().toString();
        //String mDeviceAddress = mDeviceInfo.substring(mDeviceInfo.length() - 17);
        //Log.v(TAG, "Device_Address " + mDeviceAddress);

        Bundle mBundle = new Bundle();
        //mBundle.putString("DeviceAddress", mDeviceAddress);
        Intent mBackIntent = new Intent();
        mBackIntent.putExtras(mBundle);
        setResult(Activity.RESULT_OK, mBackIntent);
        finish();
    }
    
    public void startBluetoothTasks(){
        //Already connected, skip the rest
        if (mAdapter.isEnabled()) {
            onBluetoothConnected();
            return;
        }
        //Check if we're allowed to enable Bluetooth. If so, listen for a
        //successful enabling
        if (mAdapter.enable()) {
            BluetoothBroadcastReceiver.register(this, this);
        } else {
            Log.e(TAG, "Unable to enable Bluetooth. Is Airplane Mode enabled?");
        }
    }
    
    public void deviceLimitReached(){
    	Toast.makeText(BluetoothSelectActivity.this, "Cannot pair more than 6 devices, please remove extra device(s) and try again.", Toast.LENGTH_LONG).show();
    }
    
    public void noDevices(){
    	Toast.makeText(BluetoothSelectActivity.this, "No paired devices found, please pair devices and try again.", Toast.LENGTH_LONG).show();
    }
    
    public void onActivityResult(int mRequestCode, int mResultCode, Intent mDataIntent) 
    {
        super.onActivityResult(mRequestCode, mResultCode, mDataIntent);

        switch (mRequestCode) 
        {
            case REQUEST_CONNECT_DEVICE:
                if (mResultCode == Activity.RESULT_OK) 
                {
                    Bundle mExtra = mDataIntent.getExtras();
                    String mDeviceAddress = mExtra.getString("DeviceAddress");
                    //SELECTED_DEVICE = mExtra.getString("DeviceAddress");
                    Log.v(TAG, "Coming incoming address " + mDeviceAddress);
                    Log.v(TAG, "Coming incoming address SELECTED_DEVICE " + SELECTED_DEVICE);
                }
                break;
        }
//        //Already connected, skip the rest
//        if (mAdapter.isEnabled()) {
//            onBluetoothConnected();
//            return;
//        }
//
//        //Check if we're allowed to enable Bluetooth. If so, listen for a
//        //successful enabling
//        if (mAdapter.enable()) {
//            BluetoothBroadcastReceiver.register(this, this);
//        } else {
//            Log.e(TAG, "Unable to enable Bluetooth. Is Airplane Mode enabled?");
//        }
    }
    @Override
    protected void onDestroy(){
    	super.onDestroy();
    	//onBluetoothDisconnected();
    }

    @Override
    public void onBluetoothError () {
        Log.e(TAG, "There was an error enabling the Bluetooth Adapter.");
    }

    @Override
    public void onBluetoothConnected () {
        new BluetoothA2DPRequester(this).request(this, mAdapter);
    }
    
    @Override
    public void onBluetoothDisconnected () {
        //new BluetoothA2DPRequester(this).request(this, mAdapter);
    	new BluetoothA2DPRequester(this).requestUnregister(this, mBluetoothHeadset, mAdapter);
    	//safeUnregisterReceiver
    }
    
    public void getDeviceToConnect(String channelSelected){
    	SELECTED_DEVICE = DBAdapter.getSingleEntryChannelSwitch(channelSelected);
    	Log.e(TAG, "channelSelected: " + channelSelected);
    }
    
    public void isDeviceInRange(String deviceChannel){
    	String deviceChannelString = DBAdapter.getSingleEntryChannelSwitch(deviceChannel);
    	String checkAddr = DBDeviceAdapter.getSingleEntryAddrCheck(deviceChannelString);
    	Log.e(TAG, "channelSelected isDeviceInRange: " + deviceChannel);
        String notExist = "NOT EXIST";
        
        if (checkAddr.equals(notExist)){
		      Intent connectedIntent = new Intent(BluetoothSelectActivity.this, DeviceNotFoundDialogActivity.class);
		      startActivity(connectedIntent);
        } else {
		      Intent connectedIntent = new Intent(BluetoothSelectActivity.this, MainActivity.class);
		      startActivity(connectedIntent);
        }
    }

    @Override
    public void onA2DPProxyReceived (final BluetoothHeadset proxy) {
    	
		int activeChannelFromBtn = GlobalSettings.getActiveChannel().getNumber();
		String activeChannelForAddr = String.valueOf(activeChannelFromBtn);
		
    	mAdapter.startDiscovery();
    	BroadcastReceiver mReceiver = new BroadcastReceiver() {
    		public void onReceive(Context context, Intent intent) {
    		    String action = intent.getAction();
    		    if (BluetoothDevice.ACTION_FOUND.equals(action)) 
    		    {
    		        // Get the BluetoothDevice object from the Intent
    		        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
    		        Log.e(TAG, "discoveredArrayList1.add(device.getAddress()): " + device.getAddress());
                    HashMap<String, String> deviceMap = new HashMap<String, String>();
                    deviceMap.put(device.getName(), device.getAddress());
                    discoveredArrayList.add(device.getAddress());
                    
	                String checkAddr = DBDeviceAdapter.getSingleEntryAddr(device.getAddress());
	                Log.d("mBluetoothAdapter.startDiscovery() device", "checkAddr: "+ checkAddr);
	                String exist = "EXIST";
	                String notExist = "NOT EXIST";
	                
	                if (checkAddr.equals(exist)){
	                	//DBAdapter.updateEntry(addrText, channelPos);
	                	
	                	Log.e(TAG, "mBluetoothAdapter.startDiscovery() checkAddr.equals(EXIST) BluetoothSelectActivity: " + device.getAddress());
	                } else if(checkAddr.equals(notExist)){
		        		DBDeviceAdapter.insertEntry(device.getAddress());
		        		Log.e(TAG, "mBluetoothAdapter.startDiscovery() checkAddr.equals(notExist) BluetoothSelectActivity: " + device.getAddress());
	                }
                    //DBDeviceAdapter.insertEntry(device.getAddress());
    		       Log.e(TAG, "discoveredArrayList.add(device.getAddress()): " + device.getAddress());
    		    }
    		  }
    		};

    		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND); 
    		registerReceiver(mReceiver, filter);
    		
    		String isDeviceInRange = DBDeviceAdapter.getSingleEntryAddr(SELECTED_DEVICE);
    		String existsInDB = "EXIST";
    		if(isDeviceInRange.equals(existsInDB)){
    		    final ProgressDialog dialog = ProgressDialog.show(BluetoothSelectActivity.this, "","Connecting..." , true);
    		    dialog.show();
    		    Handler handler = new Handler();
    		    handler.postDelayed(new Runnable() {
    		        public void run() {
    		        	
    		        	//kk this is the original before the logic that was working
    		        	Method connect = getConnectMethod();
    		        	
    		        	BluetoothDevice device = findBondedDeviceByAddress(mAdapter, SELECTED_DEVICE);
    		        	Log.e(TAG, "SELECTED_DEVICE: " + SELECTED_DEVICE);
    	        
    		        	//If either is null, just return. The errors have already been logged
    		        	if (connect == null || device == null) {
    		        		return;
    		        	}

    		        	try {
    		        		connect.setAccessible(true);
    		        		connect.invoke(proxy, device);
    		        	} catch (InvocationTargetException ex) {
    		        		Log.e(TAG, "Unable to invoke connect(BluetoothDevice) method on proxy. " + ex.toString());
    		        	} catch (IllegalAccessException ex) {
    		        		Log.e(TAG, "Illegal Access! " + ex.toString());
    		        	}
    		        	dialog.dismiss();
    		        	}   
    		        //KK TRY TO SET THIS TO LIKE 10 AND SEE IF WORK ON TABLET BEFORE CIG-KK_032814_0529
    		        //IF NOT SET GLOBALSETTINGS THINGS TO VOLUME LOUD LIEK LEVEL 2-AFTER CIG-KK
    		    	}, 6000); 
    		      Intent connectedIntent = new Intent(BluetoothSelectActivity.this, MainActivity.class);
    		      startActivity(connectedIntent);
    	    	} else {
      		      Intent connectedIntent = new Intent(BluetoothSelectActivity.this, DeviceNotFoundDialogActivity.class);
      		      startActivity(connectedIntent);
    	    	}
    		}

    /**
     * Wrapper around some reflection code to get the hidden 'connect()' method
     * @return the connect(BluetoothDevice) method, or null if it could not be found
     */
    private Method getConnectMethod () {
        try {
//            return BluetoothA2dp.class.getDeclaredMethod("connect", BluetoothDevice.class);
        	return BluetoothHeadset.class.getDeclaredMethod("connect", BluetoothDevice.class);
        } catch (NoSuchMethodException ex) {
            Log.e(TAG, "Unable to find connect(BluetoothDevice) method in BluetoothA2dp proxy.");
            return null;
        }
    }

    /**
     * Search the set of bonded devices in the BluetoothAdapter for one that matches
     * the given name
     * @param adapter the BluetoothAdapter whose bonded devices should be queried
     * @param name the name of the device to search for
     * @return the BluetoothDevice by the given name (if found); null if it was not found
     */
//    private static BluetoothDevice findBondedDeviceByName (BluetoothAdapter adapter, String name) {
//        for (BluetoothDevice device : getBondedDevices(adapter)) {
//            if (name.matches(device.getName())) {
//                Log.v(TAG, String.format("Found device with name %s and address %s.", device.getName(), device.getAddress()));
//                return device;
//            }
//        }
//        Log.w(TAG, String.format("Unable to find device with name %s.", name));
//        return null;
//    }
    private static BluetoothDevice findBondedDeviceByAddress (BluetoothAdapter adapter, String addr) {
        for (BluetoothDevice device : getBondedDevices(adapter)) {
            if (addr.matches(device.getAddress())) {
                Log.v(TAG, String.format("Found device with name %s and address %s.", device.getName(), device.getAddress()));
                return device;
            }
        }
        Log.w(TAG, String.format("Unable to find device with addr %s.", addr));
        return null;
    }

    /**
     * Safety wrapper around BluetoothAdapter#getBondedDevices() that is guaranteed
     * to return a non-null result
     * @param adapter the BluetoothAdapter whose bonded devices should be obtained
     * @return the set of all bonded devices to the adapter; an empty set if there was an error
     */
    private static Set<BluetoothDevice> getBondedDevices (BluetoothAdapter adapter) {
        Set<BluetoothDevice> results = adapter.getBondedDevices();
        if (results == null) {
            results = new HashSet<BluetoothDevice>();
        }
        return results;
    }
    
    private void ListPairedDevices() {
        Set<BluetoothDevice> mPairedDevices = mAdapter.getBondedDevices();
        if (mPairedDevices.size() > 0) 
        {
            for (BluetoothDevice mDevice : mPairedDevices) 
            {
                Log.v(TAG, "PairedDevices: " + mDevice.getName() + " " + mDevice.getAddress());
                //Log.v(TAG, "PairedDevicesType: " + mDevice.getType() + " " + mDevice.getAddress());
                Log.v(TAG, "PairedDevicesClass: " + mDevice.getClass() + " " + mDevice.getAddress());
            }
        }
    }
    
	public void toggleRecordingPause() {
		audioService.togglePauseRecording();
		Log.v(TAG, "BlutoothSelectActivity toggleRecordingPause FIRED");
	}
    
	@Override
	public void onBackPressed() {
		//BluetoothBroadcastReceiver.safeUnregisterReceiver(this, );
		//onBluetoothDisconnected();
		Toast.makeText(BluetoothSelectActivity.this, "Please select a channel or cancel.", Toast.LENGTH_LONG).show();
	      Intent connectedIntent = new Intent(BluetoothSelectActivity.this, MainActivity.class);
	      startActivity(connectedIntent);
	}
}
