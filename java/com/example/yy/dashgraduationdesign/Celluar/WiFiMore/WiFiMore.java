package com.example.yy.dashgraduationdesign.Celluar.WiFiMore;

import android.util.Log;

import com.example.yy.dashgraduationdesign.Entities.Message;
import com.example.yy.dashgraduationdesign.Entities.Segment;
import com.example.yy.dashgraduationdesign.Integrity.IntegrityCheck;
import com.example.yy.dashgraduationdesign.util.dipatchers.Bus;

import java.net.InetAddress;
import java.net.UnknownHostException;


public class WiFiMore extends Thread {
	private static final String TAG = WiFiMore.class.getSimpleName();

	private int url;

	public WiFiMore(int url) {
		this.url = url;
	}

	@Override
	public void run() {
		IntegrityCheck IC = IntegrityCheck.getInstance();
		Segment Seg = IC.getSeg(url);
		if (Seg != null) {
			while (!Seg.checkIntegrity()) {
				int miss;
				try {
					miss = Seg.getMiss();
				} catch (Segment.SegmentException e) {
					e.printStackTrace();
					break;
				}
				Log.v(TAG, "no " + url + " " + miss);
				Message msg = new Message();
				msg.setMessage(Bus.SYSTEM_MESSAGE+url+"~"+miss);
				try {
					Bus.sendMsgTo(msg, InetAddress.getByName("192.168.49.1"));
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
			}
			Log.d(TAG, "yes " + url);
		} else {
			Log.e(TAG, "a " + url);
		}
	}
}
