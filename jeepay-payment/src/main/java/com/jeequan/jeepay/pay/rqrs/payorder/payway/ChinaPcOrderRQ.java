package com.jeequan.jeepay.pay.rqrs.payorder.payway;

import com.jeequan.jeepay.core.constants.CS;
import com.jeequan.jeepay.pay.rqrs.payorder.CommonPayDataRQ;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

/*
 * 支付方式： CHINA_PC
 *
 * @author czw
 * @site https://www.jeequan.com
 * @date 2023/5/26 09:34
 */
@Data
public class ChinaPcOrderRQ extends CommonPayDataRQ {

    /** 是否发生实际支付 **/
    @Range(min = 0, max = 1, message = "支付模式: 0为支付,1为支付")
    private Byte payMode=1;

    /** 构造函数 **/
    public ChinaPcOrderRQ(){
        this.setWayCode(CS.PAY_WAY_CODE.UNION_PC);
    }

}
