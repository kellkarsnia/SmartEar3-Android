package com.steelmantools.smartear;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

public class HardwareDialogActivity extends Activity {
	
	public final static String LOG_TAG = HardwareDialogActivity.class.getSimpleName();
	
	final Context context = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hardware_dialog);
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);
 
			// set title
			alertDialogBuilder.setTitle("Missing Required Bluetooth Devices");
 
			// set dialog message
			alertDialogBuilder
				.setMessage("To purchase the required SmartEAR bluetooth devices, please visit jsproductsinc.com or call 1-800-362-7011.")
				.setCancelable(false)
				.setPositiveButton("View Website",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						
						// Open Website
					    Intent intent;

					    try {
					        intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.jsproductsinc.com"));
					        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					        startActivity(intent);
					    } catch (Exception e) {
					        Log.e("Exception Caught", e.toString());
					    }
						// if this button is clicked, close
						// current activity
						HardwareDialogActivity.this.finish();
					}
				  })
				.setNegativeButton("Retry",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						
	     	          	Intent intent = new Intent(HardwareDialogActivity.this, MainActivity.class);
	     	          	startActivity(intent);
						dialog.cancel();
					}
				});
 
				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();
 
				// show it
				alertDialog.show();
			
	}
	
	public void onBackPressed()  
	{  
//	    this.startActivity(new Intent(HardwareDialogActivity.this,HardwareDialogActivity.class));  
	    Toast.makeText(this, "You must choose an option to continue", Toast.LENGTH_LONG).show();

	    return;  
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.hardware_dialog, menu);
		return true;
	}

}
