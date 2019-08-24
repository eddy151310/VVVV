package com.ahdi.lib.utils.cryptor.rsa;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

import com.ahdi.lib.utils.config.ConfigHelper;
import com.ahdi.lib.utils.utils.AppGlobalUtil;

public class RSAConfig implements OnSharedPreferenceChangeListener {
	private volatile String publicModulus = "";
	private volatile String publicExponent = "";

	//SDK密码加密
	private volatile String publicKey_pwd_pay = ConfigHelper.PAY_PASSWORD_RSA;
	private volatile String publicKey_pwd_login = ConfigHelper.LOGIN_PASSWORD_RSA;
	public static String PUBLIC_KEY_FILE = "PUBLIC_KEY_FILE";
	public static String PUBLIC_MODULUS = "PUBLIC_MODULUS";
	public static String PUBLIC_EXPONENT = "PUBLIC_EXPONENT";
	public static String PUBLIC_KEY_pwd_pay = "PwdPublicKeyPay";
	public static String PUBLIC_KEY_pwd_login = "PwdPublicKeyLogin";
	public static String PUBLIC_KEY_NUM = "KeySeq";
	private volatile String publicKeyNum = "3";

	private static RSAConfig config = null;

	public static RSAConfig instance() {
		if (config == null) {
			config = new RSAConfig();
		}

		return config;
	}

	private RSAConfig() {
		SharedPreferences pref = AppGlobalUtil.getInstance().getContext().getSharedPreferences(PUBLIC_KEY_FILE, Context.MODE_PRIVATE);
		pref.registerOnSharedPreferenceChangeListener(this);
		publicModulus = pref.getString(PUBLIC_MODULUS, publicModulus);
		publicExponent = pref.getString(PUBLIC_EXPONENT, publicExponent);
		publicKey_pwd_pay = pref.getString(PUBLIC_KEY_pwd_pay, publicKey_pwd_pay);
        publicKey_pwd_login = pref.getString(PUBLIC_KEY_pwd_login, publicKey_pwd_login);
		publicKeyNum = pref.getString(PUBLIC_KEY_NUM, publicKeyNum);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (key.equalsIgnoreCase(PUBLIC_MODULUS)) {
			publicModulus = sharedPreferences.getString(key, publicModulus);
		} else if (key.equalsIgnoreCase(PUBLIC_EXPONENT)) {
			publicExponent = sharedPreferences.getString(PUBLIC_EXPONENT, publicExponent);
		} else if (key.equalsIgnoreCase(PUBLIC_KEY_NUM)) {
			publicKeyNum = sharedPreferences.getString(PUBLIC_KEY_NUM, publicKeyNum);
		}else if (key.equalsIgnoreCase(PUBLIC_KEY_pwd_pay)) {
			publicKey_pwd_pay = sharedPreferences.getString(PUBLIC_KEY_pwd_pay, publicKey_pwd_pay);
		}else if (key.equalsIgnoreCase(PUBLIC_KEY_pwd_login)) {
            publicKey_pwd_login = sharedPreferences.getString(PUBLIC_KEY_pwd_login, publicKey_pwd_login);
        }
	}



	public String getPublicKey_pwd_pay() {
		return publicKey_pwd_pay;
	}

    public String getPublicKey_pwd_login() {
        return publicKey_pwd_login;
    }

}