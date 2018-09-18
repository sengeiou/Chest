package com.stur.lib.bt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Build;

import com.stur.lib.Log;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Sturmegezhutz on 2018/1/22.
 */

public class BluetoothUtils {
    public static String getTag() {
        return new Object() {
            public String getClassName() {
                String clazzName = this.getClass().getName();
                return clazzName.substring(clazzName.lastIndexOf('.') + 1, clazzName.lastIndexOf('$'));
            }
        }.getClassName();
    }

    /**
     * 是否支持蓝牙
     * @return
     * 注意：在小于等于JELLY_BEAN_MR1 (API 17)的版本中,使用BluetoothAdapter.getDefaultAdapter(),获取BluetoothAdapter
     *      在大于等于JELLY_BEAN_MR2 (API 18)的版本中,使用getSystemService(Context.BLUETOOTH_SERVICE),获取BluetoothManager ,然后通过BluetoothManager.getAdapter()的方式, 获取BluetoothAdapter.
     */
    public static boolean isBluetoothSupported(Context context) {
        BluetoothAdapter adapter = null;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            adapter = BluetoothAdapter.getDefaultAdapter();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            adapter = bluetoothManager.getAdapter();
        }
        if (null == adapter) {
            return false;
        } else {
            return true;
        }
    }

    public static Set<BluetoothDevice> getConnectedDevices() {
        Set<BluetoothDevice> deviceList = new HashSet<BluetoothDevice>();
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        Class<BluetoothAdapter> bluetoothAdapterClass = BluetoothAdapter.class;//得到BluetoothAdapter的Class对象
        try {//得到连接状态的方法
            Method method = bluetoothAdapterClass.getDeclaredMethod("getConnectionState", (Class[]) null);
            //打开权限
            method.setAccessible(true);
            int state = (int) method.invoke(adapter, (Object[]) null);

            if(state == BluetoothAdapter.STATE_CONNECTED){
                Log.i(getTag(),"BluetoothAdapter.STATE_CONNECTED");
                Set<BluetoothDevice> devices = adapter.getBondedDevices();
                Log.i(getTag(),"devices:"+devices.size());

                for(BluetoothDevice device : devices){
                    Method isConnectedMethod = BluetoothDevice.class.getDeclaredMethod("isConnected", (Class[]) null);
                    method.setAccessible(true);
                    boolean isConnected = (boolean) isConnectedMethod.invoke(device, (Object[]) null);
                    if(isConnected){
                        Log.i(getTag(),"connected:"+device.getName());
                        deviceList.add(device);
                    }
                }
                return deviceList;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return null;
        }
    }

    public static Set<BluetoothDevice> getBondedDevices() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter != null) {
            return adapter.getBondedDevices();
        } else {
            return null;
        }
    }

    /**
     * 是否连接蓝牙音频设备（比如蓝牙耳机）
     * 参见 framwork/android.bluetooth.BluetoothHeadset.java/isAudioConnected()
     * 未完成
     */
    public static boolean isBluetoothAudioConnected (Context context) {
        /*BluetoothManager bm = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        List<BluetoothDevice> deviceList = mBluetoothHeadset.getConnectedDevices();

        if (deviceList.isEmpty()) {
            return false;
        }
        for (int i = 0; i < deviceList.size(); i++) {
            BluetoothDevice device = deviceList.get(i);
            boolean isAudioOn = mBluetoothHeadset.isAudioConnected(device);
            Log.v(getTag(), "isBluetoothAudioConnected: ==> isAudioOn = " + isAudioOn
                    + "for headset: " + device);
            if (isAudioOn) {
                return true;
            }
        }*/
        return false;
    }
}
