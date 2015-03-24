package com.steelmantools.smartear;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class RecordingFragment extends Fragment {

//	private static final long maxTime = 90000;
	private static final long maxTime = 91000;
	private static final SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss", Locale.US);

	protected boolean recording;
	protected long millisUntilFinished;
	protected boolean recordingFinished;

	private TextView recordingTimer;
	private TextView timeRemaining;
	private ImageButton stopButton;
	private ImageButton pauseButton;
	private ImageButton saveButton;
	private CountDownTimer countDownTimer;

	@Override
	public void onCreate(Bundle saveButtondInstanceState) {
		super.onCreate(saveButtondInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveButtondInstanceState) {
		View view = inflater.inflate(R.layout.fragment_recording, container, false);
		
//		String recOnRes = "ON";
//		if(recOnRes.equals(GlobalSettings.getIsRecordingOn())){
//			
//		} else {
//			recording = false;
//			recordingFinished = false;
//			millisUntilFinished = 0;
//		}

		recording = false;
		recordingFinished = false;
		millisUntilFinished = 0;

		recordingTimer = (TextView) view.findViewById(R.id.recordingTimer);
		timeRemaining = (TextView) view.findViewById(R.id.timeRemaining);
		stopButton = (ImageButton) view.findViewById(R.id.stopButton);
		stopButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				finishRecording(true);
//				SharedPreferences pref = getActivity().getSharedPreferences("STEELMANpref", 0);
//		        Editor editor = pref.edit();
//		        editor.putString("RECORDONOFF", recordOnOff);
//		        editor.commit();
				String recordOnOff = "OFF";
				String stopRec = "STOPPED";
				GlobalSettings.setIsRecordingOn(recordOnOff);
		        Log.d("REC FRAG","isRecordOnOff = OFF RECFRAG");
		        GlobalSettings.setIsRecordingPaused(stopRec);
		        
		        SharedPreferences pref = getActivity().getSharedPreferences("STEELMANpref", 0);
		        pref.edit().remove("TIMEREMAINING").commit();
		        
		        finishRecording(true);
		        
			}
		});
		pauseButton = (ImageButton) view.findViewById(R.id.pauseButton);
		pauseButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				toggleRecordingPause();
			}
		});
		saveButton = (ImageButton) view.findViewById(R.id.saveButton);
		saveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (GlobalSettings.isRecordingVideo()) {
					finishRecording(true);
				} else {
//					finishRecording(false);
					String recordOnOff = "OFF";
					String stopRec = "STOPPED";
					GlobalSettings.setIsRecordingOn(recordOnOff);
			        Log.d("REC FRAG","isRecordOnOff = OFF RECFRAG");
			        GlobalSettings.setIsRecordingPaused(stopRec);
			        
					finishRecording(true);
					Intent intent = new Intent(RecordingFragment.this.getActivity(), SavedRecordingsActivity.class);
					RecordingFragment.this.getActivity().startActivity(intent);
				}
			}
		});
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		String isPaused = "PAUSED";
		if(isPaused.equals(GlobalSettings.getIsRecordingPaused())){
			toggleRecordingPause();
		}else{
			startRecording(maxTime);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		
		//String timeLeft = timeRemaining.getText().toString();
		SharedPreferences pref = getActivity().getSharedPreferences("STEELMANpref", 0);
        Editor editor = pref.edit();
        String millisConv = Long.toString(millisUntilFinished);
        editor.putString("TIMEREMAINING", millisConv);
        editor.commit();
		
		String recordOnOff = "ON";
		if(recordOnOff.equals(GlobalSettings.getIsRecordingOn())){
			Log.d("REC FRAG","onPause recordOnOff=ON fired");
			finishRecording(false);
//			finishRecording(true);
		} else {
			finishRecording(true);
		}
//		finishRecording(true);
	}

	private void startRecording(long timeLeftToRecord) {
//		String isPaused = "PAUSED";
//		if(isPaused.equals(GlobalSettings.getIsRecordingPaused())){
//			toggleRecordingPause();
//		}else{
		if (!recordingFinished && timeLeftToRecord > 0) {
			recording = true;
			countDownTimer = new CountDownTimer(timeLeftToRecord, 1000) {
				public void onTick(long millisUntilFinished) {
					recordingTimer.setText(timeFormat.format(new Date(maxTime - millisUntilFinished)));
//					timeRemaining.setText("Time Remaining: " + timeFormat.format(new Date(millisUntilFinished + 1000)));
					timeRemaining.setText("Time Remaining: " + timeFormat.format(new Date(millisUntilFinished)));
					RecordingFragment.this.millisUntilFinished = millisUntilFinished;
				}

				public void onFinish() {
					recordingTimer.setText(timeFormat.format(new Date(maxTime)));
					timeRemaining.setText("Time Remaining: " + timeFormat.format(new Date(0)));
					Toast.makeText(RecordingFragment.this.getActivity(), "Recording finished", Toast.LENGTH_SHORT).show();
					RecordingFragment.this.millisUntilFinished = 0;
					finishRecording(true);
				}
			}.start();
		}
		//}
	}

	private void finishRecording(final boolean restartSampling) {
		if (!recordingFinished) {
			recording = false;
			recordingFinished = true;
			countDownTimer.cancel();
			((MainActivity) getActivity()).recordingDone(restartSampling);
			millisUntilFinished = 0;
		}
	}

	private void toggleRecordingPause() {
		if (recordingFinished) {
			Log.d("REC FRAG","toggleRecordingPause recordingFinished FIRED");
			return;
		} else if (recording) {
			recording = false;
			countDownTimer.cancel();
			Log.d("REC FRAG","toggleRecordingPause recording FIRED");
		} else {
//			millisUntilFinished = 2000;
			//startRecording(millisUntilFinished);
	        SharedPreferences pref = getActivity().getSharedPreferences("STEELMANpref", 0);
	        String milliLeft = pref.getString("TIMEREMAINING", " ");
	        if(milliLeft != null && milliLeft.length() > 0 && milliLeft != " "){
		        long millis = Long.parseLong(milliLeft);
		        millisUntilFinished = millis;
				startRecording(millisUntilFinished);
				Log.d("REC FRAG","toggleRecordingPause startRecording FIRED, millisUntilFinished: " + millisUntilFinished);
	        } else {
	        	startRecording(millisUntilFinished);
	        	Log.d("REC FRAG","toggleRecordingPause startRecording FIRED on else, millisUntilFinished: " + millisUntilFinished);
	        }
		}
		((MainActivity) getActivity()).toggleRecordingPause();
	}

}
