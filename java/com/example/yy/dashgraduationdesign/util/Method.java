package com.example.yy.dashgraduationdesign.util;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.example.yy.dashgraduationdesign.Entities.FileFragment;
import com.example.yy.dashgraduationdesign.Entities.Message;
import com.example.yy.dashgraduationdesign.Entities.SendTask;
import com.example.yy.dashgraduationdesign.Integrity.IntegrityCheck;
import com.example.yy.dashgraduationdesign.R;
import com.example.yy.dashgraduationdesign.util.dipatchers.Bus;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;



/**
 * Created by David on 16/4/21.
 */
public class Method {
    public final static int LOCAL_VIDEO_SEGID = 1;
    private static final String TAG = Method.class.getSimpleName();

    private Method() {

    }
    public static InetAddress intToInetAddress(int hostAddress) {
        byte[] addressBytes = {(byte) (0xff & hostAddress),
                (byte) (0xff & (hostAddress >> 8)),
                (byte) (0xff & (hostAddress >> 16)),
                (byte) (0xff & (hostAddress >> 24))};

        try {
            return InetAddress.getByAddress(addressBytes);
        } catch (UnknownHostException e) {
            throw new AssertionError();
        }
    }

    public static void record(FileFragment f, String type) {
        record(f, type, "");
    }

    public static void record(FileFragment f, String type, String des) {
        String startOffset = String.valueOf(f.getStartIndex());
        String stopOffset = String.valueOf(f.getStopIndex());
        String segId = String.valueOf(f.getSegmentID());
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss:SSS");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = format.format(curDate);
        String text = "count:" + des + " sId:" + segId + "\t start:" + startOffset + "\t stop:"
                + stopOffset + "\t time:" + System.currentTimeMillis() + "\t " + str + "\n";
        String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ltcptest/";
        File filedir = new File(dir);
        filedir.mkdir();
        File file = new File(dir, "l" + type + "_ch1_sp0.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file, true);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fos.write(text.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendMessage(SocketChannel bSc, byte[] bytesObj) throws MyException {
        Log.d("send size:", String.valueOf(bytesObj.length) + " ip address:" + String.valueOf(bSc.socket().getInetAddress()));
        ByteBuffer buf = ByteBuffer.allocate(bytesObj.length);
        buf.put(bytesObj);
        buf.flip();
        try {
            try {
                while (buf.hasRemaining()) {
                    bSc.write(buf);
                }
            } catch (SocketException e) {
                throw new MyException();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Message readMessage(SocketChannel socketChannel) throws MyException {
        return readMessage(socketChannel, 164345);
    }

    public static Message readMessage(SocketChannel sc, int wantSize) throws MyException {
        try {
            //33787
            //328699
            //message 696

            ByteBuffer buf = ByteBuffer.allocate(wantSize);
            //read in while
            int byteRead = 0;
            int i = 0;
            while (byteRead < buf.limit()) {
                int count = sc.read(buf);
                if (count < 0) break;
                byteRead += count;
                //check the last fragment,
                //try to wait  seconds.
//                if(count==0) {
//                    i++;
//                    TimeUnit.SECONDS.sleep(1);
//                    if (i>6) {
//                        if(byteRead==696) {  Log.d(TAG, "最后一片读取");  break;}
//                    }
//                }

                if (count != 0) Log.d(TAG, "接收的字节：" + String.valueOf(byteRead));
            }
            if (byteRead > 0) {
                buf.flip();
                byte[] content = new byte[buf.limit()];
                buf.get(content);
                ByteArrayInputStream byteArrayInputStream =
                        new ByteArrayInputStream(content);
                ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
                Message message = (Message) objectInputStream.readObject();
                objectInputStream.close();
                byteArrayInputStream.close();
                buf.clear();
                return message;

            }

        } catch (StreamCorruptedException e) {
            // Thrown when control information that was read from an object
            // stream violates internal consistency checks.
//            e.printStackTrace();
            Log.d(TAG, "下载完毕");
        } catch (EOFException e) {
            //exception because of the end of stream
            //reconnect
            try {
                sc.socket().close();
                throw new MyException();
            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
//        catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        return null;
    }

    public static void changeApState(Context context, WifiManager wifiManager, Boolean open) {
        WifiConfiguration apConfig = new WifiConfiguration();
        apConfig.SSID = context.getString(R.string.ssid);
        apConfig.preSharedKey = context.getString(R.string.ap_password);
        apConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        try {
            java.lang.reflect.Method method = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
            try {
                method.invoke(wifiManager, apConfig, open);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
//                e.printStackTrace();
                e.getTargetException().printStackTrace();
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static String getRandomString(int length) { //length表示生成字符串的长度
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }
    //send message ,with header and content
    public static void send(Message msg, SocketChannel sc) throws MyException {
        byte[] msgBytes = msg.getBytes();
        Message msgHeader = new Message();
        msgHeader.setLength(msgBytes.length);
        byte[] headerBytes = msgHeader.getBytes();
        Log.d(TAG, "header size:" + String.valueOf(headerBytes.length)+" content size:"+ String.valueOf(msgBytes.length));
        Method.sendMessage(sc, headerBytes);
        Method.sendMessage(sc, msgBytes);
    }
    private static final Bus bus = Bus.Singleton.Instance.getInstance();
    public static void read(SocketChannel mSc) throws MyException {
        Message msgHeader = Method.readMessage(mSc, 287);
        if (msgHeader == null) return;
        Log.d(TAG, "message length:" + msgHeader.getMsgLength());
        Message msg = Method.readMessage(mSc, msgHeader.getMsgLength());
        InetAddress mClient = mSc.socket().getInetAddress();
        if (msg != null)
            bus.dispatch(msg,mClient);
    }



    public static void display(Context context, CharSequence charSequence) {
        Toast.makeText(context, charSequence, Toast.LENGTH_SHORT).show();
    }




}