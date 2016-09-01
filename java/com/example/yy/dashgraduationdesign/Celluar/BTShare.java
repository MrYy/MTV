package com.example.yy.dashgraduationdesign.Celluar;

import android.util.Log;

import com.example.yy.dashgraduationdesign.Celluar.GroupCell.GroupCell;
import com.example.yy.dashgraduationdesign.Entities.FileFragment;
import com.example.yy.dashgraduationdesign.Entities.Message;
import com.example.yy.dashgraduationdesign.util.Method;
import com.example.yy.dashgraduationdesign.util.dipatchers.Bus;

/**
 * Created by zxc on 2016/8/29.
 */
public class BTShare implements CellularSharePolicy {
    @Override
    public void announceDisplay(String uri) {
        if(Bus.isOwner){
            //send message
            Message msg = new Message();
            String groupSession = Method.SHA(Bus.userName+ System.currentTimeMillis(),"SHA-256");
            msg.setMessage(Bus.SYSTEM_MESSAGE_SHARE_NETWORK+"http://127.0.0.1:9999"+uri+"~"
                    +groupSession);
            GroupCell.groupSession = groupSession;
            Log.d("TAG", "group session is :" + groupSession);
            Log.d("TAG", msg.getMessage());
            Bus.sendMsgToAll(msg);
        }

    }

    @Override
    public void handleFragment(FileFragment fragment) {
        //BT分享时，下载完一个fragment后不主动分享给任何人。
    }
}
