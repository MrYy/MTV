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

class UDPHandler extends Handler {
    public UDPHandler(Looper looper) {
        super(looper);
    }
}
public class UDPChannel extends Thread{
    private UDPHandler handler ;
    private static final String TAG = UDPChannel.class.getSimpleName();
    private final int serverPort = 7895;

    //    public UDPChannel(UDPHandler handler) {
//        Looper.prepare();
//        Looper.loop();
//        start();
//    }
    private String getBuffermap() {
        String buffermapString;
        SparseArray<Segment> urlmap = IntegrityCheck.getInstance().getUrlMap();
        int size = urlmap.size();
        JSONObject buffermapJson = new JSONObject();
        for (int i =1;i<=size;i++) {
            boolean[] buffermap = urlmap.get(i).getBuffermap();
            JSONArray arraymap = new JSONArray();
            for (int j =0;j<buffermap.length;j++) {
                arraymap.put(buffermap[j]);
            }
            try {
                buffermapJson.put(String.valueOf(i), arraymap);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        buffermapString = buffermapJson.toString();
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
            local = InetAddress.getByName("192.168.49.255");
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

    private void getMessage() {
        Log.d(TAG, "start listening");
        byte[] message = new byte[100];

        try {
            DatagramSocket datagramSocket = new DatagramSocket(serverPort);
            datagramSocket.setBroadcast(true);
            DatagramPacket datagramPacket = new DatagramPacket((message), message.length);

            while (true) {
                datagramSocket.receive(datagramPacket);
                String msg = new String(datagramPacket.getData()).trim();
                Log.d(TAG, "get msg:  " + msg);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void run() {
        super.run();
        if (!Bus.isOwner)
            getMessage();
        else
            while (true) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sendMessage(getBuffermap());
            }
    }
}
