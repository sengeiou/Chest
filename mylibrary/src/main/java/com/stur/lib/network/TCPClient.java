package com.stur.lib.network;

import android.content.Context;

import com.stur.lib.Log;
import com.stur.lib.constant.StConstant;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class TCPClient {
    private String mServer = null;
    private int mPort = 0;
    private Socket mSocket = null;
    private Context mContext;
    private static TCPClient sTCPClient;
    int mTotalBytesRcvd = 0;

    protected OnTCPListener mListener;

    public TCPClient(Context context, String server, int port, OnTCPListener listener) {
        mServer = server != null && server.length()>0 ? server : StConstant.DEFAULT_SERVER;
        mPort = port > 0 ? port : StConstant.DEFAULT_PORT;
        //mSocket  = new Socket(mServer, mPort);
        setListener(listener);
    }

    public static TCPClient init(Context context, String server, int port, OnTCPListener listener) {
        sTCPClient = new TCPClient(context, server, port, listener);
        return sTCPClient;
    }

    public static TCPClient getInstance() {
        if (sTCPClient == null) {
         throw new RuntimeException(
             "TCPServer.getInstance can't be called before inited()");
         }
        return sTCPClient;
     }

    private void setListener(OnTCPListener listener) {
        Log.d(this, "setListener");
        mListener = listener;
    }

    public void send(final String data) throws IOException {
        Log.d(this, "sending mServer = " + mServer + ", mPort = " + mPort + ", payload = " + data);
        if (mServer == null || mServer.length() == 0) {
            throw new IllegalArgumentException("server address illegal");
        }

        Thread thread = new Thread(new Runnable() {
            public void run() {
                if(mSocket != null) {
                    try {
                        byte[] output = data.getBytes();
                        OutputStream os = mSocket.getOutputStream();
                        os.write(output);
                        Log.d(this, "send success");
                        mListener.onMsgSended(data);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        Log.e(this, "send exception: " + e);
                        mListener.onTextViewPrint("send exception: " + e);
                        e.printStackTrace();
                    }

                }
            }
        });
        thread.start();
    }

    public class ClientThread implements Runnable{

        @Override
        public void run() {
            InputStream is = null;
            try {
                //mSocket = new Socket(mServer, mPort);
                mSocket = new Socket();
                //mSocket.setSoTimeout(2000);
                //Thread.sleep(5000);
                //mSocket.bind(new InetSocketAddress("192.168.49.245", null));
                mSocket.connect(new InetSocketAddress(InetAddress.getByName(mServer) ,mPort), 5000);
                Log.d(this, "connect success");

                is = mSocket.getInputStream();

                int bytesRcvd;
                byte [] receiveBuf=new byte[StConstant.BUFSIZE];

                while(true) {
                    //一般的做法是while循环，从is中读取byte流，如果读满之后read会停止阻塞，返回读取的长度
                    //while循环体中负责把buffer中读满的内容保存下来，因为下一个循环会重新覆盖buffer
                    //注意如果已知接收的内容大于buffer长度，在保存时要注意只保存读取的长度，因为buffer中长度以后的内容不会被自动清零
                    //直至读取到流末尾，或者对端关闭流导致抛出异常，返回-1
                    while((bytesRcvd = is.read(receiveBuf)) != -1){
                        String str = new String(receiveBuf, "US-ASCII");
                        Log.d(this, bytesRcvd + "bytes received: " + str);
                        mListener.onMsgReceived(str);
                    }
                }
                //mSocket.shutdownInput();
            } catch (IOException e) {
                Log.e(this, "ClientThread run exception: " + e);
                mListener.onTextViewPrint("run exception: " + e);
                e.printStackTrace();
            } /*catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }*/finally {
                /*try {
                    if(is!=null)
                        is.close();
                    if(mSocket!=null)
                        mSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
            }

        }

    }

    public void start() {
        Log.d(this, "start E");
        Thread thread = new Thread(new ClientThread());
        thread.start();
    }

    public void exit() throws IOException {
        Log.d(this, "socket closing");
        if (mSocket != null) {
            mSocket .close();
        }
    }
}