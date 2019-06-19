package com.stur.lib.web.mina;

import android.content.Context;

public class ConnectionConfig {
    private Context context;

    private String ip;

    private int port;

    private int readBufferSize;

    private long connectionTimeout;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getReadBufferSize() {
        return readBufferSize;
    }

    public void setReadBufferSize(int readBufferSize) {
        this.readBufferSize = readBufferSize;
    }

    public long getConnectionTime() {
        return connectionTimeout;
    }

    public void setConnectionTime(long connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public static class Builder{

        private Context context;

        private String ip = "";

        private int port = 9123;

        private int readBufferSize = 10240;

        private long connectionTimeout = 10000;

        public Builder(Context context){
            this.context = context;
        }

        public Builder setIp(String ip){
            this.ip = ip;
            return this;
        }

        public Builder setPort(int port){
            this.port = port;
            return  this;
        }

        public Builder setReadBuilder(int size){
            this.readBufferSize = size;
            return this;
        }

        public Builder setConnectionTimeout(int time){
            this.connectionTimeout = time;
            return this;
        }

        private void applyConfig(ConnectionConfig config){
            config.context = this.context;
            config.ip = this.ip;
            config.port = this.port;
            config.readBufferSize = this.readBufferSize;
            config.connectionTimeout = this.connectionTimeout;
        }

        public ConnectionConfig builder(){
            ConnectionConfig config = new ConnectionConfig();
            applyConfig(config);
            return config;
        }
    }
}
