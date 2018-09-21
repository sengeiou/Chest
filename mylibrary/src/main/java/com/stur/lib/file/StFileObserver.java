package com.stur.lib.file;


import android.os.FileObserver;

import com.stur.lib.Log;

/**
 * Android原生的文件或文件夹内容改变监听器
 * FileObserver类是一个用于监听文件访问、创建、修改、删除、移动等操作的监听器，基于linux的INotify
 * FileObserver是个抽象类，必须继承它才能使用。每个FileObserver对象监听一个单独的文件或者文件夹，
 * 如果监视的是一个文件夹，那么文件夹下所有的文件和级联子目录的改变都会触发监听的事件(实际不支持)
 * 注意：FileObserver对象必须保持一个引用，确保不被垃圾收集器回收掉，否则就不会触发事件，
 * 这里可以考虑使用Service服务。
 */
public class StFileObserver extends FileObserver {

        public StFileObserver(String path) {
            //这种构造方法是默认监听所有事件的,
            // 如果使用 super(String,int)这种构造方法，则int参数是要监听的事件类型.
            super(path);
            //开始监听
            startWatching();    //使用完成后需要使用 stopWatching() 停止监听
        }

    /**
     * 监听的事件包括：
     * ACCESS，即文件被访问
     * MODIFY，文件被修改
     * ATTRIB，文件属性被修改，如 chmod、chown、touch 等
     * CLOSE_WRITE，可写文件被 close
     * CLOSE_NOWRITE，不可写文件被 close
     * OPEN，文件被 open
     * MOVED_FROM，文件被移走,如 mv
     * MOVED_TO，文件被移来，如 mv、cp
     * CREATE，创建新文件
     * DELETE，文件被删除，如 rm
     * DELETE_SELF，自删除，即一个可执行文件在执行时删除自己
     * MOVE_SELF，自移动，即一个可执行文件在执行时移动自己
     * CLOSE，文件被关闭，等同于(IN_CLOSE_WRITE | IN_CLOSE_NOWRITE)
     * ALL_EVENTS，包括上面的所有事件。
     * @param event
     * @param path
     */
        @Override
        public void onEvent(int event, String path) {
            switch (event) {
                case FileObserver.ALL_EVENTS:
                    Log.d(this, "FileListener onEvent ALL_EVENTS: path = " + path);
                    break;
                case FileObserver.CREATE:
                    Log.d(this, "FileListener onEvent CREATE: path = " + path);
                    break;
            }
        }
    }