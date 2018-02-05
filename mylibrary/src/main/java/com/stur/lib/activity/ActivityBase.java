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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.stur.lib.StConstant;
import com.stur.lib.Log;
import com.stur.lib.PackageUtils;
import com.stur.lib.R;
import com.stur.lib.SystemPropertiesProxy;
import com.stur.lib.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.stur.lib.StConstant.DEFAULT_ACTIVITY;


public class ActivityBase extends FragmentActivity
        implements View.OnClickListener,
        OnPageChangeListener {
    protected TextView tab1Tv, tab2Tv, tab3Tv;   // 三个textview
    protected ImageView cursorImg;    // 指示器
    protected ViewPager viewPager;
    protected ArrayList<Fragment> fragmentsList;    // fragment对象集合
    protected int currentIndex = 0;    // 记录当前选中的tab的index
    protected int offset = 0;    // 指示器的偏移量
    protected int leftMargin = 0;    // 左margin
    protected int screenWidth = 0;    // 屏幕宽度
    protected int screen1_3;    // 屏幕宽度的三分之一
    protected RelativeLayout.LayoutParams lp;

    BroadcastReceiver mBroadcastReceiver;

    @Override
    protected void onCreate(Bundle bundle) {
        // TODO Auto-generated method stub
        super.onCreate(bundle);
        setContentView(R.layout.activity_base);
        init();

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
                                StConstant.REQUEST_CODE_WIFI_SCAN);
                    }
                });
                return;
            }
            requestPermissions(new String[] { Manifest.permission.ACCESS_COARSE_LOCATION },
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

    private void init() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screen1_3 = screenWidth / 3;

        cursorImg = (ImageView) findViewById(R.id.iv_cursor);
        lp = (RelativeLayout.LayoutParams) cursorImg.getLayoutParams();
        leftMargin = lp.leftMargin;

        tab1Tv = (TextView) findViewById(R.id.tv_tab1);
        tab2Tv = (TextView) findViewById(R.id.tv_tab2);
        tab3Tv = (TextView) findViewById(R.id.tv_tab3);
        //
        tab1Tv.setOnClickListener(this);
        tab2Tv.setOnClickListener(this);
        tab3Tv.setOnClickListener(this);

        initViewPager();
    }

    protected void initViewPager() {}

    @Override
    public void onClick(View v) {
        /* for Resource IDs are not allowed to be used in a switch statement in Android library modules
        switch (v.getId()) {
            case R.id.tv_tab1:
                viewPager.setCurrentItem(0);
                break;
            case R.id.tv_tab2:
                viewPager.setCurrentItem(1);
                break;
            case R.id.tv_tab3:
                viewPager.setCurrentItem(2);
                break;
        }*/

        if (v.getId() == R.id.tv_tab1 ) {
            viewPager.setCurrentItem(0);
        } else if (v.getId() == R.id.tv_tab2) {
            viewPager.setCurrentItem(1);
        } else if (v.getId() == R.id.tv_tab3) {
            viewPager.setCurrentItem(2);
        }


    }

    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {
        offset = (screen1_3 - cursorImg.getLayoutParams().width) / 2;
        Log.d(this, position + "--" + positionOffset + "--"+ positionOffsetPixels);
        final float scale = getResources().getDisplayMetrics().density;
        if (position == 0) {// 0<->1
            lp.leftMargin = (int) (positionOffsetPixels / 3) + offset;
            //startTestActivity();
        } else if (position == 1) {// 1<->2
            lp.leftMargin = (int) (positionOffsetPixels / 3) + screen1_3 +offset;
        }
        cursorImg.setLayoutParams(lp);
        currentIndex = position;
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageSelected(int arg0) {
    }

    public void startTestActivity() {
        String clsName = SystemPropertiesProxy.get(this, StConstant.PROP_ACTIVITY_NAME, DEFAULT_ACTIVITY);
        if (clsName != null && clsName.length() > 0) {
            try {
                Class cls = Class.forName(clsName);
                Intent intent = new Intent(this, cls);
                startActivity(intent);
                return;
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
