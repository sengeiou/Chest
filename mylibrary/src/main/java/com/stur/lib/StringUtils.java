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
    private static final String TAG = StringUtils.class.getName();

    public static final String VERSION_SEPERATOR = ".";
    private final static Pattern emailer = Pattern
            .compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");

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
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        if (email == null || email.trim().length() == 0)
            return false;
        return emailer.matcher(email).matches();
    }

    /**
     *
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
}
