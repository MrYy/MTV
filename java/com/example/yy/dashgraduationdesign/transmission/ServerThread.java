package com.example.yy.dashgraduationdesign.transmission;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.yy.dashgraduationdesign.Entities.FileFragment;
import com.example.yy.dashgraduationdesign.Entities.Message;
import com.example.yy.dashgraduationdesign.Entities.SendTask;
import com.example.yy.dashgraduationdesign.util.dipatchers.Bus;
import com.example.yy.dashgraduationdesign.util.Method;
import com.example.yy.dashgraduationdesign.util.MyException;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by zxc on 2016/6/15.
 */
public class ServerThread extends Thread {
    public static final String TAG = ServerThread.class.getSimpleName();
    private InetAddress ip;
    private Context context;
    private Set<InetAddress> clients;
    private int count = 0;

    public ServerThread(InetAddress ip, Context context) {
        clients = new HashSet<InetAddress>();
        this.ip = ip;
        this.context = context;
    }

    @Override
    public void run() {
        super.run();
        Log.d(TAG, "server is running");
        InetSocketAddress addr = new InetSocketAddress(ip.getHostAddress(), 12345);

        try {
            Selector selector = Selector.open();
            ServerSocketChannel ssc = ServerSocketChannel.open();
            ssc.configureBlocking(false);
            ssc.socket().bind(addr);
            ssc.register(selector, SelectionKey.OP_ACCEPT);
            while (true) {
                int readChannel = selector.select();
                if (readChannel == 0) continue;
                Set<SelectionKey> selectedChannel = selector.selectedKeys();
                Iterator ite = selectedChannel.iterator();
                while (ite.hasNext()) {
                    SelectionKey mKey = (SelectionKey) ite.next();
                    if (mKey.isAcceptable()) {
                        Log.d(TAG, "ap accept new socket");
                        SocketChannel sc = ((ServerSocketChannel) mKey.channel()).accept();
                        InetAddress clientAddr = sc.socket().getInetAddress();
                        Log.d(TAG, "client address:" + clientAddr.toString());
                        clients.add(clientAddr);
                        Bus.setClients(clients);
                        Log.d(TAG, "server thread set:" + String.valueOf(clients.size()) + ":" + clients);
                        Log.d(TAG, "view video set :" + String.valueOf(Bus.getClients().size()) + Bus.getClients());
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "已连接", Toast.LENGTH_SHORT).show();
                            }
                        });
                        sc.configureBlocking(false);
                        sc.register(mKey.selector(), SelectionKey.OP_WRITE | SelectionKey.OP_READ);
                        Iterator<String> iterator = Bus.onLineUsers.iterator();
                        while (iterator.hasNext()) {
                            Message nameMsg = new Message();
                            String mName = iterator.next();
                            nameMsg.setName(mName);
                            nameMsg.setMessage(mName);
                            Bus.sendMsgToAll(nameMsg);
                        }
                    } else if (mKey.isReadable()) {
                        SocketChannel mSc = (SocketChannel) mKey.channel();
                        Method.read(mSc);
                    }
                    else if (mKey.isWritable()) {
                        SocketChannel sc = (SocketChannel) mKey.channel();
                        InetAddress mRemoteAddr = sc.socket().getInetAddress();
                        sc.socket().setTcpNoDelay(true);
                        try {
                            if(!Bus.sendMessageQueue.isEmpty()) {
                                SendTask sendTask = Bus.sendMessageQueue.peek();
                                Message msg =sendTask .getMsg();

                                count++;
                                if (count < 15) {
                                                                    Log.d(TAG, "msg type:"+ String.valueOf(msg.getType())+"remote addr:" + String.valueOf(mRemoteAddr) + " setAddr:" + sendTask.getmClients()+
                                " set size:"+ String.valueOf(sendTask.getmClients().size())
                                +" msg:"+msg.getMessage()+" msg queue:"+ String.valueOf(Bus.sendMessageQueue.size()));
                                }
                                if (sendTask.getmClients().contains(mRemoteAddr)) {
                                    msg.setName(Bus.userName);
                                    Method.send(msg, sc);
                                    sendTask.getmClients().remove(mRemoteAddr);
                                }
                                if (sendTask.getmClients().size() == 0) {
                                    Bus.sendMessageQueue.poll();
                                }
                            }
//                            Log.d(TAG, "after message"+" queue size "+String.valueOf(ViewVideoActivity.sendMessageQueue.size()));

                            if(!Bus.taskMessageQueue.isEmpty()) {
                                //发送报文
                                SendTask sendTask = Bus.taskMessageQueue.peek();
                                Message msg = sendTask.getMsg();
                                if (sendTask.getmClients().contains(mRemoteAddr)) {
                                    FileFragment ff = msg.getFragment();
                                    Message msgObj = new Message();
                                    msgObj.setFragment(ff);
                                    Method.send(msgObj, sc);
                                    sendTask.getmClients().remove(mRemoteAddr);
                                }
                                if (sendTask.getmClients().size() == 0) {
                                    Bus.taskMessageQueue.poll();
                                }
                            }
//                            Log.d(TAG, "after task");

                        } catch (MyException e) {
                            Log.d(TAG, "catch");
                        }
//                        Log.d(TAG, "finish writing");
                    }
                    ite.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }

    }
}
