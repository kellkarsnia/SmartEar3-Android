package com.steelmantools.smartear;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.audiofx.Equalizer;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.steelmantools.smartear.db.DBAdapter;
import com.steelmantools.smartear.db.DBDeviceAdapter;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewStyle;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.BarGraphView;
import com.steelmantools.smartear.audio.AudioConsumer;
import com.steelmantools.smartear.audio.AudioService;
import com.steelmantools.smartear.audio.AudioState;
import com.steelmantools.smartear.audio.BTConnect;

public class GraphingActivity extends FragmentActivity implements AudioConsumer {

	public static String LOG_TAG = GraphingActivity.class.getSimpleName();

	public static String STATE_ARG = "STATE_ARG";

	protected ImageButton microphone;
	protected ImageButton clamp;
	protected double xPlot;
	protected LineGraphView graphView;
	protected BarGraphView bargraphView;
	protected Button changeChannelButton;
	protected GraphViewSeries graphDataSeries1;
	protected GraphViewSeries graphDataSeries2;
	protected GraphViewSeries graphDataSeries3;
	protected GraphViewSeries graphDataSeries4;
	protected GraphViewSeries graphDataSeries5;
	protected GraphViewSeries graphDataSeries6;
	protected List<GraphViewSeries> allData;
	protected AudioState audioState;
	protected AudioService audioService;
	protected Equalizer equalizer;
	protected LinearLayout graphLayout; // available to do screen grabs
	protected LinearLayout graphLayoutBar; // available to do screen grabs
	protected List<ChannelColorRange> channelColors;

	protected View fakeLegendLayout;
	protected LinearLayout legendLayout;
	private TextView legendChannel1;
	private TextView legendChannel2;
	private TextView legendChannel3;
	private TextView legendChannel4;
	private TextView legendChannel5;
	private TextView legendChannel6;
	
    private static final boolean DEBUG = true;
    private AudioManager mAudioManager = null;
    //private Context mContext = null;
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
    ProgressDialog progDailog;
    
    protected boolean recordingBT;
    
    private String currentDeviceAddr;
    String rnOrJS;
    
	   DBAdapter DBAdapter;
	   DBDeviceAdapter DBDeviceAdapter;
	   
