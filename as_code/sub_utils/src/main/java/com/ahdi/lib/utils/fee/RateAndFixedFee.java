package com.ahdi.lib.utils.fee;
import android.text.TextUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class RateAndFixedFee implements Fee {
    public static final char split = '+';

    protected BigDecimal fee;
    protected BigDecimal rate;

    private byte type = FixedRateFee.TYPE_1;

    private BigDecimal minPay;

    public RateAndFixedFee() {
        super();
    }

    /**
     * 
     * @param rate
     *            费率，百分数例如10.0 手续费10%
     * @param fee
     *            固定手续费
     */
    public RateAndFixedFee(String rate, long fee) {
        this(rate, new BigDecimal(fee));
    }

    /**
     * 
     * @param rate
     *            费率，百分数例如10.0 手续费10%
     * @param fee
     *            固定手续费
     */
    public RateAndFixedFee(String rate, BigDecimal fee) {
        super();
        this.init(rate, fee);
    }

    private void check(BigDecimal fee) {
        if (fee == null) {
            return;
        }
        if (fee.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("fee < 0");
        }
    }

    protected void init(String rateStr, BigDecimal fee) {
        this.check(fee);
        if (TextUtils.isEmpty(rateStr)) {
            throw new IllegalArgumentException("rateStr is empty");
        }
        BigDecimal rate = new BigDecimal(rateStr);
        int c = rate.compareTo(BigDecimal.ZERO);
        byte type = FixedRateFee.TYPE_1;
        if (c < 0) {
            throw new IllegalArgumentException("rate < 0");
        }
        if (fee == null || fee.compareTo(BigDecimal.ZERO) <= 0) {
            fee = BigDecimal.ZERO;
        } else {
            fee = fee.stripTrailingZeros();
        }
        BigDecimal minPay = fee;
        if (c == 0) {
            type = FixedRateFee.TYPE_0;
            rate = BigDecimal.ZERO;
        } else {
            c = rate.compareTo(new BigDecimal(100));
            if (c >= 0) {
                throw new IllegalArgumentException("rate >= 100");
            }
            rate = rate.stripTrailingZeros();
            minPay = pay(rate, type, fee, 10);
        }
        this.fee = fee;
        this.rate = rate;
        this.type = type;
        this.minPay = minPay;
    }

    @Override
    public String getType() {
        return Fee.FEE_TYPE_RATE_AND_FEE;
    }

    @Override
    public BigDecimal fee(BigDecimal pay, int scale) {
        if (pay == null || pay.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("pay <= 0");
        }
        if (this.type == FixedRateFee.TYPE_0) {
            return this.fee;
        }
        if (this.type == FixedRateFee.TYPE_100) {
            return pay.stripTrailingZeros();
        }
        BigDecimal fee = this.rate.multiply(pay).movePointLeft(2);
        fee = fee.add(this.fee);
        if (pay.compareTo(fee) < 0) {
            return pay.stripTrailingZeros();
        }
        return fee.stripTrailingZeros();
    }

    @Override
    public FeeInf feeInf(BigDecimal pay, int scale) {
        return new FeeInf(this.fee(pay, scale), this.rate.toPlainString()
                + "%+" + this.fee.toPlainString());
    }

    private static BigDecimal pay(BigDecimal rate, byte type, BigDecimal want,
            int scale) {
        if (want == null || want.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("want <= 0");
        }
        if (type == FixedRateFee.TYPE_0) {
            return want;
        }
        if (type == FixedRateFee.TYPE_100) {
            throw new UnsupportedOperationException("rate=100, all pay is fee");
        }
        BigDecimal d = new BigDecimal(100).subtract(rate);
        return want.divide(d, 4 + scale, RoundingMode.UP).movePointRight(2)
                .setScale(scale, RoundingMode.UP).stripTrailingZeros();
    }

    @Override
    public BigDecimal pay(BigDecimal want, int scale) {
        if (want == null || want.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("want <= 0");
        }
        return pay(rate, type, want.add(fee), scale);
    }

    @Override
    public BigDecimal getMinPay() {
        return this.minPay;
    }

    @Override
    public RateAndFixedFee init(String express) {
        int idx = express.indexOf(split);
        if (idx < 0) {
            this.init(express, BigDecimal.ZERO);
        } else {
            this.init(express.substring(0, idx),
                    new BigDecimal(express.substring(idx + 1)));
        }
        return this;
    }

    @Override
    public RateAndFixedFee clone() {
        try {
            return (RateAndFixedFee) super.clone();
        } catch (CloneNotSupportedException e) {
            return new RateAndFixedFee(this.rate.toPlainString(), this.fee);
        }
    }

    @Override
    public String express() {
        return this.rate.toPlainString() + split + this.fee.toPlainString();
    }
}
