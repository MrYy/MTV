package com.example.yy.dashgraduationdesign.Celluar.GroupCell;

import android.util.Log;

import com.example.yy.dashgraduationdesign.Entities.FileFragment;
import com.example.yy.dashgraduationdesign.Entities.Message;
import com.example.yy.dashgraduationdesign.Entities.Segment;
import com.example.yy.dashgraduationdesign.Integrity.IntegrityCheck;
import com.example.yy.dashgraduationdesign.util.dipatchers.Bus;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GroupCell extends Thread {
	private static final String TAG = GroupCell.class.getSimpleName();
	private int url;
	public static String groupSession;
	public GroupCell(int url) {
		super();
		this.url = url;
	}

	@Override
	public void run() {
		Log.d(TAG, "test " + url);
		HttpURLConnection connection = null;
		IntegrityCheck IC = IntegrityCheck.getInstance();
		try {
			URL uurl = new URL(IntegrityCheck.GROUP_TAG + "?filename=" + url
						+ ".mp4&sessionid="+groupSession+
						 "&user_name=" + Bus.userName);

			Log.d(TAG, "" + uurl);
			while (true) {
				Segment Seg = IC.getSeg(url);
				if(Seg.checkIntegrity()){
					break;
				}
				connection = (HttpURLConnection) uurl.openConnection();
				connection.setRequestMethod("POST");
				connection.setConnectTimeout(5000);
				connection.setUseCaches(false);
				connection.setDoInput(true);
				connection.setRequestProperty("Accept-Encoding", "");
				connection.setDoOutput(true);
//				Log.d(TAG,
//						"ResponseCode " + url + " "
//								+ connection.getResponseCode());

				if (connection.getResponseCode() == 206) {
					String videoName = connection.getHeaderField("video-name");
					String videoRate = videoName.split("/")[1];
					String videoSegment = videoName.split("-")[1];
					Log.d(TAG, "video rate:" + String.valueOf(videoRate));
					Message msg = new Message();
					InputStream in = connection.getInputStream();
					String contentRange = connection.getHeaderField(
							"Content-Range").toString();
//					Log.d(TAG, "Content-Range " + contentRange);
					String range = contentRange.split(" ")[1].trim();
					String start = range.split("-")[0];
					String end = range.split("-")[1].split("/")[0];
					String total = range.split("-")[1].split("/")[1];
					int startOffset = Integer.parseInt(start);
					int endOffset = Integer.parseInt(end);
					int totalLength = Integer.parseInt(total);
					int pieceLength = endOffset - startOffset;
					if (pieceLength<0 || totalLength<0) return;
					byte[] tmpbuff = new byte[pieceLength];
					int hasRead = 0;
					while (hasRead < pieceLength) {
						hasRead += in.read(tmpbuff, hasRead, pieceLength
								- hasRead);
					}
					IC.setSegLength(url, totalLength);
					FileFragment fm = new FileFragment(startOffset, endOffset,
							url,totalLength);
					Log.d(TAG, "" + url + " " + fm);
					fm.setData(tmpbuff);
					IC.insert(url, fm);
					IC.getSeg(url).checkIntegrity();
					Bus.configureData.getCellularSharePolicy().handleFragment(fm);
				}else if (connection.getResponseCode() == 200) {
//					Log.d(TAG, "else " + url);
//					CellularDown.queryFragment(CellularDown.CellType.CellMore,
//							url);
//					break;
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			Log.d(TAG, "MalformedURLException");
		} catch (IOException e) {
			e.printStackTrace();
			Log.d(TAG, "IOException");
		} catch (FileFragment.FileFragmentException e) {
			e.printStackTrace();
		} finally {
			try {
				connection.disconnect();

			} catch (ArrayIndexOutOfBoundsException ie) {

			} catch (IllegalArgumentException ie) {

			}
		}
	}


}