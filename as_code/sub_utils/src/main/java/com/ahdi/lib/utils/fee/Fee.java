package com.ahdi.lib.utils.fee;

import java.math.BigDecimal;

public interface Fee extends Cloneable {
    String FEE_TYPE_FIXED = "FIXED";
    String FEE_TYPE_FIXED_RATE = "RATE";
    String FEE_TYPE_RATE_AND_FEE = "RATE+";
    String FEE_TYPE_RANGE = "RANGE";
    String FEE_TYPE_MULTI_RANGE = "RANGE+";

    public static class FeeInf {
        public final BigDecimal fee;
        public final String simpleExp;

        public FeeInf(BigDecimal fee, String simpleExp) {
            super();
            this.fee = fee;
            this.simpleExp = simpleExp;
        }

        @Override
        public String toString() {
            return "FeeInf [fee=" + fee + ", simpleExp=" + simpleExp + "]";
        }
    }

    String getType();

    /**
     * 计算手续费
     * 
     * @param 支付金额
     * @return
     */
    BigDecimal fee(BigDecimal pay, int scale);

    /**
     * 计算手续费
     * 
     * @param 支付金额
     * @return
     */
    FeeInf feeInf(BigDecimal pay, int scale);

    /**
     * 计算需要付费金额
     * 
     * @param want
     *            预想到账金额
     * @return
     */
    BigDecimal pay(BigDecimal want, int scale);

    /**
     * 获取最小支付金额
     * 
     * @return
     */
    BigDecimal getMinPay();

    /**
     * 使用格式化的字符串初始化
     * 
     * @param limits
     * @see #express()
     * @return
     */
    Fee init(String express);

    /**
     * 克隆
     * 
     * @return
     */
    Fee clone();

    /**
     * 格式化为字符串表达式。
     * 
     * @see #init(String)
     * @return
     */
    String express();

}
