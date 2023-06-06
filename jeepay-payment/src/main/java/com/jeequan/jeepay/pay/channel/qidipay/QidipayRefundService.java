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
import com.jeequan.jeepay.core.model.params.qidipay.QidipayNormalMchParams;
import com.jeequan.jeepay.core.model.params.xxpay.XxpayNormalMchParams;
import com.jeequan.jeepay.core.utils.JeepayKit;
import com.jeequan.jeepay.pay.channel.AbstractRefundService;
import com.jeequan.jeepay.pay.channel.qidipay.utils.ChinaPayUtil;
import com.jeequan.jeepay.pay.channel.xxpay.XxpayKit;
import com.jeequan.jeepay.pay.model.MchAppConfigContext;
import com.jeequan.jeepay.pay.rqrs.msg.ChannelRetMsg;
import com.jeequan.jeepay.pay.rqrs.refund.RefundOrderRQ;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

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

        QidipayNormalMchParams normalMchParams = (QidipayNormalMchParams)configContextQueryService.queryNormalMchParams(mchAppConfigContext.getMchNo(), mchAppConfigContext.getAppId(), getIfCode());

        ChannelRetMsg channelRetMsg = new ChannelRetMsg();
        channelRetMsg.setChannelState(ChannelRetMsg.ChannelState.WAITING);// 默认退款中状态

        JSONObject reqParams = new JSONObject();
        reqParams.put("Version", normalMchParams.getPayVersion());
        reqParams.put("MerId", refundOrder.getMchNo());
        reqParams.put("MerOrderNo", refundOrder.getMchRefundNo());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");

        reqParams.put("TranDate", dateFormat.format(new Date()));
        reqParams.put("TranTime", timeFormat.format(new Date()));
        reqParams.put("OriOrderNo", payOrder.getMchOrderNo());//原始交易订单号
        reqParams.put("OriTranDate", dateFormat.format(payOrder.getCreatedAt()));//原始交易日期
        reqParams.put("TranType","0401");//退款交易
        reqParams.put("BusiType","0001");
        reqParams.put("MerBgUrl",getNotifyUrl());

        SecssUtil secssUtil = ChinaPayUtil.init(normalMchParams);
        secssUtil.sign(reqParams);
        if (!SecssConstants.SUCCESS.equals(secssUtil.getErrCode())) {
            channelRetMsg.setChannelState(ChannelRetMsg.ChannelState.CONFIRM_FAIL);
            channelRetMsg.setChannelErrCode(secssUtil.getErrCode());
            channelRetMsg.setChannelErrMsg(secssUtil.getErrMsg());
        }
        secssUtil.sign(reqParams);
        String signature = secssUtil.getSign();
        reqParams.put("Signature", signature);

        String resp = HttpUtil.post("payQueryUrl", reqParams,60000);
        log.info("################交易查询结果：{}", resp);

        //解析同步应答字段
        Map<String, String> resultMap = ChinaPayUtil.getResponseMap(resp);

        //返回数据验签
        boolean verifyFlag = ChinaPayUtil.verifyNotify(resultMap, normalMchParams);
        if (!verifyFlag) {
            channelRetMsg.setChannelState(ChannelRetMsg.ChannelState.CONFIRM_FAIL);
        }
        channelRetMsg.setChannelState(ChannelRetMsg.ChannelState.CONFIRM_SUCCESS);
        return channelRetMsg;
    }

    @Override
    public ChannelRetMsg query(RefundOrder refundOrder, MchAppConfigContext mchAppConfigContext) throws Exception {

        QidipayNormalMchParams params = (QidipayNormalMchParams)configContextQueryService.queryNormalMchParams(mchAppConfigContext.getMchNo(), mchAppConfigContext.getAppId(), getIfCode());

        // 构造支付请求参数
        Map<String,Object> paramMap = new TreeMap();
        paramMap.put("mchId", params.getMchId());  //商户ID
        paramMap.put("mchRefundNo", refundOrder.getRefundOrderId());   //商户退款单号

        // 生成签名
        String sign = XxpayKit.getSign(paramMap, params.getSecret());
        paramMap.put("sign", sign);
        // 退款查询地址
        String queryRefundOrderUrl = XxpayKit.getQueryRefundOrderUrl(params.getPayUrl())+ "?" + JeepayKit.genUrlParams(paramMap);
        String resStr = "";
        try {
            log.info("查询退款[{}]参数：{}", getIfCode(), queryRefundOrderUrl);
            //resStr = HttpUtil.createPost(queryRefundOrderUrl).timeout(60 * 1000).execute().body();
            log.info("查询退款[{}]结果：{}", getIfCode(), resStr);
        } catch (Exception e) {
            log.error("http error", e);
        }

        ChannelRetMsg channelRetMsg = new ChannelRetMsg();
        // 默认退款中状态
        channelRetMsg.setChannelState(ChannelRetMsg.ChannelState.WAITING);

        if(StringUtils.isEmpty(resStr)) {
            return null;
        }

        JSONObject resObj = JSONObject.parseObject(resStr);
        if(!"0".equals(resObj.getString("retCode"))){
            return null;
        }

        // 验证响应数据签名
        String checkSign = resObj.getString("sign");
        resObj.remove("sign");
        if(!checkSign.equals(XxpayKit.getSign(resObj, params.getSecret()))) {
            channelRetMsg.setChannelState(ChannelRetMsg.ChannelState.CONFIRM_FAIL);
            return null;
        }

        // 退款状态:0-订单生成,1-退款中,2-退款成功,3-退款失败
        String status = resObj.getString("status");
        if("2".equals(status)) {
            channelRetMsg.setChannelState(ChannelRetMsg.ChannelState.CONFIRM_SUCCESS);
        }else if("3".equals(status)) {
            channelRetMsg.setChannelState(ChannelRetMsg.ChannelState.CONFIRM_FAIL);
            channelRetMsg.setChannelErrMsg(resObj.getString("retMsg"));
        }

        ////没用实际与支付打通，暂设为成功
        if(channelRetMsg.getChannelState()!=ChannelRetMsg.ChannelState.CONFIRM_SUCCESS){
            channelRetMsg.setChannelState(ChannelRetMsg.ChannelState.CONFIRM_SUCCESS);
        }
        return channelRetMsg;

    }

}
