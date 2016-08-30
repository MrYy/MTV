package com.example.yy.dashgraduationdesign.Celluar;

import com.example.yy.dashgraduationdesign.Integrity.IntegrityCheck;
import com.example.yy.dashgraduationdesign.util.dipatchers.Bus;

/**
 * Created by zxc on 2016/8/29.
 */
public class BTDown implements CellularDownPolicy {
    //BT下载时，seed GROUP,其余用户不下载，通过wifiMore.
    @Override
    public byte[] download(String playlist) {
        IntegrityCheck iTC = IntegrityCheck.getInstance();
        int tmpp = Integer.parseInt(playlist.substring(0, 1));
        if (Bus.isOwner)
            return iTC.getSegments(tmpp, CellularDown.CellType.GROUP);
        else {
            return iTC.getSegments(tmpp, CellularDown.CellType.WiFiMore);
        }
    }
}
