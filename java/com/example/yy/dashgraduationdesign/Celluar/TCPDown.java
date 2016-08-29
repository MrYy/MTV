package com.example.yy.dashgraduationdesign.Celluar;

import com.example.yy.dashgraduationdesign.Integrity.IntegrityCheck;

import java.io.ByteArrayInputStream;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by zxc on 2016/8/29.
 */
public class TCPDown implements CellularDownPolicy {
    @Override
    public byte[] download(String playist) {
        IntegrityCheck iTC = IntegrityCheck.getInstance();
        int tmpp = Integer.parseInt(playist.substring(0, 1));
        byte[] tmp = iTC.getSegments(tmpp, CellularDown.CellType.GROUP);
        return tmp;

    }

}
