package com.steelmantools.smartear;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

public class ChannelAssignmentActivity extends FragmentActivity {

	private ListView channelsListView;
	private ImageView channelsReset;
	private int lastPosition;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_channels_screen);

		channelsReset = (ImageView) findViewById(R.id.channelsReset);
		channelsReset.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				GlobalSettings.resetChannels();
				refreshAdapter();
			}
		});

		channelsListView = (ListView) findViewById(R.id.channelsListView);
		channelsListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				showChannelDialog(position);
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		refreshAdapter();
	}

	public void refreshAdapter() {
		channelsListView.setAdapter(new ChannelAssignmentAdapter(this, GlobalSettings.getChannels()));
	}
	
	public void showChannelDialog(int position) {
		lastPosition = position;
		showChannelDialog();
	}
	
	public void showChannelDialog() {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Fragment prev = fm.findFragmentByTag("channelDialog");
		if (prev != null) {
			ft.remove(prev);
		}
		ChannelAssignmentDialogFragment newFragment = ChannelAssignmentDialogFragment.newInstance(lastPosition);
		newFragment.show(ft, "channelDialog");
	}
	
	public void createNewChannelSelected() {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Fragment prev = fm.findFragmentByTag("channelNameDialog");
		if (prev != null) {
			ft.remove(prev);
		}
		ChannelNameDialogFragment newFragment = ChannelNameDialogFragment.newInstance(lastPosition);
		newFragment.show(ft, "channelNameDialog");
	}

	public void doPositiveClick() {
		refreshAdapter();
	}

	public void doNegativeClick() {
		// no action
	}

}
