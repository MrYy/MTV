package com.example.yy.dashgraduationdesign.Celluar;

import com.example.yy.dashgraduationdesign.Entities.FileFragment;

/**
 * Created by zxc on 2016/8/29.
 */
public class BTShare implements CellularSharePolicy {
    @Override
    public void handleFragment(FileFragment fragment) {
        //BT分享时，下载完一个fragment后不主动分享给任何人。
    }
}
