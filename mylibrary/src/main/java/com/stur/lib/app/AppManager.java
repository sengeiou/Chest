package com.stur.lib.app;

import com.stur.lib.Log;
import com.stur.lib.activity.ActivityBase;

import java.util.Stack;

/**
 * Created by guanxuejin on 2018/2/27.
 * 应用程序Activity管理类：用于Activity管理和应用程序退出
 */

public class AppManager {
    /**
     * Activity记录栈
     */
    private static Stack<ActivityBase> activityStack = new Stack<ActivityBase>();
    /**
     * AppManager单例
     */
    private static AppManager singleton = new AppManager();

    /**
     * 单例
     */
    private AppManager() {
    }

    /**
     * 获取AppManager单一实例
     */
    public static AppManager getInstance() {
        return singleton;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(ActivityBase activity) {
        activityStack.add(activity);
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public ActivityBase currentActivity() {
        if (activityStack.isEmpty()) {
            return null;
        }
        ActivityBase activity = activityStack.lastElement();
        return activity;
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity() {
        if (activityStack.isEmpty()) {
            return;
        }
        ActivityBase activity = activityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * 结束指定类名的Activity
     */
    public ActivityBase getFirstActivity() {
        if (activityStack.isEmpty()) {
            return null;
        }
        return activityStack.firstElement();
    }

    /**
     * 结束指定类名的Activity
     */
    public ActivityBase getActivity(Class<?> cls) {
        if (activityStack.isEmpty()) {
            return null;
        }
        for (ActivityBase activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                return activity;
            }
        }
        return null;
    }

    /**
     * 移除指定的Activity
     */
    public void removeActivity(ActivityBase activity) {
        if (activityStack.isEmpty()) {
            return;
        }
        if (activity != null) {
            activityStack.remove(activity);
            activity = null;
        }
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(ActivityBase activity) {
        if (activityStack.isEmpty()) {
            return;
        }
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        if (activityStack.isEmpty()) {
            return;
        }
        for (ActivityBase activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        if (activityStack.isEmpty()) {
            return;
        }
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    /**
     * 退出应用程序
     */
    public void AppExit(ContextBase context) {
        try {
            finishAllActivity();
            context.onExit();
        } catch (Exception e) {
            Log.e(this, "退出应用失败\r\n"+ e);
        } finally {
            // 不要执行这个，好像对推送有影响
//            incubator.os.Process.killProcess(incubator.os.Process.myPid());
            System.exit(0);
        }
    }
}
