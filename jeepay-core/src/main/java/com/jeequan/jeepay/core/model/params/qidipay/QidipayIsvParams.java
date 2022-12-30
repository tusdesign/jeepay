package com.jeequan.jeepay.core.model.params.qidipay;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jeequan.jeepay.core.model.params.IsvParams;
import com.jeequan.jeepay.core.model.params.wxpay.WxpayIsvParams;
import com.jeequan.jeepay.core.utils.StringKit;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class QidipayIsvParams extends IsvParams {

    @Override
    public String deSenData() {
        QidipayIsvParams isvParams = this;
        return ((JSONObject) JSON.toJSON(isvParams)).toJSONString();
    }
}
