package com.example.yy.dashgraduationdesign.Celluar;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by zxc on 2016/8/29.
 */
public interface CellularDownPolicy {
    //蜂窝下载策略，比如TCP每个用户都要参与下载
    //如果是BT仅仅种子用户参与下载
    //when the middleware is visited , decide how to download.
    //for example ,if you are a client of TCPers,then every one of you should download on piece from server.
    //on the other wise ,if you are one of BTers ,you should download from server as a seeders,or just send a wifimore to other clients.
    byte[] download(String playlist);
}
