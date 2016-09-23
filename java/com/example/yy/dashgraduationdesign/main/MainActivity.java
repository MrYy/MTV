package com.example.yy.dashgraduationdesign.main;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.yy.dashgraduationdesign.R;
import com.example.yy.dashgraduationdesign.VideoActivity;
import com.example.yy.dashgraduationdesign.policy.ConnectionPolicy;
import com.example.yy.dashgraduationdesign.policy.WifiDirectConnection;
import com.example.yy.dashgraduationdesign.util.dipatchers.Bus;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.vov.vitamio.LibsChecker;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    @BindView(R.id.btCreate)
    Button btCreate;
    @BindView(R.id.btConnect)
    Button btConnect;
    @BindView(R.id.Spinner_wifi_)
    Spinner SpinnerWifi;
    @BindView(R.id.btPlay)
    Button btPlay;
    @BindView(R.id.btClear)
    Button btClear;

    private ConnectionPolicy connectionPolicy;

    private Handler mHandler = new TCPHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!LibsChecker.checkVitamioLibs(this)) {
            return;
        }
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Bus.Singleton.Instance.getInstance().setHandler(mHandler);
        config();
        requestPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, 1);
        requestPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, 2);
        requestPermissions(Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS, 3);

    }

    private void config() {
        connectionPolicy = new WifiDirectConnection(this);
        Spinner spinner = (Spinner) findViewById(R.id.Spinner_wifi_);
        ArrayList<String> list = new ArrayList();
        list.add("Ad-hoc");
        list.add("Push-based");
        list.add("Pull-based");
        spinner.setAdapter(new ArrayAdapter(this,android.R.layout.simple_spinner_item,list));
        spinner.setSelection(0);
    }

    @OnClick({R.id.btCreate, R.id.btConnect, R.id.btPlay})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btCreate:
                Bus.isOwner = true;
                connectionPolicy.establish();
                break;
            case R.id.btConnect:
                connectionPolicy.connect();
                break;

            case R.id.btPlay:
                bt();
                break;
            case R.id.btClear:
                break;
        }
    }
    private void tcp() {
        if (Bus.isOwner) {
            Intent intent = new Intent(MainActivity.this, VideoActivity.class);
            intent.putExtra("path", Bus.configureData.getUrl());
            Log.d(TAG, "path is:" + Bus.configureData.getUrl());
            startActivity(intent);
        }
    }

    private void bt() {
        Intent intent = new Intent(MainActivity.this, VideoActivity.class);
        intent.putExtra("path", Bus.configureData.getUrl());
        Log.d(TAG, "path is:" + Bus.configureData.getUrl());
        startActivity(intent);
    }

    private void partyVideoPlayer() {

    }

    @Override
    protected void onPause() {
        super.onPause();
        connectionPolicy.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        connectionPolicy.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        connectionPolicy.die();
    }

    private void requestPermissions(String permission, int code) {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    permission)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(this, new String[]{permission},
                        code);
            } else {

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this, new String[]{permission},
                        code);
            }
        }
    }


}
