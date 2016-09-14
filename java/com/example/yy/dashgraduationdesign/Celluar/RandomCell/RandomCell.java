package com.example.yy.dashgraduationdesign.Celluar.RandomCell;

import android.util.Log;

import com.example.yy.dashgraduationdesign.Celluar.GroupCell.GroupCell;
import com.example.yy.dashgraduationdesign.Entities.FileFragment;
import com.example.yy.dashgraduationdesign.Entities.Message;
import com.example.yy.dashgraduationdesign.Entities.Segment;
import com.example.yy.dashgraduationdesign.Integrity.IntegrityCheck;
import com.example.yy.dashgraduationdesign.util.Method;
import com.example.yy.dashgraduationdesign.util.dipatchers.Bus;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * Created by zxc on 2016/9/13.
 */
public class RandomCell extends Thread {
    private static final String TAG = RandomCell.class.getSimpleName();
    private int url;
    private boolean hasGetFirst = false;
    private IntegrityCheck IC;
    private HttpURLConnection connection;

    public RandomCell(int url) {
        this.url = url;
    }

    @Override
    public void run() {
        IC = IntegrityCheck.getInstance();
        Segment Seg = IC.getSeg(url);
        connection = null;
        while (true) {
            if (Seg.checkIntegrity()) {
                break;
            }

            int nextStart = 0;
            if (hasGetFirst)
                nextStart = Seg.getNextPieceStart();
            else
                hasGetFirst = true;

            Log.v(TAG, url + "  is download: " + nextStart);
            //usually, 0 fragment should always download from server
            if (nextStart > 0)
                if (IntegrityCheck.health > 0) {
                    if (!Seg.getSeederAddr().equals("")) {
                        Log.d(TAG, "now check the buffer map. " + Method.printBuffermap(Seg.getSeederBuffermap()));
                        if (Seg.getSeederBuffermap()[nextStart / Segment.FRAGMENT_LENGTH]) {
                            getFromWifi(nextStart, Seg.getSeederAddr());
                            continue;
                        }
                        Log.d(TAG, "no buffer,download from cellular");
                    }
                }
            getFromCellular(nextStart);
//            if (nextStart == -1) {
//                //doesn't has a array map yet
//                if (Seg.getSeederBuffermap() != null)
//                    Seg.setBuffermap(new boolean[Seg.getSeederBuffermap().length]);
//                continue;
//            }
//            if (!Bus.isOwner)
//            {
//                getFromWifi(nextStart, Seg.getSeederAddr());
//                continue;
//            }

        }
    }


    private void getFromCellular(int nextStart) {
        try {
            URL uurl = new URL(IntegrityCheck.GROUP_TAG + "?filename=" + url
                    + ".mp4&sessionid=" + GroupCell.groupSession +
                    "&user_name=" + Bus.userName + "&miss=" + nextStart);

//                    Log.d(TAG, "" + uurl);
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
                InputStream in = connection.getInputStream();
                String videoName = connection.getHeaderField("video-name");
                String videoRate = videoName.split("/")[1];
//                        Log.d(TAG, "video rate:" + String.valueOf(videoRate));
                Message msg = new Message();

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
                if (pieceLength < 0 || totalLength < 0) {
                    return;
                }
                byte[] tmpbuff = new byte[pieceLength];
                int hasRead = 0;
                while (hasRead < pieceLength) {
                    hasRead += in.read(tmpbuff, hasRead, pieceLength
                            - hasRead);
                }
                IC.setSegLength(url, totalLength);
                FileFragment fm = new FileFragment(startOffset, endOffset,
                        url, totalLength);
                Log.d(TAG, "" + url + " " + fm);
                fm.setData(tmpbuff);
                IC.insert(url, fm, 0);
                Bus.configureData.getCellularSharePolicy().handleFragment(fm);
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


    private void getFromWifi(int nextStart, String seederAddr) {
        Message msg = new Message();
        msg.setMessage(Bus.SYSTEM_MESSAGE+url+"~"+nextStart+"~"+Bus.clientAddr);
        msg.setCount(nextStart);
        try {
            Bus.sendMsgTo(msg, InetAddress.getByName(seederAddr));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }
}