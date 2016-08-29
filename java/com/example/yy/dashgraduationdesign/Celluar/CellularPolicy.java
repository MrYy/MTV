package com.example.yy.dashgraduationdesign.Celluar;

import com.example.yy.dashgraduationdesign.Entities.FileFragment;

/**
 * Created by zxc on 2016/8/29.
 */
public interface CellularPolicy {
    //蜂窝下载一个分片和如何分享策略
    void handleMessage(FileFragment fragment);
}
