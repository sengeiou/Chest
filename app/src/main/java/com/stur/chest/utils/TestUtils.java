package com.stur.chest.utils;

import android.content.Context;
import android.os.Environment;

import com.stur.lib.Utils;
import com.stur.lib.camera.CameraUtils;


/**
 * Created by Sturmegezhutz on 2018/9/15.
 */

public class TestUtils {
    /**
     * 单元测试模块，按照首字母排序
     *
     * @param context
     */
    public void unitTest(final Context context)  {
        String ret = "";
        /*********** AdbUtils ************/
        //AdbUtils.enableAdb(context);

        /*********** AlarmUtils ************/
        //AlarmUtils.setPowerOffAlarm(context, System.currentTimeMillis() + 80000);

        /*********** AudioUtils ************/
        //AudioUtils.playRmOgg(context, Uri.parse("android.resource://" + context.getPackageName()+"/"+ R.raw.scan_new));
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 40; i++) {
                    AudioUtils.playRmOgg(context, Uri.parse("android.resource://" + context.getPackageName()+"/"+ R.raw.scan_new));
                    //AudioUtils.playSpOgg(context, "/system/etc/Scan_new.ogg");
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();*/
        //AudioUtils.playSpOgg(context, "/system/etc/Scan_buzzer.ogg");
        //int ret = AudioUtils.getCurrentVolume(context, AudioManager.STREAM_VOICE_CALL);

        /*********** BluetoothUtils ************/
        //BluetoothUtils.getBondedDevices();

        /*********** CameraUtils ************/
        CameraUtils.takePhoto(context, CameraUtils.createImageFile(Environment.getExternalStorageDirectory() + "/stur/"), 1);

        /*********** ContactsUtils ************/
        //ContactsUtils.queryContacts(getContext());

        /*********** CrashReport ************/
        //CrashReport.testJavaCrash();
        //mTvOutput.setText(PackageUtils.getSign(getContext(), "com.qualcomm.uimremoteclient"));

        /*********** Date ************/
        //DateUtils.getTwoDay("20181025", "20190327");
        //long l = DateUtils.getElapsedRealtime();

        /*********** DB ************/
        //Settings.System.putInt(getContext().getContentResolver(),"pointer_screenshotchord", 1);
        //Settings.Secure.putInt(getContext().getContentResolver(), "lock_screen_show_notifications", 0);

        /*********** Display ************/
        /*(new ColorManagerUtils(context)).setDisplayMode(ColorManagerUtils.CE_BLACKWHITE);
        (new ColorManagerUtils(context)).setDefaultMode(ColorManagerUtils.CE_BLACKWHITE);*/

        /*********** FileUtils ************/
        //FileUtils.deleteDirectory("data/user/0/com.android.providers.contacts");
        //FileUtils.shareApk(context, "com.stur.chest");
        /*try {
                    loadSettingsConfigXml("/system/etc/un_settings_conf.xml");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }*/
        //FileUtils.deleteDirectory("/sdcard/YunDaLog");
        /*RecursiveFileObserver lister = new RecursiveFileObserver("/sdcard/YUNDARecord/Normal Records/");
        lister.startWatching();*/
        //List<String> list = FileUtils.searchAllFile("/sdcard/YUNDARecord/Normal Records/", "YR");

        /*********** GMS ************/

        /*********** Intent ************/
        //IntentUtils.sendUnreadedNumIntent(context);

        /*String[] intents = {"un.intent.action.AUTO_CALL_RECORDER_ENABLED_CHANGED", "un.intent.action.AUTO_CALL_RECORDER_PERIOD_CHANGED"};
        IntentUtils.registerIntent(context, intents, new IntentUtils.IntentReceivedCallback() {
            @Override
            public void onIntentReceived(Intent intent) {
                if ("un.intent.action.AUTO_CALL_RECORDER_ENABLED_CHANGED".equals(intent.getAction())) {
                    int slotId = intent.getIntExtra("call_record_slot_id", 0);    // 当前被改变的卡槽ID，0为卡1，1为卡2
                    boolean enabled = intent.getBooleanExtra("call_record_enabled",false);  //当前被改变的卡槽ID的自动录音开关状态
                    Log.d(this, "onIntentReceived enabled change: slotId = " + slotId + ", enabled = " + enabled);
                } else if ("un.intent.action.AUTO_CALL_RECORDER_PERIOD_CHANGED".equals(intent.getAction())) {
                    int slotId = intent.getIntExtra("call_record_slot_id", 0);    // 当前被改变的卡槽ID，0为卡1，1为卡2
                    int peroid = intent.getIntExtra("call_record_period",0);  //当前被改变的卡槽ID的自动录音的周期变化
                    Log.d(this, "onIntentReceived: peroid change slotId = " + slotId + ", peroid = " + peroid);
                }
            }
        });*/
        /*IntentUtils.registerIntent(context, UsbManager.ACTION_USB_STATE, new IntentUtils.IntentReceivedCallback(){

        });*/

        /*********** JAVA ************/
        /**
         * 参数引用传递，
         * 实参：如果是基本类型或者String，则实参不会变（传的是值）；
         * 如果是对象集合或者数组，则实参会改变（传的是引用）。
         */
        /*String str = "1";
        function1(str);
        Log.d(this, "str = " + str);*/

        /*********** Location ************/
        //LocationHelper.getInstance(context).startLocation();

        /*********** Logcat ************/
        //Utils.execLogcat(context, "");

        /*********** NetworkUtils ************/
        //kNetworkUtils.requestCardConnect(context, 1);

        /*********** OsUtils ************/
        /*String brandName = OsUtils.getDeviceBrand();
        if ("Android".equals(brandName)) {
            brandName = "UROVO";
        }
        String deviceName = OsUtils.getSystemModel();
        String systemVersion = OsUtils.getSystemVersion();
        String channel = brandName + "_" + deviceName + "_" + systemVersion;
        UIHelper.toastMessageMiddle(context, "channel = " + channel);*/

        /*********** PackageUtils ************/
        //PackageUtils.initiateClearUserData(getContext(), "com.android.systemui");
        //boolean ret = PackageUtils.hasSystemFeature(context);
        //PackageUtils.setDefaultDialer(context, "com.android.dialer");

        /*********** qcnvitems ************/
        /*String l = null;
        try {
            QcNvItems qni = new QcNvItems(context);
            l = qni.getQcnVersion();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        /*********** ReflectUtil ************/
        // 反射读取静态变量
        /*Boolean featureEnabled = (Boolean) ReflectUtil.getStaticFieldValue("com.uner.android.feature.UnerFeature",
                "FEATURE_SET_DEFALUT_PERMISSION", false);*/

        /*********** startActivity ************/
        //拉起某个界面
        /*Intent intent = new Intent(context, ChipsailingFingerprintActivity.class);
        context.startActivity(intent);*/
        /*Intent intent =
                new Intent("android.provider.Telephony.SECRET_CODE", Uri.parse("android_secret_code://" + "76266344"));
        ComponentName cn = new ComponentName("com.qualcomm.qti.modemtestmode", "com.qualcomm.qti.modemtestmode.DefaultReceiver");
        intent.setComponent(cn);
        context.sendBroadcast(intent);*/

        /*Intent intent1 = new Intent();
        ComponentName cn1 = new ComponentName("com.qualcomm.qti.modemtestmode", "com.qualcomm.qti.modemtestmode.MbnFileActivate");
        intent1.setComponent(cn1);*/
        //intent.setAction("android.intent.action.MBNFileActivate");
        //intent.setClassName("com.qualcomm.qti.modemtestmode", "com.qualcomm.qti.modemtestmode.MbnFileActivate");
        //context.startActivity(intent1);

        /*PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage("com.qualcomm.qti.modemtestmode");
        context.startActivity(intent);*/

        //拉起某个服务
        /*Intent startIntent = new Intent(this, ChestService.class);
        startService(startIntent);*/

        /*********** StringUtils ************/
        //byte[] ba = StringUtils.hexStrToByteArray("00A40004023F00");

        /*********** SystemUIUtils ************/
        //SystemUIUtils.sendBroadcastForFlashLight(getContext());
        //SystemUIUtils.setLockNone(getContext());
        //SystemUIUtils.notifyLauncherUI(getContext(), "com.un.coolmessage","com.yulong.android.contacts.dial.DialActivity", 3);
        //SystemUIUtils.getWallpaperBitmap(context);

        /*********** SystemProperties ************/
        //String str = SystemPropertiesProxy.get(context, "pwv.custom.custom.attach");
        //SystemProperties.set("service.adb.tcp.port", "5555");
        //SystemPropertiesProxy.set(context, "service.adb.tcp.port", "5555");
        //Log.d(this, "SystemPropertiesProxy = " + str);

        /*********** TMUtils ************/
        //String ret = IccidParser.getInstance().getCarrier("898603");
        //TMUtils.callPhone(getContext(), "10010", 0);
        //new TMUtils.MissedCallObserver(getContext());

        /*********** ThreadUtils ************/
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InetAddress addr = InetAddress.getByName("1.2.3.4.");
                    String name = addr.getHostName();
                    Log.d(this, "getByName: " + name);
                    UIHelper.toastMessage(context, "getByName: " + name );
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    Log.d(this, "UnknownHostException");
                    UIHelper.toastMessage(context, "UnknownHostException: " + e);
                } catch (Exception e) {
                    Log.d(this, "Exception: " + e);
                    UIHelper.toastMessage(context, "Exception: " + e);
                }
            }
        }).start();*/

        /*********** UIHelper ************/
        //UIHelper.showNotification(context, 5555, UIHelper.buildNotificationBuilder(context, "test", "test").build());

        /*********** UsbUtils ************/
        //UsbUtils.getUsbPortStatus(context);

        /*********** WifiUtils ************/
        //开启ping
        //WifiUtils.startPing();
        //从arp缓存中获取某个mac地址对应的IP地址
        /*ArrayList<ClientScanResult> csrList = WifiUtils.getClientList(false, 0);
        for (ClientScanResult csr : csrList) {
        if(csr.getMac().equals("fc:25:3f:c2:3b:0a")) {
        mTv_output.setText(csr.getAddress()); }
        }*/

        Utils.display(context, "Clicked! ret = " + ret);
    }

    private void function1(String str) {
        str = "2";
    }
}
