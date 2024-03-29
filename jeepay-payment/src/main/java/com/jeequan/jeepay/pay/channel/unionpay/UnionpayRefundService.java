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
package com.jeequan.jeepay.pay.channel.unionpay;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.chinapay.secss.SecssConstants;
import com.chinapay.secss.SecssUtil;
import com.jeequan.jeepay.core.constants.CS;
import com.jeequan.jeepay.core.entity.PayOrder;
import com.jeequan.jeepay.core.entity.RefundOrder;
import com.jeequan.jeepay.core.model.params.unionpay.UnionPayConfig;
import com.jeequan.jeepay.core.model.params.unionpay.UnionPayNormalMchParams;
import com.jeequan.jeepay.pay.channel.AbstractRefundService;
import com.jeequan.jeepay.pay.channel.unionpay.utils.UnionPayUtil;
import com.jeequan.jeepay.pay.model.MchAppConfigContext;
import com.jeequan.jeepay.pay.rqrs.msg.ChannelRetMsg;
import com.jeequan.jeepay.pay.rqrs.refund.RefundOrderRQ;
import com.jeequan.jeepay.service.impl.PayOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

/*
 * 退款接口： QD支付
 *
 * @author czw
 * @site https://www.jeequan.com
 * @date 2023/02/14 9:38
 */
@Service
@Slf4j
public class UnionpayRefundService extends AbstractRefundService {

    @Autowired
    private UnionPayUtil chinaPayUtil;

    @Autowired
    private PayOrderService payOrderService;

    @Autowired
    private UnionpayPayOrderQueryService unionpayPayOrderQueryService;

    @Override
    public String getIfCode() {
        return CS.IF_CODE.UNIONPAY;
    }

    @Override
    public String preCheck(RefundOrderRQ bizRQ, RefundOrder refundOrder, PayOrder payOrder) {
        return null;
    }

    @Override
    public ChannelRetMsg refund(RefundOrderRQ bizRQ, RefundOrder refundOrder, PayOrder payOrder, MchAppConfigContext mchAppConfigContext) throws Exception {

        UnionPayNormalMchParams normalMchParams =
                (UnionPayNormalMchParams) configContextQueryService.queryNormalMchParams(mchAppConfigContext.getMchNo(), mchAppConfigContext.getAppId(), getIfCode());

        ChannelRetMsg channelRetMsg = new ChannelRetMsg();
        // 默认退款中状态
        channelRetMsg.setChannelState(ChannelRetMsg.ChannelState.WAITING);

        JSONObject reqParams = new JSONObject();

        try {

            reqParams.put("Version", normalMchParams.getPayVersion());
            reqParams.put("MerId", normalMchParams.getMchId());
            reqParams.put("MerOrderNo", refundOrder.getRefundOrderId());

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");

            reqParams.put("TranDate", dateFormat.format(new Date()));
            reqParams.put("TranTime", timeFormat.format(new Date()));
            //原始交易订单号
            reqParams.put("OriOrderNo", refundOrder.getMchRefundNo());
            //原始交易日期
            reqParams.put("OriTranDate", dateFormat.format(payOrder.getCreatedAt()));
            reqParams.put("RefundAmt",String.valueOf(refundOrder.getRefundAmount()));//退款金额
            reqParams.put("TranType", UnionPayConfig.TRAN_TYPE.TRAN_REFUND);//退款交易
            reqParams.put("BusiType", UnionPayConfig.BUSINESS_TYPE);
            reqParams.put("MerBgUrl", getNotifyUrl(refundOrder.getRefundOrderId()));

            if (chinaPayUtil.init(normalMchParams)) {

                SecssUtil secssUtil = chinaPayUtil.getSecssUtil();
                secssUtil.sign(reqParams);
                if (!SecssConstants.SUCCESS.equals(secssUtil.getErrCode())) {

                    log.error("ChinaPay返回的应答数据【验签】失败:" + secssUtil.getErrCode() + "=" + secssUtil.getErrMsg() + "支付明细编号为：" + payOrder.getMchOrderNo());
                    channelRetMsg.setChannelState(ChannelRetMsg.ChannelState.CONFIRM_FAIL);
                    channelRetMsg.setChannelErrCode(secssUtil.getErrCode());
                    channelRetMsg.setChannelErrMsg(secssUtil.getErrMsg());
                    return channelRetMsg;
                }

                String signature = secssUtil.getSign();
                reqParams.put("Signature", signature);

                String payUrl = UnionPayUtil.getPayUrl(normalMchParams.getBgPayUrl()) + UnionPayConfig.REFUND_PATH;
                String resp = HttpUtil.post(payUrl, reqParams, 60000);

                if (StringUtils.isEmpty(resp)) {
                    channelRetMsg.setChannelState(ChannelRetMsg.ChannelState.UNKNOWN); // 状态不明确
                }

                Map<String, Object> resultMap = chinaPayUtil.strToMap(resp);
                String respCode = resultMap.get("respCode").toString();//应答码
                String respMsg = resultMap.get("respMsg").toString();//应答信息

                //请求 & 响应成功， 判断业务逻辑
                if (respCode.equals(UnionPayConfig.RESPONSE_STATUS)) {
                    secssUtil.verify(resultMap);
                    if (SecssConstants.SUCCESS.equals(secssUtil.getErrCode())) {
                        log.info("{} >>> 退款成功", resultMap.get("MerOrderNo"));
                        channelRetMsg.setChannelState(ChannelRetMsg.ChannelState.CONFIRM_SUCCESS);
                    }
                } else {

                    channelRetMsg.setChannelState(ChannelRetMsg.ChannelState.CONFIRM_FAIL);
                    channelRetMsg.setChannelErrCode(respCode);
                    channelRetMsg.setChannelErrMsg(respMsg);
                }
            }

        } catch (Exception ex) {
            channelRetMsg.setChannelState(ChannelRetMsg.ChannelState.SYS_ERROR);
            log.error("退款过程中出现错误：" + ex.getMessage() + "订单号:" + refundOrder.getMchRefundNo());
        }
        return channelRetMsg;
    }

    @Override
    public ChannelRetMsg query(RefundOrder refundOrder, MchAppConfigContext mchAppConfigContext) throws Exception {

        PayOrder order = payOrderService.queryMchOrder(refundOrder.getMchNo(), refundOrder.getPayOrderId(), refundOrder.getMchRefundNo());
        if (Objects.isNull(order)) {
            return ChannelRetMsg.confirmFail(refundOrder.getMchRefundNo());
        }
        return unionpayPayOrderQueryService.query(order, mchAppConfigContext);
    }

}
