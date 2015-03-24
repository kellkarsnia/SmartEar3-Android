package com.steelmantools.smartear;

import java.util.List;
import java.util.Random;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.steelmantools.smartear.model.SavedRecording;

public class SavedRecordingsAdapter extends BaseAdapter {

	private SavedRecordingsActivity activity;
	private List<SavedRecording> recordings;
	private LayoutInflater layoutInflater;

	public SavedRecordingsAdapter(SavedRecordingsActivity activity, List<SavedRecording> recordings) {
		this.activity = activity;
		layoutInflater = LayoutInflater.from(activity);
		this.recordings = recordings;
	}

	static class ViewHolder {
		LinearLayout rootLayout;
		ImageButton playButton;
		TextView recordingName;
		TextView recordingTime;
		TextView recordingDate;
		TextView recordingLength;
		TextView recordingType;
		Button editButton;
	}

	@Override
	public int getCount() {
		return recordings.size();
	}

	@Override
	public Object getItem(int position) {
		return recordings.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.item_saved_recording, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.rootLayout = (LinearLayout) convertView.findViewById(R.id.rootLayout);
			viewHolder.editButton = (Button) convertView.findViewById(R.id.editButton);
			viewHolder.playButton = (ImageButton) convertView.findViewById(R.id.playButton);
			viewHolder.recordingDate = (TextView) convertView.findViewById(R.id.recordingDate);
			viewHolder.recordingName = (TextView) convertView.findViewById(R.id.recordingName);
			viewHolder.recordingTime = (TextView) convertView.findViewById(R.id.recordingTime);
			viewHolder.recordingLength = (TextView) convertView.findViewById(R.id.recordingLength);
			viewHolder.recordingType = (TextView) convertView.findViewById(R.id.recordingType);
			convertView.setTag(viewHolder);
		}

		final SavedRecording recording = recordings.get(position);
		final ViewHolder holder = (ViewHolder) convertView.getTag();
		holder.rootLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.itemClicked(holder.rootLayout, position);
			}
		});
		if (activity.selectedRecording != null && activity.selectedRecording.getFile().getAbsolutePath().equals(recording.getFile().getAbsolutePath())) {
			holder.rootLayout.setBackgroundResource(R.color.red_overlay);
		} else {
			holder.rootLayout.setBackgroundResource(android.R.color.transparent);
		}
		holder.editButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentManager fm = activity.getSupportFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				Fragment prev = fm.findFragmentByTag("editRecordingDialog");
				if (prev != null) {
					ft.remove(prev);
				}
				EditRecordingDialogFragment newFragment = EditRecordingDialogFragment.newInstance(recording);
				newFragment.show(ft, "editRecordingDialog");
			}
		});
 		holder.playButton.setOnClickListener(new OnClickListener() {
 			@Override
 			public void onClick(View v) {
//				Intent intent = new Intent();
//				intent.setAction(android.content.Intent.ACTION_VIEW);
				String mediaType;
				Intent intent = new Intent(activity, GraphPlaybackActivity.class);
 				if (recording.isVideo()) {
 					intent.setDataAndType(Uri.fromFile(recording.getFile()), "video/*");
 				} else {
 					intent.setDataAndType(Uri.fromFile(recording.getFile()), "audio/*");
 				}
 				activity.startActivity(intent);
//				((SavedRecordingsActivity) recordingsListView.getAdapter()).notifyDataSetChanged();
//				GraphPlaybackActivity.onRecordClick(recording.getFile());
 				GraphPlaybackActivity.onRecordClick(recording.getFile(),recording.getFile().getName());

//				if (recording.isVideo()) {
//					intent.setDataAndType(Uri.fromFile(recording.getFile()), "video/*");
//				} else {
//					intent.setDataAndType(Uri.fromFile(recording.getFile()), "audio/*");
//				}
//				activity.startActivity(intent);
//				Intent intent = new Intent();
//				intent.setAction(android.content.Intent.ACTION_VIEW);
//				if (recording.isVideo()) {
//					intent.setDataAndType(Uri.fromFile(recording.getFile()), "video/*");
//				} else {
//					intent.setDataAndType(Uri.fromFile(recording.getFile()), "audio/*");
//				}
//				activity.startActivity(intent);
 				
 //				Intent intent = new Intent();
 //				activity.setIntent(new Intent(activity, GraphPlaybackActivity.class));

				}
		});

		AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				return recording.getLength(activity);
			}

			protected void onPostExecute(String result) {
				holder.recordingLength.setText(result);
			}
		};
		task.execute();
		
	    final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	    final int N = alphabet.length();
	    Random r = new Random();
	    for (int i = 0; i < 50; i++) {
	        System.out.print(alphabet.charAt(r.nextInt(N)));
	    }
	    //int len = 4;
		
		
		holder.recordingType.setText(recording.getRecordingTypeText(activity));
		holder.recordingDate.setText(recording.getCreationDate());
		

		holder.recordingName.setText(recording.getFile().getName());
		//holder.recordingName.setText("CH: " + activeChannel + "_" + randomString(4) + "_" + recording.getFile().getName());
		holder.recordingTime.setText(recording.getCreationTime());

		return convertView;
	}
//    String randomString(int len) 
//    {
//	    final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
//	    Random r = new Random();
//        StringBuilder sb = new StringBuilder( len );
//        for( int i = 0; i < len; i++ ) 
//           sb.append( alphabet.charAt( r.nextInt(alphabet.length()) ) );
//        return sb.toString();
//    }
	
}