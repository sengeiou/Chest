package com.stur.lib;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.database.ContentObserver;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.view.KeyEvent;

import java.io.IOException;

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
     * 适合播放声音短，文件小，可以同时播放多种音频，消耗资源较小
     * 通过assets目录构造URI，不能用来播放视频，也不能播放音频。
     * Uri notification = Uri.parse("file:///android_asset/beep.ogg"); 这样就不行
     * Uri notification = Uri.parse("android.resource://"+getActivity().getPackageName()+"/"+R.raw.beep);   这样的可以
     * 在raw目录下的文件构造URI可以播放音频，也能播放视频
     * @param context
     * @param soundUri，音频文件路径，比如 Uri.parse("file://" + "/etc/Scan_buzzer.ogg");
     */
    public static Ringtone mRingtone = null;
    public static void playRmOgg(Context context, Uri soundUri) {
        Log.d(getTag(), "playRmOgg E: soundUri = " + soundUri);
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
            Log.d(getTag(), "playRmOgg: could not parse Uri: " + soundUri);
        }
    }

    /**
     * 使用SoundPool播放ogg音频文件
     * SoundPool支持多个音频文件同时播放(组合音频也是有上限的)，延时短，比较适合短促、密集的场景
     * 把一段音频加载到内存在还需要一段时间，所以如果在Load方法后立即调用play，可能会播放不成功。
     * 建议加载音频在OnCreate方法完成，播放在按钮中完成，设置OnLoadCompleteListener监听。
     * 如果是同一个soundPool重复播放，可不用每次播完就release。但如果是不同的soundPool播放同一个音频文件，则需要release
     * @param soundPath 音频文件路径，比如 "/etc/Scan_buzzer.ogg"
     */
    public static void playSpOgg(Context context, String soundPath) {
        Log.d(getTag(), "playSpOgg: soundPath = " + soundPath);
        // 实例化AudioManager对象
        AudioManager am = (AudioManager)context.getSystemService(context.AUDIO_SERVICE);
        // 返回当前AudioManager对象播放所选声音的类型的最大音量值，声音音量类型默认为多媒体，
        // STREAM_VOICE_CALL, STREAM_SYSTEM, STREAM_RING, STREAM_MUSIC, STREAM_ALARM, STREAM_NOTIFICATION...
        int soundVolType = AudioManager.STREAM_SYSTEM;
        float maxVolumn = am.getStreamMaxVolume(soundVolType);
        // 返回当前AudioManager对象的音量值
        float currentVolumn = am.getStreamVolume(soundVolType);
        // 比值
        final float volumnRatio = currentVolumn / maxVolumn;

        SoundPool soundPool;
        if (Build.VERSION.SDK_INT >= 21) {
            SoundPool.Builder builder = new SoundPool.Builder();
            //传入音频的数量
            builder.setMaxStreams(1);
            //AudioAttributes是一个封装音频各种属性的类
            AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
            //设置音频流的合适属性
            attrBuilder.setLegacyStreamType(AudioManager.STREAM_SYSTEM);
            builder.setAudioAttributes(attrBuilder.build());
            soundPool = builder.build();
        } else {
            //第一个参数是可以支持的声音数量，第二个是声音类型（可选AudioManager.STREAM_MUSIC，AudioManager.STREAM_NOTIFICATION），第三个是声音品质
            //soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
            soundPool = new SoundPool(1, AudioManager.STREAM_SYSTEM, 5);
        }

        soundPool.load(soundPath, 1);
        //第一个参数Context,第二个参数资源Id，第三个参数优先级
        //soundPool.load(context, rawId, 1);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                //第一个参数id，即传入池中的顺序，第二个和第三个参数为左右声道，第四个参数为优先级，第五个是否循环播放，0不循环，-1循环
                //最后一个参数播放比率，范围0.5到2，通常为1表示正常播放
                //soundPool.play(sampleId,1,1,1,0,1);//播放
                soundPool.play(sampleId, volumnRatio, volumnRatio, 1, 0, 1);
                //soundPool.play(1, 1, 1, 0, 0, 1);  //播放

                //回收Pool中的资源
                soundPool.release();
            }
        });
    }

    /**
     * 使用MediaPlayer播放声音 不能同时播放多种音频
     * 消耗资源较大
     * @param rawId
     */
    public static void playSoundByMedia(Context context, int rawId) {
        try {
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);
            try {
                AssetFileDescriptor file = context.getResources().openRawResourceFd(
                        rawId);
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(0.50f, 0.50f);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };
}
