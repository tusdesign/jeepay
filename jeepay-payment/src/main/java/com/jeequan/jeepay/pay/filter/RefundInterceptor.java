package com.jeequan.jeepay.pay.filter;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeequan.jeepay.core.beans.RequestKitBean;
import com.jeequan.jeepay.core.entity.PayOrder;
import com.jeequan.jeepay.core.entity.PayOrderExtend;
import com.jeequan.jeepay.core.model.ApiRes;
import com.jeequan.jeepay.service.impl.PayOrderExtendService;
import com.jeequan.jeepay.service.impl.PayOrderService;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 退单拦截器类
 * 实现HandlerInterceptor接口
 */
public class RefundInterceptor implements HandlerInterceptor {

    @Autowired
    private PayOrderService payOrderService;

    @Autowired
    private RequestKitBean requestKitBean;

    /**
     * 访问控制器方法前执行
     */
    @Override
    @SneakyThrows
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        JSONObject jsonObject = requestKitBean.getReqParamJSON();

        String payOrderId = jsonObject.getString("payOrderId");
        String mchNo = jsonObject.getString("mchNo");
        String mchOrderNo = jsonObject.getString("mchOrderNo");

        PayOrder order = payOrderService.queryMchOrder(mchNo, payOrderId, mchOrderNo);
        if (order.getState().equals(PayOrder.STATE_CLOSED)) {
            ApiRes res = ApiRes.customFail("退单失败:因为该订单已关闭,如有必要请线下人工操作!");
            returnJson(res, response);
        }
        return true;
    }


    /**
     * 返回客户端数据
     */
    private void returnJson(ApiRes res, HttpServletResponse response) throws Exception {

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=utf-8");

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            response.getWriter().println(objectMapper.writeValueAsString(res));

        } catch (IOException e) {
            throw e;
        }
    }

}
