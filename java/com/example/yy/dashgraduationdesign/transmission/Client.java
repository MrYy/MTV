package com.example.yy.dashgraduationdesign.transmission;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;


import com.example.yy.dashgraduationdesign.Entities.FileFragment;
import com.example.yy.dashgraduationdesign.Entities.Message;
import com.example.yy.dashgraduationdesign.Entities.SendTask;
import com.example.yy.dashgraduationdesign.policy.directStatus.IsOwner;
import com.example.yy.dashgraduationdesign.util.dipatchers.Bus;
import com.example.yy.dashgraduationdesign.util.Method;
import com.example.yy.dashgraduationdesign.util.MyException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by zxc on 2016/6/15.
 */
public class Client implements Runnable {
    private InetAddress remoteAddress;
    private int remotePort;
    private static final String TAG = Client.class.getSimpleName();
    private int index;
    private Context context;
    public Client(InetAddress remoteAddress, int remotePort, Context context) {
        this.remoteAddress = remoteAddress;
        this.remotePort = remotePort;
        this.context = context;
    }

    @Override
    public void run() {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        SocketChannel sc = null;
        SelectionKey mKey = null;
        Log.d(TAG, "try to connect");
        Message nameMsg = new Message();
        nameMsg.setName(Bus.userName);
        nameMsg.setMessage(Bus.userName);
        Bus.sendMsgToAll(nameMsg);
        try {
            sc = SocketChannel.open();
            sc.connect(new InetSocketAddress(remoteAddress.getHostAddress(), remotePort));
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "已连接",Toast.LENGTH_SHORT).show();
                }
            });
            Bus.clientAddr = sc.socket().getLocalAddress();
            sc.configureBlocking(false);
            sc.socket().getTcpNoDelay();
            Selector selector = Selector.open();
            sc.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
            while (true) {
                int selected = selector.select();
                if (selected == 0) continue;
                Set<SelectionKey> mKeys = selector.selectedKeys();
                Iterator ite = mKeys.iterator();
                while (ite.hasNext()) {
                    mKey = (SelectionKey) ite.next();
                    if (mKey.isReadable()) {
                        SocketChannel mSc = (SocketChannel) mKey.channel();
                        Method.read(mSc);
                    } else if (mKey.isWritable()) {
                        SocketChannel mSc = (SocketChannel) mKey.channel();
                        try {
                            while (!Bus.sendMessageQueue.isEmpty()) {
                                SendTask sendTask = Bus.sendMessageQueue.poll();
                                Message msg = sendTask.getMsg();
                                msg.setName(Bus.userName);
                                Method.send(msg, mSc);
                            }
                            while (!Bus.taskMessageQueue.isEmpty()) {
                                //发送报文
                                FileFragment ff = Bus.taskMessageQueue.poll().getMsg().getFragment();
                                Message msgObj = new Message();
                                msgObj.setFragment(ff);
                                Method.send(msgObj, mSc);
                            }
                        } catch (MyException e) {
                            Log.d(TAG, "catch");
                        }
                    }
                    ite.remove();
                }
            }
        } catch (ConnectException ce) {
            ExecutorService es = Executors.newFixedThreadPool(1);
            try {
                es.execute(new Client(InetAddress.getByName(IsOwner.ip), 12345,context));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
    }
}