	   private final BroadcastReceiver mSCOHeadsetAudioState = new BroadcastReceiver() {
		     public void onReceive(Context context, Intent intent) {
		     	int state = intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, -1);
		     	if (state == AudioManager.SCO_AUDIO_STATE_CONNECTED) {
		     		//Toast.makeText(GraphingActivity.this, "Bluetooth Recording is Ready", Toast.LENGTH_LONG).show();
		     		scoON = true;
		     	} else if (state == AudioManager.SCO_AUDIO_STATE_DISCONNECTED) {
		     		//Toast.makeText(GraphingActivity.this, "Bluetooth Recording Disabled", Toast.LENGTH_LONG).show();
		     		scoON = false;	
		     		showTimer();
		     }
		   }
		 };
		 //PC Connect Connection Receiver
		 private final BroadcastReceiver PCCommand = new BroadcastReceiver() {
		     public void onReceive(Context context, Intent intent) {
		     	if(!scoON){
		     		
		     	}
		     }
		 };
		 public void showTimer(){
	    	    progDailog = ProgressDialog.show(this, "Connecting to Device",
	            "Please wait", true);
	    new Thread() {
	        public void run() {
	            try {
	                // sleep the thread for loading
	                sleep(10000);
	            } catch (Exception e) {
	            }
	            progDailog.dismiss();
	        }
	    }.start();
		 }
		// Define Service Listener of BluetoothProfile
		private BluetoothProfile.ServiceListener mProfileListener = new BluetoothProfile.ServiceListener() {
				public void onServiceConnected(int profile, BluetoothProfile proxy) {
					if (profile == BluetoothProfile.HEADSET) {
						mBluetoothHeadset = (BluetoothHeadset) proxy;
						List<BluetoothDevice> pairedDevices = mBluetoothHeadset.getConnectedDevices();
					    // If there are paired devices
					    if (pairedDevices.size() > 0) {
					    	startSCO();
					    	for (BluetoothDevice device : pairedDevices) {
					    		Log.e(LOG_TAG, "BT Device :"+device.getName()+ " , BD_ADDR:" + device.getAddress());       //Print out Headset name  
					    		device.getAddress();
					    		currentDeviceAddr = device.getAddress();
					    		mBluetoothHeadset.isAudioConnected(device);
//					    		if(mBluetoothHeadset.isAudioConnected(device)){
//					    			//resetScreenOnStateChange();
////						    		setupGraph();	
////						    		updateChannelView();
//					    			Log.e(LOG_TAG, "mBluetoothHeadset.isAudioConnected(device) = true"); 
//					    		}	
					    		String isConnectedToBT = "ON";
					    		GlobalSettings.setIsConnectedToBT(isConnectedToBT);
					    		Log.e(LOG_TAG, "isConnectedToBT :"+GlobalSettings.getIsConnectedToBT());
					    		Toast.makeText(GraphingActivity.this, "Bluetooth Device is Connected", Toast.LENGTH_LONG).show();
					    		String recordOnOff = "ON";
					    		if(recordOnOff.equals(GlobalSettings.getIsRecordingOn())){
					    			restartRec();
					    		}
					    	}
					    } else {
					    	//Toast.makeText(GraphingActivity.this, "Could not find a connected device, please connect a device", Toast.LENGTH_LONG).show();
					    	//startSCO();
				          	Intent intentSelect = new Intent(GraphingActivity.this, ConnectDeviceDialogActivity.class);
				          	startActivity(intentSelect);
					        return;
					    }
					}
				}	
				public void onServiceDisconnected(int profile) {
					if (profile == BluetoothProfile.HEADSET) {
						mBluetoothHeadset = null;
						//Toast.makeText(GraphingActivity.this, "mBluetoothHeadset = null FIRED", Toast.LENGTH_LONG).show();
					}
				}
		};
		public void restartRec() {
    		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    		//recording = true;
    		getSupportFragmentManager()
    				.beginTransaction()
    				.setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_bottom, R.anim.slide_in_bottom,
    						R.anim.slide_out_bottom).add(R.id.recordingLayout, new RecordingFragment())
    				.addToBackStack(null).commit();
		}
		//listen for adapter state changes
		private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		    @Override
		    public void onReceive(Context context, Intent intent) {
		        final String action = intent.getAction();

		        if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
		            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
		                                                 BluetoothAdapter.ERROR);
		            switch (state) {
		            case BluetoothAdapter.STATE_OFF:
		                //setButtonText("Bluetooth off");
		                break;
		            case BluetoothAdapter.STATE_TURNING_OFF:
		                //setButtonText("Turning Bluetooth off...");
		                break;
		            case BluetoothAdapter.STATE_ON:
		            	resetScreenOnStateChange();
		            	//setAddressesOnStart();
		                //setButtonText("Bluetooth on");
		                break;
		            case BluetoothAdapter.STATE_TURNING_ON:
		                //setButtonText("Turning Bluetooth on...");
		                break;
		            }
		        }
