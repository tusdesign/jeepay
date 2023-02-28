package com.jeequan.jeepay.mgr.util;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Component
public class TimeUtil {

    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 获取昨天0点0分0秒的时间
     */
    @SneakyThrows
    public static String getBeforeFirstDayDate() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        System.out.println("当前星期(日期)：" + format.format(calendar.getTime()));
        calendar.add(Calendar.DAY_OF_WEEK, 0);
        calendar.set(Calendar.DAY_OF_WEEK, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 00);//将小时至00
        calendar.set(Calendar.MINUTE, 00);//将分钟至00
        calendar.set(Calendar.SECOND, 00);//将秒至00
        String timeString = format.format(calendar.getTime());
        return timeString;
    }

    /**
     * 获取昨天天23点59分59秒的时间
     */
    @SneakyThrows
    public static String getBeforeLastDayDate() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 23);//将小时至23
        calendar.set(Calendar.MINUTE, 59);//将分钟至59
        calendar.set(Calendar.SECOND, 59); //将秒至59
        String timeString = format.format(calendar.getTime());
        return timeString;
    }


    /**
     * 获取上一周1号0点0分0秒的时间
     */
    @SneakyThrows
    public static String getBeforeFirstWeekDate() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.WEEK_OF_YEAR, -1);
        System.out.println("上周星期(日期)：" + format.format(calendar.getTime()));
        calendar.add(Calendar.DAY_OF_WEEK, 0);
        calendar.set(Calendar.DAY_OF_WEEK, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 00);
        calendar.set(Calendar.MINUTE, 00); //将分钟至00
        calendar.set(Calendar.SECOND, 00);//将秒至00
        String timeString = format.format(calendar.getTime());
        return timeString;
    }

    /**
     * 获取上一周最后一天23点59分59秒的时间
     */
    @SneakyThrows
    public static String getBeforeLastWeekDate() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.WEEK_OF_YEAR, -1);
        System.out.println("上周星期(日期)：" + format.format(calendar.getTime()));
        calendar.set(Calendar.HOUR_OF_DAY, 23);//将小时至23
        calendar.set(Calendar.MINUTE, 59);  //将分钟至59
        calendar.set(Calendar.SECOND, 59); //将秒至59
        String timeString = format.format(calendar.getTime());
        return timeString;
    }

    /**
     * 获取上一个月1号0点0分0秒的时间
     */
    @SneakyThrows
    public static String getBeforeFirstMonthDate() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 00);//将小时至00
        calendar.set(Calendar.MINUTE, 00);//将分钟至00
        calendar.set(Calendar.SECOND, 00);  //将秒至00
        String timeString = format.format(calendar.getTime());
        return timeString;
    }


    /**
     * 获取上个月的最后一天23点59分59秒的时间
     */
    @SneakyThrows
    public static String getBeforeLastMonthDate() throws Exception {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23); //将小时至23
        calendar.set(Calendar.MINUTE, 59); //将分钟至59
        calendar.set(Calendar.SECOND, 59);//将秒至59
        String timeString = format.format(calendar.getTime());
        return timeString;
    }

    /**
     * 获取上年1号0点0分0秒的时间
     */
    @SneakyThrows
    public static String getBeforeFirstYearDate() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.YEAR, -1);
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 00);//将小时至00
        calendar.set(Calendar.MINUTE, 00);//将分钟至00
        calendar.set(Calendar.SECOND, 00);  //将秒至00
        String timeString = format.format(calendar.getTime());
        return timeString;
    }


    /**
     * 获取上年的最后一天23点59分59秒的时间
     */
    @SneakyThrows
    public static String getBeforeLastYearDate() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.YEAR, -1);
        calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMaximum(Calendar.DAY_OF_YEAR));
        calendar.set(Calendar.HOUR_OF_DAY, 23); //将小时至23
        calendar.set(Calendar.MINUTE, 59); //将分钟至59
        calendar.set(Calendar.SECOND, 59);//将秒至59
        String timeString = format.format(calendar.getTime());
        return timeString;
    }
}
