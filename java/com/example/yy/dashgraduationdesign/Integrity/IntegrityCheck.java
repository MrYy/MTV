package com.example.yy.dashgraduationdesign.Integrity;

import android.util.Log;
import android.util.SparseArray;

import com.example.yy.dashgraduationdesign.Celluar.CellularDown;
import com.example.yy.dashgraduationdesign.Entities.FileFragment;
import com.example.yy.dashgraduationdesign.Entities.Message;
import com.example.yy.dashgraduationdesign.Entities.Segment;
import com.example.yy.dashgraduationdesign.Entities.SendTask;
import com.example.yy.dashgraduationdesign.util.dipatchers.Bus;


public class IntegrityCheck {
	private static final String TAG = IntegrityCheck.class.getSimpleName();
	// single instance mode
	private static IntegrityCheck instance;
	private SparseArray<Segment> urlMap;
	public static final String URL_TAG = "http://buptant.cn/autoChart/du/video/ljw2016/zxyqwe/download.php";
	public static final String GROUP_TAG = "http://buptant.cn/autoChart/du/video/ljw2016/zxyqwe/appdown.php";
	public static final String TCP_TAG =  "http://buptant.cn/autoChart/du/video/ljw2016/zxyqwe/tcpappdown.php";
	public static final String JUNIT_TAG = "http://127.0.0.1:9999/junit.php";
	public static final String URI_TAG = "http://127.1.1.1:9999/";
	public static volatile int health = 0;
	private IntegrityCheck() {
		urlMap = new SparseArray<Segment>();
	}

	public void clear() {
		urlMap = null;
		urlMap = new SparseArray<Segment>();
	}
	public static synchronized IntegrityCheck getInstance() {
		if (instance == null) {
			instance = new IntegrityCheck();
		}
		return instance;
	}

	public byte[] getSegments(int uri) {
		return getSegments(uri, CellularDown.CellType.GROUP);
	}

	public byte[] getSegments(int uri, CellularDown.CellType ct) {
		int id = uri;
		synchronized (this) {
			if (urlMap.indexOfKey(id) < 0) {
				urlMap.put(id, new Segment(id, -1));
			}
			Segment segment = urlMap.get(id);
			if (segment.checkIntegrity()) {
				return segment.getData();
			} else {
					CellularDown.queryFragment(ct, id);
			}
		}
		while (true) {
			synchronized (this) {
				Segment segment = urlMap.get(id);
				if (segment.checkIntegrity()) {
					return segment.getData();
				}
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				return null;// Empty Data
			}
		}

	}

	public void setSegLength(int id, int totalLength) {
		Segment s = urlMap.get(id);
		s.setSegLength(totalLength);
	}

	public void insert(int id, FileFragment fm) {
		synchronized (this) {
			if (urlMap.indexOfKey(id) < 0) {
				urlMap.put(id, new Segment(id, -1));
			}
		}
		Segment s = urlMap.get(id);
		s.insert(fm);
	}

	public void insert(int id, FileFragment fm,int n) {
		synchronized (this) {
			if (urlMap.indexOfKey(id) < 0) {
				urlMap.put(id, new Segment(id, -1));
			}
		}
		Segment s = urlMap.get(id);
		s.insert(fm);
		s.checkIntegrity();
	}
	public Segment getSeg(int id) {
		return urlMap.get(id);
	}

	public synchronized SparseArray<Segment> getUrlMap() {
		return urlMap;
	}
}
