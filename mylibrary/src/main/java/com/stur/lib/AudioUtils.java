package com.stur.lib;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Handler;
import android.view.KeyEvent;

import static io.ganguo.app.gcache.disk.DiskBasedCache.TAG;

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
     * 获取音量值
     * @param context
     * @param type AudioManager.STREAM_MUSIC代表媒体音量，也可以替换成其他的类型，获取其他类型音量
     * @return
     */
    public static int getCurrentVolume(Context context, int type) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int currentVolume = audioManager.getStreamVolume(type);
        Log.d(getTag(), "getCurrentVolume: type = " + type + ", currentVolume = " + currentVolume);
        return currentVolume;
    }

    /**
     * @param context
     * @param type 例如 例如我把媒体音量设置为0（静音）
     * @param value 例如 0
     */
    public static void setCurrentVolume(Context context, int type, int value) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(type, value, AudioManager.FLAG_PLAY_SOUND);
    }

    //监听音量键被按下：在activity重写onKeyDown方法
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDown" + keyCode + "" + (keyCode == KeyEvent.KEYCODE_VOLUME_UP));
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            // 音量+键
        }
        if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){
            // 音量-键
        }
        return false;
    }

    //监听音量变化：上面通过监听按键来监听调整音量的方法，不是很靠谱，因为可能用户在设置里调整音量，所以用下面方式监听音量变化
    private void registerVolumeChangeReceiver(Context context) {
        SettingsContentObserver settingsContentObserver = new SettingsContentObserver(context, new Handler());
        context.getContentResolver()
                .registerContentObserver(android.provider.Settings.System.CONTENT_URI, true, settingsContentObserver);
    }

    private void unregisterVolumeChangeReceiver(Context context) {
        //context.getContentResolver().unregisterContentObserver(mSettingsContentObserver);
    }

    public class SettingsContentObserver extends ContentObserver {
        Context context;

        public SettingsContentObserver(Context c, Handler handler) {
            super(handler);
            context = c;
        }

        @Override
        public boolean deliverSelfNotifications() {
            return super.deliverSelfNotifications();
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            Log.d(TAG, "音量：" + currentVolume);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_PLAY_SOUND);
        }
    }

    //监听震动模式变化
    public boolean isMuteMode(Context context) {
        AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        return am.getRingerMode() == AudioManager.RINGER_MODE_SILENT;
    }

    public boolean isVibrateMode(Context context) {
        AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        return am.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE;
    }

    public boolean isNormalMode(Context context) {
        AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        return am.getRingerMode() == AudioManager.RINGER_MODE_NORMAL;
    }

    private void registerRingerModeReceiver(Context context) {
        BroadcastReceiver mRingerModeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(AudioManager.RINGER_MODE_CHANGED_ACTION)) {
                    // 监听到震动/静音/响铃的模式变化
                    if( isMuteMode(context)){
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter(AudioManager.RINGER_MODE_CHANGED_ACTION);
        context.registerReceiver(mRingerModeReceiver, filter);
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
