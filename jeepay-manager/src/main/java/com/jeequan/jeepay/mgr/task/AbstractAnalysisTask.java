package com.jeequan.jeepay.mgr.task;

import com.jeequan.jeepay.mgr.util.TimeUtil;
import lombok.SneakyThrows;
import org.apache.commons.lang3.tuple.MutablePair;
import org.springframework.transaction.annotation.Transactional;

public abstract class AbstractAnalysisTask {

    public static final byte CYCLE_DAY = 1; //按天
    public static final byte CYCLE_WEEK_ = 2; //按周
    public static final byte CYCLE_MONTH = 3; //按月
    public static final byte CYCLE_YEAR = 4; //按年

    protected abstract void process(int period) throws Exception;

    protected MutablePair<String, String> getPeriod(int period) throws Exception {
        String createTimeStart = "";//开始时间
        String createTimeEnd = "";//结束时间
        if (period==CYCLE_YEAR) {
            createTimeStart = TimeUtil.getBeforeFirstYearDate();
            createTimeEnd = TimeUtil.getBeforeLastYearDate();
        } else if (period==CYCLE_MONTH) {
            createTimeStart = TimeUtil.getBeforeFirstMonthDate();
            createTimeEnd = TimeUtil.getBeforeLastMonthDate();
        } else if (period==CYCLE_DAY) {
            createTimeStart = TimeUtil.getBeforeFirstDayDate();
            createTimeEnd = TimeUtil.getBeforeLastDayDate();
        }
        else if (period==CYCLE_WEEK_) {
            createTimeStart = TimeUtil.getBeforeFirstWeekDate();
            createTimeEnd = TimeUtil.getBeforeLastWeekDate();
        }
       return MutablePair.of(createTimeStart, createTimeEnd);
    }

}
