package com.stur.lib;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 80375140 on 2017/3/31.
 */
public class StringUtils {
    public static final String VERSION_SEPERATOR = ".";
    private final static Pattern EMAILER = Pattern
            .compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
    //MAC地址的格式输入时方便起见无需“-”，只校验字母范围和长度
    private final static Pattern MACER = Pattern
            .compile("[A-Fa-f0-9]{12}");

    /**
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        return TextUtils.isEmpty(str) || TextUtils.isEmpty(str.trim());
    }

    /**
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String... str) {
        for (String s : str) {
            if (isEmpty(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param str
     * @return
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     *
     * @param str
     * @return
     */
    public static boolean isNotEmpty(String... str) {
        for (String s : str) {
            if (isEmpty(s)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        if (email == null || email.trim().length() == 0)
            return false;
        return EMAILER.matcher(email).matches();
    }

    /**
     * 判断IP地址的合法性，这里采用了正则表达式的方法来判断
     * return true，合法
     * */
    public static boolean isIpAddr(String text) {
        if (text != null && !text.isEmpty()) {
            // 定义正则表达式
            String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\." +
                    "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." +
                    "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." +
                    "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
            // 判断ip地址是否与正则表达式匹配
            if (text.matches(regex)) {
                // 返回判断信息
                return true;
            } else {
                // 返回判断信息
                return false;
            }
        }
        return false;
    }


    public static boolean isMacAddr(String mac) {
        if (mac == null || mac.trim().length() == 0)
            return false;
        return MACER.matcher(mac).matches();
    }

    /**
     * @param phoneNumber
     * @return
     */
    public static boolean isMobileNumberValid(String phoneNumber) {
        boolean isValid = false;

        String expression = "0\\d{2,3}-\\d{5,9}|0\\d{2,3}-\\d{5,9}";
        CharSequence inputStr = phoneNumber;

        Pattern pattern = Pattern.compile(expression);

        Matcher matcher = pattern.matcher(inputStr);

        if (matcher.matches()) {
            isValid = true;
        }

        return isValid;

    }

    /**
     *
     * @param phoneNumber
     * @return
     */
    public static boolean isPhoneNumberValid(String phoneNumber) {
        boolean isValid = false;

        String expression = "^((13[0-9])|(15[^4,\\D])|(18[0,0-9]))\\d{8}$";
        CharSequence inputStr = phoneNumber;

        Pattern pattern = Pattern.compile(expression);

        Matcher matcher = pattern.matcher(inputStr);

        if (matcher.matches()) {
            isValid = true;
        }

        return isValid;

    }

    /**
     *
     * @param src
     * @param target
     * @return
     */
    public static boolean equals(String src, String target) {
        if (isEmpty(src) || isEmpty(target))
            return false;
        return src.equals(target);
    }

    public static List<String> testToList(int count) {
        List<String> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            list.add("Hello World! " + i);
        }
        return list;
    }

    /**
     *
     * @param str
     * @param seperator
     * @return
     */
    public static List<String> stringToList(String str, String seperator) {
        List<String> itemList = new ArrayList<String>();
        if (isEmpty(str)) {
            return itemList;
        }
        StringTokenizer st = new StringTokenizer(str, seperator);
        while (st.hasMoreTokens()) {
            itemList.add(st.nextToken());
        }

        return itemList;
    }

    /**
     * String s = StringHelper.format("{0} is {1}", "apple", "fruit");
     * System.out.println(s);    //杈撳嚭  apple is fruit.
     *
     * @param pattern
     * @param args
     * @return
     */
    public static String format(String pattern, Object... args) {
        for (int i = 0; i < args.length; i++) {
            pattern = pattern.replace("{" + i + "}", args[i].toString());
        }
        return pattern;
    }

    /**
     * {"1","2","3"} ==> "1,2,3"
     *
     * @return
     */
    public static String formatArray(String seperator, Object... args) {
        if (args.length <= 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            builder.append(args[i].toString() + seperator);
        }
        return builder.substring(0, builder.length() - 1);
    }

    /**
     * 根据给定的编码方式进行编码。如果调用的是不带参数的getBytes()方法，则使用默认的编码方式
     */
    public static byte[] strToByteArray(String str) {
        if (str == null) {
            return null;
        }
        byte[] byteArray = str.getBytes();
        return byteArray;
    }

    public static String byteArrayToStr(byte[] byteArray) {
        if (byteArray == null) {
            return null;
        }
        String str = new String(byteArray);
        return str;
    }

    /**
     * byte[]转十六进制String
     * 十六进制String，就是字符串里面的字符都是十六进制形式
     * 因为一个byte是八位，可以用两个十六进制位来表示
     * 因此，byte数组中的每个元素可以转换为两个十六进制形式的char
     * 所以最终的HexString的长度是byte数组长度的两倍
     * 之所以要将byte数值和0xFF按位与，是因为我们为了方便后面的无符号移位操作
     * （无符号右移运算符>>>只对32位和64位的值有意义），要将byte数据转换为int类型，
     * 而如果直接转换就会出现问题。因为java里面二进制是以补码形式存在的，
     * 如果直接转换，位扩展会产生问题，如值为-1的byte存储的二进制形式为其补码11111111，
     * 而转换为int后为11111111111111111111111111111111，直接使用该值结果就不对了。
     * 而0xFF默认是int类型，即0x000000FF，一个byte值跟0xFF相与会先将那个byte值转化成int类型运算，
     * 这样，相与的结果中高的24个比特就总会被清0，后面的运算才会正确。
     * @param byteArray
     * @return
     */
    public static String byteArrayToHexStr(byte[] byteArray) {
        if (byteArray == null){
            return null;
        }
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[byteArray.length * 2];
        for (int j = 0; j < byteArray.length; j++) {
            int v = byteArray[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * 十六进制String转byte[]
     */
    public static byte[] hexStrToByteArray(String str)
    {
        if (str == null) {
            return null;
        }
        if (str.length() == 0) {
            return new byte[0];
        }
        byte[] byteArray = new byte[str.length() / 2];
        for (int i = 0; i < byteArray.length; i++){
            String subStr = str.substring(2 * i, 2 * i + 2);
            byteArray[i] = ((byte)Integer.parseInt(subStr, 16));
        }
        return byteArray;
    }
}
