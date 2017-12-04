package com.stur.lib.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.stur.chest.Constant;
import com.stur.lib.PackageUtils;
import com.stur.lib.SystemPropertiesProxy;
import com.stur.lib.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityBase extends AppCompatActivity {

    BroadcastReceiver mBroadcastReceiver;

    @Override
    protected void onCreate(Bundle bundle) {
        // TODO Auto-generated method stub
        super.onCreate(bundle);

        mBroadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                handleIntent(intent);
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Utils.INTENT_DISPLAY);
        filter.addAction(Utils.INTENT_TEST);
        registerReceiver(mBroadcastReceiver, filter);// 注册Broadcast Receiver
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }

    protected void handleIntent(Intent intent) {
    }

    protected void checkInitkWiFiPermission() {
        int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] { Manifest.permission.ACCESS_COARSE_LOCATION },
                    Constant.REQUEST_CODE_WIFI_SCAN);
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Constant.REQUEST_CODE_WIFI_SCAN:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                } else {
                    // Permission Denied
                    Toast.makeText(this, "Wifi scan Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_CONTACTS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_CONTACTS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.RECORD_AUDIO, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_SECURE_SETTINGS, PackageManager.PERMISSION_GRANTED);

                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.WRITE_SECURE_SETTINGS) == PackageManager.PERMISSION_GRANTED) {
                    // All Permissions Granted
                } else {
                    // Permission Denied
                    Toast.makeText(this, "Some Permission is Denied", Toast.LENGTH_SHORT).show();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    // 可能用户关闭了权限且选择了不再提醒，这样每次敏感操作被权限拒绝但用户不知道，所以运行时也需要检查。
    protected void checkRuntimeWiFiPermission() {
        int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                showMessageOKCancel("You need to allow access to Contacts", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissions(new String[] { Manifest.permission.ACCESS_COARSE_LOCATION },
                                Constant.REQUEST_CODE_WIFI_SCAN);
                    }
                });
                return;
            }
            requestPermissions(new String[] { Manifest.permission.ACCESS_COARSE_LOCATION },
                    Constant.REQUEST_CODE_WIFI_SCAN);
            return;
        }
        // granted, continue to do something
    }

    // 一次请求多个权限
    protected final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    protected void checkInitkMultiPermissions() {
        List<String> permissionsNeeded = new ArrayList<String>();

        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
            permissionsNeeded.add("GPS");
        if (!addPermission(permissionsList, Manifest.permission.READ_CONTACTS))
            permissionsNeeded.add("Read Contacts");
        if (!addPermission(permissionsList, Manifest.permission.WRITE_CONTACTS))
            permissionsNeeded.add("Write Contacts");
        if (!addPermission(permissionsList, Manifest.permission.READ_EXTERNAL_STORAGE))
            permissionsNeeded.add("Read Sdcard");
        if (!addPermission(permissionsList, Manifest.permission.RECORD_AUDIO))
            permissionsNeeded.add("Record Audio");
        if (PackageUtils.AID_SYSTEM == PackageUtils.getMyUid()
                && !addPermission(permissionsList, Manifest.permission.WRITE_SECURE_SETTINGS))
            permissionsNeeded.add("Write Secure Settings");

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                String message = "You need to grant access to " + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++) {
                    message = message + ", " + permissionsNeeded.get(i);
                }

                showMessageOKCancel(message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                    }
                });
                return;
            }
            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return;
        }

        // permission granted, continue to do something
    }

    protected boolean addPermission(List<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!shouldShowRequestPermissionRationale(permission))
                return false;
        }
        return true;
    }

    protected void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this).setMessage(message).setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null).create().show();
    }

    protected String getProperity(String str) {
        return SystemPropertiesProxy.get(this, str);
    }

    protected void selectActivity(String pos, String neg, final Class posAct, final Class netAct) {
        new AlertDialog.Builder(ActivityBase.this).setMessage("Selecting:")
                .setPositiveButton(pos, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        startActivity(new Intent(ActivityBase.this, posAct));
                    }
                }).setNegativeButton(neg, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                startActivity(new Intent(ActivityBase.this, netAct));
            }
        }).create().show();
    }
}
