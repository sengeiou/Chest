/*
 * This file can resolve list or object to sp
 * save content:  ClassTest testClass = new ClassTest();
 *                SharedPreferenceUtil.save(mContext, "file_key","value_key",testClass );
 * get content:   Object object = SharedPreferenceUtil.get(mContext, "file_key","value_key");
 *                if(object != null) {
 *                    ClassTest testClass = (ClassTest) object;
 *                 }
 */
package com.stur.lib;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;

import com.stur.lib.app.ContextBase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * SharedPreferences工具类，可以保存object对象
 * <p>
 * 存储时以object存储到本地，获取时返回的也是object对象，需要自己进行强制转换
 * <p>
 * 也就是说，存的人和取的人要是同一个人才知道取出来的东西到底是个啥 ^_^
 */
public class SharedPreferenceUtils {

    public static final String KEY_MAC_ADDR = "mac_addr";

    private static ContextBase sContext = null;

    public static String getTag() {
        return new Object() {
            public String getClassName() {
                String clazzName = this.getClass().getName();
                return clazzName.substring(clazzName.lastIndexOf('.')+1, clazzName.lastIndexOf('$'));
            }
        }.getClassName();
    }


    /**
     * 注册之后在一些不方便获取context的地方就可以直接调用不带context参数的方法
     * 不注册的话必须使用默认带context的方法
     * @param context
     */
    public static void register(ContextBase context) {
        SharedPreferenceUtils.sContext = context;
    }

