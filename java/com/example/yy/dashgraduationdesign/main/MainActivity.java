package com.example.yy.dashgraduationdesign.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.yy.dashgraduationdesign.R;
import com.example.yy.dashgraduationdesign.VideoActivity;
import com.example.yy.dashgraduationdesign.policy.ConnectionPolicy;
import com.example.yy.dashgraduationdesign.policy.WifiDirectConnection;
import com.example.yy.dashgraduationdesign.util.dipatchers.Bus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.vov.vitamio.LibsChecker;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private ConnectionPolicy connectionPolicy;
    @BindView(R.id.button_create_connection)
    Button buttonCreateConnection;
    @BindView(R.id.button_connect_to)
    Button buttonConnectTo;
    @BindView(R.id.button_display)
    Button buttonDisplay;
    private Handler mHandler = new TCPHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!LibsChecker.checkVitamioLibs(this)){
            return;
        }
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Bus.Singleton.Instance.getInstance().setHandler(mHandler);
        config();
    }

    private void config() {
        connectionPolicy = new WifiDirectConnection(this);
    }
    @OnClick({R.id.button_create_connection, R.id.button_connect_to, R.id.button_display})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_create_connection:
                Bus.isOwner = true;
                connectionPolicy.establish();
                break;
            case R.id.button_connect_to:
                connectionPolicy.connect();
                break;
            case R.id.button_display:
                bt();
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
}
