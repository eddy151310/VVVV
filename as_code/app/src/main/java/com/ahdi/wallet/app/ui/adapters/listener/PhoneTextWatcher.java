package com.ahdi.wallet.app.ui.adapters.listener;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

import com.ahdi.lib.utils.config.ConfigCountry;

/**
 * @author zhaohe@iapppay.com
 * @date 2018/5/30.
 * <p>
 * 处理手机输入框 的长度限制
 * 1、8开头的输入框限制输入13位
 * 2、08以及其他开头的输入框限制输入14位
 */

public class PhoneTextWatcher implements TextWatcher {

    /**
     * 是否需要更新filter
     * 每次清空EditText上的文字之后，该标记置为true
     * 防止每次修改一个字符就需要设置filter一次
     */
    private boolean changeFilter = true;

    private InputFilter.LengthFilter phoneThirteenFilter = null;
    private InputFilter.LengthFilter phoneFourteenFilter = null;
    private InputFilter.LengthFilter phoneFifteenFilter = null;

    private EditText editText;
    private String areaCode;

    public PhoneTextWatcher(EditText editText, String areaCode) {
        this.editText = editText;
        this.areaCode = areaCode;
    }

    public PhoneTextWatcher(EditText editText) {
        this.editText = editText;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String account = editText.getText().toString();
        if (start == 0) {
            changeFilter = true;
        }
        if (!TextUtils.isEmpty(account) && changeFilter) {
            if (TextUtils.equals(areaCode, ConfigCountry.KEY_AREA_CODE)) {
                addFilter(getFourteenFilter());

            } else {
                addFilter(getFifteenFilter());
            }
            changeFilter = false;
        } else if (TextUtils.isEmpty(account)) {
            changeFilter = true;
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    /**
     * 添加过滤
     *
     * @param filter
     */
    private void addFilter(InputFilter.LengthFilter filter) {
        //获取EditText已有的filters[]
        InputFilter[] filters = editText.getFilters();
        //如果EditText没有filters[] 直接为EditText设置filter
        if (filters == null || filters.length == 0) {
            editText.setFilters(new InputFilter[]{filter});
            return;
        }
        //如果EditText有LengthFilter类型的filter  直接替换为我们自己的filter
        boolean hasLengthFilter = false;
        for (int i = 0; i < filters.length; i++) {
            if (filters[i] instanceof InputFilter.LengthFilter) {
                hasLengthFilter = true;
                filters[i] = filter;
                continue;
            }
        }
        //替换为我们自己的filter成功，需要给Editable 设置修改之后的filter[]
        //如果EditText没有LengthFilter类型的filter ，直接给EditText设置filter
        if (hasLengthFilter) {
            editText.setFilters(filters);
        } else {
            editText.setFilters(new InputFilter[]{filter});
        }
    }

    /**
     * 获取长度为13的filter
     *
     * @return
     */
    private InputFilter.LengthFilter getThirteenFilter() {

        if (phoneThirteenFilter == null) {
            phoneThirteenFilter = new InputFilter.LengthFilter(ConfigCountry.PHONE_LIMIT_MAX_LENGTH_11);
        }
        return phoneThirteenFilter;
    }

    /**
     * 获取长度为14的filter
     *
     * @return
     */
    private InputFilter.LengthFilter getFourteenFilter() {

        if (phoneFourteenFilter == null) {
            phoneFourteenFilter = new InputFilter.LengthFilter(ConfigCountry.PHONE_LIMIT_MAX_LENGTH_14);
        }
        return phoneFourteenFilter;
    }

    /**
     * 获取长度为15的filter
     *
     * @return
     */
    private InputFilter.LengthFilter getFifteenFilter() {

        if (phoneFifteenFilter == null) {
            phoneFifteenFilter = new InputFilter.LengthFilter(ConfigCountry.PHONE_LIMIT_MAX_LENGTH_15);
        }
        return phoneFifteenFilter;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }
}
