package com.ahdi.lib.utils.fee;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class MultiRangeFee implements Fee {
    private static final char split = '|';
    List<RangeFee> fees;
    public static final String[] EMPTY_STRING_ARRAY = new String[0];

    public MultiRangeFee() {
        super();
    }

    public MultiRangeFee(List<RangeFee> fees) {
        super();
        if (fees == null || fees.isEmpty()) {
            throw new IllegalArgumentException();
        }
        this.fees = fees;
    }

    @Override
    public String getType() {
        return FEE_TYPE_MULTI_RANGE;
    }

    @Override
    public BigDecimal fee(BigDecimal pay, int scale) {
        Fee fee = this.findFee(pay);
        if (fee != null) {
            return fee.fee(pay, scale);
        }
        // 区间不完整时，100%手续费,避免异常
        return pay;
    }

    @Override
    public FeeInf feeInf(BigDecimal pay, int scale) {
        Fee fee = this.findFee(pay);
        if (fee != null) {
            return fee.feeInf(pay, scale);
        }
        // 区间不完整时，100%手续费,避免异常
        return new FeeInf(pay, "100%");
    }

    private Fee findFee(BigDecimal pay) {
        for (RangeFee fee : this.fees) {
            if (fee.fit(pay)) {
                return fee;
            }
        }
        return null;
    }

    @Override
    public BigDecimal pay(BigDecimal want, int scale) {
        BigDecimal max = BigDecimal.ZERO;
        for (RangeFee fee : this.fees) {
            BigDecimal pay = fee.pay(want, scale);
            max = max.max(pay);
            if (fee.fit(pay)) {
                return pay;
            }
        }
        return max;
    }

    @Override
    public BigDecimal getMinPay() {
        BigDecimal min = null;
        for (RangeFee fee : this.fees) {
            min = min == null ? fee.getMinPay() : min.min(fee.getMinPay());
        }
        return min == null ? BigDecimal.ZERO : min;
    }

    @Override
    public MultiRangeFee init(String express) {
        String[] ex = split(express, split);
        if (ex == null || ex.length == 0) {
            throw new IllegalArgumentException();
        }
        List<RangeFee> fees = new ArrayList<RangeFee>(ex.length);
        for (String e : ex) {
            fees.add(new RangeFee().init(e));
        }
        this.fees = fees;
        return this;
    }

    @Override
    public MultiRangeFee clone() {
        MultiRangeFee fee;
        try {
            fee = (MultiRangeFee) super.clone();
        } catch (CloneNotSupportedException e) {
            fee = new MultiRangeFee();
        }
        fee.fees = new ArrayList<RangeFee>();
        for (RangeFee f : this.fees) {
            fee.fees.add(f);
        }
        return fee;
    }

    @Override
    public String express() {
        StringBuilder sbuf = new StringBuilder();
        for (RangeFee fee : this.fees) {
            sbuf.append(fee.express()).append(split);
        }
        if (sbuf.length() > 0) {
            sbuf.setLength(sbuf.length() - 1);
        }
        return sbuf.toString();
    }

    public String[] split(String str, char split) {
        if (str == null) {
            return null;
        }
        final int len = str.length();
        if (len == 0) {
            return EMPTY_STRING_ARRAY;
        }
        boolean preserveAllTokens = false;
        final List<String> list = new ArrayList<String>();
        int i = 0, start = 0;
        boolean match = false;
        boolean lastMatch = false;
        while (i < len) {
            if (str.charAt(i) == split) {
                if (match || preserveAllTokens) {
                    list.add(str.substring(start, i));
                    match = false;
                    lastMatch = true;
                }
                start = ++i;
                continue;
            }
            lastMatch = false;
            match = true;
            i++;
        }
        if (match || preserveAllTokens && lastMatch) {
            list.add(str.substring(start, i));
        }
        return list.toArray(new String[list.size()]);
    }

}
