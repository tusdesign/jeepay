package com.jeequan.jeepay.core.model.params.qidipay;

public class QidipayConfig {

    /**
    * 请求参数-通知类型 0前台 1后台 默认是后台
    * */
    public static final String SPEC_NOTIFY_TYPE = "__notifyType";

    /**
     * 通知类型-后台.
     */
    public static final String NOTIFY_TYPE_BACK = "1";

    /**
     * 特殊字段前缀.
     */
    public static final String SPEC_PRIFEX = "__";

    /**
     * 默认编码.
     */
    public static final String ENCODING = "UTF-8";
}
