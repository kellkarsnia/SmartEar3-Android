package com.steelmantools.smartear;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

public class TitledWebviewActivity extends Activity {

	public static final String TITLE = "TITLE";
	public static final String TARGET_URL = "TARGET_URL";
	
	private WebView webView;
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_titled_webview);
		
		if (savedInstanceState == null) {
			savedInstanceState = getIntent().getExtras();
		}
		
		String title = savedInstanceState.getString(TITLE);
		String targetUrl = savedInstanceState.getString(TARGET_URL);

		TextView titleView = (TextView) findViewById(R.id.header_title);
		titleView.setText(title);
		
		webView = (WebView) findViewById(R.id.mainWebView);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		webView.loadUrl(targetUrl);
		webView.setBackgroundColor(0x00000000);
	}
}
