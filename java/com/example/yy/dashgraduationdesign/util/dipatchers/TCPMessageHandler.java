package com.example.yy.dashgraduationdesign.util.dipatchers;

import android.util.Log;

import com.example.yy.dashgraduationdesign.Celluar.GroupCell.GroupCell;
import com.example.yy.dashgraduationdesign.Entities.FileFragment;
import com.example.yy.dashgraduationdesign.Entities.Message;
import com.example.yy.dashgraduationdesign.Integrity.IntegrityCheck;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zxc on 2016/8/25.
 */
public class TCPMessageHandler extends MessageHandler {
    private ExecutorService threadPool = Executors.newFixedThreadPool(1);
    private Bus bus;
    public TCPMessageHandler(Bus bus) {
        this.bus = bus;
    }
    @Override
    void handleMessage(Message message) {
        if (message != null) {
            Log.d("ChatFragment", "receive message:" + String.valueOf(message.getMessage()));
            String msgR = message.getMessage();
            if (msgR.startsWith(Bus.SYSTEM_MESSAGE_SHARE_NETWORK)) {
                android.os.Message msg = new android.os.Message();
                msg.what = 1;
                String[] infos = msgR.split("~");
                msg.obj = infos[1];
                GroupCell.groupSession = infos[2];
                bus.handle(msg);
//                    threadPool.execute(this);
            } else if (msgR.startsWith(Bus.SYSTEM_MESSAGE)) {
                int miss = Integer.parseInt(msgR.split("~")[2]);
                int url = Integer.parseInt(msgR.split("~")[1]);
                try {
                    Message msg = new Message();
                    FileFragment frag = IntegrityCheck.getInstance().getSeg(url).getFragment(miss);
                    if(frag == null) return;
                    msg.setFragment(frag);
                    Bus.sendMsgTo(msg, InetAddress.getByName("192.168.49.1"));
                } catch (FileFragment.FileFragmentException e) {
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
