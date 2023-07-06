package com.jeequan.jeepay.pay.rqrs.payorder.payway;

import com.jeequan.jeepay.core.constants.CS;
import com.jeequan.jeepay.pay.rqrs.payorder.CommonPayDataRQ;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
public class ChinaQrOrderRQ extends CommonPayDataRQ {

    /** 二维码呈现方式 "0001"为cp码, "0002”为聚合码 **/
    private String qrCodeProvider;

    /** 是否发生实际支付 **/
    @Range(min = 0, max = 1, message = "支付模式: 0为不支付,1为支付")
    private Byte payMode=1;

    /** 构造函数 **/
    public ChinaQrOrderRQ(){
        this.setWayCode(CS.PAY_WAY_CODE.UNION_QR);
    }

}
