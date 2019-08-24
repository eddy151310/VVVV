package com.ahdi.lib.utils.fee;
import java.math.BigDecimal;
import java.math.RoundingMode;

class NoFee implements Fee {
    private static final NoFee instance = new NoFee();

    public static NoFee getInstance() {
        return instance;
    }

    private NoFee() {
        super();
    }

    @Override
    public String getType() {
        return "NO_FEE";
    }

    @Override
    public BigDecimal fee(BigDecimal pay, int scale) {
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal pay(BigDecimal want, int scale) {
        if (want == null || want.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return want.setScale(scale, RoundingMode.UP).stripTrailingZeros();
    }

    @Override
    public BigDecimal getMinPay() {
        return BigDecimal.ZERO;
    }

    @Override
    public Fee init(String express) {
        return this;
    }

    @Override
    public Fee clone() {
        return this;
    }

    @Override
    public String express() {
        return "";
    }

    @Override
    public FeeInf feeInf(BigDecimal pay, int scale) {
        return new FeeInf(BigDecimal.ZERO, "0%");
    }

}
