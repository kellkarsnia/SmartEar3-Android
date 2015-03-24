package com.steelmantools.smartear;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.steelmantools.smartear.db.DBAdapter;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

public class ConnectDeviceDialogActivity extends Activity {
	
	public final static String LOG_TAG = ConnectDeviceDialogActivity.class.getSimpleName();
    private BluetoothHeadset mBluetoothHeadset;
    private boolean scoON = false;
    private boolean scoNEW = false;
    private int selected = 0;
    private String[] choices = null;
    private String[] addresses = null;
    private String[] names = null;
    private String deviceAddress = null;
    private int currentActiveDevice;
    private String activeChannelString;
    
    protected boolean recordingBT;
    
    private String currentDeviceAddr;
    String rnOrJS;
    
	   DBAdapter DBAdapter;
	
	final Context context = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connect_device_dialog);
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);
 
			// set title
			alertDialogBuilder.setTitle("No Devices Connected");
 
			// set dialog message
			alertDialogBuilder
				.setMessage("Setup devices or connect a device to continue")
				.setCancelable(false)
				.setPositiveButton("2) Connect Device",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
					    Intent intent;

					    try {
//					        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		     	          	Intent intentSelect = new Intent(ConnectDeviceDialogActivity.this, BluetoothSelectActivity.class);
		     	          	startActivity(intentSelect);
					    } catch (Exception e) {
					        Log.e("Exception Caught", e.toString());
					    }
						// if this button is clicked, close
						// current activity
					    ConnectDeviceDialogActivity.this.finish();
					}
				  })
				.setNegativeButton("1) Set Devices",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						setAddressesOnStart();
	     	          	Intent intent = new Intent(ConnectDeviceDialogActivity.this, BluetoothSetDevicesActivity.class);
	     	          	startActivity(intent);
						dialog.cancel();
					}
				});
 
				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();
 
				// show it
				alertDialog.show();
			
	}
	private BluetoothAdapter mBluetoothAdapter = null;
	
	public void setAddressesOnStart(){
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		DBAdapter=new DBAdapter(this);
		DBAdapter=DBAdapter.open();
		   
    	Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
        Iterator<BluetoothDevice> iter = devices.iterator();
        choices = new String[devices.size()];
        addresses = new String[devices.size()];
        names = new String[devices.size()];
        if(names !=null && names.length>0){
	        for(int i = 0; i<devices.size(); i++){
	        	BluetoothDevice temp = iter.next();
	        	choices[i] = temp.getName();
	        	addresses[i] = temp.getAddress();
	        	names[i] = temp.getName();
	        	
	        	String isRN = "RN";
	        	String isJS = "JS";
	        	
	        	if(choices[i].startsWith(isRN)){
	        		Log.e(LOG_TAG, "choices[i].startsWith(isRN) GraphingActivity");
	        		
	                String checkAddr = DBAdapter.getSingleEntryAddr(addresses[i]);
	                Log.d("row spinner", "checkAddr: "+ checkAddr);
	                String exist = "EXIST";
	                String notExist = "NOT EXIST";
	                
	                if (checkAddr.equals(exist)){
	                	//DBAdapter.updateEntry(addrText, channelPos);
	                } else if(checkAddr.equals(notExist)){
		        		String channelAssign = "0";
		        		DBAdapter.insertEntry(addresses[i], choices[i], channelAssign);
		        		int deviceSize = addresses[i].length();
		        		String deviceSizeString = String.valueOf(deviceSize);
	                }
	                String rowCountFromDB = DBAdapter.getRowCount();
	        		Log.d("rowCountFromDB GRAPHINGACTIVITY", "rowCountFromDB GRAPHINGACTIVITY: "+ rowCountFromDB);
					SharedPreferences pref = getApplicationContext().getSharedPreferences("STEELMANpref", 0);
    		        Editor editor = pref.edit();
    		        editor.putString("DEVICELENGTH", rowCountFromDB);
    		        editor.commit();
	        	} else if(choices[i].startsWith(isJS)){
	        		Log.e(LOG_TAG, "choices[i].startsWith(isJS) GraphingActivity");
	        		
	                String checkAddr = DBAdapter.getSingleEntryAddr(addresses[i]);
	                Log.d("row spinner", "checkAddr: "+ checkAddr);
	                String exist = "EXIST";
	                String notExist = "NOT EXIST";
	                
	                if (checkAddr.equals(exist)){
	                	//DBAdapter.updateEntry(addrText, channelPos);
	                } else if(checkAddr.equals(notExist)){
		        		String channelAssign = "0";
		        		DBAdapter.insertEntry(addresses[i], choices[i], channelAssign);
		        		int deviceSize = addresses[i].length();
		        		String deviceSizeString = String.valueOf(deviceSize);
	                }
	                String rowCountFromDB = DBAdapter.getRowCount();
	        		Log.d("rowCountFromDB GRAPHINGACTIVITY", "rowCountFromDB GRAPHINGACTIVITY: "+ rowCountFromDB);
					SharedPreferences pref = getApplicationContext().getSharedPreferences("STEELMANpref", 0);
    		        Editor editor = pref.edit();
    		        editor.putString("DEVICELENGTH", rowCountFromDB);
    		        editor.commit();
	        	}
	        }
	        if(names !=null && names.length>0){
			      //check for RN or JS in device names-KK
		        for (String s : names){
		        	int i = s.indexOf("RN");
		        	int j = s.indexOf("JS");
		        	if(i >= 0 || j >= 0){
		        		Log.e(LOG_TAG, "RN or JS found in device list");
		        		rnOrJS = "YES";
		        		
		        		if(s.contains("RN")){
			        		List<String> list = new ArrayList<String>();
			        		list.add(s);
		        		} else if(s.contains("JS")){
			        		List<String> list = new ArrayList<String>();
			        		list.add(s);
		        		}
		        		GlobalSettings.setDeviceCheck(rnOrJS);
		        	} else {
		        		Log.e(LOG_TAG, "j or i is null");
		        	}
		        }
	        	if(rnOrJS.equals("YES")){
	        		Log.e(LOG_TAG, "rnOrJS = YES");
	        		rnOrJS = "YES";
	        		GlobalSettings.setDeviceCheck(rnOrJS);
	        	} else {
	        		Log.e(LOG_TAG, "rnOrJS = NULL");
	        	}
		        if(GlobalSettings.getDeviceCheck().equals("YES")){
		        	//Toast.makeText(this, "rnOrJS equals YES", Toast.LENGTH_LONG).show();
		        } else {
     	          	Intent intent = new Intent(ConnectDeviceDialogActivity.this, HardwareDialogActivity.class);
     	          	startActivity(intent);
		        }
	        } else{
        		Log.e(LOG_TAG, "names = NULL");
 	          	Intent intent = new Intent(ConnectDeviceDialogActivity.this, HardwareDialogActivity.class);
 	          	startActivity(intent);
	        }
        } else{
	          	Intent intent = new Intent(ConnectDeviceDialogActivity.this, HardwareDialogActivity.class);
	          	startActivity(intent);
        }
	}
	
	public void onBackPressed()  
	{  
//	    this.startActivity(new Intent(HardwareDialogActivity.this,HardwareDialogActivity.class));  
	    Toast.makeText(this, "You must choose an option to continue", Toast.LENGTH_LONG).show();

	    return;  
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.hardware_dialog, menu);
		return true;
	}

}
