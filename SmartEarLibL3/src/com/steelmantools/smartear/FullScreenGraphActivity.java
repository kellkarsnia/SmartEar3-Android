package com.steelmantools.smartear;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class FullScreenGraphActivity extends GraphingActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_graph_screen);
		setup(savedInstanceState);

		new SettingsMenuInitializer(this, null);
	}

	public static class SettingsMenuInitializer {

		public SettingsMenuInitializer(final Activity activity, View root) {
			ImageButton info = (ImageButton) getView(R.id.graphScreenInfoButton, activity, root);
			info.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent i = new Intent(activity, TitledWebviewActivity.class);
					i.putExtra(TitledWebviewActivity.TITLE, activity.getResources().getText(R.string.INFO));
					i.putExtra(TitledWebviewActivity.TARGET_URL, "file:///android_asset/info.html");
					activity.startActivity(i);
				}
			});

			ImageButton equalizer = (ImageButton) getView(R.id.graphScreenEqualizerButton, activity, root);
			equalizer.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					activity.startActivity(new Intent(activity, EqualizerActivity.class));
				}
			});

			ImageButton help = (ImageButton) getView(R.id.graphScreenHelpButton, activity, root);
			help.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent i = new Intent(activity, TitledWebviewActivity.class);
					i.putExtra(TitledWebviewActivity.TITLE, activity.getResources().getText(R.string.HELP));
					i.putExtra(TitledWebviewActivity.TARGET_URL, "file:///android_asset/help.html");
					activity.startActivity(i);
				}
			});
		}
		
		public View getView(int resourceId, Activity activity, View root) {
			if (root != null) {
				return root.findViewById(resourceId);
			}
			return activity.findViewById(resourceId);
		}
	}

}
