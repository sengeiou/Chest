package com.stur.chest.db;

import android.text.TextUtils;

import com.stur.chest.utils.ApiUtils;
import com.stur.lib.SharedPreferenceUtils;

public class SharedPrefsHelper {

    public static void saveAccount(int userId, String userName, String email, String token, int status,
                                   String country, String telNo, String faxNo) {
        clearAccount();
        SharedPreferenceUtils.putInt(ApiUtils.KEY_ID, userId);
        SharedPreferenceUtils.putString(ApiUtils.KEY_NAME, userName);
        SharedPreferenceUtils.putString(ApiUtils.KEY_EMAIL, email);
        SharedPreferenceUtils.putString(ApiUtils.KEY_TOKEN, token);
        SharedPreferenceUtils.putInt(ApiUtils.KEY_STATUS, status);
        SharedPreferenceUtils.putString(ApiUtils.KEY_COUNTRY, country);
        SharedPreferenceUtils.putString(ApiUtils.KEY_TEL_NO, telNo);
        SharedPreferenceUtils.putString(ApiUtils.KEY_FAX_NO, faxNo);
    }

    public static void clearAccount() {
        SharedPreferenceUtils.clearAll();
    }

    public static void saveUserName(String userName) {
        SharedPreferenceUtils.putString(ApiUtils.KEY_NAME, userName);
    }

    public static String getUserName() {
        String userName = SharedPreferenceUtils.getString(ApiUtils.KEY_NAME);
        if (TextUtils.isEmpty(userName)) {
            return "";
        }
        return userName;
    }

    public static String getEmail() {
        String email = SharedPreferenceUtils.getString(ApiUtils.KEY_EMAIL);
        if (TextUtils.isEmpty(email)) {
            return "";
        }
        return email;
    }

    public static String getToken() {
        String token = SharedPreferenceUtils.getString(ApiUtils.KEY_TOKEN);
        if (TextUtils.isEmpty(token)) {
            return "";
        }
        return token;
    }

    public static void saveCountry(String country) {
        SharedPreferenceUtils.putString(ApiUtils.KEY_COUNTRY, "");
    }

    public static String getCountry() {
        String country = SharedPreferenceUtils.getString(ApiUtils.KEY_COUNTRY);
        if (TextUtils.isEmpty(country)) {
            return "";
        }
        return country;
    }

    public static void saveTelNo(String telNo) {
        SharedPreferenceUtils.putString(ApiUtils.KEY_TEL_NO, "");
    }

    public static String getTelNo() {
        String telNo = SharedPreferenceUtils.getString(ApiUtils.KEY_TEL_NO);
        if (TextUtils.isEmpty(telNo)) {
            return "";
        }
        return telNo;
    }

    public static void saveFaxNo(String faxNo) {
        SharedPreferenceUtils.putString(ApiUtils.KEY_FAX_NO, "");
    }

    public static String getFaxNo() {
        String faxNo = SharedPreferenceUtils.getString(ApiUtils.KEY_FAX_NO);
        if (TextUtils.isEmpty(faxNo)) {
            return "";
        }
        return faxNo;
    }
}
