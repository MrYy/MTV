package com.example.yy.dashgraduationdesign.transmission.UDP;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.SparseArray;

import com.example.yy.dashgraduationdesign.Entities.Segment;
import com.example.yy.dashgraduationdesign.Integrity.IntegrityCheck;
import com.example.yy.dashgraduationdesign.util.dipatchers.Bus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

/**
 * Created by zxc on 2016/9/13.
 */


public class UDPChannel extends Thread {
    private final String BROADCAST_IP = "192.168.49.255";

    class ListeningThread extends Thread {
        @Override
        public void run() {
            super.run();
            getMessage();
        }
        private void getMessage() {
            Log.d(TAG, "start listening");
            byte[] message = new byte[1024 * 10];

            try {
                DatagramSocket datagramSocket = new DatagramSocket(serverPort);
                datagramSocket.setBroadcast(true);
                DatagramPacket datagramPacket = new DatagramPacket((message), message.length);

                while (true) {
                    datagramSocket.receive(datagramPacket);
                    String msg = new String(datagramPacket.getData()).trim();
                    JSONObject buffermapJson = new JSONObject(msg);
                    String addr = buffermapJson.getString("address");
                    Log.d(TAG, "addr:  " + addr
                            + "client ip  " + Bus.clientAddr.toString().substring(1) +
                            "  is equal  " + addr.equals(Bus.clientAddr.toString().substring(1)));
                    if (Bus.isOwner){
                        if (addr.equals(Bus.HOST_IP)){
                            Log.d(TAG, "filter from host");
                            continue;
                        }
                    }else{
                        if (addr.equals(Bus.clientAddr.toString().substring(1)))
                        {
                            Log.d(TAG, "filter from client");
                            continue;
                        }
                    }
                    Log.d(TAG, "get msg:  " + msg);
                    handleMessage(msg);
                }
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void handleMessage(String msg) {
            //handle the msg get,turn it into buffer map.
        }
    }



    private static final String TAG = UDPChannel.class.getSimpleName();
    private final int serverPort = 7895;

    private String getBuffermap() {
        String buffermapString;
        SparseArray<Segment> urlmap = IntegrityCheck.getInstance().getUrlMap();
        int size = urlmap.size();
        JSONObject buffermapWithHead = new JSONObject();
        JSONArray buffermapJson = new JSONArray();
        for (int i = 1; i <= size; i++) {
            boolean[] buffermap = urlmap.get(i).getBuffermap();
            if (buffermap == null) break;
            JSONArray arraymap = new JSONArray();
            for (int j = 0; j < buffermap.length; j++) {
                arraymap.put(buffermap[j]);
            }
            buffermapJson.put(arraymap);
        }
        try {
            if (Bus.isOwner) {
                buffermapWithHead.put("address", Bus.HOST_IP);
            } else {
                buffermapWithHead.put("address", Bus.clientAddr.toString().substring(1));
            }
            buffermapWithHead.put("data", buffermapJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        buffermapString = buffermapWithHead.toString();
        Log.d(TAG, "buffer map is:   " + buffermapString);
        return buffermapString;
    }

    private void sendMessage(String message) {
        DatagramSocket s = null;
        try {
            s = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        InetAddress local = null;
        try {
            local = InetAddress.getByName(BROADCAST_IP);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        int msg_len = message.length();
        byte[] messageByte = message.getBytes();
        DatagramPacket p = new DatagramPacket(messageByte, msg_len, local, serverPort);
        try {
            Log.d(TAG, "send message");
            s.send(p);
        } catch (IOException e) {
            e.printStackTrace();
        }
        s.close();
    }



    @Override
    public void run() {
        super.run();
        new ListeningThread().start();
        while (true) {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sendMessage(getBuffermap());
        }
    }
}
