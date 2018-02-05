package com.stur.lib.bt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import com.stur.lib.Log;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by guanxuejin on 2018/1/22.
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
}
