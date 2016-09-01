package com.example.yy.dashgraduationdesign.Celluar;

import com.example.yy.dashgraduationdesign.Entities.FileFragment;

/**
 * Created by zxc on 2016/8/29.
 */
public interface CellularSharePolicy {
    void announceDisplay(String uri);
    //蜂窝下载一个分片后如何分享策略
    //after you download a piece from server,decide how to send to others ,whether or not .
    void handleFragment(FileFragment fragment);
}
