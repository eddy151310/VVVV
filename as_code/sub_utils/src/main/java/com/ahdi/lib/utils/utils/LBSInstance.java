package com.ahdi.lib.utils.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

//            61 ： GPS定位结果，GPS定位成功。
//            62 ： 无法获取有效定位依据，定位失败，请检查运营商网络或者wifi网络是否正常开启，尝试重新请求定位。
//            63 ： 网络异常，没有成功向服务器发起请求，请确认当前测试手机网络是否通畅，尝试重新请求定位。
//            65 ： 定位缓存的结果。
//            66 ： 离线定位结果。通过requestOfflineLocaiton调用时对应的返回结果。
//            67 ： 离线定位失败。通过requestOfflineLocaiton调用时对应的返回结果。
//            68 ： 网络连接失败时，查找本地离线定位时对应的返回结果。
//            161： 网络定位结果，网络定位定位成功。
//            162： 请求串密文解析失败。
//            167： 服务端定位失败，请您检查是否禁用获取位置信息权限，尝试重新请求定位。
//            502： key参数错误，请按照说明文档重新申请KEY。
//            505： key不存在或者非法，请按照说明文档重新申请KEY。
//            601： key服务被开发者自己禁用，请按照说明文档重新申请KEY。
//            602： key mcode不匹配，您的ak配置过程中安全码设置有问题，请确保：sha1正确，“;”分号是英文状态；且包名是您当前运行应用的包名，请按照说明文档重新申请KEY。
//            501～700：key验证失败，请按照说明文档重新申请KEY。
public class LBSInstance {

    private static final String TAG = "LBSInstance";

    public LocationClient mLocationClient = null;
    public LBSCallBack mLBSCallBack = null;
    private MyLocationListener myListener = new MyLocationListener();


    //类初始化时，不初始化这个对象(延时加载，真正用的时候再创建)
    private static LBSInstance instance;

    //构造器私有化
    private LBSInstance(){}

    //方法同步，调用效率低
    public static synchronized LBSInstance getInstance(){
        if(instance == null){
            instance = new LBSInstance();
        }
        return instance;
    }



    //BDAbstractLocationListener为7.2版本新增的Abstract类型的监听接口
    //原有BDLocationListener接口暂时同步保留。具体介绍请参考后文第四步的说明
    //初始化LBS服务
    public void initLBS(Context mContext) {

        this.mLBSCallBack = mLBSCallBack ;

        //声明LocationClient类
        mLocationClient = new LocationClient(mContext.getApplicationContext());
        //设置LocationClient的LocationClientOption相关参数
        setmLocationClient(mLocationClient);
    }


    //获取经纬度
    public void getLBS(LBSCallBack  mLBSCallBack) {

        this.mLBSCallBack = mLBSCallBack ;

        if(mLocationClient == null){
            LogUtil.e(TAG , "请先初始化定位服务");
            return ;
        }

        //注册监听函数
        mLocationClient.registerLocationListener(myListener);
        mLocationClient.start();
    }


    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取经纬度相关（常用）的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明
            if(location != null  ){
                String city = location.getStreet();
                double latitude = location.getLatitude();    //获取纬度信息
                double longitude = location.getLongitude();    //获取经度信息
                float radius = location.getRadius();    //获取定位精度，默认值为0.0f
                //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准
                String coorType = location.getCoorType();

                //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明
                int errorCode = location.getLocType();
                String errorMsg = location.getLocTypeDescription();

                if (null != location && radius > 0.0f ) {
                    mLBSCallBack.onSuccess( location );
                }else{
                    mLBSCallBack.onFail(errorCode , errorMsg );
                }
            }




            mLocationClient.stop();
        }
    }

    public void setmLocationClient(LocationClient mLocationClient) {

        LocationClientOption option = new LocationClientOption();

        //可选，设置定位模式，默认高精度
        //LocationMode.Hight_Accuracy：高精度；
        //LocationMode. Battery_Saving：低功耗；
        //LocationMode. Device_Sensors：仅使用设备；
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);

        //可选，设置返回经纬度坐标类型，默认GCJ02
        //GCJ02：国测局坐标；
        //BD09ll：百度经纬度坐标；
        //BD09：百度墨卡托坐标；
        //海外地区定位，无需设置坐标类型，统一返回WGS84类型坐标
        option.setCoorType("bd09ll");


        //可选，设置是否需要地址信息，默认不需要
        option.setIsNeedAddress(true);

        //可选，设置发起定位请求的间隔，int类型，单位ms
        //如果设置为0，则代表单次定位，即仅定位一次，默认为0
        //如果设置非0，需设置1000ms以上才有效
        option.setScanSpan(0);

        //可选，设置是否使用gps，默认false
        //使用高精度和仅用设备两种定位模式的，参数必须设置为true
        option.setOpenGps(true);

        //可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false
        option.setLocationNotify(true);

        //可选，定位SDK内部是一个service，并放到了独立进程。
        //设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)
        option.setIgnoreKillProcess(false);

        //可选，设置是否收集Crash信息，默认收集，即参数为false
        option.SetIgnoreCacheException(true);

        //可选，V7.2版本新增能力
        //如果设置了该接口，首次启动定位时，会先判断当前Wi-Fi是否超出有效期，若超出有效期，会先重新扫描Wi-Fi，然后定位
        option.setWifiCacheTimeOut(5 * 60 * 1000);

        //可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false
        option.setEnableSimulateGps(false);

        //mLocationClient为第二步初始化过的LocationClient对象
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        //更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明
        mLocationClient.setLocOption(option);
    }



    public interface LBSCallBack {

        void onSuccess( BDLocation location);

        void onFail( int errorCode , String errorMsg );
    }

}
