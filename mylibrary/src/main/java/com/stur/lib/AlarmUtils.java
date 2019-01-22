/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.stur.lib;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.format.DateFormat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormatSymbols;
import java.util.Calendar;

public class AlarmUtils {
    // ~ Static fields/initializers
    // ---------------------------------------------\

    /** Action */
    static final String ACTION_ALARM_SHUTDOWN = "stur.intent.action.ALARM_SHUTDOWN";
    /** Action */
    static final String ACTION_ALARM_STARTUP = "stur.intent.action.ALARM_STARTUP";

    /** intent extra key */
    static final String EXTRA_TIME = "time";
    /** 1 */
    static final String INDEX_MONDEY = "0";
    /** 2 */
    static final String INDEX_TUESDAY = "1";
    /** 3 */
    static final String INDEX_WEDNESDAY = "2";
    /** 4 */
    static final String INDEX_THURSDAY = "3";
    /** 5 */
    static final String INDEX_FRIDAY = "4";
    /** 6 */
    static final String INDEX_SATURDAY = "5";
    /** 7 */
    static final String INDEX_SUNDAY = "6";
    /** 1 */
    private static final int VALUE_MONDEY = 0x01;
    /** 2 */
    private static final int VALUE_TUESDAY = 0x02;
    /** 3 */
    private static final int VALUE_WEDNESDAY = 0x04;
    /** 4 */
    private static final int VALUE_THURSDAY = 0x08;
    /** 5 */
    private static final int VALUE_FRIDAY = 0x10;
    /** 6 */
    private static final int VALUE_SATURDAY = 0x20;
    /** 7 */
    private static final int VALUE_SUNDAY = 0x40;
    /** 0x7f */
    private static final int VALUE_SEVEN_F = 0x7f;
    /** 4 */
    private static final int VALUE_FOUR = 4;
    /** 5 */
    private static final int VALUE_FIVE = 5;
    /** 7 */
    private static final int VALUE_SEVEN = 7;
    /** 24 */
    private static final int VALUE_TWENTY_FOUR = 24;
    /** 60 */
    private static final int VALUE_SIXTY = 60;
    /** 1000 */
    private static final int VALUE_ONE_THOUSAND = 1000;
    /** : */
    public static final String COMMA = ":";
    /** 12 */
    private static final String M12 = "h:mm aa";
    /** 24 */
    static final String M24 = "kk:mm";
    private static PendingIntent mPwOnAlarmintent = null;
    private static PendingIntent mPwOffAlarmintent = null;

    /**
     *  Whether the auto startup mode is enabled.
     * 0 = disabled
     * 1 = enabled
     * @hide
     */
    public static final String AUTO_STARTUP_ENABLE = "auto_startup_enable";
    /**
     *  Whether the auto stutdown mode is enabled.
     * 0 = disabled
     * 1 = enabled
     * @hide
     */
    public static final String AUTO_SHUTDOWN_ENABLE = "auto_shutdown_enable";
    /**
     * auto shutdown repeat schedule settings
     * value is string,such as "0, 1, 2, 3, 4, 5, 6"
     *
     * @hide
     */
    public static final String AUTO_SHUTDOWN_REPEAT = "auto_shutdown_repeat";
    /**
     * auto startup repeat schedule settings
     * value is string,such as "0, 1, 2, 3, 4, 5, 6"
     *
     * @hide
     */
    public static final String AUTO_STARTUP_REPEAT = "auto_startup_repeat";
    /**
     * auto startup time settings
     * value is string,such as "7:00"
     *
     * @hide
     */
    public static final String AUTO_STARTUP_TIME = "auto_startup_time";
     /**
     * auto shutdown time settings
     * value is string,such as "23:00"
     *
     * @hide
     */
    public static final String AUTO_SHUTDOWN_TIME = "auto_shutdown_time";

    // ~ Constructors
    // -----------------------------------------------------------


