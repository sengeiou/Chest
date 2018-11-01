package com.stur.lib.location;

import android.content.Context;
import android.telephony.CellLocation;
import android.telephony.cdma.CdmaCellLocation;

/*import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;*/
import com.stur.lib.Log;

//import com.baidu.location.BDAbstractLocationListener;
//import com.baidu.location.BDLocation;
//import com.baidu.location.LocationClientOption;

/**
 * BDLocation类，封装了定位SDK的定位结果，在BDLocationListener的onReceive方法中获取。通过该类用户可以获取错误码，位置的坐标，精度半径等信息。具体方法请参考类参考。
 * 获取定位返回错误码：：
 * public int getLocType ( )
 * 返回值：
 * 61 ： GPS定位结果，GPS定位成功。
 * 62 ： 无法获取有效定位依据，定位失败，请检查运营商网络或者WiFi网络是否正常开启，尝试重新请求定位。
 * 63 ： 网络异常，没有成功向服务器发起请求，请确认当前测试手机网络是否通畅，尝试重新请求定位。
 * 65 ： 定位缓存的结果。
 * 66 ： 离线定位结果。通过requestOfflineLocaiton调用时对应的返回结果。
 * 67 ： 离线定位失败。通过requestOfflineLocaiton调用时对应的返回结果。
 * 68 ： 网络连接失败时，查找本地离线定位时对应的返回结果。
 * 161： 网络定位结果，网络定位成功。
 * 162： 请求串密文解析失败，一般是由于客户端SO文件加载失败造成，请严格参照开发指南或demo开发，放入对应SO文件。
 * 167： 服务端定位失败，请您检查是否禁用获取位置信息权限，尝试重新请求定位。
 * 502： AK参数错误，请按照说明文档重新申请AK。
 * 505：AK不存在或者非法，请按照说明文档重新申请AK。
 * 601： AK服务被开发者自己禁用，请按照说明文档重新申请AK。
 * 602： key mcode不匹配，您的AK配置过程中安全码设置有问题，请确保：SHA1正确，“;”分号是英文状态；
 * 且包名是您当前运行应用的包名，请按照说明文档重新申请AK。
 * 501～700：AK验证失败，请按照说明文档重新申请AK。
 * 如果不能定位，请记住这个返回值，并到百度LBS开放平台论坛Andriod定位SDK版块中进行交流，网址：http://bbs.lbsyun.baidu.com/forum.php?mod=forumdisplay&fid=10 。
 * 若返回值是162~167，请将错误码、IMEI和定位时间反馈至邮箱loc-bugs@baidu.com，以便我们跟进追查问题。
 */
public class LocationHelper {
    public static String getTag() {
        return new Object() {
            public String getClassName() {
                String clazzName = this.getClass().getName();
                return clazzName.substring(clazzName.lastIndexOf('.') + 1, clazzName.lastIndexOf('$'));
            }
        }.getClassName();
    }

