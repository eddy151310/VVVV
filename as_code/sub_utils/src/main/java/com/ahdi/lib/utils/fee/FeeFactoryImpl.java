package com.ahdi.lib.utils.fee;
import java.util.HashMap;
import java.util.Map;



public class FeeFactoryImpl extends FeeFactory {
    Map<String, Class<? extends Fee>> typeMappedClass;
    public static final String conf = FeeFactoryImpl.class.getName()
            + ".properties";

    public FeeFactoryImpl() {
        typeMappedClass = new HashMap<String, Class<? extends Fee>>();
        typeMappedClass.put(Fee.FEE_TYPE_FIXED, FixedFee.class);
        typeMappedClass.put(Fee.FEE_TYPE_FIXED_RATE, FixedRateFee.class);
        typeMappedClass.put(Fee.FEE_TYPE_MULTI_RANGE, MultiRangeFee.class);
        typeMappedClass.put(Fee.FEE_TYPE_RANGE, RangeFee.class);
        typeMappedClass.put(Fee.FEE_TYPE_RATE_AND_FEE, RateAndFixedFee.class);
    }

    public String toString(Fee fee) {
        return fee.getType() + ":" + fee.express();
    }

    @Override
    public Fee create(String express) {
        express = trimToNull(express);
        if (express == null) {
            return NoFee.getInstance();
        }
        int idx = express.indexOf(':');
        if (idx <= 0) {
            return NoFee.getInstance();
        }
        String type = express.substring(0, idx);
        Class<? extends Fee> c = this.typeMappedClass.get(type);
        if (c == null) {
            return NoFee.getInstance();
        }
        Fee f;
        try {
            f = c.newInstance();
        } catch (Exception e) {
            return NoFee.getInstance();
        }
        return f.init(express.substring(idx + 1));
    }

    @Override
    public String toExpress(Fee fee) {
        if (fee == null || fee == NoFee.getInstance()) {
            return "";
        }
        return fee.getType() + ":" + fee.express();
    }
    public  String trimToNull(String str) {
        str = str == null ? null : str.trim();
        return (str == null || str.length() == 0) ? null : str;
    }
}

