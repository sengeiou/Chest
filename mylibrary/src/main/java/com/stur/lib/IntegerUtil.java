package com.stur.lib;

import android.support.annotation.NonNull;

import com.stur.lib.exception.ParameterException;


/**
 * Created by Administrator on 2016/3/18.
 */
public class IntegerUtil {
    public static int getInt(String intValue) throws ParameterException {
        try {
            return Integer.parseInt(intValue);
        } catch (NumberFormatException e) {
            throw new ParameterException(" Parameter is not a valid value.");
        }
    }

    public static int getInt(String intValue, @NonNull String valueName) throws ParameterException {
        try {
            return Integer.parseInt(intValue);
        } catch (NumberFormatException e) {
            throw new ParameterException(valueName + " is not a valid value.");
        }
    }

    public static long getLong(String longValue, @NonNull String valueName) throws ParameterException {
        try {
            return Long.getLong(longValue);
        } catch (NumberFormatException e) {
            throw new ParameterException(valueName + " is not a valid value.");
        }
    }

    public static int getPriceAtFen(String priceValue) throws ParameterException {
        int pos = priceValue.indexOf(".");
        if (pos < 0) {
            return getInt(priceValue) * 100;
        } else {
            String yuan = priceValue.substring(0, pos);
            String fen = (pos + 1 < priceValue.length()) ? priceValue.substring(pos + 1) : "0";
            return getInt(yuan) * 100 + getInt(fen);
        }
    }

    public static String getPriceStringAtYuan(int priceAtFen) {
        int yuan = priceAtFen%100;
        int fen = priceAtFen - yuan*100;
        return String.valueOf(yuan) + "." + String.valueOf(fen);
    }
}
