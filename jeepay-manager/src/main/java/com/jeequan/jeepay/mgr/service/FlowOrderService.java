package com.jeequan.jeepay.mgr.service;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jeequan.jeepay.core.entity.PayOrder;
import com.jeequan.jeepay.mgr.config.ExcelResultHandler;
import com.jeequan.jeepay.service.mapper.PayOrderMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class FlowOrderService {

    @Autowired
    private PayOrderMapper payOrderMapper;

    public void exportFlowOrder(PayOrder payOrder, JSONObject paramJSON, LambdaQueryWrapper<PayOrder> wrapper) throws IOException {

        List<String> headerArray = Arrays.asList("支付订单号", "商户号", "服务商号", "应用ID", "商户名称", "商户模式", "商户订单号", "支付接口代码", "支付方式代码", "支付金额,单位分", "商户手续费费率", "商户手续费", "货币代码",
                "支付状态", "回调状态", "客户端IP", "商品标题", "描述信息", "额外参数", "渠道用户标识", "渠道订单号", "退款状态", "退款次数", "退款总金额", "分账模式", "分账状态",
                "分账时间", "渠道支付错误码", "渠道支付错误描述", "商户扩展参数", "异步通知地址", "页面跳转地址", "订单失效时间", "订单支付成功时间", "创建时间", "更新时间");

        List<String> fieldArray = Arrays.asList("payOrderId", "mchNo", "isvNo", "appId", "mchName", "mchType", "mchOrderNo", "ifCode", "wayCode", "amount", "mchFeeRate",
                "mchFeeAmount", "currency", "state", "notifyState", "clientIp", "subject", "body", "channelExtra", "channelUser",
                "channelOrderNo", "refundState", "refundTimes", "refundAmount", "divisionMode", "divisionState",
                "divisionLastTime", "errCode", "errMsg", "extParam", "notifyUrl", "returnUrl", "expiredTime", "successTime", "createdAt", "updatedAt");

        if (StringUtils.isNotEmpty(payOrder.getPayOrderId())) {
            wrapper.eq(PayOrder::getPayOrderId, payOrder.getPayOrderId());
        }
        if (StringUtils.isNotEmpty(payOrder.getMchNo())) {
            wrapper.eq(PayOrder::getMchNo, payOrder.getMchNo());
        }
        if (StringUtils.isNotEmpty(payOrder.getIsvNo())) {
            wrapper.eq(PayOrder::getIsvNo, payOrder.getIsvNo());
        }
        if (payOrder.getMchType() != null) {
            wrapper.eq(PayOrder::getMchType, payOrder.getMchType());
        }
        if (StringUtils.isNotEmpty(payOrder.getWayCode())) {
            wrapper.eq(PayOrder::getWayCode, payOrder.getWayCode());
        }
        if (StringUtils.isNotEmpty(payOrder.getMchOrderNo())) {
            wrapper.eq(PayOrder::getMchOrderNo, payOrder.getMchOrderNo());
        }
        if (payOrder.getState() != null) {
            wrapper.eq(PayOrder::getState, payOrder.getState());
        }
        if (payOrder.getNotifyState() != null) {
            wrapper.eq(PayOrder::getNotifyState, payOrder.getNotifyState());
        }
        if (StringUtils.isNotEmpty(payOrder.getAppId())) {
            wrapper.eq(PayOrder::getAppId, payOrder.getAppId());
        }
        if (payOrder.getDivisionState() != null) {
            wrapper.eq(PayOrder::getDivisionState, payOrder.getDivisionState());
        }
        if (paramJSON != null) {
            if (StringUtils.isNotEmpty(paramJSON.getString("createdStart"))) {
                wrapper.ge(PayOrder::getCreatedAt, paramJSON.getString("createdStart"));
            }
            if (StringUtils.isNotEmpty(paramJSON.getString("createdEnd"))) {
                wrapper.le(PayOrder::getCreatedAt, paramJSON.getString("createdEnd"));
            }
        }

        //根据业务Id查询
        if (paramJSON != null && StringUtils.isNotEmpty(paramJSON.getString("businessId"))) {
            wrapper.apply("CASE WHEN JSON_VALID(ext_param) THEN JSON_EXTRACT(ext_param,'$.businessId')={0} ELSE null END"
                    , paramJSON.getString("businessId"));
        }

        // 三合一订单
        if (paramJSON != null && StringUtils.isNotEmpty(paramJSON.getString("unionOrderId"))) {
            wrapper.and(wr -> {
                wr.eq(PayOrder::getPayOrderId, paramJSON.getString("unionOrderId"))
                        .or().eq(PayOrder::getMchOrderNo, paramJSON.getString("unionOrderId"))
                        .or().eq(PayOrder::getChannelOrderNo, paramJSON.getString("unionOrderId"));
            });
        }

        wrapper.orderByDesc(PayOrder::getCreatedAt);

        String exportExcelFileName = "订单流水";
        ExcelResultHandler<PayOrder> handler = new ExcelResultHandler<PayOrder>(headerArray, fieldArray, exportExcelFileName, true) {
            public void tryFetchDataAndWriteToExcel() {
                payOrderMapper.streamQuery(wrapper, this);
            }
        };

        handler.ExportExcel();
    }
}