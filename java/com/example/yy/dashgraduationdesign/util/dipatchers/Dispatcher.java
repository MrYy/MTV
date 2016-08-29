package com.example.yy.dashgraduationdesign.util.dipatchers;

import android.os.Message;

import java.net.InetAddress;

/**
 * Created by zxc on 2016/8/25.
 */
public interface Dispatcher {
    //receive the message from wifi,and decide whether to send it to another client,or just save it you self.
    //接收到通过wifi传输的封装的消息后，决定如何分发，是否转发等等。
    //分发消息
    void dispatch(com.example.yy.dashgraduationdesign.Entities.Message message, InetAddress mClient);
    //处理消息
    void handle(Message message);
}
