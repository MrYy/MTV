package com.example.yy.dashgraduationdesign.util.dipatchers;

import android.util.Log;

import com.example.yy.dashgraduationdesign.Entities.FileFragment;
import com.example.yy.dashgraduationdesign.Entities.Message;
import com.example.yy.dashgraduationdesign.Entities.SendTask;
import com.example.yy.dashgraduationdesign.Integrity.IntegrityCheck;

import java.net.InetAddress;

/**
 * Created by zxc on 2016/9/12.
 */
public class MultiPullDispatcher implements Dispatcher {
    private static final String TAG = MultiPullDispatcher.class.getSimpleName();
    private Bus bus;

    public MultiPullDispatcher(Bus bus) {
        new MultiPullMessageHandler(bus).start();
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
                Log.d(TAG, "receive fragment");
                FileFragment ff = msg.getFragment();
                IntegrityCheck.getInstance().insert(ff.getSegmentID(), ff, 0);

                break;
        }
        Bus.getClients().add(mClient);
    }

    @Override
    public void handle(android.os.Message message) {

    }
}
