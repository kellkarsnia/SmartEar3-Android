package com.steelmantools.smartear;

import java.util.Random;
import com.steelmantools.smartear.db.DBAdapter;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends GraphingActivity {

	public final static String LOG_TAG = MainActivity.class.getSimpleName();

	public boolean showingSettings;
	public boolean recording;

	private ImageButton auto;
	private ImageButton settings;
	private ImageButton changeView;
	private ImageButton icon;
	private ImageView mainReset;
	private ImageButton channelAssign;
	private ImageButton record;
	private ImageButton recordings;
	private ImageView mainBluetoothButton;
	public RelativeLayout graphParentLayout;
	public TextView mainTapInstructions;
	
	public ProgressDialog mDialog;

	public static MainActivity mainActivityRef;
	
    private static final String TAG = "Audio Separate - MainActivity: ";
    private static final boolean DEBUG = true;
    private AudioManager mAudioManager = null;
    private Context mContext = null;
    private BluetoothHeadset mBluetoothHeadset;
    private boolean scoON = false;
    
    private BluetoothAdapter mAdapter;
    
    DBAdapter DBAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (!GlobalSettings.IS_INIT) {
			GlobalSettings.init(this);
		}
		
		super.onCreate(savedInstanceState);
//		dalvik.system.VMRuntime.getRuntime().setMinimumHeapSize(32 * 1024 * 1024);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		mainActivityRef = this;
		recording = false;
		
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		
		String checkOn = "ON";
		String checkOff = "OFF";
		String checkStart = "START";
		if(GlobalSettings.getIsRecordingOn().equals(checkStart)){
	        Log.e(LOG_TAG, "MAIN OnCreate isRecordOnOff was null, set to OFF");
		}
        
        Log.e(LOG_TAG, "MAIN OnCreate FIRED");

		if (GlobalSettings.isPhase3()) {
			setContentView(R.layout.activity_main_screen_3);
			
			//new GetTask(this).execute();

			graphParentLayout = (RelativeLayout) findViewById(R.id.graphParentLayout);
			graphParentLayout.setDrawingCacheEnabled(true);
			graphParentLayout.setAlwaysDrawnWithCacheEnabled(true);
			graphParentLayout.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);

			channelAssign = (ImageButton) findViewById(R.id.channelAssignButton);
			channelAssign.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!recording) {
						MainActivity.this.navigate(new Intent(MainActivity.this, ChannelAssignmentActivity.class));
					}
				}
			});
			record = (ImageButton) findViewById(R.id.recordButton);
			record.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					recordPressed();
					String recordOnOff = "ON";
					GlobalSettings.setIsRecordingOn(recordOnOff);
					GlobalSettings.setRandomString(randomString(4));
				}
			});
			recordings = (ImageButton) findViewById(R.id.recordingsButton);
			recordings.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(MainActivity.this, SavedRecordingsActivity.class);
					MainActivity.this.startActivity(intent);
				}
			});

		} else {
			setContentView(R.layout.activity_main_screen);

			icon = (ImageButton) findViewById(R.id.iconButton);
			icon.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					navigate(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.jsproductsinc.com/")));
				}
			});
		}

		showingSettings = false;

		if (savedInstanceState == null) {
			savedInstanceState = getIntent().getExtras();
			getSupportFragmentManager().beginTransaction().add(R.id.mainMeterFragment, new AnalogMeterFragment())
					.setTransition(FragmentTransaction.TRANSIT_NONE).commit();
		}
		auto = (ImageButton) findViewById(R.id.mainAutoButton);
		auto.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!recording) {
					toggleAutoActive();
				}
			}
		});
		settings = (ImageButton) findViewById(R.id.mainSettingsButton);
		settings.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!recording) {
					toggleSettings();
				}
			}
		});
		changeView = (ImageButton) findViewById(R.id.mainChangeViewButton);
		changeView.setOnClickListener(new OnClickListener() {
			/**
			 * Changes between the analog and digital displays
			 */
			@Override
			public void onClick(View v) {
				if (!recording) {
					audioState.analogActive = !audioState.analogActive;
					updateMeterFragment();
				}
			}
		});
		mainReset = (ImageView) findViewById(R.id.mainReset);
		mainReset.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!showingSettings && !recording) {
					GlobalSettings.resetAll(MainActivity.this);
					setRefreshRate(GlobalSettings.getRefreshRate());
					Toast.makeText(MainActivity.this, "All settings reset", Toast.LENGTH_SHORT).show();
				}
			}
		});
		mainBluetoothButton = (ImageView) findViewById(R.id.mainBluetoothButton);
		mainBluetoothButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!showingSettings && !recording) {
					navigate(new Intent(MainActivity.this, BluetoothSetDevicesActivity.class));
				}
			}
		});
		OnClickListener toFullScreenGraph = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!recording) {
					if (showingSettings) {
						MainActivity.super.onBackPressed();
					} else {
						if (GlobalSettings.isPhase3()) {
							if (!MainActivity.this.showingSettings) {
								navigate(new Intent(MainActivity.this, FullScreenGraphActivity.class));
							}
						} else {
							navigate(new Intent(MainActivity.this, FullScreenGraphActivity.class));
						}
					}
				}
			}
		};
		mainTapInstructions = (TextView) findViewById(R.id.mainTapInstructions);
		mainTapInstructions.setOnClickListener(toFullScreenGraph);
		findViewById(R.id.fakeGraphView).setOnClickListener(toFullScreenGraph);

		setup(savedInstanceState);
	}
	
