package com.stur.lib.os;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.net.Uri;
import android.telecom.DefaultDialerManager;
import android.text.TextUtils;

import com.stur.lib.Log;
import com.stur.lib.constant.StActivityName;

import java.util.List;

public class PackageUtils {
    public static final int AID_ROOT        = 0;  /* traditional unix root user */
    public static final int  AID_SYSTEM     = 1000;  /* system server */
    public static final int  AID_RADIO      = 1001;  /* telephony subsystem, RIL */
    public static final int AID_BLUETOOTH   = 1002;  /* bluetooth subsystem */
    public static final int AID_GRAPHICS    = 1003;  /* graphics devices */

    public static String getTag() {
        return new Object() {
            public String getClassName() {
                String clazzName = this.getClass().getName();
                return clazzName.substring(clazzName.lastIndexOf('.') + 1, clazzName.lastIndexOf('$'));
            }
        }.getClassName();
    }

    public static int getAppVersionCode(Context context) {
        if (context != null) {
            PackageManager pm = context.getPackageManager();
            if (pm != null) {
                PackageInfo pi;
                try {
                    pi = pm.getPackageInfo(context.getPackageName(), 0);
                    if (pi != null) {
                        return pi.versionCode;
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return -1;
    }

    public static int getAppUid(Context context, String pkt) throws NameNotFoundException {
        PackageManager pm = context.getPackageManager();
        ApplicationInfo ai = pm.getApplicationInfo(pkt, PackageManager.GET_ACTIVITIES);
        Log.d(getTag(), "getAppUid: " + ai.uid);
        return ai.uid;
    }

    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    public static String getAppInfo(Context context) {
        try {
            String pkName = context.getPackageName();
            String versionName = context.getPackageManager().getPackageInfo(
                    pkName, 0).versionName;
            int versionCode = context.getPackageManager()
                    .getPackageInfo(pkName, 0).versionCode;
            return pkName + "   " + versionName + "  " + versionCode;
        } catch (Exception e) {
        }
        return null;
    }

    public static int getMyPid() {
        return android.os.Process.myPid();
    }

    public static int getMyTid() {
        return android.os.Process.myTid();
    }

    public static int getMyUid() {
        return android.os.Process.myUid();
    }

    public static String getMyUA() {
        return System.getProperty("http.agent");
    }

    /**
     * 是否是系统软件或者是系统软件的更新软件
     * @return
     */
    public boolean isSystemApp(PackageInfo pInfo) {
        return ((pInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    public boolean isSystemUpdateApp(PackageInfo pInfo) {
        return ((pInfo.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0);
    }

    public boolean isUserApp(PackageInfo pInfo) {
        return (!isSystemApp(pInfo) && !isSystemUpdateApp(pInfo));
    }

    public static boolean isApkInstalled(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 直接输入包名即可查看应用签名密钥KeyStores的 MD5 值
    public static Signature[] getRawSignature(Context paramContext, String paramString) {
        if ((paramString == null) || (paramString.length() == 0)) {
            Log.e(getTag(), "getSignature, packageName is null");
            return null;
        }
        PackageManager localPackageManager = paramContext.getPackageManager();
        PackageInfo localPackageInfo;
        try {
            localPackageInfo = localPackageManager.getPackageInfo(paramString, PackageManager.GET_SIGNATURES);
            if (localPackageInfo == null) {
                Log.e(getTag(), "info is null, packageName = " + paramString);
                return null;
            }
        } catch (PackageManager.NameNotFoundException localNameNotFoundException) {
            Log.e(getTag(), "NameNotFoundException");
            return null;
        }
        return localPackageInfo.signatures;
    }

    public static String getSign(Context context, String paramString) {
        Signature[] arrayOfSignature = getRawSignature(context, paramString);
        if ((arrayOfSignature == null) || (arrayOfSignature.length == 0)) {
            Log.e(getTag(), "signs is null");
            return null;
        }

        while (true) {
            int i = arrayOfSignature.length;
            for (int j = 0; j < i; j++)
                return MD5.getMessageDigest(arrayOfSignature[j].toByteArray());
        }
    }

    /*
    * @param context the caller context
    * @param pkt the whole packet path name
    * @param cls the whole class path name
    */
    public static void startActivity(Context context, String pkt, String cls, boolean isMainAcivity) {
        Intent intent;
        if (isMainAcivity) {
            intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
        } else {
            intent = new Intent();
        }
        ComponentName cn = new ComponentName(pkt, cls);
        intent.setComponent(cn);
        context.startActivity(intent);
    }

    public static void startOfflineLogActivity(Context context) {
        String[] cpn = null;
        if (OsUtils.isHardWareVendorQualcomm()) {
            cpn = StActivityName.OFFLINE_LOG_YL.split("/");
        } else if (OsUtils.isHardWareVendorMediaTek()) {
            cpn = StActivityName.OFFLINE_LOG_MTK.split("/");
        }

        ComponentName componentName = new ComponentName(cpn[0], cpn[1]);
        Intent intent = new Intent();
        intent.setComponent(componentName);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /*
     * prerogative method to initiate clearing user data for a system package
     * new a IPackageDataObserver.Stub to handled failed issue if needed
     * @param context the caller context
     * @param packageName the package name to be cleared
     */
    public static void initiateClearUserData(Context context, String packageName) {
        // Invoke uninstall or clear user data based on sysPackage
        Log.i(getTag(), "Clearing user data for package : " + packageName);
        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        //依赖framework.jar，暂时屏蔽
        /*boolean res = am.clearApplicationUserData(packageName, new IPackageDataObserver.Stub() {
            public void onRemoveCompleted(final String pkt, final boolean succeeded) {
                //判断succeeded，如果清除失败在这里处理
                boolean succ = succeeded;
            }
        });
        if (!res) {
            // Clearing data failed for some obscure reason. Just log error for now
            Log.e(getTag(), "Couldnt clear application user data for package:"+packageName);
        } else {
        }*/
    }

    /**
     * 系统是否支持某Feature
     */
    public static boolean hasSystemFeature(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_PRINTING);
    }

    /**
     * 获取系统默认App
     * @param context
     * @return
     */
    public static String getDefaultApp(Context context) {
        return null;
    }

    public static String getDefaultDialer(Context context) {
        String ret = DefaultDialerManager.getDefaultDialerApplication(context);
        Log.d(getTag(), "getDefaultDialer X: " + ret);
        return ret;
    }

    /**
     * 设置系统默认浏览器应用
     * @param context
     * @param pkg
     */
    public static void setDefaultBrowser(Context context, String pkg) {
        Log.d(getTag(), "setDefaultBrowser E:");
        PackageManager packageManager = context.getPackageManager();
        String str1 = "android.intent.category.DEFAULT";
        String str2 = "android.intent.category.BROWSABLE";
        String str3 = "android.intent.action.VIEW";

        // 设置默认项的必须参数之一,用户的操作符合该过滤器时,默认设置起效
        IntentFilter filter = new IntentFilter(str3);
        filter.addCategory(str1);
        filter.addCategory(str2);
        filter.addDataScheme("http");
        // 设置浏览页面用的Activity
        ComponentName component = new ComponentName("com.UCMobile",
                "com.UCMobile.main.UCMobile");

        Intent intent = new Intent(str3);
        intent.addCategory(str2);
        intent.addCategory(str1);
        Uri uri = Uri.parse("http://");
        intent.setDataAndType(uri, null);

        // 找出手机当前安装的所有浏览器程序
        List<ResolveInfo> resolveInfoList = packageManager
                .queryIntentActivities(intent,
                        PackageManager.GET_INTENT_FILTERS);

        int size = resolveInfoList.size();
        ComponentName[] arrayOfComponentName = new ComponentName[size];
        for (int i = 0; i < size; i++) {
            ActivityInfo activityInfo = resolveInfoList.get(i).activityInfo;
            String packageName = activityInfo.packageName;
            String className = activityInfo.name;

            Log.d(getTag(), "setDefaultBrowser： packageName = " + packageName + "， className = " + className);

            // 清除之前的默认设置
            packageManager.clearPackagePreferredActivities(packageName);
            ComponentName componentName = new ComponentName(packageName,
                    className);
            arrayOfComponentName[i] = componentName;
        }
        packageManager.addPreferredActivity(filter,
                IntentFilter.MATCH_CATEGORY_SCHEME, arrayOfComponentName,
                component);
    }

    /**
     * 设置系统默认InCallUI，例如 com.android.dialer
     * 如需ROM修改，则改/device/qcom/common/product/overlay/packages/services/Telecomm/res/values/config.xml
     * 和/vendor/qcom/proprietary/resource-overlay/common/Telecomm/res/values/config.xml
     * @param context
     * @param pkg
     */
    public static void setDefaultDialer(Context context, String pkg) {
        Log.d(getTag(), "setDefaultDialer E: pkg = " + pkg);
        if (pkg != null && !pkg.equals(getDefaultDialer(context))) {
            DefaultDialerManager.setDefaultDialerApplication(context, pkg);
        }
    }
}