    public static int getBaseId(Context context) {
        CellLocation cellLocation = null;
        /*TelephonyManager telephonyManager = TelephonyManager.from(context);
        int defaultDataSub = SubscriptionManager.from(context).getDefaultDataSubscriptionId();
        int phoneId = SubscriptionManager.getPhoneId(defaultDataSub);
        if (telephonyManager != null) {
            cellLocation = telephonyManager.getCellLocation();
            int pt = telephonyManager.getCurrentPhoneType();
        }*/

        CdmaCellLocation cdmaCellLoaction = null;
        if (cellLocation instanceof CdmaCellLocation) {
            cdmaCellLoaction = (CdmaCellLocation) cellLocation;
        } else {
            Log.w(getTag(), "the celllocation is not CdmaCellLocation.");
            if (cellLocation != null) {
                Log.w(getTag(), "the celllocation class is " + cellLocation.getClass().getName());
            } else {
                Log.w(getTag(), "the celllocation is null");
            }
        }
        if (cdmaCellLoaction != null) {
            int baseId = cdmaCellLoaction.getBaseStationId();
            Log.d(getTag(), "baseId = " + baseId);
            return baseId;
        } else {
            Log.e(getTag(), "TelephonyManager return CdmaCellLocation value is null");
        }
        return 0;
    }

/*    private Context context;
    private LocationClient locationClient;
    private BDLocationListener bdLocationListener;
    private LocLoadListener locLoadListener;

    public LocationHelper(Context ctx, LocLoadListener listener) {
        context = ctx;
        locLoadListener = listener;
        locationClient = new LocationClient(ctx.getApplicationContext());

        bdLocationListener = new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation location) {
                //获取定位结果
                StringBuffer sb = new StringBuffer(256);
                sb.append("time : ");
                sb.append(location.getTime());    //获取定位时间
                sb.append("\nerror code : ");
                sb.append(location.getLocType());    //获取类型类型
                sb.append("\nlatitude : ");
                sb.append(location.getLatitude());    //获取纬度信息
                sb.append("\nlontitude : ");
                sb.append(location.getLongitude());    //获取经度信息
                sb.append("\nradius : ");
                sb.append(location.getRadius());    //获取定位精准度

                if (location.getLocType() == BDLocation.TypeGpsLocation) {
                    // GPS定位结果
                    sb.append("\nspeed : ");
                    sb.append(location.getSpeed());    // 单位：公里每小时
                    sb.append("\nsatellite : ");
                    sb.append(location.getSatelliteNumber());    //获取卫星数
                    sb.append("\nheight : ");
                    sb.append(location.getAltitude());    //获取海拔高度信息，单位米
                    sb.append("\ndirection : ");
                    sb.append(location.getDirection());    //获取方向信息，单位度
                    sb.append("\naddr : ");
                    sb.append(location.getAddrStr());    //获取地址信息
                    sb.append("\ndescribe : ");
                    sb.append("gps定位成功");
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                    // 网络定位结果
                    sb.append("\naddr : ");
                    sb.append(location.getAddrStr());    //获取地址信息
                    sb.append("\noperationers : ");
                    sb.append(location.getOperators());    //获取运营商信息
                    sb.append("\ndescribe : ");
                    sb.append("网络定位成功");
                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {
                    // 离线定位结果
                    sb.append("\ndescribe : ");
                    sb.append("离线定位成功，离线定位结果也是有效的");
                } else if (location.getLocType() == BDLocation.TypeServerError) {
                    sb.append("\ndescribe : ");
                    sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                    sb.append("\ndescribe : ");
                    sb.append("网络不同导致定位失败，请检查网络是否通畅");
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                    sb.append("\ndescribe : ");
                    sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                }

                sb.append("\nlocationdescribe : ");
                sb.append(location.getLocationDescribe());    //位置语义化信息

                List<Poi> list = location.getPoiList();    // POI数据
                if (list != null) {
                    sb.append("\n poilist size = : ");
                    sb.append(list.size());
                    for (Poi p : list) {
                        sb.append("\npoi= : ");
                        sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                    }
                }

                Logger.d("Baidu LocationApiDem : " + sb.toString());

                locLoadListener.onUpdateLocation(sb.toString());
                locationClient.stop();
            }

            @Override
            public void onConnectHotSpotMessage(String s, int i) {

            }
        };
        locationClient.registerLocationListener(bdLocationListener);
        initLocation();
    }*/

    /**
     * 初始化定位
     */
    /*private void initLocation() {
        //LocationClientOption option = new LocationClientOption();
        /**
         可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
         */
    //option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);

    /**
     * 可选，默认gcj02，设置返回的定位结果坐标系
     * WGS84：大地座标系，也是目前广泛使用的GPS全球卫星定位系统使用的坐标系；
     * GCJ02：表示经过国测局加密的坐标；
     * BD09：百度坐标系，其中bd09ll是百度经纬度坐标，bd09mc表示百度墨卡托米制坐标；
     * 对于百度定位Android SDK产品，海外地区只能使用WGS84，国内可使用GCJ02和BD09.
     */
    //option.setCoorType("bd09ll");

    //int span = 1000;
    //option.setScanSpan(span);
    //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的

    //option.setIsNeedAddress(true);
    //可选，设置是否需要地址信息，默认不需要

