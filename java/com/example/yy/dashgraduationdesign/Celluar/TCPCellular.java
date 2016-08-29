package com.example.yy.dashgraduationdesign.Celluar;

import com.example.yy.dashgraduationdesign.Entities.FileFragment;
import com.example.yy.dashgraduationdesign.Entities.Message;
import com.example.yy.dashgraduationdesign.util.dipatchers.Bus;

/**
 * Created by zxc on 2016/8/29.
 */
public class TCPCellular implements CellularPolicy {
    @Override
    public void handleFragment(FileFragment fm) {
        Message msgF = new Message();
        msgF.setFragment(fm);
        Bus.sendMsg(msgF);
    }
}
