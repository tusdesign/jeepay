package com.jeequan.jeepay.mgr.service;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jeequan.jeepay.core.entity.PayOrder;
import com.jeequan.jeepay.mgr.config.ExcelResultHandler;
import com.jeequan.jeepay.service.mapper.PayOrderMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class FlowOrderService {

    @Autowired
    private PayOrderMapper payOrderMapper;

    public void exportFlowOrder(PayOrder payOrder, JSONObject paramJSON, LambdaQueryWrapper<PayOrder> wrapper) {

        List<String> headerArray = Arrays.asList("姓名", "年龄");
        List<String> fieldArray = Arrays.asList("username", "age");

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

        //定义要导出的excel的文件名，不带"xlsx"后缀。
        String exportExcelFileName = "文件测试";

        //每次导出new一个handler对象，将headerArray,fieldArray,exportExcelFileName传递进去。
        ExcelResultHandler<PayOrder> handler = new ExcelResultHandler<PayOrder>(headerArray, fieldArray, exportExcelFileName, false) {
            public void tryFetchDataAndWriteToExcel() {
                payOrderMapper.streamQuery(wrapper, this);
            }
        };

        handler.ExportExcel();
    }
}