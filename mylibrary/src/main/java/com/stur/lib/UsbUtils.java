package com.stur.lib;

import android.content.Context;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbPort;
import android.hardware.usb.UsbPortStatus;

/**
 * Created by guanxuejin on 2019/4/22 0022.
 */

public class UsbUtils {
    public static String getTag() {
        return new Object() {
            public String getClassName() {
                String clazzName = this.getClass().getName();
                return clazzName.substring(clazzName.lastIndexOf('.')+1, clazzName.lastIndexOf('$'));
            }
        }.getClassName();
    }

    public static void getUsbPortStatus(Context context) {
        Log.d(getTag(), "getUsbPortStatus E:");
        UsbManager um = context.getSystemService(UsbManager.class);
        UsbPort usbPort;
        UsbPortStatus portStatus;
        UsbPort[] ports = um.getPorts();
        if (ports == null) {
            Log.d(getTag(), "getUsbPortStatus: return null");
            return;
        }
        // For now look for a connected port, in the future we should identify port in the
        // notification and pick based on that.
        final int l = ports.length;
        Log.d(getTag(), "getUsbPortStatus: ports length = " + l);
        for (int i = 0; i < l; i++) {
            UsbPortStatus status = um.getPortStatus(ports[i]);
            Log.d(getTag(), "getUsbPortStatus: usbPort = " + ports[i] + "connected status = " + status.isConnected());
            if (status.isConnected()) {
                usbPort = ports[i];
                portStatus = status;
                Log.d(getTag(), "getUsbPortStatus: usbPort = " + usbPort + ", portStatus = " + portStatus);
                break;
            }
        }
    }
}
