package com.jeequan.jeepay.core.model.params.unionpay;

public class UnionPayConfig {

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



    /**
     * 前台支付路径
     */
    public static final String FRONTPAYPATH="page/nref/000000000017/0/0/0/0/0";

    /**
     * 支付订单查询路径
     */
    public static final String BGPAYPATH="forward/syn/000000000060/0/0/0/0/0";

    /**
     * 退款接口
     */
    public static final String REFUNDPATH="forward/syn/000000000065/0/0/0/0/0";

    /**
     * 二维码支付
     */
    public static final String QRPAYPATH="momsMgr/bgTransGet";

}
