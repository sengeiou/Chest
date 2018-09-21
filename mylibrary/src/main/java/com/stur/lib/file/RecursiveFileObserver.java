package com.stur.lib.file;

import android.os.FileObserver;
import android.util.ArrayMap;
import com.stur.lib.Log;
import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

/**
 * 在原生的FileObserver基础上，主要解决了几个问题：
 * 无法监听目录的创建、删除
 * 开启监听之后，新创建的目录无法监听
 * 冗余注册监听的问题
 * 冗余注册监听是通过用ArrayMap键值对来解决的，以监听目录的绝对路径作为key，
 * 以监听器作为value。（也可以使用ArraySet集合实现）而开启监听之后，
 * 新创建的目录无法监听的问题则是通过监听FileObserver.CREATE事件，
 * 当创建新目录且该目录还没注册监听时，就注册并启动监听
 */
public class RecursiveFileObserver extends FileObserver {
    Map<String, SingleFileObserver> mObservers;
    String mPath;
    int mMask;

    public RecursiveFileObserver(String path) {
        this(path, ALL_EVENTS);
    }

    public RecursiveFileObserver(String path, int mask) {
        super(path, mask);
        mPath = path;
        mMask = mask;
    }

    @Override
    public void startWatching() {
        Log.d(this, "startWatching");
        if (mObservers != null) {
            return;
        }
        mObservers = new ArrayMap<>();
        Stack stack = new Stack();
        stack.push(mPath);

        while (!stack.isEmpty()) {
            String temp = (String) stack.pop();
            mObservers.put(temp, new SingleFileObserver(temp, mMask));
            File path = new File(temp);
            File[] files = path.listFiles();
            if (null == files)
                continue;
            for (File f : files) {
                if (f.isDirectory() && !f.getName().equals(".") && !f.getName().equals("..")) {
                    stack.push(f.getAbsolutePath());
                }
            }
        }
        Iterator<String> iterator = mObservers.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            mObservers.get(key).startWatching();
        }
    }

    @Override
    public void stopWatching() {
        Log.d(this, "stopWatching");
        if (mObservers == null) {
            return;
        }

        Iterator<String> iterator = mObservers.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            mObservers.get(key).stopWatching();
        }
        mObservers.clear();
        mObservers = null;
    }

    @Override
    public void onEvent(int event, String path) {
        int el = event & FileObserver.ALL_EVENTS;
        switch (el) {
            case FileObserver.ATTRIB:
                Log.i(this, "ATTRIB: " + path);
                break;
            case FileObserver.CREATE:
                File file = new File(path);
                if (file.isDirectory()) {
                    Stack stack = new Stack();
                    stack.push(path);
                    while (!stack.isEmpty()) {
                        String temp = (String) stack.pop();
                        if (mObservers.containsKey(temp)) {
                            continue;
                        } else {
                            SingleFileObserver sfo = new SingleFileObserver(temp, mMask);
                            sfo.startWatching();
                            mObservers.put(temp, sfo);
                        }
                        File tempPath = new File(temp);
                        File[] files = tempPath.listFiles();
                        if (null == files)
                            continue;
                        for (File f : files) {
                            if (f.isDirectory() && !f.getName().equals(".") && !f.getName()
                                    .equals("..")) {
                                stack.push(f.getAbsolutePath());
                            }
                        }
                    }
                }
                Log.i(this, "CREATE: " + path);
                break;
            case FileObserver.DELETE:
                Log.i(this, "DELETE: " + path);
                break;
            case FileObserver.DELETE_SELF:
                Log.i(this, "DELETE_SELF: " + path);
                break;
            case FileObserver.MODIFY:
                Log.i(this, "MODIFY: " + path);
                break;
            case FileObserver.MOVE_SELF:
                Log.i(this, "MOVE_SELF: " + path);
                break;
            case FileObserver.MOVED_FROM:
                Log.i(this, "MOVED_FROM: " + path);
                break;
            case FileObserver.MOVED_TO:
                Log.i(this, "MOVED_TO: " + path);
                break;
        }


    }

    class SingleFileObserver extends FileObserver {
        String mPath;

        public SingleFileObserver(String path) {
            this(path, ALL_EVENTS);
            mPath = path;
        }

        public SingleFileObserver(String path, int mask) {
            super(path, mask);
            mPath = path;
        }

        @Override
        public void onEvent(int event, String path) {
            if (path != null) {
                String newPath = mPath + "/" + path;
                RecursiveFileObserver.this.onEvent(event, newPath);
            }
        }
    }
}