package com.jeequan.jeepay.pay.rqrs.payorder.payway;

import com.jeequan.jeepay.core.constants.CS;
import com.jeequan.jeepay.pay.rqrs.payorder.CommonPayDataRQ;
import lombok.Data;

/*
 * 支付方式： CHINA_PC
 *
 * @author czw
 * @site https://www.jeequan.com
 * @date 2023/5/26 09:34
 */
@Data
public class ChinaPcOrderRQ extends CommonPayDataRQ {

    /** 构造函数 **/
    public ChinaPcOrderRQ(){
        this.setWayCode(CS.PAY_WAY_CODE.UNION_PC);
    }

}
