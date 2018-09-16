package com.stur.chest.utils;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.stur.chest.dto.UserAccountDTO;
import com.stur.lib.Log;
import com.stur.lib.UIHelper;
import com.stur.lib.location.LocationHelper;
import com.stur.lib.web.HttpFactory;
import com.stur.lib.web.api.HttpResponseListener;
import com.stur.lib.web.request.HttpMethod;
import com.stur.lib.web.request.HttpRequest;
import com.stur.lib.web.response.HttpResponse;

import java.lang.reflect.Type;

/**
 * Created by guanxuejin on 2018/9/15.
 */

public class TestUtils {
    /**
     * 单元测试模块，按照首字母排序
     * @param context
     */
    public void unitTest(Context context) {
        /*********** BluetoothUtils ************/
        //BluetoothUtils.getBondedDevices();

        /*********** ContactsUtils ************/
        //ContactsUtils.queryContacts(getContext());

        /*********** CrashReport ************/
        //CrashReport.testJavaCrash();
        //mTvOutput.setText(PackageUtils.getSign(getContext(), "com.qualcomm.uimremoteclient"));

        /*********** DB ************/
        //Settings.System.putInt(getContext().getContentResolver(),"pointer_screenshotchord", 1);
        //Settings.Secure.putInt(getContext().getContentResolver(), "lock_screen_show_notifications", 0);

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

        /*********** HTTP ************/
        //testHttpRequest(context);

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
        LocationHelper.getInstance(context).startLocation();

        /*********** PackageUtils ************/
        //PackageUtils.initiateClearUserData(getContext(), "com.android.systemui");

        /*********** startActivity ************/
        //拉起某个界面
        /*Intent intent = new Intent(this, WiFiActivity.class);
        startActivity(intent);*/
        //拉起某个 服务
        /*Intent startIntent = new Intent(this, ChestService.class);
        startService(startIntent);*/

        /*********** StringUtils ************/
        //byte[] ba = StringUtils.hexStrToByteArray("00A40004023F00");

        /*********** SystemUIUtils ************/
        //SystemUIUtils.sendBroadcastForFlashLight(getContext());
        //SystemUIUtils.setLockNone(getContext());
        //SystemUIUtils.notifyLauncherUI(getContext(), "com.un.coolmessage","com.yulong.android.contacts.dial.DialActivity", 3);

        /*********** TMUtils ************/
        //String ret = IccidParser.getInstance().getCarrier("898603");
        //TMUtils.callPhone(getContext(), "10010", 0);
        //new TMUtils.MissedCallObserver(getContext());

        /*********** WifiUtils ************/
        //开启ping
        //WifiUtils.startPing();
        //从arp缓存中获取某个mac地址对应的IP地址
        /*ArrayList<ClientScanResult> csrList = WifiUtils.getClientList(false, 0);
        for (ClientScanResult csr : csrList) {
        if(csr.getMac().equals("fc:25:3f:c2:3b:0a")) {
        mTv_output.setText(csr.getAddress()); }
        }*/
    }

    private static void testHttpRequest(final Context context) {
        HttpRequest httpRequest = new HttpRequest("http://10.66.128.123:8088/test", HttpMethod.POST);
        httpRequest.addParams(ApiUtils.KEY_TOKEN, "123456");
        HttpFactory.getHttpService().sendRequest(httpRequest, new HttpResponseListener() {
            @Override
            public void onSuccess(HttpResponse response) {
                Log.d(this, "[response]" + response.toString());
                Type type = new TypeToken<UserAccountDTO>() {
                }.getType();
                UserAccountDTO uat = response.convert(type);
                String email = uat.getEmail();
                UIHelper.toastMessageMiddle(context, "email = " + email);
            }
        });
    }

    private void function1(String str) {
        str = "2";
    }
}
