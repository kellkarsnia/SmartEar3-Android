package com.steelmantools.smartear;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class BluetoothSelectSettingActivity extends Activity {
    private static final String TAG = "BluetoothSelectSettingActivity";

    String SELECTED_DEVICE;

    /**
     * Local reference to the device's BluetoothAdapter
     */
    private BluetoothAdapter mAdapter;
    BluetoothDevice mBluetoothDevice;
    private static final int REQUEST_CONNECT_DEVICE = 1;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Store a local reference to the BluetoothAdapter
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        
      ListPairedDevices();
      Intent connectIntent = new Intent(BluetoothSelectSettingActivity.this, DeviceListSettingActivity.class);
      startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
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
                    SELECTED_DEVICE = mExtra.getString("DeviceAddress");
                    Log.v(TAG, "Coming incoming address " + mDeviceAddress);
                    Log.v(TAG, "Coming incoming address SELECTED_DEVICE " + SELECTED_DEVICE);
                }
                break;
        }
    }
    @Override
    protected void onDestroy(){
    	super.onDestroy();
    }
    @Override
    protected void onResume(){
    	super.onDestroy();
    	ListPairedDevices();
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
    
//	@Override
//	public void onBackPressed() {
//		//BluetoothBroadcastReceiver.safeUnregisterReceiver(this, );
//		//onBluetoothDisconnected();
//	}
}
