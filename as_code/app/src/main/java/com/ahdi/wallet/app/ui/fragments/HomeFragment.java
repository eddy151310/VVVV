package com.ahdi.wallet.app.ui.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ahdi.wallet.app.sdk.UserSdk;
import com.ahdi.wallet.app.bean.LoginBean;
import com.ahdi.wallet.app.callback.OtherSdkCallBack;
import com.ahdi.wallet.app.callback.UserSdkCallBack;
import com.ahdi.wallet.app.response.BannerInfoRsp;
import com.ahdi.wallet.app.response.LoginRsp;
import com.ahdi.wallet.app.schemas.AuthSchema;
import com.ahdi.wallet.app.schemas.BannerSchema;
import com.ahdi.lib.utils.base.ActivityManager;
import com.ahdi.lib.utils.base.BaseFragment;
import com.ahdi.lib.utils.bean.SchemaBean;
import com.ahdi.lib.utils.bean.UserData;
import com.ahdi.lib.utils.config.ConfigCountry;
import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.fingerprint.FingerprintSDK;
import com.ahdi.lib.utils.imagedown.ImageDownUtil;
import com.ahdi.lib.utils.utils.AmountUtil;
import com.ahdi.lib.utils.utils.AppGlobalUtil;
import com.ahdi.lib.utils.utils.CleanConfigUtil;
import com.ahdi.lib.utils.utils.DeviceUtil;
import com.ahdi.lib.utils.utils.LogUtil;
import com.ahdi.lib.utils.utils.ProfileUserUtil;
import com.ahdi.lib.utils.utils.SchemaUtil;
import com.ahdi.lib.utils.utils.TouchIDStateUtil;
import com.ahdi.lib.utils.widgets.CheckSafety;
import com.ahdi.lib.utils.widgets.ToastUtil;
import com.ahdi.lib.utils.widgets.banner.Banner;
import com.ahdi.lib.utils.widgets.banner.BannerAdapter;
import com.ahdi.lib.utils.widgets.dialog.LoadingDialog;
import com.ahdi.wallet.GlobalApplication;
import com.ahdi.wallet.R;
import com.ahdi.wallet.app.main.AppMain;
import com.ahdi.wallet.app.ui.activities.AppMainActivity;
import com.ahdi.wallet.app.ui.activities.balance.BalanceActivity;
import com.ahdi.wallet.app.ui.activities.biometric.GuideTouchIDUnlockActivity;
import com.ahdi.wallet.app.ui.activities.msg.MessageActivity;
import com.ahdi.wallet.app.ui.activities.payPwd.PayPwdGuideSetActivity;
import com.ahdi.wallet.module.QRCode.ui.activities.PayQRCodeActivity;
import com.ahdi.wallet.module.payment.topup.ui.activities.TopUpActivity;
import com.ahdi.wallet.app.ui.activities.transList.TransListActivity;
import com.ahdi.wallet.module.payment.transfer.ui.activities.TransferMainActivity;
import com.ahdi.wallet.app.ui.activities.other.WebBannerActivity;
import com.ahdi.wallet.zbar.CaptureActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Date: 2017/12/27 上午11:14
 * Author: kay lau
 * Description:
 */
public class HomeFragment extends BaseFragment implements View.OnClickListener {

    public interface OnUpdateProfileListener {

        void onUpdateProfile();
    }

    private static final String TAG = HomeFragment.class.getSimpleName();
    /**
     * 相机权限的请求码
     */
    private static final int REQUEST_CAMERA_PERMISSION = 101;
    /**
     * title
     */
    private Button btn_message = null;
    /**
     * 用户信息区域
     */
    private ImageView iv_user_icon, iv_eye;
    private TextView tv_lName, tv_balance;
    /**
     * 功能区域
     */
    private RelativeLayout rl_scan_qr, rl_my_qr, rl_transfer, rl_transaction;
    /**
     * banner
     */
    private Banner home_banner;
    private LinearLayout ll_show_all;
    private ImageView iv_banner_left, iv_banner_right;

