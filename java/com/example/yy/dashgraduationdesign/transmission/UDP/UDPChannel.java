package com.example.yy.dashgraduationdesign.transmission.UDP;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.yy.dashgraduationdesign.util.dipatchers.Bus;

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

//    public UDPChannel(UDPHandler handler) {
//        Looper.prepare();
//        Looper.loop();
//        start();
//    }

    private void sendMessage() {
        String message = "hello";
        int server_port = 7894;

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
        DatagramPacket p = new DatagramPacket(messageByte, msg_len, local, 7895);
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
        int port = 7895;
        byte[] message = new byte[100];

        try {
            DatagramSocket datagramSocket = new DatagramSocket(port);
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
        if (Bus.isOwner)
            getMessage();
        else
            while (true) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sendMessage();
            }

    }
}
