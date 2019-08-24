package com.ahdi.wallet.app.ui.widgets;

import android.text.TextUtils;

import com.ahdi.wallet.app.schemas.BankSchema;

import java.util.Comparator;

/**
 * 用来对ListView中的数据根据A-Z进行排序，前面两个if判断主要是将不是以汉字开头的数据放在后面
 */
public class PinyinComparator implements Comparator<BankSchema> {

    public int compare(BankSchema o1, BankSchema o2) {
        //这里主要是用来对ListView里面的数据根据ABCDEFG...来排序
        String str1 = o1.getSortLetters();
        String str2 = o2.getSortLetters();
        if(!TextUtils.isEmpty(str1) && !TextUtils.isEmpty(str2)){
            if("@".equals(str1) || "#".equals(str2)){
                return -1;
            }else if("#".equals(str1)|| "@".equals(str2)){
                return 1;
            }else{
                return str1.compareTo(str2);
            }
        }else{
            return -1;
        }
    }
}
