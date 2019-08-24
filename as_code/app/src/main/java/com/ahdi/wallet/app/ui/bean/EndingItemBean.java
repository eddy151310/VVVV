package com.ahdi.wallet.app.ui.bean;

/**
 * Date: 2018/5/28 上午10:34
 * Author: kay lau
 * Description:
 */
public class EndingItemBean {

    private String endingKey;
    private int endingKeyColor = -1;
    private String endingValue;
    private int endingValueColor = -1;
    /**是否为分割线*/
    private boolean isDivider = false;

    public EndingItemBean(String endingKey, String endingValue) {
        this.endingKey = endingKey;
        this.endingValue = endingValue;
    }

    public EndingItemBean(String endingKey, String endingValue, int endingValueColor) {
        this.endingKey = endingKey;
        this.endingValue = endingValue;
        this.endingValueColor = endingValueColor;
    }

    public EndingItemBean(boolean isDivider) {
        this.isDivider = isDivider;
    }

    public String getEndingKey() {
        return endingKey;
    }

    public String getEndingValue() {
        return endingValue;
    }

    public boolean isDivider() {
        return isDivider;
    }

    public int getEndingKeyColor() {
        return endingKeyColor;
    }

    public int getEndingValueColor() {
        return endingValueColor;
    }
}