    /**
     * isAgainQuery: 锁屏与首页界面来回切换时, onResume()方法多次执行,
     * 保证一次网络请求执行完成之后, 如果界面再切换时继续执行下一次网络请求
     */
    private boolean isAgainQuery = true;

    /**
     * 是否显示余额, 默认显示余额
     */
    private boolean isShowEye = true;
    /**
     * banner需要的数据
     */
    private List<BannerSchema> mDatas = new ArrayList<>();
    private String moreBanner;
    private BannerSchema[] staticBanners;
    private String areaCode = ConfigCountry.KEY_AREA_CODE;

    private OnUpdateProfileListener onUpdateProfileListener;

    private String from;

    public void setFrom(String from) {
        this.from = from;
    }

    public void setOnUpdateProfileListener(OnUpdateProfileListener onUpdateProfileListener) {
        this.onUpdateProfileListener = onUpdateProfileListener;
    }

    @Override
    protected View initRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.layout_fragment_home, container, false);
    }

    @Override
    protected void initView(View view) {

        View titleView = view.findViewById(R.id.title);
        titleView.setBackgroundResource(R.color.CC_00000000);
        //初始化控件
        Button btn_back = view.findViewById(R.id.btn_back);
        btn_back.setBackgroundColor(Color.TRANSPARENT);
        btn_back.setText(getString(R.string.AppHome_A0));
        btn_back.setTextColor(getResources().getColor(R.color.CC_282934));
        btn_back.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.text_size_17));
        btn_back.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.common_mbm_nav_logo), null, null, null);
        btn_back.setCompoundDrawablePadding(5);
        //设置距左R.dimen.dp_10
        RelativeLayout.LayoutParams layoutParams;
        layoutParams = (RelativeLayout.LayoutParams) btn_back.getLayoutParams();
        layoutParams.setMargins(getResources().getDimensionPixelSize(R.dimen.dp_10), 0, 0, 0);//4个参数按顺序分别是左上右下
        btn_back.setLayoutParams(layoutParams);
        btn_message = view.findViewById(R.id.btn_next);
        btn_message.setVisibility(View.VISIBLE);
        btn_message.setOnClickListener(this);

        //用户信息
        iv_user_icon = view.findViewById(R.id.iv_user_icon);
        tv_lName = view.findViewById(R.id.tv_lName);
        iv_eye = view.findViewById(R.id.iv_eye);
        iv_eye.setOnClickListener(this);
        tv_balance = view.findViewById(R.id.tv_today_transaction);

        //banner
        home_banner = view.findViewById(R.id.home_banner);
        ll_show_all = view.findViewById(R.id.ll_show_all);
        ll_show_all.setOnClickListener(this);
        iv_banner_left = view.findViewById(R.id.iv_banner_left);
        iv_banner_left.setOnClickListener(this);
        iv_banner_right = view.findViewById(R.id.iv_banner_right);
        iv_banner_right.setOnClickListener(this);

        //功能区域
        rl_scan_qr = view.findViewById(R.id.rl_scan_qr);
        rl_scan_qr.setOnClickListener(this);
        rl_my_qr = view.findViewById(R.id.rl_my_qr);
        rl_my_qr.setOnClickListener(this);
        rl_transfer = view.findViewById(R.id.rl_topup);
        rl_transfer.setOnClickListener(this);
        rl_transaction = view.findViewById(R.id.rl_transaction);
        rl_transaction.setOnClickListener(this);
    }

    @Override
    protected void initData(View view) {
        refreshBalance();
        updateMsgStatus(ProfileUserUtil.getInstance().isHasMsg());
        initBanner();
        try {
            refreshBanner(new JSONObject(AppGlobalUtil.getInstance().getString(mActivity, Constants.LOCAL_BANNER_JSON_KEY)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id != R.id.iv_eye && !isCanClick()) {
            return;
        }
        //需要引导支付密码的按钮 扫码、收付款、转账、充值
        if ((id == R.id.rl_scan_qr || id == R.id.rl_my_qr || id == R.id.rl_topup)
                && !CheckSafety.checkSafe(mActivity, PayPwdGuideSetActivity.class)) {
            return;
        }
        switch (id) {
            case R.id.rl_scan_qr:
                //测试 Fragment 调用 AppMainActivity 内的方法
//                AppMainActivity mAppMainActivity = (AppMainActivity)mActivity;
//                if(!mAppMainActivity.checkPermissions(mAppMainActivity.request_type_camera)){
//                    mAppMainActivity.requestPermissions(mAppMainActivity.request_type_camera);
//                }else{
//
//                }

                checkCameraPer();
                break;
            case R.id.rl_my_qr:
                openPayCode();
                break;
            case R.id.rl_topup:
                openChargeActivity();
                break;
            case R.id.rl_transaction:
                openTransList();
                break;
            case R.id.iv_eye:
                balanceShowSwitch();
                break;
            case R.id.btn_next:
                openMsgList();
                break;
            case R.id.ll_show_all:
                gotoWebBannerActivity(moreBanner);
                break;
            case R.id.iv_banner_left:
                if (staticBanners != null && staticBanners.length > 0) {
                    gotoWebBannerActivity(staticBanners[0].getClick());
                }
                break;
            case R.id.iv_banner_right:
                if (staticBanners != null && staticBanners.length > 1) {
                    gotoWebBannerActivity(staticBanners[1].getClick());
                }
                break;
            default:
                break;
        }
    }

    private void openTransfer() {
        Intent intent = new Intent(mActivity, TransferMainActivity.class);
        startActivity(intent);
    }

    private void balanceShowSwitch() {
        if (isShowEye) {
            isShowEye = false;
            hiddenBalance();
        } else {
            isShowEye = true;
            showBalance();
        }
    }

    private void openMsgList() {
        Intent intentToMsg = new Intent(mActivity, MessageActivity.class);
        startActivity(intentToMsg);
    }

    private void openTransList() {
        Intent intent4 = new Intent(mActivity, TransListActivity.class);
        startActivity(intent4);
    }

    private void openChargeActivity() {
        Intent topUp = new Intent(mActivity, TopUpActivity.class);
        startActivity(topUp);
    }

    /**
     * 跳到H5网页
     *
     * @param clickUrl
     */
    private void gotoWebBannerActivity(String clickUrl) {
        if (TextUtils.isEmpty(clickUrl)) {
            LogUtil.e(TAG, "url为空");
            return;
        }
        if (SchemaUtil.isHttpSchema(clickUrl)) {
            Intent intent = new Intent(mActivity, WebBannerActivity.class);
            intent.putExtra(Constants.LOCAL_WEB_VIEW_URL_KEY, clickUrl);
            startActivity(intent);

        } else {
            SchemaBean schemaBean = SchemaUtil.doScheme(clickUrl);
            if (schemaBean.getType() == SchemaUtil.TYPE_BALANCE) {
                // 打开余额界面
                Intent intent = new Intent(mActivity, BalanceActivity.class);
                startActivity(intent);

            } else if (schemaBean.getType() == SchemaUtil.TYPE_TRANSFER) {
                //引导设置 支付密码
                if (CheckSafety.checkSafe(mActivity, PayPwdGuideSetActivity.class)) {
                    // 开始转账
                    openTransfer();
                }
            } else {
                LogUtil.e(TAG, "url 格式不合规");
            }
        }
    }

    /**
     * 检查摄像头权限，做相应的处理
     */
    private void checkCameraPer() {

        if (Build.VERSION.SDK_INT < 23 || ContextCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openScan();
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }

    private void openScan() {
        if (DeviceUtil.cameraIsCanUse()) {
            Intent intent = new Intent(mActivity, CaptureActivity.class);
            startActivity(intent);
        } else {
            openAppSettingDetails();
        }
    }

    /**
     * 打开Pay码界面
     */
    private void openPayCode() {
        Intent intent = new Intent(mActivity, PayQRCodeActivity.class);
        mActivity.startActivity(intent);
    }

    private void hiddenBalance() {
        if (tv_balance != null) {
            tv_balance.setText(ConfigCountry.KEY_CURRENCY_SYMBOL.concat(Constants.KEY_SIX_XING));
        } else {
            LogUtil.e(TAG, "tv_balance is null");
        }
        if (iv_eye != null) {
            iv_eye.setImageResource(R.mipmap.common_eye_close_white);
        } else {
            LogUtil.e(TAG, "iv_eye is null");
        }
    }

    private void showBalance() {
        String balance = ProfileUserUtil.getInstance().getAccountBalance();
        if (!TextUtils.isEmpty(balance)) {
            if (tv_balance != null) {
                tv_balance.setText(AmountUtil.getFormatAmount(ConfigCountry.KEY_CURRENCY_SYMBOL, balance));
            } else {
                LogUtil.e(TAG, "tv_balance is null");
            }
        } else {
            if (tv_balance != null) {
                tv_balance.setText(ConfigCountry.KEY_CURRENCY_SYMBOL.concat(Constants.KEY_SIX_LINE));
            } else {
                LogUtil.e(TAG, "tv_balance is null");
            }
        }
        if (iv_eye != null) {
            iv_eye.setImageResource(R.mipmap.common_eye_show_white);
        } else {
            LogUtil.e(TAG, "iv_eye is null");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isHidden()) {
            LogUtil.d(TAG, " ----------- isHidden------------------");
            return;
        }
        refreshBalance();
        if (!isQuest()) {
            return;
        }
        String sid = GlobalApplication.getApplication().getSID();
        String lName = getLoginName();
        String voucher = AppGlobalUtil.getInstance().getString(mActivity, Constants.LOCAL_VOUCHER_KEY);

        if (TextUtils.isEmpty(sid)
                && !TextUtils.isEmpty(lName)
                && !TextUtils.isEmpty(voucher)) {
            api_auto_login(lName, voucher);// 自动登录

        } else if (!TextUtils.isEmpty(sid)
                && TextUtils.equals(from, GuideTouchIDUnlockActivity.TAG)) {
            // 手动登录, 引导指纹解锁界面点击skip, 打开首页时请求接口用户概要 banner
            if (onUpdateProfileListener != null) {
                onUpdateProfileListener.onUpdateProfile();
            }
            getBannerInfo();
            // 避免再次回到首页时, 会继续执行请求接口用户概要 banner
            setFrom("");
        } else {
            if (isAgainQuery) {
                getBannerInfo();//获取Banner Info
            }
        }
        updateMsgStatus(ProfileUserUtil.getInstance().isHasMsg());
    }

    private String getLoginName() {
        String lName = AppGlobalUtil.getInstance().getString(mActivity, Constants.LOCAL_LNAME_KEY);
        areaCode = AppGlobalUtil.getInstance().getString(mActivity, Constants.LOCAL_AREA_CODE_KEY);
        if (!TextUtils.isEmpty(lName) && lName.startsWith(areaCode)) {
            return lName;
        }
        if (!TextUtils.isEmpty(areaCode)) {
            lName = areaCode.concat(lName);
        }
        return lName;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            onResume();
        }
    }

    private void refreshBalance() {
        String balance = ProfileUserUtil.getInstance().getAccountBalance();
        if (TextUtils.isEmpty(balance)) {
            balance = "0";
        }
        onShowBalance(balance);
        refreshLoginState();
    }

    private void onShowBalance(String balance) {
        if (isShowEye) {
            if (tv_balance != null) {
                tv_balance.setText(AmountUtil.getFormatAmount(ConfigCountry.KEY_CURRENCY_SYMBOL, balance));
            }
            showBalance();
        } else {
            hiddenBalance();
        }
    }

    private void refreshLoginState() {
        if (iv_eye != null) {
            iv_eye.setVisibility(View.VISIBLE);
        } else {
            LogUtil.e(TAG, "iv_eye is null");
        }
        UserData userData = ProfileUserUtil.getInstance().getUserData();
        if (userData != null) {
            tv_lName.setText(userData.getsLName());
            tv_lName.setVisibility(View.VISIBLE);
            String avatar = userData.getAvatar();
            if (!TextUtils.isEmpty(avatar) && mActivity != null) {
                ImageDownUtil.downMySelfPhoto(mActivity, avatar, iv_user_icon);
            }
        }
    }

    private LoginBean getLoinBean(String loginName, String password) {
        LoginBean bean = new LoginBean();
        bean.setLoginName(loginName);
        bean.setLoginPassword(password); //LV方式    voucher 不需要加密
        bean.setLoginType(Constants.LV_KEY);
        return bean;
    }

    private int autoLoginNum = 3; //尝试3次
    private LoadingDialog loadingDialog;

    /**
     * 自动登录
     */
    private void api_auto_login(String loginName, String voucher) {
        autoLoginNum--;
        LogUtil.d(TAG, "---自动登录--");
        if (loadingDialog == null) {
            loadingDialog = showLoading();
        }
        AppMain.getInstance().goToLogin(mActivity, getLoinBean(loginName, voucher), new UserSdkCallBack() {
            @Override
            public void onResult(String code, String errorMsg, JSONObject jsonObject) {
                if (TextUtils.equals(code, UserSdk.LOCAL_PAY_SUCCESS)) {
                    closeLoading(loadingDialog);
                    loadingDialog = null;
                    LoginRsp resp = LoginRsp.decodeJson(LoginRsp.class, jsonObject);
                    //保存user
                    GlobalApplication.getApplication().updateUserSchema(resp.getUser());
                    AuthSchema auth = resp.getAuth();
                    if (auth != null) {
                        //保存sid
                        ProfileUserUtil.getInstance().setSID(auth.sid);
                    }
                    if (resp.getAccount() != null) {
                        GlobalApplication.getApplication().updateAccountSchema(resp.getAccount());
                    }
                    AppMain.getInstance().onTerminalBind();
                    refreshBalance();
                    if (onUpdateProfileListener != null) {
                        onUpdateProfileListener.onUpdateProfile();
                    }
                    getBannerInfo();
                    String LName = AppGlobalUtil.getInstance().getLName(mActivity);
                    if (!TouchIDStateUtil.isSkipGuideUnlock(mActivity, LName)
                            && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                            && FingerprintSDK.isSupport(mActivity)
                            && FingerprintSDK.isHasFingerprints(mActivity)
                            && !TouchIDStateUtil.isStartTouchIDUnlock(mActivity, LName)) {
                        // 引导设置指纹解锁登录
                        Intent intent = new Intent(mActivity, GuideTouchIDUnlockActivity.class);
                        startActivity(intent);
                    }
                } else if (TextUtils.equals(code, UserSdk.LOCAL_PAY_NETWORK_EXCEPTION)) {
                    if (autoLoginNum > 0) {
                        LogUtil.e(TAG, "网络异常 执行第" + (3 - autoLoginNum) + "次尝试自动登录");
                        api_auto_login(loginName, voucher);
                    } else {
                        intentLoginActivity(mActivity, UserSdk.LOCAL_PAY_NETWORK_EXCEPTION, errorMsg, false);
                    }
                } else {
                    intentLoginActivity(mActivity, code, errorMsg, true);
                }
            }
        });
    }

    private void updateMsgStatus(boolean isHasMsg) {
        if (isHasMsg) {
            btn_message.setBackgroundResource(R.drawable.selector_btn_has_message);
        } else {
            btn_message.setBackgroundResource(R.drawable.selector_btn_no_message);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION && grantResults != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openScan();
        } else {
            openAppSettingDetails();
        }
    }

    private void intentLoginActivity(Activity mActivity, String errorCode, String errorMsg, boolean isClean) {
        closeLoading(loadingDialog);
        LogUtil.d(TAG, "跳转到登录界面");
        autoLoginNum = 3;
        //登录凭证无效   PP002 无需提示
        if (!TextUtils.equals(errorCode, Constants.RET_CODE_PP002)) {
            ToastUtil.showToastLong(mActivity, errorMsg);
        }
        if (isClean) {
            //清空Voucher
            CleanConfigUtil.cleanVoucher();
        }
        // 都去登录页面
        ActivityManager.getInstance().openLoginActivity(mActivity);
    }

    /**
     * 获取banner的数据
     */
    private void getBannerInfo() {
        if (!isAgainQuery) {
            return;
        }
        isAgainQuery = false;
        AppMain.getInstance().getBannerInfo(mActivity, Constants.BANNER_REQUEST_PARAM, new OtherSdkCallBack() {
            @Override
            public void onResult(String code, String errorMsg, JSONObject jsonObject) {
                isAgainQuery = true;
                if (TextUtils.equals(code, Constants.RET_CODE_SUCCESS)) {
                    refreshBanner(jsonObject);
                } else {
                    LogUtil.e(TAG, errorMsg);
                }
            }
        });
    }

    public void refreshBanner(JSONObject jsonObject) {
        if (jsonObject == null || mActivity == null) {
            return;
        }
        BannerInfoRsp bannerInfoResponse = BannerInfoRsp.decodeJson(BannerInfoRsp.class, jsonObject);
        if (bannerInfoResponse != null) {
            //保存相关数据
            AppGlobalUtil.getInstance().putString(AppGlobalUtil.getInstance().getContext(), Constants.LOCAL_BANNER_JSON_KEY, jsonObject.toString());

            moreBanner = bannerInfoResponse.more;
            //静态Banner
            BannerSchema[] banners = bannerInfoResponse.staticBanners;
            if (banners != null && banners.length > 0) {
                staticBanners = banners;
                if (banners.length == 1) {
                    ImageDownUtil.downStaticBannerImage(mActivity, banners[0].getContent(), iv_banner_left);
                } else {
                    ImageDownUtil.downStaticBannerImage(mActivity, banners[0].getContent(), iv_banner_left);
                    ImageDownUtil.downStaticBannerImage(mActivity, banners[1].getContent(), iv_banner_right);
                }
            }

            //轮播Banner
            BannerSchema[] scrollBanners = bannerInfoResponse.scrollBanners;
            if (scrollBanners != null && scrollBanners.length > 0) {
                ArrayList<BannerSchema> mDatasTemp = new ArrayList<>(Arrays.asList(scrollBanners));

                //1、长度不同 直接刷新
                if (mDatas.size() != mDatasTemp.size()) {
                    updateBannerDatas(mDatasTemp);
                    return;
                }

                //2、循环变遍历
                for (int j = 0; j < mDatas.size(); j++) {
                    String urlLocal = mDatas.get(j).getContent();
                    String urlTemp = mDatasTemp.get(j).getContent();

                    //2.1  两个数组中的图片click地址 更新
                    mDatas.get(j).setClick(mDatasTemp.get(j).getClick());

                    //2.2 两个数组中的图片地址 不相等  直接刷新
                    if (!TextUtils.equals(urlLocal, urlTemp)) {
                        updateBannerDatas(mDatasTemp);
                        break;
                    }
                }
            }
        }
    }

    /**
     * 更新轮播banner的数据
     *
     * @param mDatasTemp
     */
    private void updateBannerDatas(ArrayList<BannerSchema> mDatasTemp) {
        mDatas.clear();
        mDatas.addAll(mDatasTemp);
        home_banner.notifyDataHasChanged();
    }

    private void initBanner() {
        mDatas.add(new BannerSchema());//占位
        home_banner.setBannerAdapter(new BannerAdapter<BannerSchema>(mDatas) {

            @Override
            protected void bindTips(TextView tv, BannerSchema bannerModel) {
            }

            @Override
            public void bindImage(ImageView imageView, BannerSchema bannerModel) {
                ImageDownUtil.downScrollBannerImage(mActivity, bannerModel.getContent(), imageView);
            }
        });

        home_banner.setOnBannerItemClickListener(new Banner.OnBannerItemClickListener() {
            @Override
            public void onItemClick(int position) {
                gotoWebBannerActivity(mDatas.get(position).getClick());
            }
        });
        home_banner.notifyDataHasChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (home_banner != null) {
            home_banner.pauseScroll();
            home_banner.removeAllViews();
            home_banner = null;
        }
    }

    public void updateProfile(String balance, boolean isHasMsg) {
        if (!TextUtils.isEmpty(balance) && isShowEye) {
            tv_balance.setText(balance);
        }
        updateMsgStatus(isHasMsg);
    }
}