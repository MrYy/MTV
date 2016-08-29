package com.example.yy.dashgraduationdesign.Celluar;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by zxc on 2016/8/29.
 */
public interface CellularDownPolicy {
    //蜂窝下载策略，比如TCP每个用户都要参与下载
    //如果是BT仅仅种子用户参与下载
    byte[] download(String playlist);
}
