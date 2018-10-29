package com.stur.lib.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.Toast;

import com.stur.lib.Log;
import com.stur.lib.R;
import com.stur.lib.app.AppManager;
import com.stur.lib.app.ContextBase;
import com.stur.lib.app.IContext;
import com.stur.lib.config.ConfigManager;
import com.stur.lib.constant.StConstant;
import com.stur.lib.os.BusProvider;
import com.stur.lib.os.PackageUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.READ_PHONE_STATE;

/**
 * 本应用中的Activity的基础类，里面集成了一些通用的方法，尽量本应用的所有Activity都继承该类
 * AppManager中的一些Activity堆栈操作依赖这里的初始数据
 * 友盟的统计需要在所有Activity的onResume和onPause中添加处理，所以直接加在这里
 */
public abstract class ActivityBase extends AppCompatActivity implements IContext {

    @Override
    protected void onCreate(Bundle bundle) {
        // TODO Auto-generated method stub
        super.onCreate(bundle);

        AppManager.getInstance().addActivity(this);
        BusProvider.getInstance().register(this);

        beforeInitView();
        initView();
        initListener();
        initData();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        BusProvider.getInstance().unregister(this);
        AppManager.getInstance().removeActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //集成umeng的移动统计功能5-4，包括onResume和onPause两部分
        if (ConfigManager.getInstance().getUmengEnabled()) {
            MobclickAgent.onResume(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ConfigManager.getInstance().getUmengEnabled()) {
            MobclickAgent.onPause(this);
        }
    }

    //子类重写该函数来setContentView
    protected abstract void beforeInitView();

    //控件初始化的一些操作
    protected abstract void initView();

    //Listener初始化的一些操作
    protected abstract void initListener();

    //数据初始化的一些操作
    protected abstract void initData();

    //获取上下文环境
    @Override
    public <T extends ContextBase> T getAppContext() {
        return ContextBase.getInstance();
    }

    protected void checkInitkWiFiPermission() {
        int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    StConstant.REQUEST_CODE_WIFI_SCAN);
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case StConstant.REQUEST_CODE_WIFI_SCAN:
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
                perms.put(READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);

                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.WRITE_SECURE_SETTINGS) == PackageManager.PERMISSION_GRANTED
                        && perms.get(READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
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
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                StConstant.REQUEST_CODE_WIFI_SCAN);
                    }
                });
                return;
            }
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    StConstant.REQUEST_CODE_WIFI_SCAN);
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
        if (!addPermission(permissionsList, Manifest.permission.READ_PHONE_STATE))
            permissionsNeeded.add("Read Phone State");
        if (PackageUtils.AID_SYSTEM == PackageUtils.getMyUid()
                && !addPermission(permissionsList, Manifest.permission.WRITE_SECURE_SETTINGS))
            permissionsNeeded.add("Write Secure Settings");

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationa
                String message = getString(R.string.grant_access) + permissionsNeeded.get(0);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                Log.d(this, "KeyEvent.KEYCODE_MENU");
                break;
            case KeyEvent.KEYCODE_HOME:
                Log.d(this, "KeyEvent.KEYCODE_HOME");
                finish();
                break;
            case KeyEvent.KEYCODE_BACK:
                Log.d(this, "KeyEvent.KEYCODE_BACK");
                break;
            default:
                Log.d(this, "no matched: " + keyCode);
        }
        return super.onKeyDown(keyCode, event);
    }
}