//		        if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED) && action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)){
//			    	Toast.makeText(GraphingActivity.this, "Cannot find bluetooth device.", Toast.LENGTH_LONG).show();
//		          	Intent intentSelect = new Intent(GraphingActivity.this, ConnectDeviceDialogActivity.class);
//		          	startActivity(intentSelect);
//		        }
		    }
		};
		
		public void resetScreenOnStateChange(){
			Intent intent = new Intent(GraphingActivity.this, MainActivity.class);
			GraphingActivity.this.startActivity(intent);
		}
		
	    //The BroadcastReceiver that listens for bluetooth broadcasts
	    private final BroadcastReceiver BTReceiver = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	        String action = intent.getAction();

	        if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
	        	String isConnectedToBT = "ON";
	        	GlobalSettings.setIsConnectedToBT(isConnectedToBT);
	        	getAddressActiveChannel();
	            //Toast.makeText(context, "BT Device Connected", Toast.LENGTH_SHORT).show();
	        } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
	            //Do something if disconnected
	        	String isConnectedToBT = "OFF";
	        	GlobalSettings.setIsConnectedToBT(isConnectedToBT);
	          	Intent intentSelect = new Intent(GraphingActivity.this, ConnectDeviceDialogActivity.class);
	          	startActivity(intentSelect);
	        } else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
	        	//Do something if disconnect requested
	        	String isConnectedToBT = "OFF";
	        	GlobalSettings.setIsConnectedToBT(isConnectedToBT);
	        }
	    }
	};
		 // Local Bluetooth adapter
		 private BluetoothAdapter mBluetoothAdapter = null;
			public void checkBluetoothConnection(){
			    
		        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		        // If the adapter is null, then Bluetooth is not supported
		        if (mBluetoothAdapter == null) {
		            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
		            finish();
		            return;
		        }
		        int REQUEST_ENABLE_BT = RESULT_OK;
		        // Check whether BT is enabled
		        if (!mBluetoothAdapter.isEnabled()) {			//checks if bluetooth is enabled, if not it asks for permission to enable it
				    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
				    //setAddressesOnStart();
				}
				if(REQUEST_ENABLE_BT != RESULT_OK){
					return;
				}
				if(mBluetoothAdapter.isEnabled()){
					DBAdapter=new DBAdapter(this);
					DBAdapter=DBAdapter.open();
					
					setAddressesOnStart();
					mBluetoothAdapter.startDiscovery();
					mReceiver = new BroadcastReceiver() {
						public void onReceive(Context context, Intent intent) {
						    String action = intent.getAction();
						    if (BluetoothDevice.ACTION_FOUND.equals(action)) 
						    {
						        // Get the BluetoothDevice object from the Intent
						        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
						        Log.e(LOG_TAG, "mBluetoothAdapter.startDiscovery() device address: " + device.getAddress());
						    }
						  }
						};

						IntentFilter filterFound = new IntentFilter(BluetoothDevice.ACTION_FOUND); 
						registerReceiver(mReceiver, filterFound);
					try {
						getUuids();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
			public void closeProxy(){
				mBluetoothAdapter.closeProfileProxy(BluetoothProfile.HEADSET, mBluetoothHeadset);
			}
			
		    public void PCConnect(String PCAddr){
//		    	IntentFilter newintent = new IntentFilter();
//		        newintent.addAction("CONNECT_PC");
////		        mContext.registerReceiver(PCCommand, newintent);
////		        GraphingActivity.this.registerReceiver(PCCommand, newintent);
//		        registerReceiver(PCCommand, newintent);
		        
		        mBluetoothAdapter.getProfileProxy(this, mProfileListener, BluetoothProfile.HEADSET);
		        Log.d(LOG_TAG, "mProfileListener in PCCONNECT FIRED: ");
				int activeChannelChanged = GlobalSettings.getActiveChannel().getNumber();
				String activeChannelChangedValue = String.valueOf(activeChannelChanged);
				Log.d(LOG_TAG, "activeChannelChangedValue in PCCONNECT FIRED: " + activeChannelChangedValue);
		        currentDeviceAddr = DBAdapter.getSingleEntryChannelSwitch(activeChannelChangedValue);
		        Log.d(LOG_TAG, "currentDeviceAddr in PCCONNECT FIRED: " + currentDeviceAddr);
			    //resetScreenOnStateChange();
		        if(currentDeviceAddr != null){
			        BTConnect BTPC = new BTConnect(currentDeviceAddr);
			        BTPC.resetBluetooth();
		        }
			    BTConnect BTPC = new BTConnect(PCAddr);
//			    progDailog = ProgressDialog.show(this, "Connecting to Device",
//			            "Please wait", true);
//			    new Thread() {
//			        public void run() {
//			            try {
//			                // sleep the thread for connecting
//			                sleep(1000);
//			            } catch (Exception e) {
//			            }
//			            progDailog.dismiss();
//			        }
//			    }.start();
			    BTPC.Start();
		    }
		    
		    public void getUuids() throws NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		    	BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

		    	Method getUuidsMethod = BluetoothAdapter.class.getDeclaredMethod("getUuids", null);

		    	ParcelUuid[] uuids = (ParcelUuid[]) getUuidsMethod.invoke(adapter, null);

		    	for (ParcelUuid uuid: uuids) {
		    	    Log.d(LOG_TAG, "UUID: " + uuid.getUuid().toString());
		    	}
		    }
		    
			public void setAddressesOnStart(){
				DBAdapter=new DBAdapter(this);
				DBAdapter=DBAdapter.open();
				   
		    	Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
		        Iterator<BluetoothDevice> iter = devices.iterator();
		        choices = new String[devices.size()];
		        addresses = new String[devices.size()];
//		        Arrays.sort(addresses);
//		        List<String> sorted = Arrays.asList(addresses);
//		        Log.e(LOG_TAG, "Arrays.sort(addresses): " + sorted);
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
			                Log.d("row spinner", "addresses[i]: "+ addresses[i]);
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
		     	          	Intent intent = new Intent(GraphingActivity.this, HardwareDialogActivity.class);
		     	          	startActivity(intent);
				        }
			        } else{
		        		Log.e(LOG_TAG, "names = NULL");
	     	          	Intent intent = new Intent(GraphingActivity.this, HardwareDialogActivity.class);
	     	          	startActivity(intent);
			        }
		        } else{
     	          	Intent intent = new Intent(GraphingActivity.this, HardwareDialogActivity.class);
     	          	startActivity(intent);
		        }
			}
			
			public void getAddressActiveChannel(){
				String channelOneActive = "1";
				String channelTwoActive = "2";
				String channelThreeActive = "3";
				String channelFourActive = "4";
				String channelFiveActive = "5";
				String channelSixActive = "6";
				
				int activeChannelFromBtn = GlobalSettings.getActiveChannel().getNumber();
				String activeChannelForAddr = String.valueOf(activeChannelFromBtn);
				
				if(activeChannelForAddr.equals(channelOneActive)){
					PCConnect(addresses[0]);
					Log.e(LOG_TAG, "getAddressActiveChannel is ONE: " + addresses[0]);
				} else if(activeChannelForAddr.equals(channelTwoActive)){
					PCConnect(addresses[1]);
					Log.e(LOG_TAG, "getAddressActiveChannel is TWO: " + addresses[1]);
				} else if(activeChannelForAddr.equals(channelThreeActive)){
					PCConnect(addresses[2]);
				} else if(activeChannelForAddr.equals(channelFourActive)){
					PCConnect(addresses[3]);
				} else if(activeChannelForAddr.equals(channelFiveActive)){
					PCConnect(addresses[4]);
				} else if(activeChannelForAddr.equals(channelSixActive)){
					PCConnect(addresses[5]);
				}
			}

	/**
	 * A connection object for binder to the AudioService.
	 */
	protected ServiceConnection serviceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder binder) {
			audioService = ((AudioService.AudioServiceBinder) binder).getService();
			audioService.setAudioConsumer(GraphingActivity.this);
		}
		@Override
		public void onServiceDisconnected(ComponentName className) {
			audioService = null;
		}
	};

	/**
	 * Toggles playback based on headphone connectivity.
	 */
	protected BroadcastReceiver headphoneJackReceiver = new BroadcastReceiver() {

		@SuppressWarnings("unused")
		@Override
		public void onReceive(Context context, Intent intent) {
			AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
			audioManager.setMicrophoneMute(true);
			
			int state = intent.getIntExtra("state", 0);
			String headsetName = intent.getStringExtra("name");
			int microphone = intent.getIntExtra("microphone", 0);

			GlobalSettings.setHeadphonesConnected(state == 1);
			String message = null;
			if (state == 0) {
				message = "Connect headphones to enable playback";
			} else if (microphone == 0) {
				message = "Using built in microphone";
				message = "Connect microphone to enable recording.";
				//trying set mic mute=true here to try and kill double mic instances on tablet-KK_031114_0915
				//audioManager.setMicrophoneMute(true); //GARY TURN INTERNAL MIC OFF
			} else if (microphone == 1) {
				message = "Using external microphone";
				audioManager.setMicrophoneMute(false); //GARY TURN INTERNAL MIC OFF
				//trying set mic mute=true here to try and kill double mic instances on tablet-KK_031114_0915
				//audioManager.setMicrophoneMute(true); //GARY TURN INTERNAL MIC OFF
			}
			if (message != null) {
				//Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
			}
		}
	};

	/**
	 * Prepare the graphing and ui output code as well as liking to the audio service.
	 * 
	 * @param savedInstanceState
	 */
	protected void setup(Bundle savedInstanceState) {
		
		IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
		registerReceiver(BTReceiver, filter1);
		
		//setAddressesOnStart();
		String isDeviceConnected = "ON";
		if(isDeviceConnected.equals(GlobalSettings.getIsConnectedToBT())){
			channelColors = new ArrayList<ChannelColorRange>();
			ChannelColorRange initial = new ChannelColorRange(0, GlobalSettings.getActiveChannel().getAndroidColor(this));
			channelColors.add(initial);

			if (savedInstanceState != null && savedInstanceState.containsKey(STATE_ARG)) {
				audioState = (AudioState) savedInstanceState.get(STATE_ARG);
			} else {
				audioState = new AudioState();
			}
			graphLayout = (LinearLayout) findViewById(R.id.graphLayout);
			graphLayoutBar = (LinearLayout) findViewById(R.id.graphLayoutBar);

			if(GlobalSettings.isLineGraph()){
				graphLayout.setVisibility(View.VISIBLE);
			}else{
				graphLayoutBar.setVisibility(View.VISIBLE);
			}

			setupGraph();	
			if (GlobalSettings.isPhase3()) {
				changeChannelButton = (Button) findViewById(R.id.changeChannelView);
				changeChannelButton.setVisibility(View.VISIBLE);
				changeChannelButton.setSoundEffectsEnabled(false);
		       
				changeChannelButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						
		                String recordOn = "ON";
						if(recordOn.equals(GlobalSettings.getIsRecordingOn())){
							toggleRecordingPauseGraphing();
							Log.e(LOG_TAG, "isRecordOnOff = ON");
						}
						closeProxy();
	     	          	Intent intent = new Intent(GraphingActivity.this, BluetoothSelectActivity.class);
	     	          	startActivity(intent);
					}
				});
				fakeLegendLayout = findViewById(R.id.fakeLegendLayout);
				legendLayout = (LinearLayout) findViewById(R.id.legendLayout);

				legendChannel1 = (TextView) findViewById(R.id.legendChannel1TextView);
				legendChannel2 = (TextView) findViewById(R.id.legendChannel2TextView);
				legendChannel3 = (TextView) findViewById(R.id.legendChannel3TextView);
				legendChannel4 = (TextView) findViewById(R.id.legendChannel4TextView);
				legendChannel5 = (TextView) findViewById(R.id.legendChannel5TextView);
				legendChannel6 = (TextView) findViewById(R.id.legendChannel6TextView);

				updateChannelView();
			}
			microphone = (ImageButton) findViewById(R.id.microphoneButton);
			microphone.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					GlobalSettings.setMicrophoneActive(true);
					setAudioSource();
				}
			});
			clamp = (ImageButton) findViewById(R.id.clampButton);
			clamp.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					GlobalSettings.setMicrophoneActive(false);
					setAudioSource();
				}
			});
		} else {
	          	Intent intentSelect = new Intent(GraphingActivity.this, ConnectDeviceDialogActivity.class);
	          	startActivity(intentSelect);
		}
	}
	public void toggleRecordingPauseGraphing() {
		audioService.togglePauseRecording();
	}
    public void startSCO(){
        // get the Audio Service context
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (mAudioManager == null){
                Log.e(LOG_TAG, "Audiomanager is null");
                finish();
                return;
        }
    	if(DEBUG)
    		Log.e(LOG_TAG, "SCO Start Attempted");
   		if(!scoON){
   			scoON = true;
   			mAudioManager.setBluetoothScoOn(true);
  			mAudioManager.startBluetoothSco();
  		}else{
    		//Toast.makeText(this, "Audio Stream already started, or starting", Toast.LENGTH_LONG).show();
   		}
    }
	public void updateChannelView() {
		if (GlobalSettings.isPhase3()) {
			changeChannelButton.setText("CH" + GlobalSettings.getActiveChannel().getNumber());
			int channelColor = GlobalSettings.getActiveChannel().getAndroidColor(this);
			changeChannelButton.setTextColor(channelColor);
			allData.get(GlobalSettings.getActiveChannelIndex()).appendData(new GraphViewData(xPlot, 0), true, 20);
			if (audioService != null) {
				audioService.changeChannel(GlobalSettings.getActiveChannel());
			}
		}
	}
	/**
	 * Sets the legend used in video recording to the latest names for each channel.
	 */
	public void updateLegend() {
		setChannelDisplayName(legendChannel1, 0);
		setChannelDisplayName(legendChannel2, 1);
		setChannelDisplayName(legendChannel3, 2);
		setChannelDisplayName(legendChannel4, 3);
		setChannelDisplayName(legendChannel5, 4);
		setChannelDisplayName(legendChannel6, 5);
	}

	private void setChannelDisplayName(TextView textView, int index) {
		String name = GlobalSettings.getChannels().get(index).getName();
		if (name == null || "".equals(name)) {
			textView.setText(index + " - Unassigned");
		} else {
			textView.setText(index + " - " + name);
		}
	}
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(STATE_ARG, audioState);
	}
	@Override
	public void onResume() {
		super.onResume();
		
		checkBluetoothConnection();
		
//	    progDailog = ProgressDialog.show(this, "Connecting to Device",
//	            "Please wait", true);
//	    new Thread() {
//	        public void run() {
//	            try {
//	                // sleep the thread for loading
//	                sleep(11000);
//	            } catch (Exception e) {
//	            }
//	            progDailog.dismiss();
//	        }
//	    }.start();
		
			Log.e(LOG_TAG, "GraphingActivity onResume getIsConnectedToBT = ON");
			registerReceiver(headphoneJackReceiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));

			Intent i = new Intent(this, AudioService.class);
			startService(i);
			bindService(new Intent(this, AudioService.class), serviceConnection, Context.BIND_AUTO_CREATE);
			
		    IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
		    registerReceiver(mReceiver, filter);
		    
	        IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
	        //KK commented out these 2 below to get tablet and note working, they are working without, may need to uncomment to get prompts working again-KK_032814_0611
	        IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
	        IntentFilter filter3 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
		    registerReceiver(BTReceiver, filter1);
		  //KK commented out these 2 below to get tablet and note working, they are working without, may need to uncomment to get prompts working again(same as above comment)-KK_032814_0611
		    registerReceiver(BTReceiver, filter2);
		    registerReceiver(BTReceiver, filter3);
		    
	    	IntentFilter newintent = new IntentFilter();
	        newintent.addAction("CONNECT_PC");
	        registerReceiver(PCCommand, newintent);
	        
	        IntentFilter newintent2 = new IntentFilter();
	        newintent2.addAction(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED);
	        registerReceiver(mSCOHeadsetAudioState, newintent2);
			
