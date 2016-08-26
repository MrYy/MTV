package com.example.yy.dashgraduationdesign.util.dipatchers;

import android.os.Message;

import java.net.InetAddress;

/**
 * Created by zxc on 2016/8/25.
 */
public interface Dispatcher {
    //分发消息
    void dispatch(com.example.yy.dashgraduationdesign.Entities.Message message, InetAddress mClient);
    //处理消息
    void handle(Message message);
}
