package com.example.yy.dashgraduationdesign;

import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yy.dashgraduationdesign.Entities.ConfigureData;
import com.example.yy.dashgraduationdesign.util.Method;
import com.example.yy.dashgraduationdesign.util.dipatchers.Bus;

import java.util.concurrent.TimeUnit;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

public class VideoActivity extends FragmentActivity implements MediaPlayer.OnInfoListener, MediaPlayer.OnCompletionListener {
    private ViewGroup mGroup;
    private static final String TAG = VideoActivity.class.getSimpleName();
    private FrameLayout frameLayout;
    private VideoView mVideoView;
    private int vheight=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_video);
        String path = getIntent().getStringExtra("path");
        initPlayVideo(path);
    }

    private void initPlayVideo(String path) {
        Log.d(TAG, path);
        mVideoView= (VideoView) findViewById(R.id.buffer);
        frameLayout= (FrameLayout) findViewById(R.id.fragment_video_player);
        vheight= frameLayout.getHeight();
        Log.d(TAG, "initPlayVideo: frameLoyout"+frameLayout.getHeight()+" "+frameLayout.getWidth());
        if (path == "") {
            // Tell the user to provide a media file URL/path.
            Toast.makeText(this,"Please edit url",Toast.LENGTH_LONG).show();
            return;
        } else {
            //streamVideo
            mVideoView.setVideoURI(Uri.parse(path));
            mVideoView.requestFocus();
            mVideoView.setOnInfoListener(VideoActivity.this);
            mVideoView.setOnCompletionListener(VideoActivity.this);
            //mVideoView.setOnBufferingUpdateListener(ViewVideoActivity.this);
            mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    // optional need Vitamio 4.0
                    mediaPlayer.setPlaybackSpeed(1.0f);
                }
            });

            LinearLayout.LayoutParams fl_lp = new LinearLayout.LayoutParams(
                    getHeightPixel(this),
                    getWidthPixel(this)-getStatusBarHeight(this)
            );
            frameLayout.setLayoutParams(fl_lp);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        switch(what){
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                if(mVideoView.isPlaying()){
                    mVideoView.pause();
                }
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                mVideoView.start();
                break;
        }
        return true;
    }
    @Override
    public void onCompletion(MediaPlayer mp) {
        //playSetLayout.setVisibility(View.VISIBLE);
        mVideoView.seekTo(0);
    }
    private int getHeightPixel(FragmentActivity activity) {
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        return localDisplayMetrics.heightPixels;
    }
    public int getWidthPixel(FragmentActivity activity)
    {
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        return localDisplayMetrics.widthPixels;
    }
    public  int getStatusBarHeight(FragmentActivity activity){
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        return statusBarHeight;
    }

}
