package com.ahdi.lib.utils.fee;
import java.math.BigDecimal;
import java.math.RoundingMode;


public class FixedFee implements Fee {

    protected BigDecimal fee = BigDecimal.ZERO;

    public FixedFee() {
        super();
    }

    public FixedFee(long fee) {
        this(new BigDecimal(fee));
    }

    public FixedFee(BigDecimal fee) {
        super();
        this.init(fee);
    }

    private void init(BigDecimal fee) {
        check(fee);
        if (fee == null) {
            this.fee = BigDecimal.ZERO;
        } else {
            this.fee = fee.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO
                    : fee;
        }
    }

    @Override
    public String getType() {
        return Fee.FEE_TYPE_FIXED;
    }

    @Override
    public BigDecimal fee(BigDecimal pay, int scale) {
        if (pay == null || pay.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("pay <= 0");
        }
        return fee.setScale(scale, RoundingMode.UP).min(pay)
                .stripTrailingZeros();
    }

    @Override
    public FeeInf feeInf(BigDecimal pay, int scale) {
        return new FeeInf(this.fee(pay, scale), fee.toPlainString());
    }

    @Override
    public BigDecimal pay(BigDecimal want, int scale) {
        if (want == null || want.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("want <= 0");
        }
        return want.add(fee).setScale(scale, RoundingMode.UP)
                .stripTrailingZeros();
    }

    @Override
    public BigDecimal getMinPay() {
        return this.fee;
    }

    @Override
    public FixedFee init(String express) {
        express = trimToNull(express);
        BigDecimal fee = express == null ? null : new BigDecimal(express);
        this.init(fee);
        return this;
    }

    private void check(BigDecimal fee) {
        if (fee != null && fee.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("fee < 0");
        }
    }

    @Override
    public FixedFee clone() {
        try {
            return (FixedFee) super.clone();
        } catch (CloneNotSupportedException e) {
            return new FixedFee(this.fee);
        }
    }

    @Override
    public String express() {
        return fee.toPlainString();
    }
    public  String trimToNull(String str) {
        str = str == null ? null : str.trim();
        return (str == null || str.length() == 0) ? null : str;
    }

}
