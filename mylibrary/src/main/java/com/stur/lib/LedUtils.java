package com.stur.lib;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.BatteryManager;

import com.android.server.lights.Light;
import com.android.server.lights.LightsManager;

import java.io.FileOutputStream;
import java.io.IOException;

import static com.android.internal.R.id.status;
import static com.stur.lib.file.FileUtils.tranferFileToByteArray;

/**
 * Created by guanxuejin on 2018/12/25 0025.
 */

public class LedUtils {
    // 颜色定义
    public static final int RED = 0;
    public static final int GREEN = 1;
    public static final int BLUE = 2;

    // 亮度值定义
    public static final byte[] LIGHT_ON = {'2', '5', '5'};
    public static final byte[] LIGHT_OFF = {'0'};

    // 写入系统目录
    public static String RED_LED_DEV = "/sys/class/leds/red/brightness";     //system用户
    public static String GREEN_LED_DEV = "/sys/class/leds/green/brightness";
    public static String BLUE_LED_DEV = "/sys/class/leds/blue/brightness";

    // SQ52TG的LED系统目录
    // LED的背光，最高218
    public static String SQ52TG_LED_BRIGHT_BACKLIGHT = "/sys/class/leds/lcd-backlight/brightness";
    public static String SQ52TG_LED_BRIGHT_FLASH_0 = "/sys/class/leds/led:flash_0/brightness";
    public static String SQ52TG_LED_BRIGHT_FLASH_1 = "/sys/class/leds/led:flash_1/brightness";
    // 闪光灯的开关状态，开启为1，关闭为0
    public static String SQ52TG_LED_BRIGHT_SWITCH = "/sys/class/leds/led:switch/brightness";
    // 后置闪光灯的亮度值，打开为120，关闭为0
    public static String SQ52TG_LED_BRIGHT_TORCH_0 = "/sys/class/leds/led:torch_0/brightness";
    // 前置闪光灯的亮度值，始终为0
    public static String SQ52TG_LED_BRIGHT_TORCH_1 = "/sys/class/leds/led:torch_1/brightness";
    public static String SQ52TG_LED_BRIGHT_MMC_0 = "/sys/class/leds/mmc0::/brightness";
    public static String SQ52TG_LED_BRIGHT_MMC_1 = "/sys/class/leds/mmc1::/brightness";
    // 红色LED指示灯的亮度值
    public static String SQ52TG_LED_BRIGHT_RED = "/sys/class/leds/red/brightness";
    public static String SQ52TG_LED_BRIGHT_TORCH_LIGHT_0 = "/sys/class/leds/torch-light0/brightness";
    // LED的背光，最低为161，最高为4095
    public static String SQ52TG_LED_BRIGHT_WLED = "/sys/class/leds/wled/brightness";
    /**
     * SQ52TG的通知灯控制文件：/sys/class/doubleled/led/doubleled
     * 总共有4颗灯，一颗红色的是充电指示灯，另外还有3颗红绿蓝，即四颗灯为红红绿蓝
     * echo '1'> /sys/class/doubleled/led/doubleled 红灯亮
     * echo '2'> /sys/class/doubleled/led/doubleled 红灯灭
     * echo '3'> /sys/class/doubleled/led/doubleled 蓝灯亮
     * echo '4'> /sys/class/doubleled/led/doubleled 蓝灯灭
     * echo '255'>/sys/devices/soc/qpnp-smbcharger-17/leds/red/brightness  开启充电指示灯
     * echo '0'>/sys/devices/soc/qpnp-smbcharger-17/leds/red/brightness       关闭充电指示灯
     * echo '0'>/sys/devices/soc/qpnp-smbcharger-17/leds/red/brightness        关闭绿灯
     * echo '255'>/sys/devices/soc/qpnp-smbcharger-17/leds/red/brightness     开启绿灯
     */
    // 要注意这些系统文件是否为system用户
    public static String SQ52TG_LED_NOTIFICATION = "/sys/class/doubleled/led/doubleled";
    public static String SQ52TG_LED_CHARGE = "/sys/devices/soc/qpnp-smbcharger-17/leds/red/brightness";
    public static final byte[] RED_ON = {'1'};
    public static final byte[] RED_OFF = {'2'};
    public static final byte[] BLUE_ON = {'3'};
    public static final byte[] BLUE_OFF = {'4'};

    public static String getTag() {
        return new Object() {
            public String getClassName() {
                String clazzName = this.getClass().getName();
                return clazzName.substring(clazzName.lastIndexOf('.') + 1, clazzName.lastIndexOf('$'));
            }
        }.getClassName();
    }

