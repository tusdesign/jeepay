package com.jeequan.jeepay.pay.channel.unionpay.utils;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    public static String getToday(String format) {
        SimpleDateFormat dfs = new SimpleDateFormat(format);
        try {
            Date date = new Date();
            return dfs.format(date);
        } catch (Exception e) {
        }
        return null;
    }

    public static String getNextDate(String oldDate, int sizeOfParamValue) {
        try {
            if ((oldDate.length() != 8) || (!StringUtils.isNumeric(oldDate))) {
                return null;
            }
            int year = new Integer(oldDate.substring(0, 4)).intValue();
            int month = new Integer(oldDate.substring(4, 6)).intValue() - 1;
            int date = new Integer(oldDate.substring(6, 8)).intValue();

            Calendar calender = Calendar.getInstance();
            calender.set(year, month, date);
            calender.add(5, sizeOfParamValue);

            year = calender.get(1);
            month = calender.get(2) + 1;
            date = calender.get(5);

            return Formator.fillLeft(new StringBuilder().append(year).toString(), 4, '0') + Formator.fillLeft(new StringBuilder().append(month).toString(), 2, '0') +
                    Formator.fillLeft(new StringBuilder().append(date).toString(), 2, '0');
        } catch (Exception e) {
        }

        return null;
    }
}