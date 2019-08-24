package com.ahdi.lib.utils.paycode;

public class Luhn {

    private static char getCheckCode(String data, int length) {
        int x = 0, y = 0, num;
        for (int i = (length - 1); i >= 0; i--) {
            if (((length - i) & 1) == 1) {// 从右往左偶数位（待计算的校验位占一位）
                num = (data.charAt(i) - '0');
                num *= 2;
                if (num > 9) {
                    num -= 9;
                }
                x += num;
            } else {
                y += (data.charAt(i) - '0');
            }
        }
        int check = 10 - ((x + y) % 10);
        if (check == 10) {
            return '0';
        }
        return (char) ('0' + check);
    }

    public static char getCheckCode(String data) {
        return getCheckCode(data, data.length());
    }
}
