package com.jeequan.jeepay.core.model.params.unionpay;

public class UnionPayConfig {


    /**
     * 证书验证相关参数处理
     */
    public static final String SPEC_NOTIFY_TYPE = "__notifyType";//请求参数-通知类型 0前台 1后台 默认是后台
    public static final String NOTIFY_TYPE_BACK = "1";//通知类型-后台.
    public static final String SPEC_PRIFEX = "__";//特殊字段前缀.
    public static final String ENCODING = "UTF-8";//默认编码.


    public static final String ACCESS_TYPE_MCH = "0";//以商户身份接入
    public static final String ACCESS_TYPE_ISV = "1";//以机构身份接入

    public static final String BUSINESS_TYPE = "0001";//业务类型

    public static final String RESPONSE_STATUS = "0000";//接口响应码
    public static final String DEFAULT_ERROR_CODE = "9999";//默认错误码.

    /**
     * 银联支付相关api
     */
    public static final String FRONTPAY_PATH = "page/nref/000000000017/0/0/0/0/0";//前台支付路径
    public static final String BACKPAY_PATH = "forward/syn/000000000060/0/0/0/0/0";//支付订单查询路径
    public static final String REFUND_PATH = "forward/syn/000000000065/0/0/0/0/0";//退款接口
    public static final String QRPAY_PATH = "momsMgr/bgTransGet";// 二维码支付


    /**
     * 订单状态
     */
    public interface ORDER_STATUS_TYPE {

        String ORDER_STATUS_SUCCESS = "0000";//订单成功状态
        String ORDER_STATUS_INIT = "0001";//初始状态
        String ORDER_STATUS_FAIL = "0003";//消费交易失败
        String ORDER_REFUND_STATUS_SUCCESS = "1013";//退款成功
        String ORDER_REFUND_STATUS_FAIL = "0009";//退款失败
    }

    /**
     * 交易类型
     */
    public interface TRAN_TYPE {

        String TRAN_COMPOSITE = "0000";// 综合支付
        String TRAN_BANK_PERSONAL = "0001";  //个人网银支付
        String TRAN_BANK_ENTERPRISE = "0002";  //企业网银支付
        String TRAN_CREDIT = "0003";  //授信交易
        String TRAN_QUICK = "0004";  //快捷支付
        String TRAN_ORDER = "0005";  // 下单支付
        String TRAN_CERTIFICATION = "0006";// 认证支付
        String TRAN_HIRE_PURCHASE = "0007";// 分期付款
        String TRAN_BACK = "0008";// 后台支付
        String TRAN_QR = "0009";// 扫码支付
        String TRAN_AGENCY = "0101";// 代收
        String TRAN_PRE_AUTHORIZATION = "0201";// 预授权交易
        String TRAN_SELECT = "0502";  //查询订单
        String TRAN_REFUND = "0401";//退款
    }

}
