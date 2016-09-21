package com.example.yy.dashgraduationdesign.main;

/**
 * Created by zxc on 2016/9/21.
 */
public class Data{
    public String ip;
    public String netMask;
    public String macAddr;

    public static class Builder{
        String ip;
        String netmask;
        String macAddr;
        public Data create() {
            Data data = new Data();
            data.ip = ip;
            data.netMask = netmask;
            data.macAddr = macAddr;
            return data;
        }

        public Builder setIp(String ip) {
            this.ip = ip;
            return this;
        }

        public Builder setNetmask(String netmask) {
            this.netmask = netmask;
            return this;
        }

        public Builder setMacAddr(String macAddr) {
            this.macAddr = macAddr;
            return this;
        }
    }
}