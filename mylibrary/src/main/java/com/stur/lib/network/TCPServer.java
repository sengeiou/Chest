package com.stur.lib.network;

import android.content.Context;

import com.stur.lib.Log;
import com.stur.lib.constant.StConstant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {
    private int mPort = 0;
    private ServerSocket mSvrSocket = null;
    private Socket mCltSocket = null;
    int mTotalBytesRcvd = 0;

    protected OnTCPListener mListener;

    private static TCPServer sTCPServer;
    private Context mContext;

    public TCPServer(Context context, int port, OnTCPListener listener) {
        mContext = context;
        mPort = port > 0 ? port : StConstant.DEFAULT_PORT;
        //mSocket  = new ServerSocket(mPort);

        setListener(listener);
    }

    public static TCPServer init(Context context, int port, OnTCPListener listener) {
        sTCPServer = new TCPServer(context, port, listener);
        return sTCPServer;
    }

    public static TCPServer getInstance() {
        if (sTCPServer == null) {
         throw new RuntimeException(
             "TCPServer.getInstance can't be called before inited()");
         }
        return sTCPServer;
     }

    private void setListener(OnTCPListener listener) {
        Log.d(this, "setListener");
        mListener = listener;
    }

    public void send(final String data) throws IOException {
        Log.d(this, "sending data: " + data);
        Thread thread = new Thread(new Runnable() {
            public void run() {
                if(mCltSocket != null) {
                    OutputStream os;
                    try {
                        os = mCltSocket.getOutputStream();
                        /*PrintWriter pw = new PrintWriter(os);
                        pw.write(data);
                        pw.flush();*/
                        os.write(data.getBytes());
                        Log.d(this, "send success");
                        mListener.onMsgSended(data);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        Log.e(this, "send data exception: " + e);
                        mListener.onTextViewPrint("send exception: " + e);
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

    public void start() {
        Log.d(this, "start E");
        new Thread(new Runnable() {
            public void run() {
                try {
                    mSvrSocket = new ServerSocket(mPort);
                    mSvrSocket.setReuseAddress(true);
                    mCltSocket = null;

                    int count = 0;
                    while (true) {
                        mCltSocket = mSvrSocket.accept();
                        Thread thread = new Thread(new ServerThread(mCltSocket));
                        thread.start();

                        count++;
                        InetAddress address = mCltSocket.getInetAddress();
                        String str = "connected times: " + count + ", and this client is: " + address.getHostAddress();
                        Log.d(this, str);
                        mListener.onTextViewPrint(str);
                    }
                    // mSvrSocket.close();
                } catch (IOException e) {
                    Log.e(this, "start exception: " + e);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void exit() throws IOException {
        Log.d(this, "socket closing");
        if (mCltSocket != null) {
            mCltSocket.close();
        }

        if (mSvrSocket != null) {
            mSvrSocket.close();
        }

    }

    public class ServerThread implements Runnable{

        Socket socket = null;
        public ServerThread(Socket socket) {
        this.socket = socket;
    }

        @Override
        public void run() {
            InputStream is = null;
            InputStreamReader isr = null;
            BufferedReader br = null;
            OutputStream os = null;
            PrintWriter pw = null;
            try {
                is = socket.getInputStream();
                int bytesRcvd;
                byte [] receiveBuf=new byte[StConstant.BUFSIZE];

                while((bytesRcvd = is.read(receiveBuf)) != -1){
                    String str = new String(receiveBuf, "US-ASCII");
                    Log.d(this, bytesRcvd + "bytes received: " + str);
                    mListener.onMsgReceived(str);
                }
                //socket.shutdownInput();

                /*os = socket.getOutputStream();
                pw = new PrintWriter(os);
                pw.write("server sending data !");
                pw.flush();*/
            } catch (IOException e) {
                Log.e(this, "ServerThread run exception: " + e);
                mListener.onTextViewPrint("run exception: " + e);
                e.printStackTrace();
            }finally {
                /*Log.d(this, "finally close server socket");
                try {
                    if(pw!=null)
                        pw.close();
                    if(os!=null)
                        os.close();
                    if(br!=null)
                        br.close();
                    if(isr!=null)
                        isr.close();
                    if(is!=null)
                        is.close();
                    if(socket!=null)
                        socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
            }
        }
    }
}