    /**
     * writeObject 方法负责写入特定类的对象的状态，以便相应的 readObject 方法可以还原它
     * 最后，用Base64.encode将字节文件转换成Base64编码保存在String中
     *
     * @param object
     *            待加密的转换为String的对象
     * @return String 加密后的String
     */
    private static String Object2String(Object object) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            String string = new String(Base64.encode(byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
            objectOutputStream.close();
            return string;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 使用Base64解密String，返回Object对象
     *
     * @param objectString
     *            待解密的String
     * @return object 解密后的object
     */
    private static Object String2Object(String objectString) {
        byte[] mobileBytes = Base64.decode(objectString.getBytes(), Base64.DEFAULT);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(mobileBytes);
        ObjectInputStream objectInputStream = null;
        try {
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            Object object = objectInputStream.readObject();
            objectInputStream.close();
            return object;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 使用SharedPreference保存对象
     *
     * @param fileKey
     *            储存文件的key
     * @param key
     *            储存对象的key
     * @param saveObject
     *            储存的对象
     */
    public static void save(Context context, String fileKey, String key, Object saveObject) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileKey, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String string = Object2String(saveObject);
        editor.putString(key, string);
        editor.commit();
    }

    public static void save(String fileKey, String key, Object saveObject) {
        SharedPreferences sharedPreferences = sContext.getSharedPreferences(fileKey, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String string = Object2String(saveObject);
        editor.putString(key, string);
        editor.commit();
    }

    /**
     * 获取SharedPreference保存的对象
     *
     * @param fileKey
     *            储存文件的key
     * @param key
     *            储存对象的key
     * @return object 返回根据key得到的对象
     */
    public static Object get(Context context, String fileKey, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileKey, Activity.MODE_PRIVATE);
        String string = sharedPreferences.getString(key, null);
        if (string != null) {
            Object object = String2Object(string);
            return object;
        } else {
            return null;
        }
    }

    public static Object get(String fileKey, String key) {
        SharedPreferences sharedPreferences = sContext.getSharedPreferences(fileKey, Activity.MODE_PRIVATE);
        String string = sharedPreferences.getString(key, null);
        if (string != null) {
            Object object = String2Object(string);
            return object;
        } else {
            return null;
        }
    }

    public static boolean set(Context context, String fileName, String key, boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    public static boolean set(String fileName, String key, boolean value) {
        SharedPreferences sharedPreferences = sContext.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    public static boolean contatins(Context context, String fileName, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return sharedPreferences.contains(key);
    }

    public static boolean contatins(String fileName, String key) {
        SharedPreferences sharedPreferences = sContext.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return sharedPreferences.contains(key);
    }

    /**
     * 获取Preference设置
     */
    protected static SharedPreferences getSharedPreferences(Context context) {
        if (context == null) {
            Log.e(getTag(), "context is null)");
        }
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * 写入配置信息，需要最后面进行 commit()
     *
     * @param key
     * @param value
     * @return
     */
    public static void putString(Context context, String key, String value) {
        SharedPreferences sharedPref = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void putString(String key, String value) {
        SharedPreferences sharedPref = getSharedPreferences(sContext);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * 写入配置信息，需要最后面进行 commit()
     *
     * @param key
     * @param value
     * @return
     */
    public static void putInt(Context context, String key, int value) {
        SharedPreferences sharedPref = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static void putInt(String key, int value) {
        SharedPreferences sharedPref = getSharedPreferences(sContext);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * 写入配置信息，需要最后面进行 commit()
     *
     * @param key
     * @param value
     * @return
     */
    public static void putLong(Context context, String key, long value) {
        SharedPreferences sharedPref = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static void putLong(String key, long value) {
        SharedPreferences sharedPref = getSharedPreferences(sContext);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    /**
     * 写入配置信息，需要最后面进行 commit()
     *
     * @param key
     * @param value
     * @return
     */
    public static void putBoolean(Context context, String key, boolean value) {
        SharedPreferences sharedPref = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void putBoolean(String key, boolean value) {
        SharedPreferences sharedPref = getSharedPreferences(sContext);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * 读取配置信息
     *
     * @param key
     * @return
     */
    public static boolean getBoolean(Context context, String key, boolean def) {
        return getSharedPreferences(context).getBoolean(key, def);
    }

    public static boolean getBoolean(String key, boolean def) {
        return getSharedPreferences(sContext).getBoolean(key, def);
    }

    /**
     * 读取配置信息
     *
     * @param key
     * @return
     */
    public static String getString(Context context, String key) {
        return getSharedPreferences(context).getString(key, null);
    }

    public static String getString(String key) {
        return getSharedPreferences(sContext).getString(key, null);
    }

    /**
     * 读取配置信息
     *
     * @param key
     * @return
     */
    public static int getInt(Context context, String key) {
        return getSharedPreferences(context).getInt(key, 0);
    }

    public static int getInt(String key) {
        return getSharedPreferences(sContext).getInt(key, 0);
    }

    /**
     * 读取配置信息
     *
     * @param key
     * @return
     */
    public static long getLong(Context context, String key) {
        return getSharedPreferences(context).getLong(key, 0L);
    }

    public static long getLong(String key) {
        return getSharedPreferences(sContext).getLong(key, 0L);
    }

    /**
     * 本地是否保存有该值
     *
     * @param key
     * @return
     */
    public static boolean containsKey(Context context, String key) {
        return getSharedPreferences(context).contains(key);
    }

    public static boolean containsKey(String key) {
        return getSharedPreferences(sContext).contains(key);
    }

    /**
     * 删除配置信息，可以同时删除多个
     *
     * @param keys
     */
    public static void remove(Context context, String... keys) {
        SharedPreferences sharedPref = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        for (String key : keys) {
            editor.remove(key);
        }
        editor.commit();
    }

    public static void remove(String... keys) {
        SharedPreferences sharedPref = getSharedPreferences(sContext);
        SharedPreferences.Editor editor = sharedPref.edit();
        for (String key : keys) {
            editor.remove(key);
        }
        editor.commit();
    }

    /**
     * 清除所有配置文件
     */
    public static void clearAll(Context context) {
        SharedPreferences sharedPref = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.commit();
    }

    public static void clearAll() {
        SharedPreferences sharedPref = getSharedPreferences(sContext);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.commit();
    }
}

// 待保存的Object对象（实现 Serializable ）
/*public class ClassTest implements Serializable {

    public String mAppId;

    public int mPlatForm;

    public List<ClassInner> mList;

    public static class ClassInner implements Serializable {
        public int id;
        public List<ClassInnerOther> mInnerList;

    }

    public static class ClassInnerOther implements Serializable {
        public String name;
        public int value;
    }
}*/