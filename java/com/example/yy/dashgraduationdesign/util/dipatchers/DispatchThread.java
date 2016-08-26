package com.example.yy.dashgraduationdesign.util.dipatchers;

import android.util.Log;

import com.example.yy.dashgraduationdesign.Celluar.GroupCell.GroupCell;
import com.example.yy.dashgraduationdesign.Entities.Message;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zxc on 2016/8/25.
 */
public class DispatchThread extends Thread {
    private ExecutorService threadPool = Executors.newFixedThreadPool(1);
    private Bus bus = Bus.Singleton.Instance.getInstance();
    @Override
    public void run() {
        super.run();
        while(true) {
            Message message = Bus.getMsg();
            if(message!=null) {
                Log.d("ChatFragment", "receive message:" + String.valueOf(message.getMessage()));
                String msgR = message.getMessage();
                if (msgR.equals(Bus.SYSTEM_MESSAGE_SHARE_LOCAL)) {
                    android.os.Message msg = new android.os.Message();
                    msg.what = 1;
                    bus.handle(msg);
                    threadPool.execute(this);
                    return;
                } else if (msgR.startsWith(Bus.SYSTEM_MESSAGE_SHARE_NETWORK)) {
                    android.os.Message msg = new android.os.Message();
                    msg.what = 2;
                    String[] infos = msgR.split("~");
                    msg.obj = infos[1];
                    GroupCell.groupSession = infos[2];
                    bus.handle(msg);
                    threadPool.execute(this);
                    return;
                } else if (msgR.startsWith(Bus.SYSTEM_MESSAGE)) {
                    android.os.Message msg = new android.os.Message();
                    msg.what = 4;
                    msg.obj = msgR.split("~")[1];
                    bus.handle(msg);
                    threadPool.execute(this);
                    return;
                }
            }

        }

    }
}
