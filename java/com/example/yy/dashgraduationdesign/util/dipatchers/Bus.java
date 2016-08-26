package com.example.yy.dashgraduationdesign.util.dipatchers;

import android.os.Handler;

import com.example.yy.dashgraduationdesign.DASHProxyServer.DashProxyServer;
import com.example.yy.dashgraduationdesign.Entities.ConfigureData;
import com.example.yy.dashgraduationdesign.Entities.Message;
import com.example.yy.dashgraduationdesign.Entities.SendTask;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by zxc on 2016/8/25.
 */
public class Bus implements Dispatcher {
    //Dispatcher中注册用户的handler
    private static final String TAG = Bus.class.getSimpleName();
    private  Handler currHandler;

    public void setHandler(Handler currHandler) {
        this.currHandler = currHandler;
    }
    //枚举单例模式
    private static Bus instance;
    public static boolean isConnected;
    private Dispatcher dispatcher = new TCPDispatcher();
    @Override
    public void dispatch(Message msg, InetAddress mClient) {
        dispatcher.dispatch(msg,mClient);
    }

    @Override
    public void handle(android.os.Message message) {
        currHandler.sendMessage(message);
    }


    public enum Singleton {
        Instance;

        Singleton() {
            new DashProxyServer();
            if (instance == null) instance = new Bus();
            configureData.setWorkingMode(ConfigureData.WorkMode.G_MDOE);
        }

        public Bus getInstance() {
            return instance;
        }
    }

    public static ConfigureData configureData = new ConfigureData("http://127.0.0.1:9999/4/index.m3u8");
    public static final BlockingQueue<SendTask> taskMessageQueue = new LinkedBlockingQueue<SendTask>();
    public static final BlockingQueue<SendTask> sendMessageQueue = new LinkedBlockingQueue<SendTask>();
    public static final BlockingQueue<Message> receiveMessageQueue = new LinkedBlockingQueue<Message>();
    public static boolean isOwner = false;
    public static String userName;
    public static final String SYSTEM_MESSAGE_SHARE_LOCAL = "asdfnvlxczvoj3asfpizfj323fsadf[]]adfadsf,./";
    public static final String SYSTEM_MESSAGE_SHARE_NETWORK = "asdfxczv;asfde[asdfqwer324asfd~";
    public static final String SYSTEM_MESSAGE = "asfsadwaasdfxczvasdfqqweqwr~";
    public static final Set<String> onLineUsers = new ConcurrentSkipListSet<>();
    private final static Lock lock = new ReentrantLock();
    private final static Condition condition = lock.newCondition();
    private static Set<InetAddress> mClients = new HashSet<>();

    public static void sendMsg(Message msg) {
        SendTask sendTask = new SendTask();
        sendTask.setMsg(msg);
        if (isOwner) {
            sendTask.setClients(mClients);
        }
        sendMessageQueue.add(sendTask);
    }

    public static synchronized Set<InetAddress> getClients() {
        return mClients;
    }

    public static synchronized void setClients(Set<InetAddress> clients) {
        mClients = clients;
    }

    public static void insertReceiveMQ(Message msg) {
        lock.lock();
        try {
            condition.signalAll();
            receiveMessageQueue.add(msg);
        } finally {
            lock.unlock();
        }
    }
    public  static Message getMsg() {
        lock.lock();
        try {
            while (receiveMessageQueue.size() == 0) try {
                condition.await();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return receiveMessageQueue.poll();
        }finally {
            lock.unlock();
        }
    }


}