//			checkBluetoothConnection();
			
			String checkForRec = GlobalSettings.getIsRecordingOn();
			
			String checkOn = "ON";
			String checkOff = "OFF";
			String checkStart = "START";
			if(GlobalSettings.getIsRecordingOn().equals(checkOn)){
		        Log.e(LOG_TAG, "MAIN OnCreate isRecordOnOff was null, set to OFF");
		        //toggleRecordingPauseGraphing();
			}
		
		int activeChannelFromBtn = GlobalSettings.getActiveChannel().getNumber();
		String activeChannelOnResume = String.valueOf(activeChannelFromBtn);
	}

	@Override
	public void onPause() {
		super.onPause();
			unbindService(serviceConnection);
			stopService(new Intent(this, AudioService.class));
			unregisterReceiver(headphoneJackReceiver);
			unregisterReceiver(mReceiver);
			unregisterReceiver(BTReceiver);
			unregisterReceiver(PCCommand);
			unregisterReceiver(mSCOHeadsetAudioState);

		//kk, was getting error here onpause that this reciever was never registered. maybe this is messing up switching?
		//unregisterReceiver(mReceiver);
		//stopService(mProfileListener);
	}

	protected void onServiceAttached() {
	}

	protected void setupGraph() {
		graphView = new LineGraphView(this, "");
		bargraphView = new BarGraphView(this, "");
		
		graphView.setDrawBackground(false);
		graphView.setScrollable(true);
		graphView.setViewPort(0, 20);
		graphView.setManualYAxisBounds(120, 0);
		graphView.setHorizontalLabels(new String[] {});
		
		bargraphView.setScrollable(true);
		bargraphView.setViewPort(0, 20);
		bargraphView.setManualYAxisBounds(120, 0);
		bargraphView.setHorizontalLabels(new String[] {});

		GraphViewStyle graphStyle = new GraphViewStyle();
		graphStyle.setGridColor(Color.GRAY);
		graphStyle.setVerticalLabelsColor(Color.RED);
		graphStyle.setNumHorizontalLabels(100);

		if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {
			graphStyle.setTextSize((float) 15);
			graphStyle.setVerticalLabelsWidth(30);
		} else {
			graphStyle.setVerticalLabelsWidth(55);
		}

		graphView.setGraphViewStyle(graphStyle);
		graphLayout.addView(graphView);
		bargraphView.setGraphViewStyle(graphStyle);
		graphLayoutBar.addView(bargraphView);
		
		graphDataSeries1 = new GraphViewSeries(new GraphViewData[] {});
		graphDataSeries1.getStyle().color = GlobalSettings.getChannels().get(0).getAndroidColor(this);
		graphDataSeries1.getStyle().thickness = 8;
		graphView.addSeries(graphDataSeries1);
		bargraphView.addSeries(graphDataSeries1);
		if (GlobalSettings.isPhase3()) {
			graphDataSeries2 = new GraphViewSeries(new GraphViewData[] {});
			graphDataSeries2.getStyle().color = GlobalSettings.getChannels().get(1).getAndroidColor(this);
			graphDataSeries2.getStyle().thickness = 8;
			graphView.addSeries(graphDataSeries2);
			bargraphView.addSeries(graphDataSeries2);
			graphDataSeries3 = new GraphViewSeries(new GraphViewData[] {});
			graphDataSeries3.getStyle().color = GlobalSettings.getChannels().get(2).getAndroidColor(this);
			graphDataSeries3.getStyle().thickness = 8;
			graphView.addSeries(graphDataSeries3);
			bargraphView.addSeries(graphDataSeries3);
			graphDataSeries4 = new GraphViewSeries(new GraphViewData[] {});
			graphDataSeries4.getStyle().color = GlobalSettings.getChannels().get(3).getAndroidColor(this);
			graphDataSeries4.getStyle().thickness = 8;
			graphView.addSeries(graphDataSeries4);
			bargraphView.addSeries(graphDataSeries4);
			graphDataSeries5 = new GraphViewSeries(new GraphViewData[] {});
			graphDataSeries5.getStyle().color = GlobalSettings.getChannels().get(4).getAndroidColor(this);
			graphDataSeries5.getStyle().thickness = 8;
			graphView.addSeries(graphDataSeries5);
			bargraphView.addSeries(graphDataSeries5);
			graphDataSeries6 = new GraphViewSeries(new GraphViewData[] {});
			graphDataSeries6.getStyle().color = GlobalSettings.getChannels().get(5).getAndroidColor(this);
			graphDataSeries6.getStyle().thickness = 8;
			graphView.addSeries(graphDataSeries6);
			bargraphView.addSeries(graphDataSeries6);
		}

		allData = new ArrayList<GraphViewSeries>();
		allData.add(graphDataSeries1);
		if (GlobalSettings.isPhase3()) {
			allData.add(graphDataSeries2);
			allData.add(graphDataSeries3);
			allData.add(graphDataSeries4);
			allData.add(graphDataSeries5);
			allData.add(graphDataSeries6);
		}
		xPlot = 0;
	}

	protected void setAudioSource() {
		microphone.setImageResource(GlobalSettings.isMicrophoneActive() ? R.drawable.microphone_on : R.drawable.microphone_off);
		clamp.setImageResource(GlobalSettings.isMicrophoneActive() ? R.drawable.clamp_off : R.drawable.clamp_on);
	}

	@Override
	public void consumeReading(final int decibel, final int average, final int maximum, boolean actual) {
		if (actual) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (allData != null) {
						for (int i = 0; i < allData.size(); i++) {
							if (i == GlobalSettings.getActiveChannelIndex()) {
								allData.get(i).appendData(new GraphViewData(xPlot, decibel), true, 20);
							} else {
								allData.get(i).appendData(new GraphViewData(xPlot, 0), true, 20);
							}
						}
						xPlot += 1d;
					}
				}
			});
		}
	}

	@Override
	public void bindEqualizer(Equalizer equalizer) {
		this.equalizer = equalizer;
	}

	@Override
	public void consumeReadingBuffer(ShortBuffer buffer) {
		// do nothing
	}
	
	public void doPositiveClick(String activeChannelChangedValue){

	}
	

}
