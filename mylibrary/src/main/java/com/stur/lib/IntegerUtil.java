package com.stur.lib;

import android.support.annotation.NonNull;

import com.stur.lib.exception.ParameterException;


/**
 * Created by Administrator on 2016/3/18.
 */
public class IntegerUtil {
    public static String getTag() {
        return new Object() {
            public String getClassName() {
                String clazzName = this.getClass().getName();
                return clazzName.substring(clazzName.lastIndexOf('.')+1, clazzName.lastIndexOf('$'));
            }
        }.getClassName();
    }

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
        int yuan = priceAtFen % 100;
        int fen = priceAtFen - yuan * 100;
        return String.valueOf(yuan) + "." + String.valueOf(fen);
    }

    /**
     * byte数组与 int 的相互转换
     * byte数组限定1-4个元素
     */
    public static int byteArrayToInt(byte[] b) {
        if (b != null && b.length >= 1 && b.length <= 4) {
            switch (b.length) {
                case 1:
                    //Java 总是把 byte 当做有符处理；我们可以通过将其和 0xFF 进行二进制与得到它的无符值
                    return b[0] & 0xFF;
                case 2:
                    return b[1] & 0xFF |
                            (b[0] & 0xFF) << 8;
                case 3:
                    return b[2] & 0xFF |
                            (b[1] & 0xFF) << 8 |
                            (b[0] & 0xFF) << 16;
                case 4:
                    return b[3] & 0xFF |
                            (b[2] & 0xFF) << 8 |
                            (b[1] & 0xFF) << 16 |
                            (b[0] & 0xFF) << 24;
                default:
                    return 0;
            }
        } else {
            Log.e(getTag(), "byteArrayToInt error: malformed length of byte[]");
            return 0;
        }
    }

    public static byte[] intToByteArray(int i) {
        return new byte[]{
                (byte) ((i >> 24) & 0xFF),
                (byte) ((i >> 16) & 0xFF),
                (byte) ((i >> 8) & 0xFF),
                (byte) (i & 0xFF)
        };
    }

    public static Integer intToInteger(int i) {
        return new Integer(i);
    }
}
