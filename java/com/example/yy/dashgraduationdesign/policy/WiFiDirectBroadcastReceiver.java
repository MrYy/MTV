/*
 WiFi Direct File Transfer is an open source application that will enable sharing 
 of data between Android devices running Android 4.0 or higher using a WiFi direct
 connection without the use of a separate WiFi access point.This will enable data 
 transfer between devices without relying on any existing network infrastructure. 
 This application is intended to provide a much higher speed alternative to Bluetooth
 file transfers. 

 Copyright (C) 2012  Teja R. Pitla
 Contact: teja.pitla@gmail.com

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.example.yy.dashgraduationdesign.policy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.util.Log;

import com.example.yy.dashgraduationdesign.policy.directStatus.Status;
import com.example.yy.dashgraduationdesign.util.Method;
import com.example.yy.dashgraduationdesign.util.dipatchers.Bus;

import java.io.IOException;



/*
Some of this code is developed from samples from the Google WiFi Direct API Guide 
http://developer.android.com/guide/topics/connectivity/wifip2p.html
*/


public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "WIFI DIRECT";
    private WifiP2pManager manager;
    private Channel channel;
    private Context context;
    private Status status;
    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel, Context context, Status status) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.context = context;
        this.status = status;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Check to see if Wi-Fi is enabled and notify appropriate activity
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                Method.display(context,"wifi直连已启用");
                status.supportWifiDirect();
            } else {
                Method.display(context,"正在打开wifi直连");
            }
            
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers
            status.findPeers();
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

        	NetworkInfo networkState = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
        	
        	if(networkState.isConnected())
        	{
                status.connectSuccess();
               //和其他设备连接，获取owner 的ip
                manager.requestConnectionInfo(channel, new WifiP2pManager.ConnectionInfoListener() {
                    @Override
                    public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
                        Log.d("owner", "group owner ip is :" + wifiP2pInfo.groupOwnerAddress.getHostAddress());
                    }
                });
                try {
                    Bus.BROADCAST_IP = Method.getBroadcastAddress(context).toString().substring(1);
                    Log.d(TAG, "broadcast ip:  " + Bus.BROADCAST_IP);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        	else
        	{
                Log.d(TAG, "not connect");
            }
            
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing

        }
    }
}