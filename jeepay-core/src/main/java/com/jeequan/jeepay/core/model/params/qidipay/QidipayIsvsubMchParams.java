package com.jeequan.jeepay.core.model.params.qidipay;

import com.jeequan.jeepay.core.model.params.IsvsubMchParams;
import lombok.Data;

@Data
public class QidipayIsvsubMchParams extends IsvsubMchParams {
    /** 子商户ID **/
    private String subMchId;

    /** 子账户appID **/
    private String subMchAppId;

}
