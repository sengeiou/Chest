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
        if (sContext == null) {
            Log.e(getTag(), "sContext == null, register() should be called previously!");
            return;
        }
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
        if (sContext == null) {
            Log.e(getTag(), "sContext == null, register() should be called previously!");
            return null;
        }
        SharedPreferences sharedPreferences = sContext.getSharedPreferences(fileKey, Activity.MODE_PRIVATE);
        String string = sharedPreferences.getString(key, null);
        if (string != null) {
            Object object = String2Object(string);
            return object;
        } else {
            return null;
        }
    }

    /**
     * 写入boolean型pref配置到指定的pref文件
     * @param context
     * @param fileName
     * @param key
     * @param value
     * @return
     */
    public static boolean set(Context context, String fileName, String key, boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    /**
     * 写入boolean型pref配置到指定的pref文件
     * 注意：这个接口的调用前提是之前已经register()过了
     * @param fileName
     * @param key
     * @param value
     * @return  返回是否设置成功
     */
    public static boolean set(String fileName, String key, boolean value) {
        if (sContext == null) {
            Log.e(getTag(), "sContext == null, register() should be called previously!");
            return false;
        }
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
        if (sContext == null) {
            Log.e(getTag(), "sContext == null, register() should be called previously!");
            return false;
        }
        SharedPreferences sharedPreferences = sContext.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return sharedPreferences.contains(key);
    }

    /**
     * 从默认pref文件中获取Preference设置，比如 com.stur.chest_preferences.xml
     */
    protected static SharedPreferences getSharedPreferences(Context context) {
        if (context == null) {
            Log.e(getTag(), "context is null)");
            return null;
        }
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * 获取指定的pref文件
     * @param context
     * @param fileName
     * @param mode
     * 私有模式: Context.MODE_PRIVATE 的值是 0;
     *     ①只能被创建这个文件的当前应用访问
     *     ②若文件不存在会创建文件；若创建的文件已存在则会覆盖掉原来的文件
     * 追加模式: Context.MODE_APPEND 的值是 32768;
     *     ①只能被创建这个文件的当前应用访问
     *     ②若文件不存在会创建文件；若文件存在则在文件的末尾进行追加内容
     * 可读模式: Context.MODE_WORLD_READABLE的值是1;
     *     ①创建出来的文件可以被其他应用所读取
     * 可写模式: Context.MODE_WORLD_WRITEABLE的值是2
     *    ①允许其他应用对其进行写入。
     * @return
     */
    protected static SharedPreferences getSharedPreferences(Context context, String fileName,int mode) {
        if (context == null) {
            Log.e(getTag(), "context is null)");
            return null;
        }
        return context.getSharedPreferences(fileName, mode);
    }


    /**
     * 写入配置信息，需要最后面进行 commit()
     *
     * @param key
     * @param value
     * @return 返回是否写入成功
     */
    public static boolean putString(Context context, String key, String value) {
        SharedPreferences sharedPref = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    public static boolean putString(String key, String value) {
        if (sContext == null) {
            Log.e(getTag(), "sContext == null, register() should be called previously!");
            return false;
        }
        SharedPreferences sharedPref = getSharedPreferences(sContext);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    /**
     * 写入配置信息，需要最后面进行 commit()
     *
     * @param key
     * @param value
     * @return
     */
    public static boolean putInt(Context context, String key, int value) {
        SharedPreferences sharedPref = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key, value);
        return editor.commit();
    }

    public static boolean putInt(String key, int value) {
        if (sContext == null) {
            Log.e(getTag(), "sContext == null, register() should be called previously!");
            return false;
        }
        SharedPreferences sharedPref = getSharedPreferences(sContext);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key, value);
        return editor.commit();
    }

    /**
     * 写入配置信息，需要最后面进行 commit()
     *
     * @param key
     * @param value
     * @return
     */
    public static boolean putLong(Context context, String key, long value) {
        SharedPreferences sharedPref = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(key, value);
        return editor.commit();
    }

    public static boolean putLong(String key, long value) {
        if (sContext == null) {
            Log.e(getTag(), "sContext == null, register() should be called previously!");
            return false;
        }
        SharedPreferences sharedPref = getSharedPreferences(sContext);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(key, value);
        return editor.commit();
    }

    /**
     * 写入配置信息，需要最后面进行 commit()
     *
     * @param key
     * @param value
     * @return
     */
    public static boolean putBoolean(Context context, String key, boolean value) {
        SharedPreferences sp = getSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    public static boolean putBoolean(String key, boolean value) {
        if (sContext == null) {
            Log.e(getTag(), "sContext == null, register() should be called previously!");
            return false;
        }
        SharedPreferences sp = getSharedPreferences(sContext);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    public static boolean putBoolean(Context context, String fileName, String key, boolean value) {
        SharedPreferences sp = getSharedPreferences(context, fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    public static boolean putBoolean(String fileName, String key, boolean value) {
        if (sContext == null) {
            Log.e(getTag(), "sContext == null, register() should be called previously!");
            return false;
        }
        SharedPreferences sp = getSharedPreferences(sContext, fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    /**
     * 读取配置信息
     * @param key
     * @return
     */
    public static boolean getBoolean(Context context, String key, boolean def) {
        return getSharedPreferences(context).getBoolean(key, def);
    }

    public static boolean getBoolean(String key, boolean def) {
        if (sContext == null) {
            Log.e(getTag(), "sContext == null, register() should be called previously!");
            return def;
        }
        return getSharedPreferences(sContext).getBoolean(key, def);
    }

    /**
     * 从指定的pref文件中读取boolean型配置，默认Context.MODE_PRIVATE
     * @param context
     * @param fileName
     * @param key
     * @param def  默认值
     * @return
     */
    public static boolean getBoolean(Context context, String fileName, String key, boolean def) {
        SharedPreferences sp = getSharedPreferences(context, fileName, Context.MODE_PRIVATE);
        return sp.getBoolean(key, def);
    }

    public static boolean getBoolean(String fileName, String key, boolean def) {
        if (sContext == null) {
            Log.e(getTag(), "sContext == null, register() should be called previously!");
            return def;
        }
        SharedPreferences sp = getSharedPreferences(sContext, fileName, Context.MODE_PRIVATE);
        return sp.getBoolean(key, def);
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
        if (sContext == null) {
            Log.e(getTag(), "sContext == null, register() should be called previously!");
            return null;
        }
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
        if (sContext == null) {
            Log.e(getTag(), "sContext == null, register() should be called previously!");
            return -1;
        }
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
        if (sContext == null) {
            Log.e(getTag(), "sContext == null, register() should be called previously!");
            return -1;
        }
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
        if (sContext == null) {
            Log.e(getTag(), "sContext == null, register() should be called previously!");
            return false;
        }
        return getSharedPreferences(sContext).contains(key);
    }

    /**
     * 删除配置信息，可以同时删除多个
     *
     * @param keys
     */
    public static boolean remove(Context context, String... keys) {
        SharedPreferences sharedPref = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        for (String key : keys) {
            editor.remove(key);
        }
        return editor.commit();
    }

    public static boolean remove(String... keys) {
        if (sContext == null) {
            Log.e(getTag(), "sContext == null, register() should be called previously!");
            return false;
        }
        SharedPreferences sharedPref = getSharedPreferences(sContext);
        SharedPreferences.Editor editor = sharedPref.edit();
        for (String key : keys) {
            editor.remove(key);
        }
        return editor.commit();
    }

    /**
     * 清除所有配置文件
     */
    public static boolean clearAll(Context context) {
        SharedPreferences sharedPref = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        return editor.commit();
    }

    public static boolean clearAll() {
        if (sContext == null) {
            Log.e(getTag(), "sContext == null, register() should be called previously!");
            return false;
        }
        SharedPreferences sharedPref = getSharedPreferences(sContext);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        return editor.commit();
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