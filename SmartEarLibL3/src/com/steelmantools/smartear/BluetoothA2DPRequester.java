package com.steelmantools.smartear;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.bluetooth.BluetoothHeadset;;

/**
 *
 * Copyright 2013 Kevin Coppock
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Sends an asynchronous request to the BluetoothAdapter for an instance of a
 * BluetoothA2dp proxy.
 */
public class BluetoothA2DPRequester implements BluetoothProfile.ServiceListener {
    private Callback mCallback;
    BluetoothProfile bluetoothProfile;

    /**
     * Creates a new instance of an A2DP Proxy requester with the
     * callback that should receive the proxy once it is acquired
     * @param callback the callback that should receive the proxy
     */
    public BluetoothA2DPRequester(Callback callback) {
        mCallback = callback;
    }

    /**
     * Start an asynchronous request to acquire the A2DP proxy. The callback
     * will be notified when the proxy is acquired
     * @param c the context used to obtain the proxy
     * @param adapter the BluetoothAdapter that should receive the request for proxy
     */
//    public void request (Context c, BluetoothAdapter adapter) {
//        adapter.getProfileProxy(c, this, BluetoothProfile.A2DP);
//    }
    public void request (Context c, BluetoothAdapter adapter) {
        adapter.getProfileProxy(c, this, BluetoothProfile.HEADSET);
    }
    
    public void requestUnregister (Context c, BluetoothProfile bluetoothProfile, BluetoothAdapter adapter) {
        //adapter.getProfileProxy(c, this, BluetoothProfile.HEADSET);
    	//BluetoothProfile bluetoothProfile;
//        if (mCallback != null) {
////          mCallback.onA2DPProxyReceived((BluetoothA2dp) bluetoothProfile);
//      	mCallback.onA2DPProxyReceived((BluetoothHeadset) bluetoothProfile);
//        }
        adapter.closeProfileProxy(BluetoothProfile.HEADSET, bluetoothProfile);
    }

    @Override
    public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
        if (mCallback != null) {
//            mCallback.onA2DPProxyReceived((BluetoothA2dp) bluetoothProfile);
        	mCallback.onA2DPProxyReceived((BluetoothHeadset) bluetoothProfile);
        }
    }

//    @Override
//    public void onServiceDisconnected(int i) {
//        //It's a one-off connection attempt; we don't care about the disconnection event.
//    	//adapter.closeProfileProxy(BluetoothProfile.HEADSET, mBluetoothHeadset);
//    }
    
    @Override
    public void onServiceDisconnected(int i) {
        //It's a one-off connection attempt; we don't care about the disconnection event.
    	//adapter.closeProfileProxy(BluetoothProfile.HEADSET, mBluetoothHeadset);
    }

    public static interface Callback {
//        public void onA2DPProxyReceived (BluetoothA2dp proxy);
    	public void onA2DPProxyReceived (BluetoothHeadset proxy);
    }
}
