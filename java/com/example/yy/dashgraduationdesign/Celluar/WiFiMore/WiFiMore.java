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
	/**
	 * 这里还需要进行改动，
	 * wifimore时间间隔太长
	 * 也会重复发送缺失文件的信息
	 */
	private int url;
	private Message nowSend;
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
				Message msg = new Message();
				msg.setMessage(Bus.SYSTEM_MESSAGE+url+"~"+miss+"~"+Bus.clientAddr);
				msg.setCount(miss);
				if(nowSend !=null)
					if(nowSend.getCount()==msg.getCount()) continue;
				nowSend = msg;
				Log.v(TAG, "no " + url + " " + miss+" client:"+Bus.clientAddr);
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
