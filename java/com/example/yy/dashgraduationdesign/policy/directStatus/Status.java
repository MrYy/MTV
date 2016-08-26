package com.example.yy.dashgraduationdesign.policy.directStatus;

/**
 * Created by zxc on 2016/8/4.
 */
public interface Status {
    //初始化方法
     void supportWifiDirect();

     void findPeers();

     void connectSuccess();
}
