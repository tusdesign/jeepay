package com.jeequan.jeepay.mgr.task.job;

import com.jeequan.jeepay.core.entity.SysJob;
import com.jeequan.jeepay.mgr.util.EnumTime;
import com.jeequan.jeepay.mgr.util.TimeUtil;
import org.apache.commons.lang3.tuple.MutablePair;

public abstract class AbstractAnalysisJob {

    /**
     * 交易类型常量
     */
    protected static final String DEALTYPE_DEPARTMENTAL = "DEPARTMENTAL";//部门
    protected static final String DEALTYPE_PERSONAL = "PERSONAL";//个人
    protected static final String DEALTYPE_RENT = "RENT";//月租

    public abstract void process(SysJob job) throws Exception;

    protected MutablePair<String, String> getPeriod(String period, String timeFirst, String timeLast) throws Exception {

        String createTimeStart = "";//开始时间
        String createTimeEnd = "";//结束时间

        if (EnumTime.TIMETYPE.YEAR.key.equals(period)) {
            createTimeStart = TimeUtil.getBeforeFirstYearDate();
            createTimeEnd = TimeUtil.getBeforeLastYearDate();
        } else if (EnumTime.TIMETYPE.MONTH.key.equals(period)) {
            createTimeStart = TimeUtil.getBeforeFirstMonthDate();
            createTimeEnd = TimeUtil.getBeforeLastMonthDate();
        } else if (EnumTime.TIMETYPE.DAY.key.equals(period)) {
            createTimeStart = TimeUtil.getBeforeFirstDayDate();
            createTimeEnd = TimeUtil.getBeforeLastDayDate();
        } else if (EnumTime.TIMETYPE.WEEK.key.equals(period)) {
            createTimeStart = TimeUtil.getBeforeFirstWeekDate();
            createTimeEnd = TimeUtil.getBeforeLastWeekDate();
        } else if (EnumTime.TIMETYPE.OTHER.key.equals(period)) {
            createTimeStart = timeFirst;
            createTimeEnd = timeLast;
        }
        return MutablePair.of(createTimeStart, createTimeEnd);
    }

}
