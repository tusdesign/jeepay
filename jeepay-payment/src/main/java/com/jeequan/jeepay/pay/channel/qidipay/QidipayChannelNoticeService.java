package com.jeequan.jeepay.pay.channel.qidipay;

import com.alibaba.fastjson.JSONObject;
import com.alipay.service.schema.util.StringUtil;
import com.chinapay.secss.SecssConstants;
import com.chinapay.secss.SecssUtil;
import com.jeequan.jeepay.core.constants.CS;
import com.jeequan.jeepay.core.entity.PayOrder;
import com.jeequan.jeepay.core.exception.ResponseException;
import com.jeequan.jeepay.core.model.params.qidipay.QidipayConfig;
import com.jeequan.jeepay.core.model.params.qidipay.QidipayNormalMchParams;
import com.jeequan.jeepay.core.model.params.xxpay.XxpayNormalMchParams;
import com.jeequan.jeepay.pay.channel.AbstractChannelNoticeService;
import com.jeequan.jeepay.pay.channel.qidipay.utils.ChinaPayUtil;
import com.jeequan.jeepay.pay.channel.xxpay.XxpayKit;
import com.jeequan.jeepay.pay.model.MchAppConfigContext;
import com.jeequan.jeepay.pay.rqrs.msg.ChannelRetMsg;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Service
@Slf4j
public class QidipayChannelNoticeService extends AbstractChannelNoticeService {

    @Override
    public String getIfCode() {
        return CS.IF_CODE.QIDIPAY;
    }

    @Override
    public MutablePair<String, Object> parseParams(HttpServletRequest request, String urlOrderId, NoticeTypeEnum noticeTypeEnum) {

        try {
            JSONObject params = getReqParamJSON();
            String payOrderId = params.getString("mchOrderNo");
            return MutablePair.of(payOrderId, params);

        } catch (Exception e) {
            log.error("error", e);
            throw ResponseException.buildText("ERROR");
        }
    }

    @Override
    public ChannelRetMsg doNotice(HttpServletRequest request, Object params, PayOrder payOrder, MchAppConfigContext mchAppConfigContext, NoticeTypeEnum noticeTypeEnum) {

        try {

            QidipayNormalMchParams qidipayNormalMchParams = (QidipayNormalMchParams)
                    configContextQueryService.queryNormalMchParams(mchAppConfigContext.getMchNo(), mchAppConfigContext.getAppId(), getIfCode());

            //获取请求头参数到paramsMap
            JSONObject jsonParam = (JSONObject) params;

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            log.info("当前时间：" + sdf.format(new Date()) + "银联支付回调原始参数：" + jsonParam.toString());

            String outTradeNo = jsonParam.get("MerOrderNo") == null ? "" : jsonParam.getString("MerOrderNo"); // 渠道订单号

            SecssUtil secssUtil = ChinaPayUtil.init(qidipayNormalMchParams);
            String sign = jsonParam.getString("Signature"); //返回数据验签
            if (StringUtils.isEmpty(sign)) {
                secssUtil.verify(jsonParam);
            }
            if (!SecssConstants.SUCCESS.equals(secssUtil.getErrCode())) {
                log.error("ChinaPay返回的应答数据【验签】失败:" + secssUtil.getErrCode() + "=" + secssUtil.getErrMsg() + "支付明细编号为：" + outTradeNo);
                throw ResponseException.buildText("ERROR");
            }

            ResponseEntity okResponse = textResp("success");

            ChannelRetMsg result = new ChannelRetMsg();
            result.setChannelOrderId(jsonParam.getString("MerOrderNo")); //渠道订单号
            result.setResponseEntity(okResponse); //响应数据
            result.setChannelState(ChannelRetMsg.ChannelState.WAITING);

            if ("00".equals(secssUtil.getErrCode())) {
                String orderStatus = jsonParam.getString("OrderStatus");
                if ("0000".equals(orderStatus)) {
                    result.setChannelState(ChannelRetMsg.ChannelState.CONFIRM_SUCCESS);
                } else {
                    result.setChannelState(ChannelRetMsg.ChannelState.CONFIRM_FAIL);
                }
            }
            return result;

        } catch (Exception e) {
            log.error("error", e);
            throw ResponseException.buildText("ERROR");
        }
    }
}
