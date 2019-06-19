package com.stur.lib.web.mina;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.stur.lib.Log;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;


public class ConnectionManager {
    public static final String BROADCAST_ACTION ="com.sip.action.mina";

    public static final String MESSAGE ="message";

    private ConnectionConfig mConfig;

    private WeakReference<Context> mContext; //避免内存泄漏

    private NioSocketConnector mConnection;

    private IoSession mSession;

    private InetSocketAddress mAddress;

    public ConnectionManager(ConnectionConfig config) {
        this.mConfig = config;
        this.mContext =new WeakReference<Context>(config.getContext());
        init();
    }

    //通过构建者模式来进行初始化
    private void init() {
        Log.d(this, "init E: mAddress = " + mConfig.getIp() + ", BufferSize = " + mConfig.getReadBufferSize());
        mAddress = new InetSocketAddress(mConfig.getIp(),mConfig.getPort());
        //创建连接对象
        mConnection = new NioSocketConnector();
        //设置连接地址
        mConnection.setDefaultRemoteAddress(mAddress);
        //设置ReadBuffer大小
        mConnection.getSessionConfig().setReadBufferSize(mConfig.getReadBufferSize());
        //添加日志过滤
        mConnection.getFilterChain().addLast("Logging",new LoggingFilter());
        //编码过滤
        mConnection.getFilterChain().addLast("codec",new ProtocolCodecFilter(
                new ObjectSerializationCodecFactory()));
        //设置连接监听
        mConnection.setHandler(new DeafultHandler(mContext.get()));
    }

    //连接方法（外部调用）
    public boolean connect(){
        Log.d(this, "connect E:");
        try {
            ConnectFuture futrue = mConnection.connect();
            //一直连接，直至成功
            futrue.awaitUninterruptibly();
            mSession = futrue.getSession();
            Log.d(this, "connect ret = " + (mSession == null ? false : true));
        }catch (Exception e){
            Log.w(this, "connect Exception: " + e);
            return false;
        }
        return mSession == null ? false : true;
    }

    //断开连接方法（外部调用）
    public void disConnect(){
        //关闭
        mConnection.dispose();
        //大对象置空
        mConnection = null;
        mSession = null;
        mAddress = null;
        mContext = null;
    }

    //内部类实现事物处理
    private class DeafultHandler extends IoHandlerAdapter {
        private Context mContext;

        public DeafultHandler(Context context){
            this.mContext = context;
        }

        /**
         * 会话创建时回调的方法
         * @param session
         * @throws Exception
         */
        @Override
        public void sessionCreated(IoSession session) throws Exception {
            Log.d(this, "sessionCreated");
        }

        /**
         * 连接成功时回调的方法
         * @param session
         * @throws Exception
         */
        @Override
        public void sessionOpened(IoSession session) throws Exception {
            Log.d(this, "sessionOpened");
            //将我们的session 保存到我们sessionManager 中，从而可以发送消息到服务器
            SessionManager.getInstance().setIoSession(session);
        }

        @Override
        public void sessionClosed(IoSession session) throws Exception {
            super.sessionClosed(session);
            Log.d(this, "sessionClosed");
        }

        @Override
        public void messageReceived(IoSession session, Object message) throws Exception {
            Log.d(this, "messageReceived E:");
            if (mContext!=null){
                Intent intent = new Intent(BROADCAST_ACTION);
                intent.putExtra(MESSAGE, message.toString());
                //使用局部广播，保证安全性
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            }
        }

        @Override
        public void messageSent(IoSession session, Object message) throws Exception {
            super.messageSent(session, message);
            Log.d(this, "messageSent");
        }
    }
}
