package com.jeequan.jeepay.mgr.task;

import com.jeequan.jeepay.mgr.rqrs.EnumTime;
import com.jeequan.jeepay.mgr.util.TimeUtil;
import lombok.SneakyThrows;
import org.apache.commons.lang3.tuple.MutablePair;
import org.springframework.transaction.annotation.Transactional;

public abstract class AbstractAnalysisTask {

    protected abstract void process(String period) throws Exception;

    protected MutablePair<String, String> getPeriod(String period) throws Exception {
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
        }
       return MutablePair.of(createTimeStart, createTimeEnd);
    }

}
