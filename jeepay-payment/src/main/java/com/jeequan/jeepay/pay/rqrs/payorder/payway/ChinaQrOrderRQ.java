package com.jeequan.jeepay.pay.rqrs.payorder.payway;

import com.jeequan.jeepay.core.constants.CS;
import com.jeequan.jeepay.pay.rqrs.payorder.CommonPayDataRQ;
import lombok.Data;

@Data
public class ChinaQrOrderRQ extends CommonPayDataRQ {

    /** 二维码呈现方式 "0001"为cp码, "0002”为聚合码 **/
    private String qrCodeProvider;

    /** 构造函数 **/
    public ChinaQrOrderRQ(){
        this.setWayCode(CS.PAY_WAY_CODE.UNION_QR);
    }

}
