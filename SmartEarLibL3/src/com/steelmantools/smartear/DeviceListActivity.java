package com.steelmantools.smartear;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class DeviceListActivity extends Activity 
{
    protected static final String TAG = "TAG";
    private BluetoothAdapter mBluetoothAdapter;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
    private ArrayAdapter<String> mDeviceChannelsArrayAdapter;
    //public Button btChannelAssignButton;
    public Spinner btChannelAssignSpinner;
    //ArrayList channelsList;

    @Override
    protected void onCreate(Bundle mSavedInstanceState) 
    {
        super.onCreate(mSavedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.device_list);

        setResult(Activity.RESULT_CANCELED);
        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);

        ListView mPairedListView = (ListView) findViewById(R.id.paired_devices);
        mPairedListView.setAdapter(mPairedDevicesArrayAdapter);
        mPairedListView.setOnItemClickListener(mDeviceClickListener);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        final Set<BluetoothDevice> mPairedDevices = mBluetoothAdapter.getBondedDevices();

        if (mPairedDevices.size() > 0) 
        {
            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            for (BluetoothDevice mDevice : mPairedDevices) 
            {
                mPairedDevicesArrayAdapter.add(mDevice.getName() + "\n" + mDevice.getAddress());
            }
        } 
        else 
        {
            String mNoDevices = getResources().getText(R.string.none_paired).toString();
            mPairedDevicesArrayAdapter.add(mNoDevices);
        }
    }

    @Override
    protected void onDestroy() 
    {
        super.onDestroy();
        if (mBluetoothAdapter != null) 
        {
            mBluetoothAdapter.cancelDiscovery();
        }
    }

    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() 
    {
        public void onItemClick(AdapterView<?> mAdapterView, View mView, int mPosition, long mLong) 
        {
        	final Set<BluetoothDevice> mPairedDevices = mBluetoothAdapter.getBondedDevices();
        	AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                    DeviceListActivity.this);
            builderSingle.setIcon(R.drawable.ic_launcher);
            builderSingle.setTitle("Select Channel For Device");
            //mDeviceChannelsArrayAdapter
            final ArrayAdapter<String> mDeviceChannelsArrayAdapter = new ArrayAdapter<String>(
            		DeviceListActivity.this,
                    android.R.layout.select_dialog_singlechoice);
            if (mPairedDevices.size() > 0) 
            {
            	String sizeOne = "1";
            	String sizeTwo = "2";
            	String sizeThree = "3";
            	String sizeFour = "4";
            	String sizeFive = "5";
            	String sizeSix = "6";
            	if(mPairedDevices.size() == 1){
            		mDeviceChannelsArrayAdapter.add("CH " + sizeOne);
            	} else if(mPairedDevices.size() == 2){
            		mDeviceChannelsArrayAdapter.add("CH " + sizeOne);
            		mDeviceChannelsArrayAdapter.add("CH " + sizeTwo);
            	} else if(mPairedDevices.size() == 3){
            		mDeviceChannelsArrayAdapter.add("CH " + sizeOne);
            		mDeviceChannelsArrayAdapter.add("CH " + sizeTwo);
            		mDeviceChannelsArrayAdapter.add("CH " + sizeThree);
            	} else if(mPairedDevices.size() == 4){
            		mDeviceChannelsArrayAdapter.add("CH " + sizeOne);
            		mDeviceChannelsArrayAdapter.add("CH " + sizeTwo);
            		mDeviceChannelsArrayAdapter.add("CH " + sizeThree);
            		mDeviceChannelsArrayAdapter.add("CH " + sizeFour);
            	} else if(mPairedDevices.size() == 5){
            		mDeviceChannelsArrayAdapter.add("CH " + sizeOne);
            		mDeviceChannelsArrayAdapter.add("CH " + sizeTwo);
            		mDeviceChannelsArrayAdapter.add("CH " + sizeThree);
            		mDeviceChannelsArrayAdapter.add("CH " + sizeFour);
            		mDeviceChannelsArrayAdapter.add("CH " + sizeFive);
            	} else if(mPairedDevices.size() == 6){
            		mDeviceChannelsArrayAdapter.add("CH " + sizeOne);
            		mDeviceChannelsArrayAdapter.add("CH " + sizeTwo);
            		mDeviceChannelsArrayAdapter.add("CH " + sizeThree);
            		mDeviceChannelsArrayAdapter.add("CH " + sizeFour);
            		mDeviceChannelsArrayAdapter.add("CH " + sizeFive);
            		mDeviceChannelsArrayAdapter.add("CH " + sizeSix);
            	}
            } 
            builderSingle.setNegativeButton("cancel",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            builderSingle.setAdapter(mDeviceChannelsArrayAdapter,
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String strName = mDeviceChannelsArrayAdapter.getItem(which);
                            AlertDialog.Builder builderInner = new AlertDialog.Builder(
                            		DeviceListActivity.this);
                            builderInner.setMessage(strName);
                            builderInner.setTitle("Your Selected Item is");
                            builderInner.setPositiveButton("Ok",
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            builderInner.show();
                        }
                    });
            builderSingle.show();
            
            mBluetoothAdapter.cancelDiscovery();
            String mDeviceInfo = ((TextView) mView).getText().toString();
            String mDeviceAddress = mDeviceInfo.substring(mDeviceInfo.length() - 17);
            Log.v(TAG, "Device_Address " + mDeviceAddress);

            Bundle mBundle = new Bundle();
            mBundle.putString("DeviceAddress", mDeviceAddress);
            Intent mBackIntent = new Intent();
            mBackIntent.putExtras(mBundle);
            setResult(Activity.RESULT_OK, mBackIntent);
            finish();
        }
    };

}