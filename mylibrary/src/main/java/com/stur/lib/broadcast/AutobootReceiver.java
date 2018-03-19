/*
 * Copyright (c) 2015 Qualcomm Technologies, Inc.
 * All Rights Reserved.
 * Confidential and Proprietary - Qualcomm Technologies, Inc.
 */

package com.stur.lib.broadcast;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.stur.lib.Log;
import com.stur.lib.constant.StConstant;

import static com.stur.lib.os.OsUtils.isServiceRunning;

public class AutobootReceiver extends BroadcastReceiver {

    private final static String mClassName = null; //ControlledService.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        /*if (UserHandle.myUserId() != 0) {
            Log.d(this, "Not primary user, ignore broadcast intents");
            return;
        }*/
        String intentAction = intent.getAction();
        if (Intent.ACTION_BOOT_COMPLETED.equals(intentAction)) {
            if (!isServiceRunning(context, mClassName, 200) && StConstant.isRoleServer()) {
                Log.d(this, "Starting " + mClassName + " : " + intentAction + " received. ");
                startService(context);
            } else {
                Log.d(this, mClassName + " is already running. " +
                           intentAction + " ignored. ");
            }
        } else {
            Log.e(this, "Received Intent: " + intent.toString());
        }
    }

    private void startService(Context context) {
        ComponentName comp = new ComponentName(context.getPackageName(), mClassName);
        ComponentName service = context.startService(new Intent().setComponent(comp));
        if (service == null) {
            Log.e(this, "Could Not Start Service " + comp.toString());
        } else {
            Log.e(this, mClassName + " service Started Successfully");
        }
    }
}
