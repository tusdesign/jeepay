package com.jeequan.jeepay.pay.channel.unionpay;

import com.alibaba.fastjson.JSONObject;
import com.chinapay.secss.SecssConstants;
import com.chinapay.secss.SecssUtil;
import com.jeequan.jeepay.core.constants.CS;
import com.jeequan.jeepay.core.entity.RefundOrder;
import com.jeequan.jeepay.core.exception.ResponseException;
import com.jeequan.jeepay.core.model.params.unionpay.UnionPayNormalMchParams;
import com.jeequan.jeepay.pay.channel.AbstractChannelRefundNoticeService;
import com.jeequan.jeepay.pay.channel.unionpay.utils.UnionPayUtil;
import com.jeequan.jeepay.pay.model.MchAppConfigContext;
import com.jeequan.jeepay.pay.rqrs.msg.ChannelRetMsg;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@Slf4j
public class UnionpayChannelRefundNoticeService extends AbstractChannelRefundNoticeService {

    @Autowired
    private UnionPayUtil chinaPayUtil;

    @Override
    public String getIfCode() {
        return CS.IF_CODE.UNIONPAY;
    }

    @Override
    public MutablePair<String, Object> parseParams(HttpServletRequest request, String urlOrderId, NoticeTypeEnum noticeTypeEnum) {
        try {
            JSONObject params = getReqParamJSON();
            String refundOrderId = params.getString("MerOrderNo");
            return MutablePair.of(refundOrderId, params);

        } catch (Exception e) {
            log.error("error", e);
            throw ResponseException.buildText("ERROR");
        }
    }

    @Override
    public ChannelRetMsg doNotice(HttpServletRequest request, Object params, RefundOrder refundOrder, MchAppConfigContext mchAppConfigContext, NoticeTypeEnum noticeTypeEnum) {

        try {

            UnionPayNormalMchParams unionPayNormalMchParams = (UnionPayNormalMchParams) configContextQueryService.queryNormalMchParams(mchAppConfigContext.getMchNo(), mchAppConfigContext.getAppId(), getIfCode());

            //获取请求头参数到paramsMap
            JSONObject jsonParam = (JSONObject) params;
            //返回数据验签
            String sign = jsonParam.getString("Signature");
            // 验证参数失败
            boolean verifyResult = verifyParams(jsonParam, sign, unionPayNormalMchParams);
            if (!verifyResult) {
                throw ResponseException.buildText("ERROR");
            }

            ChannelRetMsg result = new ChannelRetMsg();
            ResponseEntity okResponse = textResp("success");
            result.setChannelOrderId(jsonParam.getString("AcqSeqId"));
            result.setResponseEntity(okResponse);
            result.setChannelState(ChannelRetMsg.ChannelState.CONFIRM_SUCCESS);
            return result;

        } catch (Exception e) {
            throw ResponseException.buildText("ERROR");
        }
    }


    public boolean verifyParams(JSONObject jsonParam, String sign, UnionPayNormalMchParams unionPayNormalMchParams) {
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            log.info("当前时间：" + sdf.format(new Date()) + "银联支付回调原始参数：" + jsonParam.toString());

            boolean initResult = chinaPayUtil.init(unionPayNormalMchParams);
            if (initResult) {
                SecssUtil secssUtil = chinaPayUtil.getSecssUtil();

                if (StringUtils.isEmpty(sign)) {
                    secssUtil.verify(jsonParam);
                }
                if (!SecssConstants.SUCCESS.equals(secssUtil.getErrCode())) {
                    String outTradeNo = jsonParam.get("MerOrderNo") == null ? "" : jsonParam.getString("MerOrderNo"); //订单号
                    log.error("ChinaPay返回的应答数据【验签】失败:" + secssUtil.getErrCode() + "=" + secssUtil.getErrMsg() + "支付明细编号为：" + outTradeNo);
                    throw ResponseException.buildText("ERROR");
                }

                if ("00".equals(secssUtil.getErrCode())) {
                    String orderStatus = jsonParam.getString("OrderStatus");
                    if ("0000".equals(orderStatus) || "1013".equals(orderStatus)) {
                        return true;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            log.error("error", e);
            throw ResponseException.buildText("ERROR");
        }
    }
}