//	class GetTask extends AsyncTask<Object, Void, String> {
//	    Context context;
//
//	    GetTask(Context context) {
//	        this.context = context;
//	    }
//
//	    @Override
//	    protected void onPreExecute() {
//	        super.onPreExecute();
//
//	        mDialog = new ProgressDialog(mContext);
//	        mDialog.setMessage("Please wait...");
//	        mDialog.show();
//	    }
//
//	    @Override
//	    protected String doInBackground(Object... params) {
//			return rnOrJS;
//	        // here you can get the details from db or web and fetch it..
//	    }
//
//	    @Override
//	    protected void onPostExecute(String result) {
//	        super.onPostExecute(result);
//
//	        mDialog.dismiss();
//	    }
//	}
	
    String randomString(int len) {
	    final String alphabet = "BCDFGHJKLMNPQRSTVWXYZ";
	    Random r = new Random();
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ ) 
           sb.append( alphabet.charAt( r.nextInt(alphabet.length()) ) );
        return sb.toString();
    }
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	@Override
	public void onResume() {
		super.onResume();
		setMainTapInstructionsText();
		setRefreshRate(GlobalSettings.getRefreshRate());
		
        String recordOn = "ON";
        Log.e(LOG_TAG, "MAIN OnResume hit before if statement");
        if(recordOn.equals(GlobalSettings.getIsRecordingOn())){
        	Log.e(LOG_TAG, "MAIN OnResume isRecordOnOff = ON");
        }
	}
	@Override
	public void onPause() {
		super.onPause();
	}
	@Override
	public void onStop() {
        String isRecOn = "ON";
		super.onStop();
	}
	@Override
	public void updateChannelView() {
		super.updateChannelView();
		setMainTapInstructionsText();
	}
	private void setMainTapInstructionsText() {
		String channelName = GlobalSettings.getActiveChannel().getName();
		if (!"".equals(channelName)) {
			mainTapInstructions.setText(channelName);
		} else {
			mainTapInstructions.setText(R.string.tap_graph_to_zoom);
		}
	}
	private void updateMeterFragment() {
		GlobalSettings.setLineGraph(!GlobalSettings.isLineGraph());
		if(GlobalSettings.isLineGraph()){
			graphLayout.setVisibility(View.VISIBLE);
			graphLayoutBar.setVisibility(View.INVISIBLE);
		}else{
			graphLayout.setVisibility(View.INVISIBLE);
			graphLayoutBar.setVisibility(View.VISIBLE);
		}
		NumericalMeterFragment targetFragment = audioState.analogActive ? new AnalogMeterFragment()
				: new NumericalMeterFragment();
		getSupportFragmentManager().beginTransaction().replace(R.id.mainMeterFragment, targetFragment)
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
	}
	private void toggleSettings() {
		if (showingSettings) {
			super.onBackPressed();
			return;
		}
		showingSettings = true;
		getSupportFragmentManager()
				.beginTransaction()
				.setCustomAnimations(R.anim.slide_in_top, R.anim.slide_out_top, R.anim.slide_in_top,
						R.anim.slide_out_top).add(R.id.sampleSettingsFragment, new SamplingSettingsFragment())
				.addToBackStack(null).commit();
	}
	private void recordPressed() {
		if (recording) {
			return;
		}
		System.gc();
		audioService.resetHistoricalData();
		audioService.removeRawFiles();
		startRecordingAudio();
	}

	/**
	 * Give the user a choice to record video if the version is high enough.
	 */
	private void startRecording() {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		recording = true;
		getSupportFragmentManager()
				.beginTransaction()
				.setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_bottom, R.anim.slide_in_bottom,
						R.anim.slide_out_bottom).add(R.id.recordingLayout, new RecordingFragment())
				.addToBackStack(null).commit();
		audioService.startRecording();
	}
	public void startRecordingVideo() {
		GlobalSettings.setRecordingVideo(true);
		startRecording();
	}
	public void startRecordingAudio() {
		GlobalSettings.setRecordingVideo(false);
		startRecording();
	}
	public void toggleRecordingPause() {
		audioService.togglePauseRecording();
	}
	public void recordingDone(final boolean restartSampling) {
		recording = false;
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
		audioService.stopRecording(restartSampling);
		super.onBackPressed();
	}
	private void toggleAutoActive() {
		if (GlobalSettings.getAutoActive() != 0) {
			GlobalSettings.setAutoActive(0);
		} else {
			GlobalSettings.setAutoActive(audioService.getAverageDB());
		}
		auto.setImageResource(GlobalSettings.getAutoActive() == 0 ? R.drawable.auto_off : R.drawable.auto_on);
	}
	public void settingsDismissed() {
		showingSettings = false;
	}
	public void navigate(Intent intent) {
		if (showingSettings) {
			toggleSettings();
		}
		startActivity(intent);
	}
	@Override
	public void consumeReading(final int decibel, final int average, final int maximum, final boolean actual) {
		super.consumeReading(decibel, average, maximum, actual);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				NumericalMeterFragment fragment = (NumericalMeterFragment) getSupportFragmentManager()
						.findFragmentById(R.id.mainMeterFragment);
				if (fragment != null) {
					fragment.consumeReading(decibel, average, maximum, actual);
				}
			}
		});
	}
	public void setRefreshRate(int refreshRate) {
		NumericalMeterFragment fragment = (NumericalMeterFragment) getSupportFragmentManager().findFragmentById(
				R.id.mainMeterFragment);
		fragment.setRefreshRate(refreshRate);
	}
	public void startedProcessingRecording() {
		audioService.videoProcessing = true;
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				updateLegend();
				legendLayout.setVisibility(View.VISIBLE);
				findViewById(R.id.mainMeterFragment).setVisibility(View.INVISIBLE);
				FragmentManager fm = getSupportFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				Fragment prev = fm.findFragmentByTag("processingDialog");
				if (prev != null) {
					ft.remove(prev);
				}
				ProcessingDialogFragment newFragment = ProcessingDialogFragment.newInstance();
				newFragment.show(ft, "processingDialog");
			}
		});
	}
}
