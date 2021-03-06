/******************************************************************************
 * Copyright (C), 2017-2027, Sturmegezhutz private property right
 * PROPRIETARY RIGHTS of Sturmegezhutz are involved in the
 * subject matter of this material.  All manufacturing, reproduction, use,
 * and sales rights pertaining to this subject matter are governed by the
 * license agreement.  The recipient of this software implicitly accepts
 * the terms of the license.

 * File name: SystemPropertiesProxy.java
 * Description: Systemproperties类在android.os下，但这个类是隐藏的，
 * 上层程序开发无法直接使用。使用Java的反射机制是可以使用这个类
 * 进行系统属性设置的程序也必须有system或root权限，
 * 否则反射调用时会抛InvocationTargetException
 * Others:

 * Department:  Communication Software Development
 * Author:      Communication Software team
 * Version:     V1.00.01
 * Date:        2016.10.03

 * Function List:
 1. ...

 * History:
 1. Author:    Sturmegezhutz
    Date:         2016.10.03
    Modification: Create file

 *******************************************************************************/
package com.stur.lib;

import android.content.Context;

import com.stur.lib.app.ContextBase;

import java.lang.reflect.Method;

public class SystemPropertiesProxy {

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
        SystemPropertiesProxy.sContext = context;
    }

    /**
     * 根据给定Key获取值.
     * @return 如果不存在该key则返回空字符串
     * @throws IllegalArgumentException 如果key超过32个字符则抛出该异常
     */
    public static String get(Context context, String key) throws IllegalArgumentException {
        String ret= "";
        try{
            ClassLoader cl = context.getClassLoader();
            @SuppressWarnings("rawtypes")
            Class SystemProperties = cl.loadClass("android.os.SystemProperties");

            //参数类型
            @SuppressWarnings("rawtypes")
            Class[] paramTypes= new Class[1];
            paramTypes[0]= String.class;

            Method get = SystemProperties.getMethod("get", paramTypes);

            //参数
            Object[] params= new Object[1];
            params[0]= new String(key);

            ret= (String) get.invoke(SystemProperties, params);

        }catch( IllegalArgumentException iAE ){
            throw iAE;
        }catch( Exception e ){
            ret= "";
            //TODO
        }

        return ret;
    }

    /**
     * 根据给定Key获取值.
     * @return 如果不存在该key则返回空字符串
     * @throws IllegalArgumentException 如果key超过32个字符则抛出该异常
     */
    public static String get(String key) throws IllegalArgumentException {
        String ret= "";
        try{
            ClassLoader cl = sContext.getClassLoader();
            @SuppressWarnings("rawtypes")
            Class SystemProperties = cl.loadClass("android.os.SystemProperties");

            //参数类型
            @SuppressWarnings("rawtypes")
            Class[] paramTypes= new Class[1];
            paramTypes[0]= String.class;

            Method get = SystemProperties.getMethod("get", paramTypes);

            //参数
            Object[] params= new Object[1];
            params[0]= new String(key);

            ret= (String) get.invoke(SystemProperties, params);
        }catch( IllegalArgumentException iAE ){
            throw iAE;
        }catch( Exception e ){
            ret= "";
            //TODO
        }

        return ret;
    }

    /**
     * 根据Key获取值.
     * @return 如果key不存在, 并且如果def不为空则返回def否则返回空字符串
     * @throws IllegalArgumentException 如果key超过32个字符则抛出该异常
     */
    public static String get(Context context, String key, String def) throws IllegalArgumentException {
        String ret= def;

        try{
            ClassLoader cl = context.getClassLoader();
            @SuppressWarnings("rawtypes")
            Class SystemProperties = cl.loadClass("android.os.SystemProperties");

            //参数类型
            @SuppressWarnings("rawtypes")
            Class[] paramTypes= new Class[2];
            paramTypes[0]= String.class;
            paramTypes[1]= String.class;

            Method get = SystemProperties.getMethod("get", paramTypes);

            //参数
            Object[] params= new Object[2];
            params[0]= new String(key);
            params[1]= new String(def);

            ret= (String) get.invoke(SystemProperties, params);
        }catch( IllegalArgumentException iAE ){
            throw iAE;
        }catch( Exception e ){
            ret= def;
            //TODO
        }
        return ret;
    }

    /**
     * 根据Key获取值.
     * @return 如果key不存在, 并且如果def不为空则返回def否则返回空字符串
     * @throws IllegalArgumentException 如果key超过32个字符则抛出该异常
     */
    public static String get(String key, String def) throws IllegalArgumentException {
        String ret= def;

        try{
            ClassLoader cl = sContext.getClassLoader();
            @SuppressWarnings("rawtypes")
            Class SystemProperties = cl.loadClass("android.os.SystemProperties");

            //参数类型
            @SuppressWarnings("rawtypes")
            Class[] paramTypes= new Class[2];
            paramTypes[0]= String.class;
            paramTypes[1]= String.class;

            Method get = SystemProperties.getMethod("get", paramTypes);

            //参数
            Object[] params= new Object[2];
            params[0]= new String(key);
            params[1]= new String(def);

            ret= (String) get.invoke(SystemProperties, params);
        }catch( IllegalArgumentException iAE ){
            throw iAE;
        }catch( Exception e ){
            ret= def;
            //TODO
        }
        return ret;
    }

    /**
     * 根据给定的key返回int类型值.
     * @param key 要查询的key
     * @param def 默认返回值
     * @return 返回一个int类型的值, 如果没有发现则返回默认值
     * @throws IllegalArgumentException 如果key超过32个字符则抛出该异常
     */
    public static Integer getInt(Context context, String key, int def) throws IllegalArgumentException {
        Integer ret= def;
        try{
            ClassLoader cl = context.getClassLoader();
            @SuppressWarnings("rawtypes")
            Class SystemProperties = cl.loadClass("android.os.SystemProperties");

            //参数类型
            @SuppressWarnings("rawtypes")
            Class[] paramTypes= new Class[2];
            paramTypes[0]= String.class;
            paramTypes[1]= int.class;

            Method getInt = SystemProperties.getMethod("getInt", paramTypes);

            //参数
            Object[] params= new Object[2];
            params[0]= new String(key);
            params[1]= new Integer(def);

            ret= (Integer) getInt.invoke(SystemProperties, params);
        }catch( IllegalArgumentException iAE ){
            throw iAE;
        }catch( Exception e ){
            ret= def;
            //TODO
        }
        return ret;
    }

    /**
     * 根据给定的key返回int类型值.
     * @param key 要查询的key
     * @param def 默认返回值
     * @return 返回一个int类型的值, 如果没有发现则返回默认值
     * @throws IllegalArgumentException 如果key超过32个字符则抛出该异常
     */
    public static Integer getInt(String key, int def) throws IllegalArgumentException {
        Integer ret= def;
        try{
            ClassLoader cl = sContext.getClassLoader();
            @SuppressWarnings("rawtypes")
            Class SystemProperties = cl.loadClass("android.os.SystemProperties");

            //参数类型
            @SuppressWarnings("rawtypes")
            Class[] paramTypes= new Class[2];
            paramTypes[0]= String.class;
            paramTypes[1]= int.class;

            Method getInt = SystemProperties.getMethod("getInt", paramTypes);

            //参数
            Object[] params= new Object[2];
            params[0]= new String(key);
            params[1]= new Integer(def);

            ret= (Integer) getInt.invoke(SystemProperties, params);
        }catch( IllegalArgumentException iAE ){
            throw iAE;
        }catch( Exception e ){
            ret= def;
            //TODO
        }
        return ret;
    }

    /**
     * 根据给定的key返回long类型值.
     * @param key 要查询的key
     * @param def 默认返回值
     * @return 返回一个long类型的值, 如果没有发现则返回默认值
     * @throws IllegalArgumentException 如果key超过32个字符则抛出该异常
     */
    public static Long getLong(Context context, String key, long def) throws IllegalArgumentException {
        Long ret= def;
        try{
            ClassLoader cl = context.getClassLoader();
            @SuppressWarnings("rawtypes")
            Class SystemProperties= cl.loadClass("android.os.SystemProperties");

            //参数类型
            @SuppressWarnings("rawtypes")
            Class[] paramTypes= new Class[2];
            paramTypes[0]= String.class;
            paramTypes[1]= long.class;

            Method getLong = SystemProperties.getMethod("getLong", paramTypes);

            //参数
            Object[] params= new Object[2];
            params[0]= new String(key);
            params[1]= new Long(def);

            ret= (Long) getLong.invoke(SystemProperties, params);
        }catch( IllegalArgumentException iAE ){
            throw iAE;
        }catch( Exception e ){
            ret= def;
            //TODO
        }
        return ret;
    }

    /**
     * 根据给定的key返回long类型值.
     * @param key 要查询的key
     * @param def 默认返回值
     * @return 返回一个long类型的值, 如果没有发现则返回默认值
     * @throws IllegalArgumentException 如果key超过32个字符则抛出该异常
     */
    public static Long getLong(String key, long def) throws IllegalArgumentException {
        Long ret= def;
        try{
            ClassLoader cl = sContext.getClassLoader();
            @SuppressWarnings("rawtypes")
            Class SystemProperties= cl.loadClass("android.os.SystemProperties");

            //参数类型
            @SuppressWarnings("rawtypes")
            Class[] paramTypes= new Class[2];
            paramTypes[0]= String.class;
            paramTypes[1]= long.class;

            Method getLong = SystemProperties.getMethod("getLong", paramTypes);

            //参数
            Object[] params= new Object[2];
            params[0]= new String(key);
            params[1]= new Long(def);

            ret= (Long) getLong.invoke(SystemProperties, params);
        }catch( IllegalArgumentException iAE ){
            throw iAE;
        }catch( Exception e ){
            ret= def;
            //TODO
        }
        return ret;
    }

    /**
     * 根据给定的key返回boolean类型值.
     * 如果值为 'n', 'no', '0', 'false' or 'off' 返回false.
     * 如果值为'y', 'yes', '1', 'true' or 'on' 返回true.
     * 如果key不存在, 或者是其它的值, 则返回默认值.
     * @param key 要查询的key
     * @param def 默认返回值
     * @return 返回一个boolean类型的值, 如果没有发现则返回默认值
     * @throws IllegalArgumentException 如果key超过32个字符则抛出该异常
     */
    public static Boolean getBoolean(Context context, String key, boolean def) throws IllegalArgumentException {
        Boolean ret= def;
        try{
            ClassLoader cl = context.getClassLoader();
            @SuppressWarnings("rawtypes")
            Class SystemProperties = cl.loadClass("android.os.SystemProperties");

            //参数类型
            @SuppressWarnings("rawtypes")
            Class[] paramTypes= new Class[2];
            paramTypes[0]= String.class;
            paramTypes[1]= boolean.class;

            Method getBoolean = SystemProperties.getMethod("getBoolean", paramTypes);

            //参数
            Object[] params= new Object[2];
            params[0]= new String(key);
            params[1]= new Boolean(def);

            ret= (Boolean) getBoolean.invoke(SystemProperties, params);
        }catch( IllegalArgumentException iAE ){
            throw iAE;
        }catch( Exception e ){
            ret= def;
            //TODO
        }
        return ret;
    }

    /**
     * 根据给定的key返回boolean类型值.
     * 如果值为 'n', 'no', '0', 'false' or 'off' 返回false.
     * 如果值为'y', 'yes', '1', 'true' or 'on' 返回true.
     * 如果key不存在, 或者是其它的值, 则返回默认值.
     * @param key 要查询的key
     * @param def 默认返回值
     * @return 返回一个boolean类型的值, 如果没有发现则返回默认值
     * @throws IllegalArgumentException 如果key超过32个字符则抛出该异常
     */
    public static Boolean getBoolean(String key, boolean def) throws IllegalArgumentException {
        Boolean ret= def;
        try{
            ClassLoader cl = sContext.getClassLoader();
            @SuppressWarnings("rawtypes")
            Class SystemProperties = cl.loadClass("android.os.SystemProperties");

            //参数类型
            @SuppressWarnings("rawtypes")
            Class[] paramTypes= new Class[2];
            paramTypes[0]= String.class;
            paramTypes[1]= boolean.class;

            Method getBoolean = SystemProperties.getMethod("getBoolean", paramTypes);

            //参数
            Object[] params= new Object[2];
            params[0]= new String(key);
            params[1]= new Boolean(def);

            ret= (Boolean) getBoolean.invoke(SystemProperties, params);
        }catch( IllegalArgumentException iAE ){
            throw iAE;
        }catch( Exception e ){
            ret= def;
            //TODO
        }
        return ret;
    }

    /**
     * 根据给定的key和值设置属性, 该方法需要特定的权限才能操作.
     * @throws IllegalArgumentException 如果key超过32个字符则抛出该异常
     * @throws IllegalArgumentException 如果value超过92个字符则抛出该异常
     */
    public static void set(Context context, String key, String val) throws IllegalArgumentException {
        try{
            //@SuppressWarnings("unused")
            //DexFile df = new DexFile(new File("/system/app/Settings.apk"));
            @SuppressWarnings("unused")
            ClassLoader cl = context.getClassLoader();
            @SuppressWarnings("rawtypes")
            Class SystemProperties = Class.forName("android.os.SystemProperties");

            //参数类型
            @SuppressWarnings("rawtypes")
            Class[] paramTypes= new Class[2];
            paramTypes[0]= String.class;
            paramTypes[1]= String.class;

            Method set = SystemProperties.getMethod("set", paramTypes);

            //参数
            Object[] params= new Object[2];
            params[0]= new String(key);
            params[1]= new String(val);

            set.invoke(SystemProperties, params);
            Log.d(getTag(), "set property: " + key + " to " + val + " success");
        }catch( IllegalArgumentException iAE ){
            Log.d(getTag(), "set property: " + key + " to " + val + " exception: " + iAE);
            throw iAE;
        }catch( Exception e ){
            Log.d(getTag(), "set property: " + key + " to " + val + " exception: " + e);
            e.printStackTrace();
            //TODO
        }
    }

    /**
     * 根据给定的key和值设置属性, 该方法需要特定的权限才能操作.
     * @throws IllegalArgumentException 如果key超过32个字符则抛出该异常
     * @throws IllegalArgumentException 如果value超过92个字符则抛出该异常
     */
    public static void set(String key, String val) throws IllegalArgumentException {
        try{
            //@SuppressWarnings("unused")
            //DexFile df = new DexFile(new File("/system/app/Settings.apk"));
            @SuppressWarnings("unused")
            ClassLoader cl = sContext.getClassLoader();
            @SuppressWarnings("rawtypes")
            Class SystemProperties = Class.forName("android.os.SystemProperties");

            //参数类型
            @SuppressWarnings("rawtypes")
            Class[] paramTypes= new Class[2];
            paramTypes[0]= String.class;
            paramTypes[1]= String.class;

            Method set = SystemProperties.getMethod("set", paramTypes);

            //参数
            Object[] params= new Object[2];
            params[0]= new String(key);
            params[1]= new String(val);

            set.invoke(SystemProperties, params);
            Log.d(getTag(), "set property: " + key + " to " + val + " success");
        }catch( IllegalArgumentException iAE ){
            Log.d(getTag(), "set property: " + key + " to " + val + " exception: " + iAE);
            throw iAE;
        }catch( Exception e ){
            Log.d(getTag(), "set property: " + key + " to " + val + " exception: " + e);
            e.printStackTrace();
            //TODO
        }
    }
}