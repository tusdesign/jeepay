/*
 * Copyright (c) 2021-2031, 河北计全科技有限公司 (https://www.jeequan.com & jeequan@126.com).
 * <p>
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE 3.0;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jeequan.jeepay.pay.channel.qidipay;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.chinapay.secss.SecssConstants;
import com.chinapay.secss.SecssUtil;
import com.jeequan.jeepay.core.constants.CS;
import com.jeequan.jeepay.core.entity.PayOrder;
import com.jeequan.jeepay.core.entity.RefundOrder;
import com.jeequan.jeepay.core.exception.ResponseException;
import com.jeequan.jeepay.core.model.params.qidipay.QidipayNormalMchParams;
import com.jeequan.jeepay.core.model.params.xxpay.XxpayNormalMchParams;
import com.jeequan.jeepay.core.utils.JeepayKit;
import com.jeequan.jeepay.pay.channel.AbstractRefundService;
import com.jeequan.jeepay.pay.channel.qidipay.utils.ChinaPayUtil;
import com.jeequan.jeepay.pay.channel.xxpay.XxpayKit;
import com.jeequan.jeepay.pay.channel.ysfpay.YsfpayPaymentService;
import com.jeequan.jeepay.pay.model.MchAppConfigContext;
import com.jeequan.jeepay.pay.rqrs.msg.ChannelRetMsg;
import com.jeequan.jeepay.pay.rqrs.refund.RefundOrderRQ;
import com.jeequan.jeepay.service.impl.PayOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/*
 * 退款接口： QD支付
 *
 * @author czw
 * @site https://www.jeequan.com
 * @date 2023/02/14 9:38
 */
@Service
@Slf4j
public class QidipayRefundService extends AbstractRefundService {

    @Autowired
    private ChinaPayUtil chinaPayUtil;

    @Autowired
    private PayOrderService payOrderService;

    @Autowired
    private QidiPayOrderQueryService qidiPayOrderQueryService;

    @Override
    public String getIfCode() {
        return CS.IF_CODE.QIDIPAY;
    }

    @Override
    public String preCheck(RefundOrderRQ bizRQ, RefundOrder refundOrder, PayOrder payOrder) {
        return null;
    }

    @Override
    public ChannelRetMsg refund(RefundOrderRQ bizRQ, RefundOrder refundOrder, PayOrder payOrder, MchAppConfigContext mchAppConfigContext) throws Exception {

        QidipayNormalMchParams normalMchParams = (QidipayNormalMchParams) configContextQueryService.queryNormalMchParams(mchAppConfigContext.getMchNo(), mchAppConfigContext.getAppId(), getIfCode());

        ChannelRetMsg channelRetMsg = new ChannelRetMsg();
        channelRetMsg.setChannelState(ChannelRetMsg.ChannelState.WAITING);// 默认退款中状态

        JSONObject reqParams = new JSONObject();
        reqParams.put("Version", normalMchParams.getPayVersion());
        reqParams.put("MerId", refundOrder.getMchNo());
        reqParams.put("MerOrderNo", refundOrder.getRefundOrderId());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");

        reqParams.put("TranDate", dateFormat.format(new Date()));
        reqParams.put("TranTime", timeFormat.format(new Date()));
        reqParams.put("OriOrderNo", payOrder.getMchOrderNo());//原始交易订单号
        reqParams.put("OriTranDate", dateFormat.format(payOrder.getCreatedAt()));//原始交易日期
        reqParams.put("TranType", "0401");//退款交易
        reqParams.put("BusiType", "0001");
        reqParams.put("MerBgUrl", getNotifyUrl());

        if (chinaPayUtil.init(normalMchParams)) {
            SecssUtil secssUtil = chinaPayUtil.getSecssUtil();
            secssUtil.sign(reqParams);
            if (!SecssConstants.SUCCESS.equals(secssUtil.getErrCode())) {
                channelRetMsg.setChannelState(ChannelRetMsg.ChannelState.CONFIRM_FAIL);
                channelRetMsg.setChannelErrCode(secssUtil.getErrCode());
                channelRetMsg.setChannelErrMsg(secssUtil.getErrMsg());
                return channelRetMsg;
            }

            String signature = secssUtil.getSign();
            reqParams.put("Signature", signature);

            String resp = HttpUtil.post("payQueryUrl", reqParams, 60000);
            Map<String, String> resultMap = chinaPayUtil.getResponseMap(resp);

            String sign = resultMap.get("Signature");
            if (StringUtils.isEmpty(sign)) {
                secssUtil.verify(resultMap);
            }
            if (!SecssConstants.SUCCESS.equals(secssUtil.getErrCode())) {
                String outTradeNo = resultMap.get("MerOrderNo") == null ? "" : resultMap.get("MerOrderNo"); //渠道订单号
                log.error("ChinaPay返回的应答数据【验签】失败:" + secssUtil.getErrCode() + "=" + secssUtil.getErrMsg() + "支付明细编号为：" + outTradeNo);
                throw ResponseException.buildText("ERROR");
            }
            channelRetMsg.setChannelState(ChannelRetMsg.ChannelState.CONFIRM_SUCCESS);
            return channelRetMsg;
        }
        return ChannelRetMsg.confirmFail();
    }

    @Override
    public ChannelRetMsg query(RefundOrder refundOrder, MchAppConfigContext mchAppConfigContext) throws Exception {

        PayOrder order= payOrderService.queryMchOrder(refundOrder.getMchNo(),refundOrder.getPayOrderId(),refundOrder.getMchRefundNo());
        if(Objects.isNull(order)){
            return ChannelRetMsg.confirmFail(refundOrder.getMchRefundNo());
        }
        return  qidiPayOrderQueryService.query(order,mchAppConfigContext);
    }

}
