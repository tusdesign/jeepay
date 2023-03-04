package com.jeequan.jeepay.mgr.rqrs;

public class EnumTime {

    public static enum TIMETYPE {

        DAY("day", 1),
        WEEK("week", 2),
        MONTH("month", 3),
        YEAR("year", 4);

        public String key;
        public int value;

        private TIMETYPE(String key, int value) {
            this.key = key;
            this.value = value;
        }

        public static TIMETYPE get(String key) {
            TIMETYPE[] values = TIMETYPE.values();
            for (TIMETYPE object : values) {
                if (object.key.equals(key)) {
                    return object;
                }
            }
            return null;
        }
    }
}
