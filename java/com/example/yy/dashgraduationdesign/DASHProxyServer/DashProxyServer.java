package com.example.yy.dashgraduationdesign.DASHProxyServer;

import android.os.Environment;
import android.util.Log;

import com.example.yy.dashgraduationdesign.Celluar.CellularDownPolicy;
import com.example.yy.dashgraduationdesign.Celluar.TCPDown;
import com.example.yy.dashgraduationdesign.Integrity.IntegrityCheck;
import com.example.yy.dashgraduationdesign.util.dipatchers.Bus;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import fi.iki.elonen.NanoHTTPD;


/**
 * Created by ljw on 6/18/15.
 */
public class DashProxyServer extends NanoHTTPD {
	private static final String TAG = DashProxyServer.class.getSimpleName();
	public static final String SERVER_ADDR = "http://yyzwt.cn:12345/dash";
	private CellularDownPolicy downPolicy = new TCPDown();
	public DashProxyServer() {
		super(9999);
		try {
			this.start();
			Log.e(TAG, "start");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



	@Override
	public Response serve(IHTTPSession session) {
		try {
			if (!getFileName(session, ".m3u8").equals("")) {
				Log.v("TAG", "filename" + session.getUri());
				IntegrityCheck.getInstance().clear();
				Bus.sendMessageQueue.clear();
				Bus.taskMessageQueue.clear();
				Bus.configureData.getCellularSharePolicy().announceDisplay(session.getUri());
				return downloadM3U8(session.getUri());

			} else {
				Log.v("TAG", "DashProxy uri:" + session.getUri());
				String playist = getFileName(session, ".mp4");
				Log.v("TAG", "playist" + playist);
				switch (Bus.configureData.getWorkingMode()) {
				case LOCAL_MODE:
					return newFixedLengthResponse(Response.Status.OK,
							"application/x-mpegurl", IntegrityCheck.getInstance().getSegments(com.example.yy.dashgraduationdesign.util.Method.LOCAL_VIDEO_SEGID));
				case G_MDOE:
					return newFixedLengthResponse(NanoHTTPD.Response.Status.OK,
							"application/x-mpegurl", Bus.configureData.getCellularDownPolicy().download(playist));
				default:
					return newFixedLengthResponse("");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return newFixedLengthResponse("");
		}
	}

	private Response downloadM3U8(String uri) throws IOException {
		String path = "/video"+uri;
		String remotePath = SERVER_ADDR + path;
		String localPath = Environment.getExternalStorageDirectory() + path;
		File file = new File(localPath);
		if (!file.isFile()){
			URL urlfile;
			HttpURLConnection httpUrl;
			BufferedInputStream bis;
			BufferedOutputStream bos;
			urlfile = new URL(remotePath);
			httpUrl = (HttpURLConnection)urlfile.openConnection();
			httpUrl.connect();
			bis = new BufferedInputStream(httpUrl.getInputStream());
			bos = new BufferedOutputStream(new FileOutputStream(file));
			File f = new File(localPath);
			int len = 1024;
			byte[] b = new byte[len];
			while ((len = bis.read(b)) != -1) {
				bos.write(b,0,len);
			}
			bos.flush();
			bis.close();
			httpUrl.disconnect();
			Log.d(TAG, "finish download m3u8");
		}
		return localFile(localPath);
	}


	private String getFileName(IHTTPSession session, String key) {
		String uri = session.getUri();
		String playlist = "";
		for (String s : uri.split("/")) {
			if (s.contains(key)) {
				playlist = s;
			}
		}
		return playlist;
	}

	private Response localFile(String str) throws IOException {
		FileInputStream fis = new FileInputStream(
				str);
		int length = fis.available();
		return newFixedLengthResponse(Response.Status.OK,
				"application/x-mpegurl", fis, length);
	}
	private Response newFixedLengthResponse(Response.IStatus status,
											String mimeType, byte[] bytes) {
		return newFixedLengthResponse(status, mimeType,
				new ByteArrayInputStream(bytes), bytes.length);
	}
}
