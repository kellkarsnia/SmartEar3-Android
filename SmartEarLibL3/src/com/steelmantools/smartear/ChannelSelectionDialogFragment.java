package com.steelmantools.smartear;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.steelmantools.smartear.db.DBAdapter;
import com.steelmantools.smartear.model.Channel;

public class ChannelSelectionDialogFragment extends DialogFragment {

	public static ChannelSelectionDialogFragment newInstance() {
		ChannelSelectionDialogFragment fragment = new ChannelSelectionDialogFragment();
		Bundle bundle = new Bundle();
		fragment.setArguments(bundle);
		fragment.setCancelable(false);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater li = LayoutInflater.from(getActivity());
		
//		DBAdapter DBAdapter;
//		
//		DBAdapter=new DBAdapter(this);
//		DBAdapter=DBAdapter.open();
		   
		return new AlertDialog.Builder(getActivity()).setView(doCreateView(li, null, null)).setTitle(R.string.select_a_channel)
//				.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
				.setPositiveButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
//						dialog.dismiss();
//		                Intent intent = new Intent(getActivity().getBaseContext(), MainActivity.class);    
//		                startActivity(intent);    
		                
						int activeChannelChanged = GlobalSettings.getActiveChannel().getNumber();
						String activeChannelChangedValue = String.valueOf(activeChannelChanged);
		                ((BluetoothSelectActivity) ChannelSelectionDialogFragment.this.getActivity()).isDeviceInRange(activeChannelChangedValue);
					}
				}).create();
	}

	public View doCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_channel_selection, container);
		
		//getDialog().setCancelable(false);

        SharedPreferences pref = getActivity().getSharedPreferences("STEELMANpref", 0);
        String getDeviceSize = pref.getString("DEVICELENGTH", " ");
        
        String deviceLengthZero = "0";
		String deviceLengthOne = "1";
		String deviceLengthTwo = "2";
		String deviceLengthThree = "3";
		String deviceLengthFour = "4";
		String deviceLengthFive = "5";
		String deviceLengthSix = "6";
        
        if(getDeviceSize.equals(deviceLengthOne)){
    		List<Button> buttons = new ArrayList<Button>();
    		buttons.add((Button) view.findViewById(R.id.channelButton1));
    		
    		//hide buttons less than filtered device array size-KK_031214_1757
    		Button channelButton2 = ((Button) view.findViewById(R.id.channelButton2));
    		channelButton2.setVisibility(View.INVISIBLE);
    		Button channelButton3 = ((Button) view.findViewById(R.id.channelButton3));
    		channelButton3.setVisibility(View.INVISIBLE);
    		Button channelButton4 = ((Button) view.findViewById(R.id.channelButton4));
    		channelButton4.setVisibility(View.INVISIBLE);
    		Button channelButton5 = ((Button) view.findViewById(R.id.channelButton5));
    		channelButton5.setVisibility(View.INVISIBLE);
    		Button channelButton6 = ((Button) view.findViewById(R.id.channelButton6));
    		channelButton6.setVisibility(View.INVISIBLE);
    		int i = 0;
    		int b = 0;
    		for (Button button : buttons) {
    			Channel channel = GlobalSettings.getChannels().get(i);
    			button.setText("Channel " + channel.getNumber());
    			button.setTextColor(channel.getAndroidColor(this.getActivity()));
    			button.setSoundEffectsEnabled(false);
    			final int index = i;
    			final int indexBT = b;
    			button.setOnClickListener(new OnClickListener() {
    				@Override
    				public void onClick(View v) {
    					GlobalSettings.setActiveChannelIndex(index);
    					GlobalSettings.setActiveBTChannelIndex(indexBT);
    					
    		        	String isConnectedToBT = "ON";
    		        	GlobalSettings.setIsConnectedToBT(isConnectedToBT);
    					
    					int activeChannelChanged = GlobalSettings.getActiveChannel().getNumber();
    					String activeChannelChangedValue = String.valueOf(activeChannelChanged);
    					
    					((BluetoothSelectActivity) ChannelSelectionDialogFragment.this.getActivity()).getDeviceToConnect(activeChannelChangedValue);
    					((BluetoothSelectActivity) ChannelSelectionDialogFragment.this.getActivity()).startBluetoothTasks();

    					ChannelSelectionDialogFragment.this.dismiss();
    				}
    			});
    			i += 1;
    		}
        } else if(getDeviceSize.equals(deviceLengthTwo)){
    		List<Button> buttons = new ArrayList<Button>();
    		buttons.add((Button) view.findViewById(R.id.channelButton1));
    		buttons.add((Button) view.findViewById(R.id.channelButton2));
    		
    		//hide buttons less than filtered device array size-KK_031214_1757
    		Button channelButton3 = ((Button) view.findViewById(R.id.channelButton3));
    		channelButton3.setVisibility(View.INVISIBLE);
    		Button channelButton4 = ((Button) view.findViewById(R.id.channelButton4));
    		channelButton4.setVisibility(View.INVISIBLE);
    		Button channelButton5 = ((Button) view.findViewById(R.id.channelButton5));
    		channelButton5.setVisibility(View.INVISIBLE);
    		Button channelButton6 = ((Button) view.findViewById(R.id.channelButton6));
    		channelButton6.setVisibility(View.INVISIBLE);
    		int i = 0;
    		int b = 0;
    		for (Button button : buttons) {
    			Channel channel = GlobalSettings.getChannels().get(i);
    			button.setText("Channel " + channel.getNumber());
    			button.setTextColor(channel.getAndroidColor(this.getActivity()));
    			button.setSoundEffectsEnabled(false);
    			final int index = i;
    			final int indexBT = b;
    			button.setOnClickListener(new OnClickListener() {
    				@Override
    				public void onClick(View v) {
    					GlobalSettings.setActiveChannelIndex(index);
    					GlobalSettings.setActiveBTChannelIndex(indexBT);
    					
    		        	String isConnectedToBT = "ON";
    		        	GlobalSettings.setIsConnectedToBT(isConnectedToBT);
    					
    					int activeChannelChanged = GlobalSettings.getActiveChannel().getNumber();
    					String activeChannelChangedValue = String.valueOf(activeChannelChanged);

    					((BluetoothSelectActivity) ChannelSelectionDialogFragment.this.getActivity()).getDeviceToConnect(activeChannelChangedValue);
    					((BluetoothSelectActivity) ChannelSelectionDialogFragment.this.getActivity()).startBluetoothTasks();

    					ChannelSelectionDialogFragment.this.dismiss();
    				}
    			});
    			i += 1;
    		}
        } else if(getDeviceSize.equals(deviceLengthThree)){
    		List<Button> buttons = new ArrayList<Button>();
    		buttons.add((Button) view.findViewById(R.id.channelButton1));
    		buttons.add((Button) view.findViewById(R.id.channelButton2));
    		buttons.add((Button) view.findViewById(R.id.channelButton3));
    		
    		//hide buttons less than filtered device array size-KK_031214_1757
    		Button channelButton4 = ((Button) view.findViewById(R.id.channelButton4));
    		channelButton4.setVisibility(View.INVISIBLE);
    		Button channelButton5 = ((Button) view.findViewById(R.id.channelButton5));
    		channelButton5.setVisibility(View.INVISIBLE);
    		Button channelButton6 = ((Button) view.findViewById(R.id.channelButton6));
    		channelButton6.setVisibility(View.INVISIBLE);
    		int i = 0;
    		int b = 0;
    		for (Button button : buttons) {
    			Channel channel = GlobalSettings.getChannels().get(i);
    			button.setText("Channel " + channel.getNumber());
    			button.setTextColor(channel.getAndroidColor(this.getActivity()));
    			button.setSoundEffectsEnabled(false);
    			final int index = i;
    			final int indexBT = b;
    			button.setOnClickListener(new OnClickListener() {
    				@Override
    				public void onClick(View v) {
    					GlobalSettings.setActiveChannelIndex(index);
    					GlobalSettings.setActiveBTChannelIndex(indexBT);
    					
    		        	String isConnectedToBT = "ON";
    		        	GlobalSettings.setIsConnectedToBT(isConnectedToBT);
    					
    					int activeChannelChanged = GlobalSettings.getActiveChannel().getNumber();
    					String activeChannelChangedValue = String.valueOf(activeChannelChanged);

    					((BluetoothSelectActivity) ChannelSelectionDialogFragment.this.getActivity()).getDeviceToConnect(activeChannelChangedValue);
    					((BluetoothSelectActivity) ChannelSelectionDialogFragment.this.getActivity()).startBluetoothTasks();
    					
    					ChannelSelectionDialogFragment.this.dismiss();
    				}
    			});
    			i += 1;
    		}
        } else if(getDeviceSize.equals(deviceLengthFour)){
    		List<Button> buttons = new ArrayList<Button>();
    		buttons.add((Button) view.findViewById(R.id.channelButton1));
    		buttons.add((Button) view.findViewById(R.id.channelButton2));
    		buttons.add((Button) view.findViewById(R.id.channelButton3));
    		buttons.add((Button) view.findViewById(R.id.channelButton4));
    		
    		//hide buttons less than filtered device array size-KK_031214_1757
    		Button channelButton5 = ((Button) view.findViewById(R.id.channelButton5));
    		channelButton5.setVisibility(View.INVISIBLE);
    		Button channelButton6 = ((Button) view.findViewById(R.id.channelButton6));
    		channelButton6.setVisibility(View.INVISIBLE);
    		int i = 0;
    		int b = 0;
    		for (Button button : buttons) {
    			Channel channel = GlobalSettings.getChannels().get(i);
    			button.setText("Channel " + channel.getNumber());
    			button.setTextColor(channel.getAndroidColor(this.getActivity()));
    			button.setSoundEffectsEnabled(false);
    			final int index = i;
    			final int indexBT = b;
    			button.setOnClickListener(new OnClickListener() {
    				@Override
    				public void onClick(View v) {
    					GlobalSettings.setActiveChannelIndex(index);
    					GlobalSettings.setActiveBTChannelIndex(indexBT);
    					
    		        	String isConnectedToBT = "ON";
    		        	GlobalSettings.setIsConnectedToBT(isConnectedToBT);
    					
    					int activeChannelChanged = GlobalSettings.getActiveChannel().getNumber();
    					String activeChannelChangedValue = String.valueOf(activeChannelChanged);

    					((BluetoothSelectActivity) ChannelSelectionDialogFragment.this.getActivity()).getDeviceToConnect(activeChannelChangedValue);
    					((BluetoothSelectActivity) ChannelSelectionDialogFragment.this.getActivity()).startBluetoothTasks();

    					ChannelSelectionDialogFragment.this.dismiss();
    				}
    			});
    			i += 1;
    		}
        } else if(getDeviceSize.equals(deviceLengthFive)){
    		List<Button> buttons = new ArrayList<Button>();
    		buttons.add((Button) view.findViewById(R.id.channelButton1));
    		buttons.add((Button) view.findViewById(R.id.channelButton2));
    		buttons.add((Button) view.findViewById(R.id.channelButton3));
    		buttons.add((Button) view.findViewById(R.id.channelButton4));
    		buttons.add((Button) view.findViewById(R.id.channelButton5));
    		
    		//hide buttons less than filtered device array size-KK_031214_1757
    		Button channelButton6 = ((Button) view.findViewById(R.id.channelButton6));
    		channelButton6.setVisibility(View.INVISIBLE);
    		int i = 0;
    		int b = 0;
    		for (Button button : buttons) {
    			Channel channel = GlobalSettings.getChannels().get(i);
    			button.setText("Channel " + channel.getNumber());
    			button.setTextColor(channel.getAndroidColor(this.getActivity()));
    			button.setSoundEffectsEnabled(false);
    			final int index = i;
    			final int indexBT = b;
    			button.setOnClickListener(new OnClickListener() {
    				@Override
    				public void onClick(View v) {
    					GlobalSettings.setActiveChannelIndex(index);
    					GlobalSettings.setActiveBTChannelIndex(indexBT);
    					
    		        	String isConnectedToBT = "ON";
    		        	GlobalSettings.setIsConnectedToBT(isConnectedToBT);
    					
    					int activeChannelChanged = GlobalSettings.getActiveChannel().getNumber();
    					String activeChannelChangedValue = String.valueOf(activeChannelChanged);
    					
    					((BluetoothSelectActivity) ChannelSelectionDialogFragment.this.getActivity()).getDeviceToConnect(activeChannelChangedValue);
    					((BluetoothSelectActivity) ChannelSelectionDialogFragment.this.getActivity()).startBluetoothTasks();
    					
    					ChannelSelectionDialogFragment.this.dismiss();
    				}
    			});
    			i += 1;
    		}
        } else if(getDeviceSize.equals(deviceLengthSix)){
    		List<Button> buttons = new ArrayList<Button>();
    		buttons.add((Button) view.findViewById(R.id.channelButton1));
    		buttons.add((Button) view.findViewById(R.id.channelButton2));
    		buttons.add((Button) view.findViewById(R.id.channelButton3));
    		buttons.add((Button) view.findViewById(R.id.channelButton4));
    		buttons.add((Button) view.findViewById(R.id.channelButton5));
    		buttons.add((Button) view.findViewById(R.id.channelButton6));
    		int i = 0;
    		int b = 0;
    		for (Button button : buttons) {
    			Channel channel = GlobalSettings.getChannels().get(i);
    			button.setText("Channel " + channel.getNumber());
    			button.setTextColor(channel.getAndroidColor(this.getActivity()));
    			button.setSoundEffectsEnabled(false);
    			final int index = i;
    			final int indexBT = b;
    			button.setOnClickListener(new OnClickListener() {
    				@Override
    				public void onClick(View v) {
    					GlobalSettings.setActiveChannelIndex(index);
    					GlobalSettings.setActiveBTChannelIndex(indexBT);
    					
    		        	String isConnectedToBT = "ON";
    		        	GlobalSettings.setIsConnectedToBT(isConnectedToBT);
    					
    					int activeChannelChanged = GlobalSettings.getActiveChannel().getNumber();
    					String activeChannelChangedValue = String.valueOf(activeChannelChanged);

    					((BluetoothSelectActivity) ChannelSelectionDialogFragment.this.getActivity()).getDeviceToConnect(activeChannelChangedValue);
    					((BluetoothSelectActivity) ChannelSelectionDialogFragment.this.getActivity()).startBluetoothTasks();
    					
    					ChannelSelectionDialogFragment.this.dismiss();
    				}
    			});
    			i += 1;
    		}
        } else if(getDeviceSize.equals(deviceLengthZero) || getDeviceSize == null){
        	((BluetoothSelectActivity) ChannelSelectionDialogFragment.this.getActivity()).noDevices();
        	
    		Button channelButton1 = ((Button) view.findViewById(R.id.channelButton1));
    		channelButton1.setVisibility(View.INVISIBLE);
    		Button channelButton2 = ((Button) view.findViewById(R.id.channelButton2));
    		channelButton2.setVisibility(View.INVISIBLE);
    		Button channelButton3 = ((Button) view.findViewById(R.id.channelButton3));
    		channelButton3.setVisibility(View.INVISIBLE);
    		Button channelButton4 = ((Button) view.findViewById(R.id.channelButton4));
    		channelButton4.setVisibility(View.INVISIBLE);
    		Button channelButton5 = ((Button) view.findViewById(R.id.channelButton5));
    		channelButton5.setVisibility(View.INVISIBLE);
    		Button channelButton6 = ((Button) view.findViewById(R.id.channelButton6));
    		channelButton6.setVisibility(View.INVISIBLE);
        } else {
        	((BluetoothSelectActivity) ChannelSelectionDialogFragment.this.getActivity()).deviceLimitReached();
    		//hide buttons less than filtered device array size-KK_031214_1757
    		Button channelButton1 = ((Button) view.findViewById(R.id.channelButton1));
    		channelButton1.setVisibility(View.INVISIBLE);
    		Button channelButton2 = ((Button) view.findViewById(R.id.channelButton2));
    		channelButton2.setVisibility(View.INVISIBLE);
    		Button channelButton3 = ((Button) view.findViewById(R.id.channelButton3));
    		channelButton3.setVisibility(View.INVISIBLE);
    		Button channelButton4 = ((Button) view.findViewById(R.id.channelButton4));
    		channelButton4.setVisibility(View.INVISIBLE);
    		Button channelButton5 = ((Button) view.findViewById(R.id.channelButton5));
    		channelButton5.setVisibility(View.INVISIBLE);
    		Button channelButton6 = ((Button) view.findViewById(R.id.channelButton6));
    		channelButton6.setVisibility(View.INVISIBLE);
        }
		return view;  
	}

}
