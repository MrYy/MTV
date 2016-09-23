package com.example.yy.dashgraduationdesign.Celluar;

import com.example.yy.dashgraduationdesign.Integrity.IntegrityCheck;

/**
 * Created by zxc on 2016/9/12.
 * full version of bt, compared to the old version only pull from one seeder.
 * This version will contains multiple seeders, and buffer map shared with UDP.
 */
public class MultiPullDown implements CellularDownPolicy {
    @Override
    public byte[] download(String playlist) {
        IntegrityCheck iTC = IntegrityCheck.getInstance();
        if (playlist.length() == 0) return new byte[0];
        int tmpp = Integer.parseInt(playlist.substring(0, 1));
        byte[] tmp = iTC.getSegments(tmpp, CellularDown.CellType.RandomCell);
        return tmp;

    }
}
