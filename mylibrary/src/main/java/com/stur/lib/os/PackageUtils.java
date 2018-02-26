package com.stur.lib.os;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import com.stur.lib.Log;

/**
 * Created by guanxuejin on 2018/2/26.
 */

public class PackageUtils {
    public static String getTag() {
        return new Object() {
            public String getClassName() {
                String clazzName = this.getClass().getName();
                return clazzName.substring(clazzName.lastIndexOf('.')+1, clazzName.lastIndexOf('$'));
            }
        }.getClassName();
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
}
