package com.ahdi.lib.utils.fee;
import java.math.BigDecimal;


public class RangeFee implements Fee {
    public static final char split = '#';
    public static final char SPLIT_RANGE = '-';
    private BigDecimal min;
    private BigDecimal max;

    private Fee fee;

    public RangeFee() {
        super();
    }

    /**
     * 
     * @param min
     *            最小值（含）
     * @param max
     *            最大值（不含）
     * @param fee
     *            费率
     */
    public RangeFee(long min, long max, Fee fee) {
        this(new BigDecimal(min), new BigDecimal(max), fee);
    }

    /**
     * 
     * @param min
     *            最小值（含）
     * @param max
     *            最大值（不含）
     * @param fee
     *            费率
     */
    public RangeFee(BigDecimal min, BigDecimal max, Fee fee) {
        super();
        this.set(min, max, fee);
    }

    private void set(BigDecimal min, BigDecimal max, Fee fee) {
        if (min != null && min.compareTo(BigDecimal.ZERO) <= 0) {
            min = null;
        }
        if (max != null && max.compareTo(BigDecimal.ZERO) <= 0) {
            max = null;
        }
        if (fee == null) {
            throw new NullPointerException();
        }
        this.min = min == null ? min : min.stripTrailingZeros();
        this.max = max == null ? max : max.stripTrailingZeros();
        this.fee = fee;
    }

    @Override
    public String getType() {
        return FEE_TYPE_RANGE;
    }

    @Override
    public BigDecimal fee(BigDecimal pay, int scale) {
        return this.fee.fee(pay, scale);
    }

    @Override
    public FeeInf feeInf(BigDecimal pay, int scale) {
        return this.fee.feeInf(pay, scale);
    }

    @Override
    public BigDecimal pay(BigDecimal want, int scale) {
        return this.fee.pay(want, scale);
    }

    public boolean fit(BigDecimal pay) {
        if (pay == null || pay.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("pay <= 0");
        }
        if (min == null || min.compareTo(pay) <= 0) {
            if (max == null || max.compareTo(pay) > 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public BigDecimal getMinPay() {
        if (this.min == null) {
            return this.fee.getMinPay();
        }
        return this.min.max(this.fee.getMinPay());
    }

    @Override
    public RangeFee init(String express) {
        int idx = express.indexOf(split);
        if (idx < 0) {
            throw new IllegalArgumentException(express);
        }
        String limits = express.substring(0, idx);
        String feeExpress = express.substring(idx + 1);
        limits = trimToNull(limits);
        BigDecimal min = null;
        BigDecimal max = null;
        if (limits != null) {
            idx = limits.indexOf(SPLIT_RANGE);
            if (idx < 0) {
                min = this.parse(limits, limits);
                max = null;
            } else {
                min = this.parse(limits.substring(0, idx), limits);
                max = this.parse(limits.substring(idx + 1), limits);
            }
        }
        Fee fee = FeeFactory.getInstance().create(feeExpress);
        this.set(min, max, fee);
        return this;
    }

    private BigDecimal parse(String v, String org) {
        v = trimToNull(v);
        if (v == null) {
            return null;
        }
        try {
            return new BigDecimal(v);
        } catch (NumberFormatException e) {
            throw new RuntimeException("invalid range string: " + org, e);
        }
    }

    @Override
    public RangeFee clone() {
        RangeFee f;
        try {
            f = (RangeFee) super.clone();
            f.fee = this.fee.clone();
        } catch (CloneNotSupportedException e) {
            f = new RangeFee();
            f.fee = this.fee.clone();
            f.min = this.min;
            f.max = this.max;
        }
        return f;
    }

    @Override
    public String express() {
        return this.getRange() + split
                + FeeFactory.getInstance().toExpress(fee);
    }

    private String getRange() {
        if (min == null) {
            if (max == null) {
                return "";
            }
            return String.valueOf(SPLIT_RANGE) + max.toPlainString();
        } else {
            if (max == null) {
                return min.toPlainString() + SPLIT_RANGE;
            }
            return min.toPlainString() + SPLIT_RANGE + max.toPlainString();
        }
    }

    public  String trimToNull(String str) {
        str = str == null ? null : str.trim();
        return (str == null || str.length() == 0) ? null : str;
    }
}
