package com.stur.lib.uicc;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Xml;

import com.stur.lib.Log;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.HashMap;

public class IccidParser {
    private HashMap<String, String> mCarrierIccidMap;

    static final String PARTNER_ICCID_OVERRIDE_PATH ="iccid-conf.xml";
    private static final boolean DBG = true;
    private Context mContext;

    private static IccidParser mInstance ;
    public static IccidParser getInstance() {
            synchronized(IccidParser.class) {
                if (mInstance == null) {
                    mInstance = new IccidParser();
                }
                return mInstance;
            }
    }

    //在对外接口被调用之前初始化函数必须先被执行
    public void init(Context context) {
        mContext = context;
        try {
            loadIccidOverrides(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    boolean containsIccid(String iccid) {
        return mCarrierIccidMap.containsKey(iccid);
    }

    public String getCarrier(String iccid) {
        if (DBG) Log.d(this, "iccid:" + iccid + ", carrier:" + mCarrierIccidMap.get(iccid));
        return mCarrierIccidMap.get(iccid);
    }

    /**
     * 使用PULL解析XML
     * @param context
     * @return
     * @throws Exception
     */
    private void loadIccidOverrides(Context context) throws Exception {
        AssetManager am = context.getAssets();
        InputStream is = am.open(PARTNER_ICCID_OVERRIDE_PATH);
        XmlPullParser pullParser = Xml.newPullParser();
        pullParser.setInput(is, "UTF-8");
        int event = pullParser.getEventType();// 触发第一个事件
        String iccid = null;
        String carrier = null;
        while (event != XmlPullParser.END_DOCUMENT) {
            switch (event) {
                case XmlPullParser.START_DOCUMENT:
                    mCarrierIccidMap = new HashMap<String, String>();  //开始读取xml时创建hm对象
                    break;
                case XmlPullParser.START_TAG:
                    if ("iccidOverride".equals(pullParser.getName())) {
                        iccid = pullParser.getAttributeValue(0);
                        carrier = pullParser.getAttributeValue(1);
                    }
                    /* 如果有body内容在下面解析，这个xml没有body，只有attr，所以不继续解析
                        else if ("person".equals(pullParser.getName())) {
                            String personA = pullParser.nextText();
                        }
                        if ("age".equals(pullParser.getName())) {
                            String ageA = pullParser.nextText();
                        }*/
                    break;
                case XmlPullParser.END_TAG:
                    if ("iccidOverride".equals(pullParser.getName())) {
                        mCarrierIccidMap.put(iccid, carrier);
                    }
                    break;
            }
            event = pullParser.next();
        }
    }
}