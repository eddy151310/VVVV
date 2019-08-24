package com.ahdi.lib.utils.paycode;

import com.ahdi.lib.utils.utils.LogUtil;

public class PayCodeCreater {

    private long timeslotLength = 30 * 1000; // pay码有效期
    private int next = 0;

    private long client;
    private byte[] key;
    private PayCodeBean payCodeBean;
    private long timeDif;

    public PayCodeCreater(long client, byte[] key, long timeDif) {
        if (client < 0 || client > MAX_CLIENT) {
            throw new IllegalArgumentException("invalid client: " + client);
        }
        this.client = client;
        this.key = key.clone();
        this.timeDif = timeDif;
    }

    public PayCodeCreater(long client, byte[] key) {
        if (client < 0 || client > MAX_CLIENT) {
            throw new IllegalArgumentException("invalid client: " + client);
        }
        this.client = client;
        this.key = key.clone();
    }

    public static final String VERSION = "9";
    public static final long MAX_CLIENT = (1l << 35) - 1;
    public static final long MAX_CLIENT_4 = (1l << 32) - 1;
    public static final int MAX_SEQ = 15;
    public static final int MAX_NEXT = 15;
    public static final int BITS_OPT = 20;
    public static final char FLAG = '2';
    public static final int MIN_LENGTH = 18;

    private long getTimeMillis() {
        return System.currentTimeMillis() + timeDif;
    }

    public void setNext(int next) {
        this.next = next;
    }

    private int getNext() {
        int n = this.next;
        if (this.next >= MAX_NEXT) {
            this.next = 0;
        } else {
            this.next++;
        }
        return n;
    }

    public PayCodeBean nextCode(int seq) {
        if (seq < 0 || seq > MAX_SEQ) {
            throw new IllegalArgumentException("invalid seq[0-15]: " + seq);
        }
        if (payCodeBean == null) {
            payCodeBean = new PayCodeBean();
        }
        HmacBasedOneTimePassword otpg = new HmacBasedOneTimePassword(this.key);
        StringBuilder sb = new StringBuilder();
        sb.append(VERSION).append(FLAG);
        int next = this.getNext();
        if (next > MAX_SEQ) {
            next = 0;
        }
        LogUtil.e("tag", "PayCodeCreater next: " + next);
        long counter = this.getTimeMillis() / this.timeslotLength;
        payCodeBean.setCounter(counter);

        int otp = otpg.generatePasswordByBits(BITS_OPT, counter,
                String.valueOf(this.client), String.valueOf(seq), String.valueOf(next));
        long or = otp;
        for (int i = 0; i < 4; i++) {
            or = (or << 4) ^ otp;
        }
        or = (or << 2) ^ otp;
        long c = this.client;
        c = (c << 4) | seq;
        c = (c << 4) | next;
        c = c ^ or;
        c = (c << BITS_OPT) | otp;
        String cStr = String.valueOf(c);
        int maxlen = MIN_LENGTH;
        for (int i = 0; i < (maxlen - cStr.length()); i++) {// 补足长度，最短18位
            sb.append('0');
        }
        sb.append(cStr);
        sb.append(Luhn.getCheckCode(sb.toString()));// 增加luhn校验位
        payCodeBean.setPayCode(sb.toString());
        payCodeBean.setNext(next);
        return payCodeBean;
    }

    public static void main(String[] args) throws InterruptedException {
        PayCodeCreater h = new PayCodeCreater(1000000, "123456".getBytes());
        for (int i = 0; i < 30; i++) {
            System.out.println(h.nextCode(i));
        }
    }

}
