package com.steelmantools.smartear.audio;
 
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
 
 
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
 
public class BTConnect{
  
  private static final String TAG = "Bluetooth Command Connect";
  private BluetoothAdapter btAdapter = null;
  private BluetoothSocket btSocket = null;
  private OutputStream outStream = null;
  private InputStream inStream = null;
  
  private String address = null;
  
  // Get SSP Service
  private static final UUID MY_UUID =
//      UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
//	      UUID.fromString("0000111f-0000-1000-8000-00805f9b34fb");
//		  UUID.fromString("00001112-0000-1000-8000-00805f9b34fb");
		  UUID.fromString("00001112-0000-1000-8000-00805f9b34fb");
 
  public BTConnect(String address){
	  this.address = address;
  }
 
  public void Start() {
 
    Log.d(TAG,"...Attempting client connect...");
    btAdapter = BluetoothAdapter.getDefaultAdapter();
 
    // Set up a pointer to the remote node using it's address.
    BluetoothDevice device = btAdapter.getRemoteDevice(address);
    Log.d(TAG,"getRemoteDevice: " + address);
 
    try {
//      btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
    	btSocket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
    	Log.d(TAG,"createInsecureRfcommSocketToServiceRecord FIRED");
    } catch (IOException e) {
      Log.e("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
    }
 
    // Establish the connection.  This will block until it connects.
    try {
      btSocket.connect();
      Log.d(TAG,"...Connection established and data link opened...");
    } catch (IOException e) {
    	Log.e(TAG,"In onResume() and unable to connect" + e.getMessage() + ".");
      try {
        btSocket.close();
      } catch (IOException e2) {
    	  Log.e(TAG,"In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
      }
    }
 
    // Create a data stream so we can talk to server.
    Log.d(TAG,"\n...Sending message to server...");
 
    try {
      outStream = btSocket.getOutputStream();
      Log.d(TAG,"\n Get outputstream FIRED");
    } catch (IOException e) {
    	Log.e(TAG, "In onResume() and output stream creation failed:" + e.getMessage() + ".");
    }
 
    String message = "Hello from Android.\n";
    byte[] msgBuffer = message.getBytes();
    try {
      outStream.write(msgBuffer);
    } catch (IOException e) {
      String msg = "In onResume() and an exception occurred during write: " + e.getMessage();
      msg = msg +  ".\n\nCheck that the SPP UUID: " + MY_UUID.toString() + " exists on server.\n\n";
       
      Log.e(TAG, msg);      
    }
  }
 
  public void Pause() {
 
	  Log.d(TAG,"\n...In onPause()...");
 
    if (outStream != null) {
      try {
        outStream.flush();
      } catch (IOException e) {
    	  Log.e(TAG, "In onPause() and failed to flush output stream: " + e.getMessage() + ".");
      }
    }
 
    try     {
      btSocket.close();
    } catch (IOException e2) {
    	Log.e(TAG, "In onPause() and failed to close socket." + e2.getMessage() + ".");
    }
  }
  
  public void resetBluetooth(){
	  if (inStream != null){
          try {inStream.close();} catch (Exception e) {
        	  Log.d(TAG,"inStream.close");
          }
          inStream = null;
	  }
	  if (outStream != null){
          try {outStream.close();} catch (Exception e) {
        	  Log.d(TAG,"outStream.close");
          }
          outStream = null;
	  }
	  if (btSocket != null){
          try {btSocket.close();} catch (Exception e) {
        	  Log.d(TAG,"btSocket.close");
          }
          btSocket = null;
	  }
  }
 
 
   
   
}