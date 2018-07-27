package com.stur.lib.sound;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.provider.Settings;
import android.telecom.Log;

/**
 * Created by guanxuejin on 2018/7/10.
 */

public class SoundUtils {
    private static final int DTMF_DURATION_MS = 120; // 声音的播放时间
    private static Object mToneGeneratorLock = new Object(); // 监视器对象锁
    //private static ToneGenerator mToneGenerator;             // 声音产生器

    public static String getTag() {
        return new Object() {
            public String getClassName() {
                String clazzName = this.getClass().getName();
                return clazzName.substring(clazzName.lastIndexOf('.')+1, clazzName.lastIndexOf('$'));
            }
        }.getClassName();
    }

    /**
     * 设置拨号盘的按键音开关
     * 未实现，实际拨号盘的按键音是在Telecomm中播放的
     */
    public void setDialPadToneEnable(Activity context, boolean enable) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        boolean dtmfToneEnabled = Settings.System.getInt(context.getContentResolver(), Settings.System.DTMF_TONE_WHEN_DIALING, 1) == 1;
        Log.d(getTag(), "setDialPadToneEnable: " + enable + ", preDTMFToneEnabled = " + dtmfToneEnabled);
        if (enable != dtmfToneEnabled) {
            ToneGenerator toneGenerator = new ToneGenerator(
                    AudioManager.STREAM_DTMF, 80); // 设置声音的大小
            //这个接口是activity中的方法，所以context需要传递activity进来
            //使用音量控制键来设置程序的音量大小。在Android系统中有多种音频流，设置该Activity中音量控制键控制的音频流
            context.setVolumeControlStream(AudioManager.STREAM_DTMF);
        }
    }

    /**
     * 播放按键声音
     */
    public static void playTone(Context context, int tone) {
        boolean dtmfToneEnabled = Settings.System.getInt(context.getContentResolver(), Settings.System.DTMF_TONE_WHEN_DIALING, 1) == 1;
        if (!dtmfToneEnabled) {
            return;
        }
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int ringerMode = audioManager.getRingerMode();
        if (ringerMode == AudioManager.RINGER_MODE_SILENT
                || ringerMode == AudioManager.RINGER_MODE_VIBRATE) {
            // 静音或者震动时不发出声音
            return;
        }
        synchronized (mToneGeneratorLock) {
            ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_DTMF, 80);
            toneGenerator.startTone(tone, DTMF_DURATION_MS);   //发出声音
        }
    }

}
