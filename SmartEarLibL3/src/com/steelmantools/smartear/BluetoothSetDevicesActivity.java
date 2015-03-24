package com.steelmantools.smartear;

import android.os.Bundle;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.steelmantools.smartear.SavedRecordingsAdapter.ViewHolder;
import com.steelmantools.smartear.db.DBAdapter;
import com.steelmantools.smartear.model.SavedRecording;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.widget.SimpleAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
//import android.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;

public class BluetoothSetDevicesActivity extends Activity
//public class BluetoothSetDevicesActivity extends FragmentActivity
{
	public static String LOG_TAG = GraphingActivity.class.getSimpleName();
	
	final Context context = this;
	private Button button_back;
	TextView tv_TEST;
	TextView textTEST;
	
    SQLiteDatabase DB;
    Cursor cursor;
    
    DBAdapter DBAdapter;
    
    ListView lv_rejectcode;
//    String[] itemsarray=new String[]{" ","1","2","3","4","5","6"};
    ArrayAdapter<CharSequence> adapterSpinner;
    
    Spinner spinnerChannels;
    
	private BluetoothAdapter mBluetoothAdapter = null;
    private String[] choices = null;
    private String[] addresses = null;
    private String[] names = null;
    private String[] filteredNames = null;
    private ArrayList<BluetoothDevice> mDevices = new ArrayList<BluetoothDevice>(); 
    private Set<BluetoothDevice> devices;
	private BluetoothSetDevicesActivity activity;
	private LayoutInflater layoutInflater;
	private String concToAdd;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bluetooth_setdevices);
		
		textTEST = (TextView) findViewById(R.id.textTEST);
		   
		DBAdapter=new DBAdapter(this);
		DBAdapter=DBAdapter.open();
		
		displayListView();
	}
		private void displayListView() 
		{
			Cursor x = DBAdapter.getAllRows();

		  int[] to = new int[] 
				  {
				   R.id.text3,
				   R.id.text1,
				   R.id.text2,
				  };

		SimpleCursorAdapter xadapter = new SimpleCursorAdapter(this,
				R.layout.bluetooth_row_view,
		        x,
		        new String[] { "CHANNELASSIGN", "CHANNEL", "ADDRESS" }, to, 0);
	
		  ListView lv_rejectcode = (ListView) findViewById(R.id.lv_rejectcode);
		  lv_rejectcode.setAdapter(xadapter);
		  lv_rejectcode.setOnItemClickListener(new OnItemClickListener() 
		  {
			   @Override
			   public void onItemClick(AdapterView<?> listView, View view,
			     int position, long id) 
			   {
				   String delID = String.valueOf(id);
				   
				   String addressReturned = DBAdapter.getSinlgeEntryById(delID);
				   String nameReturned = DBAdapter.getSinlgeEntryByChannelAssign(delID);
				   SharedPreferences pref = getApplicationContext().getSharedPreferences("STEELMANpref", 0);
			    		           Editor editor = pref.edit();
			    		           editor.putString("ADDRESS", addressReturned);
			    		           editor.putString("NAME", nameReturned);
			    		           editor.commit();

					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
							context);
			 
						// set title
						alertDialogBuilder.setTitle("Select Device Channel");
						
			            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
			                    BluetoothSetDevicesActivity.this,
			                    android.R.layout.select_dialog_singlechoice);
			            
			            String getDeviceSize = pref.getString("DEVICELENGTH", " ");
			            
			            String deviceLengthZero = "0";
			    		String deviceLengthOne = "1";
			    		String deviceLengthTwo = "2";
			    		String deviceLengthThree = "3";
			    		String deviceLengthFour = "4";
			    		String deviceLengthFive = "5";
			    		String deviceLengthSix = "6";
			    		
			    		if(getDeviceSize.equals(deviceLengthOne)){
				            arrayAdapter.add("1");
			    		} else if(getDeviceSize.equals(deviceLengthTwo)) {
				            arrayAdapter.add("1");
				            arrayAdapter.add("2");
			    		} else if(getDeviceSize.equals(deviceLengthThree)) {
				            arrayAdapter.add("1");
				            arrayAdapter.add("2");
				            arrayAdapter.add("3");
			    		} else if(getDeviceSize.equals(deviceLengthFour)) {
				            arrayAdapter.add("1");
				            arrayAdapter.add("2");
				            arrayAdapter.add("3");
				            arrayAdapter.add("4");
			    		} else if(getDeviceSize.equals(deviceLengthFive)) {
				            arrayAdapter.add("1");
				            arrayAdapter.add("2");
				            arrayAdapter.add("3");
				            arrayAdapter.add("4");
				            arrayAdapter.add("5");
			    		} else if(getDeviceSize.equals(deviceLengthSix)) {
				            arrayAdapter.add("1");
				            arrayAdapter.add("2");
				            arrayAdapter.add("3");
				            arrayAdapter.add("4");
				            arrayAdapter.add("5");
				            arrayAdapter.add("6");
			    		}
			            
			            alertDialogBuilder.setNegativeButton("Cancel",
			                    new DialogInterface.OnClickListener() {

			                        @Override
			                        public void onClick(DialogInterface dialog, int which) {
			                            dialog.dismiss();
			                        }
			                    });
			            
			            alertDialogBuilder.setAdapter(arrayAdapter,
			                    new DialogInterface.OnClickListener() {

			                        @Override
			                        public void onClick(DialogInterface dialog, int which) {
			                            String strName = arrayAdapter.getItem(which);
			                            
			    		            	int spinnerPosition;
			    		            	int listItemSelectedposition = 0;
			    		                SharedPreferences pref = getApplicationContext().getSharedPreferences("STEELMANpref", 0);
			    		                String getAddressFromPref = pref.getString("ADDRESS", " ");
			    		                String getNameFromPref = pref.getString("NAME", " ");
			    		                Log.d("getAddressFromPref", "getAddressFromPref: "+ getAddressFromPref);
			    		                String checkAddr = DBAdapter.getSingleEntryAddr(getAddressFromPref);
			    		                String exist = "EXIST";
			    		                String notExist = "NOT EXIST";
			    		                
			    		                if (checkAddr.equals(exist)){
			    		                	DBAdapter.updateEntry(getAddressFromPref, getNameFromPref, strName);
			    		                } else if(checkAddr.equals(notExist)){
			    		                	DBAdapter.insertEntry(getAddressFromPref, getNameFromPref, strName);
			    		                }
			    		                displayListView();
			                        }
			                    });
			            alertDialogBuilder.show();

				   // Get the cursor, positioned to the corresponding row in the result set
				   Cursor cursor = (Cursor) listView.getItemAtPosition(position);
				   cursor.getString(cursor.getColumnIndexOrThrow("CHANNEL"));
				   cursor.getString(cursor.getColumnIndexOrThrow("ADDRESS"));
			   }
		  });

		  Button buttonDone = (Button) findViewById(R.id.buttonDone);
		  buttonDone.setOnClickListener(new View.OnClickListener() 
		  {
			  public void onClick(View v) 
			  {
				  if(v.getId()==R.id.buttonDone)
            	  	{	  
					  String channelZero = "0";
					  String channelOne = "1";
					  String channelTwo = "2";
					  String channelThree = "3";
					  String channelFour = "4";
					  String channelFive = "5";
					  String channelSix = "6";
					  String channelDuplicate = "DUPLICATE";
					  String channelDupCheck0 = DBAdapter.getSinlgeEntryByChannelAssignment(channelZero);
					  String channelDupCheck1 = DBAdapter.getSinlgeEntryByChannelAssignment(channelOne);
					  String channelDupCheck2 = DBAdapter.getSinlgeEntryByChannelAssignment(channelTwo);
					  String channelDupCheck3 = DBAdapter.getSinlgeEntryByChannelAssignment(channelThree);
					  String channelDupCheck4 = DBAdapter.getSinlgeEntryByChannelAssignment(channelFour);
					  String channelDupCheck5 = DBAdapter.getSinlgeEntryByChannelAssignment(channelFive);
					  String channelDupCheck6 = DBAdapter.getSinlgeEntryByChannelAssignment(channelSix);
					  if(channelDupCheck1.equals(channelDuplicate)){
						  Toast.makeText(BluetoothSetDevicesActivity.this, "Cannot have multiple devices assigned to Channel 1, please update and try again.", Toast.LENGTH_LONG).show();
					  } else if(channelDupCheck2.equals(channelDuplicate)){
						  Toast.makeText(BluetoothSetDevicesActivity.this, "Cannot have multiple devices assigned to Channel 2, please update and try again.", Toast.LENGTH_LONG).show();
					  } else if(channelDupCheck3.equals(channelDuplicate)){
						  Toast.makeText(BluetoothSetDevicesActivity.this, "Cannot have multiple devices assigned to Channel 3, please update and try again.", Toast.LENGTH_LONG).show();
					  } else if(channelDupCheck4.equals(channelDuplicate)){
						  Toast.makeText(BluetoothSetDevicesActivity.this, "Cannot have multiple devices assigned to Channel 4, please update and try again.", Toast.LENGTH_LONG).show();
					  } else if(channelDupCheck5.equals(channelDuplicate)){
						  Toast.makeText(BluetoothSetDevicesActivity.this, "Cannot have multiple devices assigned to Channel 5, please update and try again.", Toast.LENGTH_LONG).show();
					  } else if(channelDupCheck6.equals(channelDuplicate)){
						  Toast.makeText(BluetoothSetDevicesActivity.this, "Cannot have multiple devices assigned to Channel 6, please update and try again.", Toast.LENGTH_LONG).show();
					  } else if(channelDupCheck0.equals(channelDuplicate)){
						  Toast.makeText(BluetoothSetDevicesActivity.this, "No devices set, please update and try again.", Toast.LENGTH_LONG).show();
					  } else {
		       	          	Intent intent = new Intent(BluetoothSetDevicesActivity.this, ConnectDeviceDialogActivity.class);
		       	          	startActivity(intent);
					  }
            	  	}
			  }
		  });	
		}
		
	    private class CustomAdapter extends BaseAdapter
	    {
    	private BluetoothAdapter mBluetoothAdapter = null;
        private String[] choices = null;
        private String[] addresses = null;
        private String[] names = null;
        private ArrayList<BluetoothDevice> mDevices = new ArrayList<BluetoothDevice>(); 
        private Set<BluetoothDevice> devices;
        private ArrayList<String> myStringArray1;
        private ArrayList<String> mFilteredDevices = new ArrayList<String>(); 
    	private BluetoothSetDevicesActivity activity;
    	private LayoutInflater layoutInflater;
    	
//    	public CustomAdapter(BluetoothSetDevicesActivity activity, Set<BluetoothDevice> devices) {
    	public CustomAdapter(BluetoothSetDevicesActivity activity, ArrayList<String> myStringArray1) {
    		this.activity = activity;
    		layoutInflater = LayoutInflater.from(activity);
    		this.myStringArray1 = myStringArray1;
    	}
        @Override
        public int getCount() {
        	Log.d("myStringArray1.size()", "myStringArray1.size(): " + (myStringArray1.size())); 
        	return myStringArray1.size();
        }
        @Override
        public Object getItem(int position) {
        	
        	return mFilteredDevices.get(position);
        }
        @Override
        public long getItemId(int position) {

            return position;
        }
        @Override
        public View getView(int position, View convertview, ViewGroup parent) {
            if(convertview==null)
            {
                convertview=layoutInflater.inflate(R.layout.bluetooth_row_view,null);
                ViewHolder viewHolder = new ViewHolder();
//                viewHolder.spinner=(Spinner)convertview.findViewById(R.id.spinnerChannels);
//                viewHolder.spinner.setAdapter(adapterSpinner);
                viewHolder.text1 = (TextView) convertview.findViewById(R.id.text1);
                viewHolder.text2 = (TextView) convertview.findViewById(R.id.text2);
                convertview.setTag(viewHolder);
            }
            
            	final ViewHolder holder = (ViewHolder) convertview.getTag();
		        
		        Iterator<String> iter2 = myStringArray1.iterator();
		        names = new String[myStringArray1.size()];
		        for(int i = 0; i<myStringArray1.size(); i++){
		        	String temp2 = iter2.next();
		        	names[i] = temp2;
		        

		        	String zero = "0";
		        	String one = "1";
		        	String two = "2";
		        	String three = "3";
		        	String four = "4";
		        	String five = "5";
		        	String pos = String.valueOf(getItemId(position));
		        	if(pos.equals(zero)){
			        	StringTokenizer tokens = new StringTokenizer(names[0], ",");
			        	String nameConc = tokens.nextToken();
			        	String addrConc = tokens.nextToken();
		        		
//		                String name = names[0];
		        		String name = nameConc;
		                holder.text1.setText(name);
//		                String addr = addresses[0];
		                String addr = addrConc;
		                holder.text2.setText(addr);
		                //myStringArray1;
		        	} else if(pos.equals(one)){
			        	StringTokenizer tokens2 = new StringTokenizer(names[1], ",");
			        	String nameConc = tokens2.nextToken();
			        	String addrConc = tokens2.nextToken();
//		                String name = names[1];
		        		String name = nameConc;
		                holder.text1.setText(name);
//		                String addr = addresses[1];
		                String addr = addrConc;
		                holder.text2.setText(addr);
		        	} else if(pos.equals(two)){
		                String name = names[2];
		                holder.text1.setText(name);
//		                String addr = addresses[2];
//		                holder.text2.setText(addr);
		        	} else if(pos.equals(three)){
		                String name = names[3];
		                holder.text1.setText(name);
//		                String addr = addresses[3];
//		                holder.text2.setText(addr);
		        	} else if(pos.equals(four)){
		                String name = names[4];
		                holder.text1.setText(name);
//		                String addr = addresses[4];
//		                holder.text2.setText(addr);
		        	} else if(pos.equals(five)){
		                String name = names[5];
		                holder.text1.setText(name);
//		                String addr = addresses[5];
//		                holder.text2.setText(addr);
		        	}
            }
//		        int spinnerPosition;
		        final int[] positions=new int[2];
		        holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

		            @Override
		            public void onItemSelected(AdapterView<?> arg0, View arg1,
		                    int arg2, long arg3) {
		            	
		            	int spinnerPosition;
		            	int listItemSelectedposition = 0;
		                //positions[0]=listItemSelectedposition;
		                positions[0]=listItemSelectedposition;
		                positions[1]=arg2;
		                spinnerPosition = arg2;
		                Log.d("row spinner", ""+holder.spinner.getChildAt(spinnerPosition));
		                Log.d("row spinner", "arg2: "+ arg2);
		                Log.d("row spinner", "positions[1]: "+ positions[1]);
		                
		                String channelPos = String.valueOf(positions[1]);
		                Log.d("row spinner", "channelPos: "+ channelPos);
		                //String nameText = holder.text1.getText().toString();
		                String addrText = holder.text2.getText().toString();
		                Log.d("row spinner", "addrText: "+ addrText);
		                String checkAddr = DBAdapter.getSingleEntryAddr(addrText);
		                Log.d("row spinner", "checkAddr: "+ checkAddr);
		                String exist = "EXIST";
		                String notExist = "NOT EXIST";
		                
		            }

		            @Override
		            public void onNothingSelected(AdapterView<?> arg0) {
		                // TODO Auto-generated method stub
		            }
		        });
		        
            return convertview;
        }
        public class ViewHolder
        {
            Spinner spinner;
            TextView text1;
            TextView text2;
        }
	    }
		

	public void onBackPressed()  
	{  
	    this.startActivity(new Intent(BluetoothSetDevicesActivity.this,MainActivity.class));  

	    return;  
	}  
}
