package com.jeequan.jeepay.mgr.task.job;

import com.jeequan.jeepay.core.entity.SysJob;
import com.jeequan.jeepay.mgr.rqrs.EnumTime;
import com.jeequan.jeepay.mgr.rqrs.JobRQ;
import com.jeequan.jeepay.mgr.util.TimeUtil;
import org.apache.commons.lang3.tuple.MutablePair;

public abstract class AbstractAnalysisJob {

    public abstract void process(SysJob job) throws Exception;

    protected MutablePair<String, String> getPeriod(String period,String timeFirst,String timeLast) throws Exception {

        String createTimeStart = "";//开始时间
        String createTimeEnd = "";//结束时间

        if (EnumTime.TIMETYPE.YEAR.key==period) {
            createTimeStart = TimeUtil.getBeforeFirstYearDate();
            createTimeEnd = TimeUtil.getBeforeLastYearDate();
        } else if (EnumTime.TIMETYPE.MONTH.key==period) {
            createTimeStart = TimeUtil.getBeforeFirstMonthDate();
            createTimeEnd = TimeUtil.getBeforeLastMonthDate();
        } else if (EnumTime.TIMETYPE.DAY.key==period) {
            createTimeStart = TimeUtil.getBeforeFirstDayDate();
            createTimeEnd = TimeUtil.getBeforeLastDayDate();
        }
        else if (EnumTime.TIMETYPE.WEEK.key==period) {
            createTimeStart = TimeUtil.getBeforeFirstWeekDate();
            createTimeEnd = TimeUtil.getBeforeLastWeekDate();
        }else if (EnumTime.TIMETYPE.OTHER.key==period){
            createTimeStart=timeFirst;
            createTimeEnd=timeLast;
        }
       return MutablePair.of(createTimeStart, createTimeEnd);
    }

}