        /*option.setOpenGps(true);
        //可选，默认false,设置是否使用gps

        option.setLocationNotify(true);
        //可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果

        option.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”

        option.setIsNeedLocationPoiList(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到

        option.setIgnoreKillProcess(false);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死

        option.SetIgnoreCacheException(false);
        //可选，默认false，设置是否收集CRASH信息，默认收集

        option.setEnableSimulateGps(false);
        //可选，默认false，设置是否需要过滤GPS仿真结果，默认需要

        locationClient.setLocOption(option);
    }

    public void startLocation() {
        if (!locationClient.isStarted()) {
            locationClient.start();
        } else {
            Toast.makeText(context, "正在定位中....", Toast.LENGTH_LONG).show();
        }
    }


    public interface LocLoadListener {
        void onUpdateLocation(String location);

        void getLocationFailed(int locType);
    }*/

    /*private static LocationHelper sInstance;

    public static LocationHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new LocationHelper(context);
        }
        return sInstance;
    }

    public LocationClient mLocationClient = null;
    //BDAbstractLocationListener为7.2版本新增的Abstract类型的监听接口
    //原有BDLocationListener接口暂时同步保留。
    private MyLocationListener myListener = new MyLocationListener();

    public LocationHelper(Context context) {
        mLocationClient = new LocationClient(context);
        mLocationClient.registerLocationListener(myListener);  //注册监听函数
        setOption();
    }

    public void setOption() {
        LocationClientOption option = new LocationClientOption();*/

        /**
         * 可选，设置定位模式，默认高精度
         * LocationMode.Hight_Accuracy：高精度；
         * LocationMode. Battery_Saving：低功耗；
         * LocationMode. Device_Sensors：仅使用设备；
         */
        //option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);

        /**
         * 可选，设置返回经纬度坐标类型，默认GCJ02
         * GCJ02：国测局坐标；
         * BD09ll：百度经纬度坐标；
         * BD09：百度墨卡托坐标；
         * 海外地区定位，无需设置坐标类型，统一返回WGS84类型坐标
         */
        //option.setCoorType("bd09ll");

        /**
         * 可选，设置发起定位请求的间隔，int类型，单位ms
         * 如果设置为0，则代表单次定位，即仅定位一次，默认为0
         * 如果设置非0，需设置1000ms以上才有效
         */
        //option.setScanSpan(1000);

        //可选，设置是否使用gps，默认false,使用高精度和仅用设备两种定位模式的，参数必须设置为true
        //option.setOpenGps(true);

        //可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false
        //option.setLocationNotify(true);

        //可选，定位SDK内部是一个service，并放到了独立进程
        //设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)
        //option.setIgnoreKillProcess(false);

        //可选，设置是否收集Crash信息，默认收集，即参数为false
        //option.SetIgnoreCacheException(false);

        //可选，V7.2版本新增能力
        //如果设置了该接口，首次启动定位时，会先判断当前Wi-Fi是否超出有效期，若超出有效期，会先重新扫描Wi-Fi，然后定位
        //option.setWifiCacheTimeOut(5 * 60 * 1000);

        //可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false
        //option.setEnableSimulateGps(false);

        //mLocationClient为第二步初始化过的LocationClient对象
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        //更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明
        //mLocationClient.setLocOption(option);
    //}

    /**
     * 只需发起定位，便能够从BDAbstractLocationListener监听接口中获取定位结果信息。
     * start()：启动定位SDK；调用start()之后只需要等待定位结果自动回调即可。
     * stop()：关闭定位SDK。开发者定位场景如果是单次定位的场景，在收到定位结果之后直接调用stop()函数即可。
     * 如果stop()之后仍然想进行定位，可以再次start()等待定位结果回调即可。
     * 自V7.2版本起，新增LocationClient.reStart()方法，用于在某些特定的异常环境下重启定位。
     * 如果开发者想按照自己逻辑请求定位，可以在start()之后按照自己的逻辑请求LocationClient.requestLocation()函数，
     * 会主动触发定位SDK内部定位逻辑，等待定位回调即可。
     */
    /*public void startLocation() {
        mLocationClient.start();
    }

    public class MyLocationListener extends BDAbstractLocationListener {
        //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
        //以下只列举部分获取经纬度相关（常用）的结果信息
        //更多结果信息获取说明，请参照类参考中BDLocation类中的说明
        @Override
        public void onReceiveLocation(BDLocation location) {
            double latitude = location.getLatitude();    //获取纬度信息
            double longitude = location.getLongitude();    //获取经度信息
            float radius = location.getRadius();    //获取定位精度，默认值为0.0f

            //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准
            String coorType = location.getCoorType();

            //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明
            int errorCode = location.getLocType();
        }
    }*/
}
