package com.example.yy.dashgraduationdesign.util.dipatchers;

import com.example.yy.dashgraduationdesign.Entities.Message;

/**
 * Created by zxc on 2016/8/26.
 */
public abstract class MessageHandler extends Thread{
    //receive the system command message and then handle them,reflect to the UI
    //处理接收到的系统消息，比如打开播放器，要求获取某个分片等等。
    @Override
    public void run() {
        super.run();
        while (true) {
            Message message = Bus.getMsg();
            handleMessage(message);
        }
    }

    abstract void handleMessage(Message message);
}