    public static String getTag() {
        return new Object() {
            public String getClassName() {
                String clazzName = this.getClass().getName();
                return clazzName.substring(clazzName.lastIndexOf('.')+1, clazzName.lastIndexOf('$'));
            }
        }.getClassName();
    }

    /**
     * set alarms
     *
     * @param context
     * @param shutdown
     *            true:shutdown; false:start
     */
    public static void setAlarmFromDB(Context context, boolean shutdown) {
        Log.d(getTag(), "setAlarmFromDB, " + (shutdown ? "Auto Power Off" : "Auto Power On"));
        if (shutdown) {
            String strRepeadDays = Settings.System.getString(context.getContentResolver(),
                    AUTO_SHUTDOWN_REPEAT);
            String strAlarmTime = Settings.System.getString(context.getContentResolver(),
                    AUTO_SHUTDOWN_TIME);
            AlarmUtils.DaysOfWeek daysOfWeek = getDaysOfWeek(strRepeadDays);
            int hour = getHour(strAlarmTime);
            int minute = getMinute(strAlarmTime);

            boolean autoShutdown = Settings.System.getInt(context.getContentResolver(),
                    AUTO_SHUTDOWN_ENABLE, 0) == 1;
            Log.d(getTag(), "setAlarmFromDB, autoShutdown = " + autoShutdown);
            if (autoShutdown) {
                long time = AlarmUtils.calculateAlarm(hour, minute, daysOfWeek).getTimeInMillis();

                setAlarm(context, time, true);
            }
        } else {
            String strRepeadDays = Settings.System.getString(context.getContentResolver(),
                    AUTO_STARTUP_REPEAT);
            String strAlarmTime = Settings.System.getString(context.getContentResolver(),
                    AUTO_STARTUP_TIME);
            AlarmUtils.DaysOfWeek daysOfWeek = getDaysOfWeek(strRepeadDays);
            int hour = getHour(strAlarmTime);
            int minute = getMinute(strAlarmTime);

            boolean autoStart = Settings.System.getInt(context.getContentResolver(),
                    AUTO_STARTUP_ENABLE, 0) == 1;
            Log.d(getTag(), "setAlarmFromDB, autoStart = " + autoStart);
            if (autoStart) {
                long time = AlarmUtils.calculateAlarm(hour, minute, daysOfWeek).getTimeInMillis();

                setAlarm(context, time, false);
            }
        }
    }

    public static void setAlarmFromDBForDate(Context context, boolean shutdown) {
        Log.d(getTag(), "setAlarmFromDB, " + (shutdown ? "Auto Power Off" : "Auto Power On"));
        if (shutdown) {
            String strRepeadDays = Settings.System.getString(context.getContentResolver(),
                    AUTO_SHUTDOWN_REPEAT);
            String strAlarmTime = Settings.System.getString(context.getContentResolver(),
                    AUTO_SHUTDOWN_TIME);
            AlarmUtils.DaysOfWeek daysOfWeek = getDaysOfWeek(strRepeadDays);
            int hour = getHour(strAlarmTime);
            int minute = getMinute(strAlarmTime);

            boolean autoShutdown = Settings.System.getInt(context.getContentResolver(),
                    AUTO_SHUTDOWN_ENABLE, 0) == 1;
            Log.d(getTag(), "setAlarmFromDB, autoShutdown = " + autoShutdown);
            if (autoShutdown) {
                long time = AlarmUtils.calculateAlarm(hour, minute, daysOfWeek).getTimeInMillis();
                setAlarm(context, time, true);

                long shutdownTime = time - System.currentTimeMillis();
                if (shutdownTime > 0) {
                    String strAlarmToast = AlarmUtils.formatToast(context, time, shutdown);
                    UIHelper.toastMessage(context, strAlarmToast);
                }
            }
        } else {
            String strRepeadDays = Settings.System.getString(context.getContentResolver(),
                    AUTO_STARTUP_REPEAT);
            String strAlarmTime = Settings.System.getString(context.getContentResolver(),
                    AUTO_STARTUP_TIME);
            AlarmUtils.DaysOfWeek daysOfWeek = getDaysOfWeek(strRepeadDays);
            int hour = getHour(strAlarmTime);
            int minute = getMinute(strAlarmTime);

            boolean autoStart = Settings.System.getInt(context.getContentResolver(),
                    AUTO_STARTUP_ENABLE, 0) == 1;
            Log.d(getTag(), "setAlarmFromDB, autoStart = " + autoStart);
            if (autoStart) {
                long time = AlarmUtils.calculateAlarm(hour, minute, daysOfWeek).getTimeInMillis();
                setAlarm(context, time, false);
                long shutdownTime = time - System.currentTimeMillis();
                if (shutdownTime > 0) {
                    String strAlarmToast = AlarmUtils.formatToast(context, time, shutdown);
                    UIHelper.toastMessage(context, strAlarmToast);
                }
            }
        }
    }

