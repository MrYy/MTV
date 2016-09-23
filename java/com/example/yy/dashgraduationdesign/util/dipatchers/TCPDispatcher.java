package com.example.yy.dashgraduationdesign.util.dipatchers;

import android.util.Log;

import com.example.yy.dashgraduationdesign.Entities.FileFragment;
import com.example.yy.dashgraduationdesign.Entities.Message;
import com.example.yy.dashgraduationdesign.Entities.SendTask;
import com.example.yy.dashgraduationdesign.Integrity.IntegrityCheck;
import com.example.yy.dashgraduationdesign.util.Method;

import java.net.InetAddress;

/**
 * Created by zxc on 2016/8/26.
 */
public class TCPDispatcher implements Dispatcher {
    //it is suitable for BT and TCP now,
    //because in our first version,BT's seeder can't receive any fragment
    private static final String TAG = TCPDispatcher.class.getSimpleName();
    private Bus bus;
    public TCPDispatcher(Bus bus) {
        new TCPMessageHandler(bus).start();
    }
    @Override
    public void dispatch(Message msg, InetAddress mClient) {
        Bus.getClients().remove(mClient);
        switch (msg.getType()) {
            case Message:
                Log.d(TAG, "receive message");
                Log.d(TAG, msg.getMessage());
                String mName;
                if (!(mName = msg.getName()).equals("")) {
                    if (msg.getMessage().equals(mName)) {
                        Bus.onLineUsers.add(mName);
                        msg.setMessage("hi 你好啊我是 "+msg.getName());
                    }
                }
                Bus.insertReceiveMQ(msg);
                if (Bus.isOwner) {
                    if (Bus.getClients().size() > 0) {
                        SendTask sendTask = new SendTask();
                        sendTask.setClients(Bus.getClients());
                        sendTask.setMsg(msg);
                        Log.d(TAG, "forward message:" + msg.getMessage());
                        Bus.sendMessageQueue.add(sendTask);
                    }
                }

                break;
            case Fragment:
                FileFragment ff = msg.getFragment();
                Method.record(ff,"bt_oral","wifi interface");
                Log.d("insert fragment", String.valueOf(ff.getSegmentID()) + " " + String.valueOf(ff.getStartIndex()));
//                    Log.d("check integrity", String.valueOf(IntegrityCheck.getInstance().getSeg(ff.getSegmentID()).checkIntegrity()));
                IntegrityCheck.getInstance().insert(ff.getSegmentID(), ff, 0);
                if (Bus.isOwner) {
                    if (Bus.getClients().size() > 0) {
                        Message mMsg = new Message();
                        mMsg.setFragment(ff);
                        SendTask sendTask = new SendTask();
                        sendTask.setMsg(mMsg);
                        sendTask.setClients(Bus.getClients());
                        Bus.taskMessageQueue.add(sendTask);
                    }
                }
                break;
        }
        Bus.getClients().add(mClient);
    }

    @Override
    public void handle(android.os.Message message) {

    }
}
