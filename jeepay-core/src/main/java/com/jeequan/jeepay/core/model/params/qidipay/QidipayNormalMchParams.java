package com.jeequan.jeepay.core.model.params.qidipay;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jeequan.jeepay.core.model.params.NormalMchParams;
import com.jeequan.jeepay.core.model.params.xxpay.XxpayNormalMchParams;
import com.jeequan.jeepay.core.utils.StringKit;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;


@Data
public class QidipayNormalMchParams extends NormalMchParams {

    /** 商户号 */
    private String mchId;

    /** 私钥 */
    private String key;

    /** 支付网关地址 */
    private String payUrl;

    @Override
    public String deSenData() {
        QidipayNormalMchParams mchParams = this;
        if (StringUtils.isNotBlank(this.key)) {
            mchParams.setKey(StringKit.str2Star(this.key, 4, 4, 6));
        }
        return ((JSONObject) JSON.toJSON(mchParams)).toJSONString();
    }
}