    /**
     * set alarm
     *
     * @param context
     * @param time
     * @param shutdown
     *            true:shutdown; false:start
     */
    // @SuppressWarnings("unused")
    public static void setAlarm(Context context, long time, boolean shutdown) {
        String strAlarmToast = AlarmUtils.formatToast(context, time, shutdown);
        Log.d(getTag(), "setAlarm, time = " + time + ", toast = " + strAlarmToast);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (shutdown) {
            long current = System.currentTimeMillis();
            long shutdownTime = time - System.currentTimeMillis();
            Log.d(getTag(), "setAlarm time = " + time + ", current = " + current );
            Intent pwOffIntent = new Intent(ACTION_ALARM_SHUTDOWN);
            pwOffIntent.putExtra(EXTRA_TIME, time);
            mPwOffAlarmintent = PendingIntent.getBroadcast(context, 0, pwOffIntent, PendingIntent.FLAG_ONE_SHOT);

            if (shutdownTime > 0) {
                Log.d(getTag(), "setAlarm time = " + time + ", current = " + current + ";elapsedRealtime = "
                        + shutdownTime + SystemClock.elapsedRealtime());
                am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, shutdownTime + SystemClock.elapsedRealtime(),
                        mPwOffAlarmintent);
            } else {
                // am.set(AlarmManager.RTC_POWEROFF_WAKEUP, time,
                // mPwOffAlarmintent);
                //modified by stur 20180726 for android8.1 upgrade
                //am.setExactAndAllowWhileIdle(AlarmManager.RTC_POWEROFF_WAKEUP, time, mPwOffAlarmintent);
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, mPwOffAlarmintent);
            }
        } else {
            Intent pwOnIntent = new Intent(ACTION_ALARM_STARTUP);
            pwOnIntent.putExtra(EXTRA_TIME, time);
            mPwOnAlarmintent = PendingIntent.getBroadcast(context, 0, pwOnIntent, PendingIntent.FLAG_ONE_SHOT);
            try {
                // am.set(AlarmManager.RTC_ALARM_WAKEUP, time,
                // mPwOnAlarmintent);//replace
                Log.d(getTag(), "am.setWindow(AlarmManager.RTC_POWEROFF_WAKEUP, time, mPwOnAlarmintent)");
                // am.set(AlarmManager.RTC_POWEROFF_WAKEUP, time,
                // mPwOnAlarmintent);
                /// am.setWindow(getAlarmType(), time, 5 * 1000,
                // mPwOnAlarmintent);
                am.setExactAndAllowWhileIdle(getAlarmType(), time, mPwOnAlarmintent);
            } catch (Exception e) {
                // TODO Auto-generated catch block
            }
        }
    }

    /**
     * RTCAlarm
     *
     * @param atTimeInSec
     */
    static void saveRTCAlarmFile(final long atTimeInSec) {
        File saveFile = new File("/sys/comip/rtc_alarm");
        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream(saveFile);
            outStream.write(String.valueOf(atTimeInSec).getBytes());
            // outStream.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            /* for change coverity check:resource leak, yanglun, 2015.10.29 */
        } finally {
            if (outStream != null) {
                try {
                    outStream.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * cancel alarm
     *
     * @param context
     * @param shutdown
     *            true:shutdwon; false:start
     */
    public static void cancelAlarm(Context context, boolean shutdown) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Log.d(getTag(), (shutdown ? "cancelAlarm, Auto Power Off " : "cancelAlarm, Auto Power On")
                + ", mPwOffAlarmintent = " + mPwOffAlarmintent + ", mPwOnAlarmintent = " + mPwOnAlarmintent);

        boolean bHandled = false;
        if (shutdown) {
            if (mPwOffAlarmintent != null) {
                bHandled = true;
                am.cancel(mPwOffAlarmintent);
                mPwOffAlarmintent = null;
            }
        } else {
            if (mPwOnAlarmintent != null) {
                bHandled = true;
                try {
                    am.cancel(mPwOnAlarmintent);

                    if ((Features.PLATFORM == Features.PLATFORM_MTK)
                       ) {
                        Log.d(getTag(), "is a startup alarm of mtk platform, so we need call cancelPoweroffAlarm");
                        cancelPoweroffAlarm(am, "com.android.settings");
                    }
                } catch (Exception ex) {
                    Log.w(getTag(), "cancelAlarm got a Exception: " + ex.getMessage());
                    bHandled = false;
                }
                mPwOnAlarmintent = null;
            }
        }

        /**
         * eric yan 110721 process has been killed? recreate
         * PendingIntent(intent,flags) PendingIntentRecord,cancel in service
         */
        if (!bHandled) {
            Intent intent = new Intent(shutdown ? ACTION_ALARM_SHUTDOWN : ACTION_ALARM_STARTUP);
            PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            if (pIntent != null) {
                am.cancel(pIntent);

                Log.d(getTag(), "!!cancelAlarm, process no exists!!");
            }
        }
    }

    /**
     * DaysOfWeek
     *
     * @param strOrigDay
     *
     * @return DaysOfWeek
     */
    private static AlarmUtils.DaysOfWeek getDaysOfWeek(String strOrigDay) {
        Log.d(getTag(), "formatDay");

        AlarmUtils.DaysOfWeek daysOfWeek = new AlarmUtils.DaysOfWeek(0);

        if ((strOrigDay == null) || (strOrigDay.length() == 0)) {
            return daysOfWeek;
        }

        if (strOrigDay.indexOf(INDEX_MONDEY) >= 0) {
            daysOfWeek.set(VALUE_MONDEY, true);
        }

        // 2
        if (strOrigDay.indexOf(INDEX_TUESDAY) >= 0) {
            daysOfWeek.set(VALUE_TUESDAY, true);
        }

        // 3
        if (strOrigDay.indexOf(INDEX_WEDNESDAY) >= 0) {
            daysOfWeek.set(VALUE_WEDNESDAY, true);
        }

        // 4
        if (strOrigDay.indexOf(INDEX_THURSDAY) >= 0) {
            daysOfWeek.set(VALUE_THURSDAY, true);
        }

        // 5
        if (strOrigDay.indexOf(INDEX_FRIDAY) >= 0) {
            daysOfWeek.set(VALUE_FRIDAY, true);
        }

        // 6
        if (strOrigDay.indexOf(INDEX_SATURDAY) >= 0) {
            daysOfWeek.set(VALUE_SATURDAY, true);
        }

        // 7
        if (strOrigDay.indexOf(INDEX_SUNDAY) >= 0) {
            daysOfWeek.set(VALUE_SUNDAY, true);
        }

        return daysOfWeek;
    }

    /**
     * Hour
     *
     * @param strTime
     *
     * @return hour
     */
    static int getHour(String strTime) {

        int returnValue = 0;

        if ((strTime == null) || (strTime.length() == 0)) {
            return returnValue;
        }

        Log.d(getTag(), "getHour, strTime = " + strTime);

        String[] times = strTime.split(COMMA);

        if (times.length >= 1) {
            try {
                returnValue = Integer.parseInt(times[0]);
            } catch (NumberFormatException e) {
                Log.e(getTag(), "getHour, cannt convert to int. time = " + times[0]);
            }
        }

        return returnValue;
    }

    /**
     * Minute
     *
     * @param strTime
     *
     * @return Minute
     */
    static int getMinute(String strTime) {

        int returnValue = 0;

        if ((strTime == null) || (strTime.length() == 0)) {
            return returnValue;
        }

        Log.d(getTag(), "Minute, strTime = " + strTime);

        String[] times = strTime.split(COMMA);

        if (times.length >= 2) {
            try {
                returnValue = Integer.parseInt(times[1]);
            } catch (NumberFormatException e) {
                Log.e(getTag(), "Minute, cannt convert to int. time = " + times[1]);
            }
        }

        return returnValue;
    }

    /**
     * tpye
     *
     * @param context
     *            Context
     * @param c
     *            Calendar
     *
     * @return strings
     */
    static String formatTime(final Context context, Calendar c) {
        String format = get24HourMode(context) ? M24 : M12;

        return (c == null) ? "" : (String) DateFormat.format(format, c);
    }

    /**
     * 24 or 12
     *
     * @param context
     *            Context
     *
     * @return true if clock is set to 24-hour mode
     */
    static boolean get24HourMode(final Context context) {
        return android.text.format.DateFormat.is24HourFormat(context);
    }

    /**
     * Given an alarm in hours and minutes, return a time suitable for setting
     * in AlarmManager.
     *
     * @param hour
     *            Always in 24 hour 0-23
     * @param minute
     *            0-59
     * @param daysOfWeek
     *            0-59
     *
     * @return a time suitable for setting in AlarmManager.
     */
    static Calendar calculateAlarm(int hour, int minute, DaysOfWeek daysOfWeek) {
        // start with now
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());

        int nowHour = c.get(Calendar.HOUR_OF_DAY);
        int nowMinute = c.get(Calendar.MINUTE);

        // if alarm is behind current time, advance one day
        if ((hour < nowHour) || ((hour == nowHour) && (minute <= nowMinute))) {
            c.add(Calendar.DAY_OF_YEAR, 1);
        }

        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        int addDays = daysOfWeek.getNextAlarm(c);

        /*
         * Log.v("** TIMES * " + c.getTimeInMillis() + " hour " + hour +
         * " minute " + minute + " dow " + c.get(Calendar.DAY_OF_WEEK) +
         * " from now " + addDays);
         */
        if (addDays > 0) {
            c.add(Calendar.DAY_OF_WEEK, addDays);
        }

        return c;
    }

    /**
     * format "Alarm set for 2 days 7 hours and 53 minutes from now"
     *
     * @param context
     * @param time
     *
     * @return left time to next alarm
     */
    static String formatToast(Context context, long time) {

        long delta = time - System.currentTimeMillis();
        long hours = delta / (VALUE_ONE_THOUSAND * VALUE_SIXTY * VALUE_SIXTY);
        long minutes = (delta / (VALUE_ONE_THOUSAND * VALUE_SIXTY)) % VALUE_SIXTY;
        long days = hours / VALUE_TWENTY_FOUR;
        hours = hours % VALUE_TWENTY_FOUR;
        String daySeq = (days == 0) ? ""
                : ((days == 1) ? context.getString(R.string.day)
                        : context.getString(R.string.days, Long.toString(days)));

        String minSeq = (minutes == 0) ? ""
                : ((minutes == 1) ? context.getString(R.string.minute)
                        : context.getString(R.string.minutes, Long.toString(minutes)));

        String hourSeq = (hours == 0) ? ""
                : ((hours == 1) ? context.getString(R.string.hour)
                        : context.getString(R.string.hours, Long.toString(hours)));

        boolean dispDays = days > 0;
        boolean dispHour = hours > 0;
        boolean dispMinute = minutes > 0;

        int index = (dispDays ? 1 : 0) | (dispHour ? 2 : 0) | (dispMinute ? VALUE_FOUR : 0);
        String[] formats = context.getResources().getStringArray(R.array.alarm_set);
        return String.format(formats[index], daySeq, hourSeq, minSeq);
    }

    static String formatToast(Context context, long time, boolean shutdown) {

        long delta = time - System.currentTimeMillis();
        long hours = delta / (VALUE_ONE_THOUSAND * VALUE_SIXTY * VALUE_SIXTY);
        long minutes = (delta / (VALUE_ONE_THOUSAND * VALUE_SIXTY)) % VALUE_SIXTY;
        long days = hours / VALUE_TWENTY_FOUR;
        hours = hours % VALUE_TWENTY_FOUR;
        String daySeq = (days == 0) ? ""
                : ((days == 1) ? context.getString(R.string.day)
                : context.getString(R.string.days, Long.toString(days)));

        String minSeq = (minutes == 0) ? ""
                : ((minutes == 1) ? context.getString(R.string.minute)
                : context.getString(R.string.minutes, Long.toString(minutes)));

        String hourSeq = (hours == 0) ? ""
                : ((hours == 1) ? context.getString(R.string.hour)
                : context.getString(R.string.hours, Long.toString(hours)));

        boolean dispDays = days > 0;
        boolean dispHour = hours > 0;
        boolean dispMinute = minutes > 0;

        int index = (dispDays ? 1 : 0) | (dispHour ? 2 : 0) | (dispMinute ? VALUE_FOUR : 0);
        String[] formats;
        if (shutdown) {
            formats = context.getResources().getStringArray(R.array.alarm_shutdown_set);
        } else {
            formats = context.getResources().getStringArray(R.array.alarm_startup_set);
        }
        return String.format(formats[index], daySeq, hourSeq, minSeq);
    }

    /**
     * Tells the StatusBar whether the alarm is enabled or disabled
     *
     * @param context
     *            Context
     * @param enabled
     *            enable or disable
     */
    static void setStatusBarIcon(Context context, boolean enabled) {
        Intent alarmChanged = new Intent(Intent.ACTION_ALARM_CHANGED);
        alarmChanged.putExtra("alarmSet", enabled);
        context.sendBroadcast(alarmChanged);
    }

    // ~ Inner Classes
    // ----------------------------------------------------------

    /**
     * flag
     *
     * @author $author$
     * @version $Revision: 1.3 $
     */
    static final class DaysOfWeek {
        /** 7 */
        private static int[] DAY_MAP = new int[] { Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY,
                Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY, };

        /** Bitmask of all repeating days */
        private int mDays;

        /**
         * Creates a new DaysOfWeek object.
         *
         * @param days
         */
        DaysOfWeek(int days) {
            mDays = days;
        }

        /**
         * strings
         *
         * @param context
         *            Context
         * @param showNever
         *            default
         *
         * @return strings
         */
        public String toString(Context context, boolean showNever) {
            StringBuilder ret = new StringBuilder();

            // no days
            if (mDays == 0) {
                return showNever ? "R.string.never" : "";
            }

            // every day
            if (mDays == VALUE_SEVEN_F) {
                return "R.string.every_day";
            }

            // count selected days
            int dayCount = 0;

            // count selected days
            int days = mDays;

            while (days > 0) {
                if ((days & 1) == 1) {
                    dayCount++;
                }

                days >>= 1;
            }

            // short or long form?
            DateFormatSymbols dfs = new DateFormatSymbols();
            String[] dayList = (dayCount > 1) ? dfs.getShortWeekdays() : dfs.getWeekdays();

            // selected days
            for (int i = 0; i < VALUE_SEVEN; i++) {
                if ((mDays & (1 << i)) != 0) {
                    ret.append(dayList[DAY_MAP[i]]);
                    dayCount -= 1;

                    if (dayCount > 0) {
                        ret.append(", ");
                    }
                }
            }

            return ret.toString();
        }

        /**
         * again?
         *
         * @param day
         *
         * @return contains?
         */
        private boolean isSet(int day) {
            return ((mDays & (1 << day)) > 0);
        }

        /**
         * contain?
         *
         * @param day
         * @param set
         *            true:yes fasle:cancel
         */
        public void set(int day, boolean set) {
            if (set) {
                mDays |= day;
            } else {
                mDays &= ~(1 << day);
            }
        }

        /**
         * again?
         *
         * @param dow
         */
        public void set(DaysOfWeek dow) {
            mDays = dow.mDays;
        }

        /**
         * values
         *
         * @return values
         */
        public int getCoded() {
            return mDays;
        }

        /**
         * Returns days of week encoded in an array of booleans.
         *
         * @return Returns days of week encoded in an array of booleans.
         */
        public boolean[] getBooleanArray() {
            boolean[] ret = new boolean[VALUE_SEVEN];

            for (int i = 0; i < VALUE_SEVEN; i++) {
                ret[i] = isSet(i);
            }

            return ret;
        }

        /**
         * need again?
         *
         * @return *
         */
        public boolean isRepeatSet() {
            return mDays != 0;
        }

        /**
         * returns number of days from today until next alarm
         *
         * @param c
         *            must be set to today
         *
         * @return returns number of days from today until next alarm
         */
        public int getNextAlarm(Calendar c) {
            if (mDays == 0) {
                return -1;
            }

            int today = (c.get(Calendar.DAY_OF_WEEK) + VALUE_FIVE) % VALUE_SEVEN;

            int day = 0;
            int dayCount = 0;

            for (; dayCount < VALUE_SEVEN; dayCount++) {
                day = (today + dayCount) % VALUE_SEVEN;

                if (isSet(day)) {
                    break;
                }
            }

            return dayCount;
        }
    }

    private static void cancelPoweroffAlarm(AlarmManager am, String className) {
        String AlarmManagerClassName = "android.app.AlarmManager";
        try {
            Class sAlarmManager = Class.forName(AlarmManagerClassName);

            Method mStudentShowMethod = null;
            if (sAlarmManager != null) {
                mStudentShowMethod = sAlarmManager.getDeclaredMethod("cancelPoweroffAlarm", String.class);
            }
            if (mStudentShowMethod != null) {
                mStudentShowMethod.invoke(am, className);
            }
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static int getAlarmType() {
        int i = AlarmManager.RTC_WAKEUP;
        try {
            Class<?> a = Class.forName("android.app.AlarmManager");
            Field field = a.getField("RTC_ALARM_WAKEUP");
            field.setAccessible(true);
            i = field.getInt(a);
            return i;
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            Class<?> a = Class.forName("android.app.AlarmManager");
            Field field = a.getField("RTC_POWEROFF_WAKEUP");
            field.setAccessible(true);
            i = field.getInt(a);
            return i;
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return i;
    }

    /**
     * QC平台在Android O以后对于关机RTC的改动较大，之前AlarmManager.RTC_POWEROFF_WAKEUP已废弃
     * 此接口设置的时间如果距离关机时间不足1分钟，则设置无效
     * 测试：AlarmUtils.setPowerOffAlarm(context, System.currentTimeMillis() + 80000);
     * @param context
     * @param timeInMillis
     */
    @TargetApi(27)
    public static void setPowerOffAlarm(Context context, long timeInMillis) {
        Log.i(getTag(), "setPowerOffAlarm: "+ timeInMillis);
        Intent intent = new Intent("org.codeaurora.poweroffalarm.action.SET_ALARM");
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        intent.setPackage("com.qualcomm.qti.poweroffalarm");
        intent.putExtra("time", timeInMillis);
        context.sendBroadcast(intent);
    }
}
