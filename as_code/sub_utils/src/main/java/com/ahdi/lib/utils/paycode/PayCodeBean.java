package com.ahdi.lib.utils.paycode;

/**
 * Date: 2018/11/29 下午4:28
 * Author: kay lau
 * Description:
 */
public class PayCodeBean {

    private String payCode;

    private long counter;

    private int next;

    public int getNext() {
        return next;
    }

    public void setNext(int next) {
        this.next = next;
    }

    public String getPayCode() {
        return payCode;
    }

    public void setPayCode(String payCode) {
        this.payCode = payCode;
    }

    public long getCounter() {
        return counter;
    }

    public void setCounter(long counter) {
        this.counter = counter;
    }
}
