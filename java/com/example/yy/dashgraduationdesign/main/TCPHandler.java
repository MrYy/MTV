package com.example.yy.dashgraduationdesign.main;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.yy.dashgraduationdesign.Entities.ConfigureData;
import com.example.yy.dashgraduationdesign.VideoActivity;
import com.example.yy.dashgraduationdesign.util.Method;
import com.example.yy.dashgraduationdesign.util.dipatchers.Bus;

/**
 * Created by zxc on 2016/8/26.
 */
public class TCPHandler extends Handler {
    private Context context;

    public TCPHandler(Context context) {
        this.context = context;
    }

    private static final String TAG = TCPHandler.class.getSimpleName();
    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        Log.d(TAG, "share");
        switch (msg.what) {
            case 1:
                Bus.configureData.setWorkingMode(ConfigureData.WorkMode.G_MDOE);
                String path = msg.obj.toString();
                Log.d(TAG, "path is :" + path);
                Intent intent = new Intent(context, VideoActivity.class);
                intent.putExtra("path", path);
                context.startActivity(intent);
                break;
                    }
    }






}
