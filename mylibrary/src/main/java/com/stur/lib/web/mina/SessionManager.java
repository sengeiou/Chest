package com.stur.lib.web.mina;

import com.stur.lib.Log;

import org.apache.mina.core.session.IoSession;

/**
 * session管理类,通过ioSession与服务器通信
 */

public class SessionManager {
    private static SessionManager mInstance = null;

    private IoSession ioSession;  //最终与服务器通信的对象

    public static SessionManager getInstance() {
        if (mInstance == null) {
            synchronized (SessionManager.class) {
                if (mInstance == null) {
                    mInstance = new SessionManager();
                }
            }
        }
        return mInstance;
    }

    private SessionManager() {

    }

    public void setIoSession(IoSession ioSession) {
        this.ioSession = ioSession;
    }

    /**
     * 将对象写到服务器，通过如下方式访问：
     * SessionManager.getmInstance().writeToServer("message to server");
     */
    public void writeToServer(Object msg) {
        Log.d(this, "writeToServer E: ioSession isNotNull = " + (ioSession != null));
        if (ioSession != null) {
            ioSession.write(msg);
        }
    }

    /**
     * 关闭连接
     */
    public void closeSession() {
        Log.d(this, "closeSession E:");
        if (ioSession != null) {
            ioSession.closeOnFlush();
        }
    }

    public void removeSession() {
        Log.d(this, "removeSession E:");
        ioSession = null;
    }
}
