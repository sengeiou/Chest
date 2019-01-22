package com.stur.lib;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 反射工具类
 *
 *
 */
public final class ReflectUtils {
    public static String getTag() {
        return new Object() {
            public String getClassName() {
                String clazzName = this.getClass().getName();
                return clazzName.substring(clazzName.lastIndexOf('.')+1, clazzName.lastIndexOf('$'));
            }
        }.getClassName();
    }

    /**
     * 加载Class
     *
     * @param className 类名
     * @return
     */
    public static Class<?> loadClass(String className) {
        // check parameter
        if (null == className || 0 == className.length()) {
            return null;
        }

        Class<?> clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            printError(e);
        }
        return clazz;
    }

    /**
     * 根据Class创建对象实例
     *
     * @param clazz Class对象
     * @param argsClass 参数类型
     * @param args 参数
     * @return
     */
    public static Object newInstance(Class<?> clazz, Class<?>[] argsClass, Object[] args) {
        // check parameter
        int argsClassLen = (null == argsClass) ? 0 : argsClass.length;
        int argsLen = (null == args) ? 0 : args.length;
        if (null == clazz || argsClassLen != argsLen) {
            return null;
        }

        Object result = null;
        try {
            Constructor<?> constructor = clazz.getConstructor(argsClass);
            result = constructor.newInstance(args);
        } catch (NoSuchMethodException e) {
            printError(e);
        } catch (InstantiationException e) {
            printError(e);
        } catch (IllegalAccessException e) {
            printError(e);
        } catch (IllegalArgumentException e) {
            printError(e);
        } catch (InvocationTargetException e) {
            printError(e);
        }

        return result;
    }

    /**
     * 根据Class创建对象实例（仅适用于对象类型参数，原始类型参数必须指定参数类型）
     *
     * @param clazz
     * @param args 参数
     * @return
     */
    public static Object newInstance(Class<?> clazz, Object[] args) {
        Class<?>[] argsClass = null;
        if (null != args && args.length > 0) {
            argsClass = new Class[args.length];
            for (int i = 0, length = args.length; i < length; i++) {
                argsClass[i] = args[i].getClass();
            }
        }
        return newInstance(clazz, argsClass, args);
    }

    /**
     * 根据类名创建对象实例
     *
     * @param className 类名
     * @param argsClass 参数类型
     * @param args 参数
     * @return
     */
    public static Object newInstance(String className, Class<?>[] argsClass, Object[] args) {
        Class<?> clazz = loadClass(className);
        return newInstance(clazz, argsClass, args);
    }

    /**
     * 获取对象变量值
     *
     * @param obj 对象
     * @param fieldName 变量名称
     * @return
     */
    public static Object getFieldValue(Object obj, String fieldName) {
        // check parameter
        if (null == obj || null == fieldName || 0 == fieldName.length()) {
            return null;
        }

        Object result = null;
        Class<?> clazz = (Class<?>) ((obj instanceof Class) ? obj : obj.getClass());
        try {
            Field field = clazz.getDeclaredField(fieldName);
            if (null != field) {
                result = field.get(obj);
            }
        } catch (NoSuchFieldException e) {
            printError(e);
        } catch (IllegalAccessException e) {
            printError(e);
        } catch (IllegalArgumentException e) {
            printError(e);
        }

        return result;
    }

    /**
     * 获取对象变量值
     *
     * @param obj 对象
     * @param fieldName 变量名称
     * @param defaultValue 默认值
     * @return
     */
    public static Object getFieldValue(Object obj, String fieldName, Object defaultValue) {
        Object value = getFieldValue(obj, fieldName);
        return (null == value) ? defaultValue : value;
    }

    /**
     * 获取类静态变量值
     *
     * @param clazz 类
     * @param fieldName 静态变量名称
     * @return
     */
    public static Object getStaticFieldValue(Class<?> clazz, String fieldName) {
        return getFieldValue(clazz, fieldName);
    }

    /**
     * 获取类静态变量值
     *
     * @param className 类名
     * @param fieldName 静态变量名称
     * @param defaultValue 默认值
     * @return
     */
    public static Object getStaticFieldValue(String className, String fieldName, Object defaultValue) {
        Class<?> clazz = loadClass(className);
        return getFieldValue(clazz, fieldName, defaultValue);
    }

    /**
     * 设置对象变量值
     *
     * @param obj 对象
     * @param fieldName 变量名称
     * @param value 值
     * @return
     */
    public static boolean setFieldValue(Object obj, String fieldName, Object value) {
        // check parameter
        if (null == obj || null == fieldName || 0 == fieldName.length()) {
            return false;
        }

        boolean result = false;
        Class<?> clazz = (Class<?>) ((obj instanceof Class) ? obj : obj.getClass());
        try {
            Field field = clazz.getDeclaredField(fieldName);
            if (null != field) {
                field.set(obj, value);
                result = true;
            }
        } catch (NoSuchFieldException e) {
            printError(e);
        } catch (IllegalAccessException e) {
            printError(e);
        } catch (IllegalArgumentException e) {
            printError(e);
        }

        return result;
    }

    /**
     * 调用对象指定方法（仅适用于无参数方法）
     *
     * @param obj
     * @param methodName
     * @return
     */
    public static Object invoke(Object obj, String methodName) {
        return invoke(obj, methodName, null, null);
    }

    /**
     * 调用对象指定方法（仅适用于无参数方法）
     *
     * @param obj
     * @param methodName
     * @param defaultValue
     * @return
     */
    public static Object invoke(Object obj, String methodName, Object defaultValue) {
        return invoke(obj, methodName, null, null, defaultValue);
    }

    /**
     * 调用对象指定方法（仅适用于对象类型参数，原始类型参数必须指定参数类型）
     *
     * @param obj 对象
     * @param methodName 方法名
     * @param args 参数
     * @return
     */
    public static Object invoke(Object obj, String methodName, Object[] args) {
        Class<?>[] argsClass = null;
        if (null != args && args.length > 0) {
            argsClass = new Class[args.length];
            for (int i = 0, length = args.length; i < length; i++) {
                argsClass[i] = args[i].getClass();
            }
        }

        return invoke(obj, methodName, argsClass, args);
    }

    /**
     * 调用对象指定方法（仅适用于对象类型参数，原始类型参数必须指定参数类型）
     *
     * @param obj 对象
     * @param methodName 方法名
     * @param args 参数
     * @param defaultValue 默认值
     * @return
     */
    public static Object invoke(Object obj, String methodName, Object[] args, Object defaultValue) {
        Object value = invoke(obj, methodName, args);
        return (null == value) ? defaultValue : value;
    }

    /**
     * 调用对象指定方法
     *
     * @param obj 对象
     * @param methodName 方法名
     * @param argsClass 参数类型
     * @param args 参数
     * @return
     */
    public static Object invoke(Object obj, String methodName, Class<?>[] argsClass, Object[] args) {
        // check parameter
        int argsClassLen = (null == argsClass) ? 0 : argsClass.length;
        int argsLen = (null == args) ? 0 : args.length;
        if (null == obj || null == methodName || 0 == methodName.length() || argsClassLen != argsLen) {
            return null;
        }

        Object result = null;
        Class<?> clazz = (Class<?>) ((obj instanceof Class) ? obj : obj.getClass());
        try {
            Method method = clazz.getMethod(methodName, argsClass);
            Log.d(getTag(), "method == null");
            if (null != method) {
                result = method.invoke(obj, args);
            }
        } catch (NoSuchMethodException e) {
            printError(e);
        } catch (IllegalAccessException e) {
            printError(e);
        } catch (IllegalArgumentException e) {
            printError(e);
        } catch (InvocationTargetException e) {
            printError(e);
        }

        return result;
    }

    /**
     * 调用对象指定方法
     *
     * @param obj 对象
     * @param methodName 方法名
     * @param argsClass 参数类型
     * @param args 参数
     * @param defaultValue 默认值
     * @return
     */
    public static Object invoke(Object obj, String methodName, Class<?>[] argsClass, Object[] args, Object defaultValue) {
        Object value = invoke(obj, methodName, argsClass, args);
        return (null == value) ? defaultValue : value;
    }

    /**
     * 调用静态方法（仅适用于无参数方法）
     *
     * @param clazz
     * @param methodName
     * @param defaultValue
     * @return
     */
    public static Object invokeStatic(Class<?> clazz, String methodName, Object defaultValue) {
        return invoke(clazz, methodName, null, null, defaultValue);
    }

    /**
     * 调用静态方法（仅适用于对象类型参数，原始类型参数必须指定参数类型）
     *
     * @param clazz 类名
     * @param methodName 方法名
     * @param args 参数
     * @return
     */
    public static Object invokeStatic(Class<?> clazz, String methodName, Object[] args) {
        return invoke(clazz, methodName, args);
    }

    /**
     * 调用静态方法
     *
     * @param clazz 类名
     * @param methodName 方法名
     * @param argsClass 参数类型
     * @param args 参数
     * @return
     */
    public static Object invokeStatic(Class<?> clazz, String methodName, Class<?>[] argsClass, Object[] args) {
        return invoke(clazz, methodName, argsClass, args);
    }

    /**
     * 调用静态方法
     *
     * @param className 类名
     * @param methodName 方法名
     * @param argsClass 参数类型
     * @param args 参数
     * @return
     */
    public static Object invokeStatic(String className, String methodName, Class<?>[] argsClass, Object[] args) {
        Class<?> clazz = loadClass(className);
        return invoke(clazz, methodName, argsClass, args);
    }

    /**
     * 调用静态方法
     *
     * @param className 类名
     * @param methodName 方法名
     * @param argsClass 参数类型
     * @param args 参数
     * @param defaultValue 默认值
     * @return
     */
    public static Object invokeStatic(String className, String methodName, Class<?>[] argsClass, Object[] args, Object defaultValue) {
        Class<?> clazz = loadClass(className);
        return invoke(clazz, methodName, argsClass, args, defaultValue);
    }

    private static void printError(Exception e) {
        if (null != e.getMessage()) {
            Log.e(getTag(), e.getMessage());
        } else {
            e.printStackTrace();
        }
    }

}
