package com.ahdi.lib.utils.fee;
import com.ahdi.lib.utils.utils.LogUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class FixedRateFee implements Fee {
    protected BigDecimal rate;

    public static final byte TYPE_0 = 0;
    public static final byte TYPE_1 = 1;
    public static final byte TYPE_100 = 100;
    private byte type = TYPE_1;

    public FixedRateFee() {
        super();
    }

    /**
     * 
     * @param rate
     *            费率，百分数例如10.0 手续费10%
     */
    public FixedRateFee(String rate) {
        this(new BigDecimal(rate));
    }

    /**
     * 
     * @param rate
     *            费率，百分数例如10.0 手续费10%
     */
    public FixedRateFee(BigDecimal rate) {
        super();
        this.init(rate);
    }

    @Override
    public String getType() {
        return Fee.FEE_TYPE_FIXED_RATE;
    }

    @Override
    public BigDecimal fee(BigDecimal pay, int scale) {
    	LogUtil.i("FixedRate", "money:"+pay.toPlainString());
        if (pay == null || pay.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("pay <= 0");
        }
        if (this.type == TYPE_0) {
            return BigDecimal.ZERO;
        }
        if (this.type == TYPE_100) {
            return pay;
        }
        LogUtil.i("FixedRate", "表达式:"+rate+"*"+pay.toPlainString()+"/2");
        return this.rate.multiply(pay).movePointLeft(2)
                .setScale(scale, RoundingMode.UP).stripTrailingZeros();
    }

    @Override
    public FeeInf feeInf(BigDecimal pay, int scale) {
        return new FeeInf(this.fee(pay, scale), this.rate.toPlainString() + "%");
    }

    @Override
    public BigDecimal pay(BigDecimal want, int scale) {
        if (want == null || want.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("want <= 0");
        }
        if (this.type == TYPE_0) {
            return want;
        }
        if (this.type == TYPE_100) {
            throw new UnsupportedOperationException("rate=100, all pay is fee");
        }
        BigDecimal d = new BigDecimal(100).subtract(this.rate);
        System.out.println(d);
        return want.divide(d, 4 + scale, RoundingMode.UP).movePointRight(2)
                .setScale(scale, RoundingMode.UP).stripTrailingZeros();
    }

    @Override
    public BigDecimal getMinPay() {
        return BigDecimal.ZERO;
    }

    @Override
    public FixedRateFee init(String express) {
        this.init(new BigDecimal(express));
        return this;
    }

    private void init(BigDecimal rate) {
        int c = rate.compareTo(BigDecimal.ZERO);
        byte type = TYPE_1;
        if (c < 0) {
            throw new IllegalArgumentException("rate < 0");
        }
        if (c == 0) {
            type = TYPE_0;
            rate = BigDecimal.ZERO;
        } else {
            c = rate.compareTo(new BigDecimal(100));
            if (c > 0) {
                throw new IllegalArgumentException("rate > 100");
            }
            if (c == 0) {
                type = TYPE_100;
            }
            rate = rate.stripTrailingZeros();
        }
        this.rate = rate;
        this.type = type;
    }

    @Override
    public FixedRateFee clone() {
        try {
            return (FixedRateFee) super.clone();
        } catch (CloneNotSupportedException e) {
            return new FixedRateFee(this.rate);
        }
    }

    @Override
    public String express() {
        return this.rate.toPlainString();
    }
}
