package com.steelmantools.smartear;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.steelmantools.smartear.logging.Log;

public class SplashScreenActivity extends Activity {

	public static final String LOG_TAG = SplashScreenActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		GlobalSettings.init(this);
		
		setContentView(R.layout.activity_splash_screen);

		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				try {
					Thread.sleep(Config.SPLASH_DISPLAY_TIME);
				} catch (InterruptedException e) {
					Log.e(LOG_TAG, "Error waiting on splash screen", e);
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void unused) {
				startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
				finish();
			}
		}.execute();
	}

}
