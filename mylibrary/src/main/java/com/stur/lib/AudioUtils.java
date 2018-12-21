package com.stur.lib;

import android.content.Context;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;

/**
 * Created by guanxuejin on 2018/12/21 0021.
 */

public class AudioUtils {
    public static String getTag() {
        return new Object() {
            public String getClassName() {
                String clazzName = this.getClass().getName();
                return clazzName.substring(clazzName.lastIndexOf('.')+1, clazzName.lastIndexOf('$'));
            }
        }.getClassName();
    }

    /**
     * 使用RingtoneManager播放ogg音频文件
     * @param context
     * @param soundPath，音频文件路径，比如 "/etc/Scan_buzzer.ogg"
     */
    public static Ringtone mRingtone = null;
    public static void playRmOgg(Context context, String soundPath) {
        final Uri soundUri = Uri.parse("file://" + soundPath);
        if (soundUri != null) {
            // 不能每播放一次声音就new一个对象来播放声音。这样能播放，但是播放了十几次就会报错。
            if (mRingtone == null) {
                mRingtone = RingtoneManager.getRingtone(context, soundUri);
            }
            if (mRingtone != null) {
                mRingtone.setStreamType(AudioManager.STREAM_SYSTEM);
                mRingtone.play();
            } else {
                Log.d(getTag(), "playRmOgg: failed to load ringtone from uri: " + soundUri);
            }
        } else {
            Log.d(getTag(), "playRmOgg: could not parse Uri: " + soundPath);
        }
    }

    /**
     * 使用SoundPool播放ogg音频文件，没成功，不推荐
     * @param soundPath 音频文件路径，比如 "/etc/Scan_buzzer.ogg"
     */
    public static void playSpOgg(Context context, String soundPath) {
        // 实例化AudioManager对象
        AudioManager am = (AudioManager)context.getSystemService(context.AUDIO_SERVICE);
        // 返回当前AudioManager对象播放所选声音的类型的最大音量值，声音音量类型默认为多媒体
        int soundVolType = 3;
        float maxVolumn = am.getStreamMaxVolume(soundVolType);
        // 返回当前AudioManager对象的音量值
        float currentVolumn = am.getStreamVolume(soundVolType);
        // 比值
        float volumnRatio = currentVolumn / maxVolumn;
        SoundPool soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        int soundID = soundPool.load(soundPath, 1);
        soundPool.play(soundID, volumnRatio, volumnRatio, 1, 0, 1);

        /*SoundPool soundpool = new SoundPool(2, AudioManager.STREAM_NOTIFICATION, 0);
        int heightBeepId = soundpool.load(soundPath, 1);
        soundpool.play(heightBeepId, 1, 1, 0, 0, 1);*/
    }
}
