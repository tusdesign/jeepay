package com.jeequan.jeepay.pay.rqrs.payorder.payway;

import com.jeequan.jeepay.core.constants.CS;
import com.jeequan.jeepay.pay.rqrs.payorder.UnifiedOrderRQ;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class QiDiAppOrderRQ extends UnifiedOrderRQ {

    /** 支付条码 **/
    //    @NotBlank(message = "支付条码不能为空")
    //    private String authCode;

    public QiDiAppOrderRQ(){
        this.setWayCode(CS.PAY_WAY_CODE.QIDI_APP);
    }
}