    public static void setledlightcolor(int color) {
        Log.d(getTag(), "setledlightcolor:" + color);
        boolean red = false, green = false, blue = false;
        switch (color) {
            case RED:
                red = true;
                break;
            case GREEN:
                green = true;
                break;
            case BLUE:
                blue = true;
                break;
            default:
                break;
        }

        try {
            FileOutputStream fRed = new FileOutputStream(RED_LED_DEV);
            fRed.write(red ? LIGHT_ON : LIGHT_OFF);
            fRed.close();
            FileOutputStream fGreen = new FileOutputStream(GREEN_LED_DEV);
            fGreen.write(green ? LIGHT_ON : LIGHT_OFF);
            fGreen.close();
            FileOutputStream fBlue = new FileOutputStream(BLUE_LED_DEV);
            fBlue.write(blue ? LIGHT_ON : LIGHT_OFF);
            fBlue.close();
        } catch (Exception e) {
            Log.e(getTag(), "Exception" + e);
        }
    }

    public static void setLedRedEnabled(boolean enabled) {
        Log.d(getTag(), "setLedRedEnabled:" + enabled);
        writeToFile(SQ52TG_LED_NOTIFICATION, enabled ? RED_ON : RED_OFF);
    }

    public static boolean isLedRedEnabled() throws IOException {
        return readLedNotificationSysFile() == 1;
    }

    public static byte readLedNotificationSysFile() throws IOException {
        byte[] bt = tranferFileToByteArray(SQ52TG_LED_NOTIFICATION);
        Log.d(getTag(), "readLedNotificationSysFile: " + bt[0]);
        return bt[0];
    }

    public static void setLedBlueEnabled(boolean enabled) {
        Log.d(getTag(), "setLedBlueEnabled:" + enabled);
        writeToFile(SQ52TG_LED_NOTIFICATION, enabled ? BLUE_ON : BLUE_OFF);
    }

    public static void setLedChargeEnabled(boolean enabled) {
        Log.d(getTag(), "setLedChargeEnabled:" + enabled);
        writeToFile(SQ52TG_LED_CHARGE, enabled ? LIGHT_ON : LIGHT_OFF);
    }

    public static void setLedGreenEnabled(boolean enabled) {
        Log.d(getTag(), "setLedGreenEnabled:" + enabled);
        writeToFile(SQ52TG_LED_CHARGE, enabled ? LIGHT_ON : LIGHT_OFF);
    }

    public static void writeToFile(String file, byte[] light) {
        try {
            FileOutputStream fRed = new FileOutputStream(file);
            fRed.write(light);
            fRed.close();
        } catch (Exception e) {
            Log.e(getTag(), "writeToFile Exception" + e);
        }
    }

    public static void notifyWithLedRed(Context context) {
        // 通知渠道的id
        String CHANNEL_ID_LED = "LED is lighting";
        // 用户可以看到的通知渠道的名字
        String CHANNEL_NAME_LED = "LED is lighting";
        int icon = R.drawable.icon_bt;
        long when = System.currentTimeMillis();
        final int ID_LED = 19871103;
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID_LED,
                CHANNEL_NAME_LED,
                NotificationManager.IMPORTANCE_MIN);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);

        Notification notification = new Notification.Builder(
                context, CHANNEL_ID_LED)
                .setSmallIcon(icon)
                .setContentText("lighting")
                .setWhen(when)
                .build();

        notification.ledARGB = 0xFFFFFF;  //这里是颜色，我们可以尝试改变，理论上0xFF0000是红色，0x00FF00是绿色
        notification.ledOnMS = 100;
        notification.ledOffMS = 100;
        notification.flags = Notification.FLAG_SHOW_LIGHTS;
        nm.notify(ID_LED, notification);
        nm.cancel(ID_LED);
    }

    public static void enableLed(Context context) {        //初始化灯的属性
        LightsManager lights = context.getSystemService(LightsManager.class);
        Light batteryLight = lights.getLight(LightsManager.LIGHT_ID_BATTERY);    //从LightsManager拿到电池灯

        int batteryLowARGB = context.getResources().getInteger(
                com.android.internal.R.integer.config_notificationsBatteryLowARGB);
        int batteryMediumARGB = context.getResources().getInteger(
                com.android.internal.R.integer.config_notificationsBatteryMediumARGB);
        int batteryFullARGB = context.getResources().getInteger(
                com.android.internal.R.integer.config_notificationsBatteryFullARGB);
        int batteryLedOn = context.getResources().getInteger(
                com.android.internal.R.integer.config_notificationsBatteryLedOn);
        int batteryLedOff = context.getResources().getInteger(
                com.android.internal.R.integer.config_notificationsBatteryLedOff);

        batteryLight.setColor(batteryLowARGB);    //充电时，常亮
        /*mBatteryLight.setFlashing(mBatteryLowARGB, Light.LIGHT_FLASH_TIMED,
                mBatteryLedOn, mBatteryLedOff);    //未充电，闪烁
        mBatteryLight.setColor(mBatteryFullARGB);    //充电满了或电量大于90%，常亮某个颜色
        mBatteryLight.setColor(mBatteryMediumARGB);    //未充满时，常亮某个颜色
        // No lights if not charging and not low
        mBatteryLight.turnOff();    //其他状态，关闭led*/
    }
}
