package com.stur.lib.time;

import android.os.Handler;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 80375140 on 2017/3/31.
 */
public class TimerUtils {
    private final static Handler mHandler = new Handler();
    private final static Timer timer = new Timer();
    private final static Map<Runnable, TimerAdapter> map = new HashMap<Runnable, TimerAdapter>();

    /**
     * run the runnable thread every interval of ms
     * @param runnable
     * @param interval
     */
    public static void setInterval(Runnable runnable, long interval) {
        TimerAdapter adapter = new TimerAdapter(runnable);
        map.put(runnable, adapter);
        timer.schedule(adapter, interval, interval);
    }

    /**
     * run the runnable thread just once after delay of ms
     * @param runnable
     * @param delay
     */
    public static void setTimeout(Runnable runnable, long delay) {
        TimerAdapter adapter = new TimerAdapter(runnable);
        map.put(runnable, adapter);
        timer.schedule(adapter, delay);
    }

    public static void killTimer(Runnable runnable) {
        if (map.containsKey(runnable)) {
            TimerAdapter adapter = map.get(runnable);
            adapter.cancel();
            timer.purge();
        }
    }

    public static void killAll() {
        for (Runnable r : map.keySet()) {
            map.get(r).cancel();
            timer.purge();
        }
    }

    private static class TimerAdapter extends TimerTask {
        private Runnable runnable;

        public TimerAdapter(Runnable runnable) {
            this.runnable = runnable;
        }

        @Override
        public void run() {
            try {
                mHandler.post(runnable);
            } catch (Throwable e) {
                System.out.println("TimerAdapter run timer error!" + e.getMessage());
            }
        }
    }
}
