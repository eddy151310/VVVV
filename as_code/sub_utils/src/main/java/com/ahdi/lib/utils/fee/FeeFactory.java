package com.ahdi.lib.utils.fee;
public abstract class FeeFactory {
    private static FeeFactory instance;

    public static FeeFactory getInstance() {
        if (instance == null) {
            synchronized (FeeFactory.class) {
                if (instance == null) {
                    instance = new FeeFactoryImpl();
                }
            }
        }
        return instance;
    }

    public abstract Fee create(String express);

    public abstract String toExpress(Fee fee);
}
