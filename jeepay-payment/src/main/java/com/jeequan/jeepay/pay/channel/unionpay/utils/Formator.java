package com.jeequan.jeepay.pay.channel.unionpay.utils;

import java.util.Calendar;

public class Formator {
    public static String formatIP(String binIPStr)
            throws NumberFormatException {
        StringBuffer intIPStr = new StringBuffer();
        String tempStr = "";
        if (binIPStr.length() != 32) {
            return "";
        }

        for (int i = 0; i < 4; i++) {
            tempStr = binIPStr.substring(i * 8, (i + 1) * 8);

            intIPStr.append(Integer.parseInt(tempStr, 2));
            if (i != 3) intIPStr.append(".");
        }
        return intIPStr.toString();
    }

    public static String formatNetmask(int position) {
        StringBuffer intIPStr = new StringBuffer();
        if ((position < 1) || (position > 31)) {
            return "255.255.255.255";
        }

        for (int i = 0; i < 4; i++) {
            int intIP = 0;

            for (int j = 8; j > 0; j--) {
                if (position >= i * 8 + j) {
                    intIP += (int) Math.pow(2.0D, 8 - j);
                }
            }
            intIPStr.append(intIP);
            if (i != 3)
                intIPStr.append(".");
        }
        return intIPStr.toString();
    }

    public static String decimal(double amt) {
        long result1 = 0L;
        if (amt > 0.0D)
            result1 = (long) (amt * 100.0D + 0.5D);
        else {
            result1 = (long) (amt * 100.0D - 0.5D);
        }

        if (result1 == 0L)
            return "0.00";
        String result;
        if (result1 > 0L) {
            result = String.valueOf(result1);

            result = fill(result, 3);
            result = result.substring(0, result.length() - 2) + "." + result.substring(result.length() - 2);
        } else {
            result = String.valueOf(Math.abs(result1));
            result = fill(result, 3);
            result = "-" + result.substring(0, result.length() - 2) + "." + result.substring(result.length() - 2);
        }
        return result;
    }

    public static long yuan2Fen(String num) {
        long fen = 0L;
        String fenStr = "";
        int pos = num.indexOf(".");

        if (pos == -1) {
            fenStr = num + "00";
        } else {
            fenStr = num.substring(0, pos);
            if (num.length() - pos >= 3)
                fenStr = fenStr + num.substring(pos + 1, pos + 3);
            else if (num.length() == pos)
                fenStr = fenStr + "00";
            else
                fenStr = fenStr + num.substring(pos + 1) + "0";
        }
        try {
            fen = Long.parseLong(fenStr);
        } catch (Exception e) {
            return 0L;
        }
        return fen;
    }

    public static long yuan2Fen(double yuan) {
        double newYuan = yuan;
        newYuan = yuan * 100.0D;
        newYuan /= 100.0D;
        double delta = yuan - newYuan;
        if (delta > 0.009990000000000001D) {
            yuan += 0.0001D;
        }

        return (long) (yuan * 100.0D);
    }

    public static String fillLeft(String str, int length, char c) {
        StringBuffer buffer = new StringBuffer("");

        if (str == null) {
            str = "";
        }

        if (length <= str.length()) {
            return str;
        }
        int strLen = length - str.length();
        for (int i = 0; i < strLen; i++) {
            buffer.append(c);
        }

        buffer.append(str);
        return buffer.toString();
    }

    public static String fillRight(String str, int length, char c) {
        StringBuffer buffer = new StringBuffer("");

        if (str == null) {
            str = "";
        }

        if (length <= str.length()) {
            return str;
        }
        buffer.append(str);
        int strLen = length - str.length();
        for (int i = 0; i < strLen; i++) {
            buffer.append(c);
        }

        return buffer.toString();
    }

    public static String fill(String str, int length) {
        return fillLeft(str, length, '0');
    }

    public static String fill(int number, int length) {
        StringBuffer buffer = new StringBuffer("");
        String str;
        if (number >= 0) {
            str = String.valueOf(number);
        } else {
            buffer.append("-");
            str = String.valueOf(Math.abs(number));
            length--;
        }
        buffer.append(fill(str, length));
        return buffer.toString();
    }

    public static String fill(String str) {
        if ((str == null) || (str.trim().equalsIgnoreCase("null"))) {
            return "";
        }
        return str;
    }

    public static String formatNowDate() {
        return formatDate(Calendar.getInstance());
    }

    public static String formatNowTime() {
        return formatTime(Calendar.getInstance());
    }

    public static String formatNowDateTime() {
        return formatDateTime(Calendar.getInstance());
    }

    public static String formatDate(Calendar date) {
        if (date == null) {
            return null;
        }

        int year = date.get(1);
        int month = date.get(2) + 1;
        int day = date.get(5);

        return year + fill(month, 2) + fill(day, 2);
    }

    public static String formatTime(Calendar date) {
        if (date == null) {
            return null;
        }

        int hour = date.get(11);
        int minute = date.get(12);
        int second = date.get(13);

        return fill(hour, 2) + fill(minute, 2) + fill(second, 2);
    }

    public static String formatDateTime(Calendar date) {
        if (date == null) {
            return null;
        }

        return formatDate(date) + formatTime(date);
    }
}