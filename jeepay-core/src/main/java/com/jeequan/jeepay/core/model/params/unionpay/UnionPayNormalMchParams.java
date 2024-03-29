package com.jeequan.jeepay.core.model.params.unionpay;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jeequan.jeepay.core.model.params.NormalMchParams;
import com.jeequan.jeepay.core.model.params.qidipay.QidipayNormalMchParams;
import com.jeequan.jeepay.core.utils.StringKit;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class UnionPayNormalMchParams  extends NormalMchParams {

    /** 商户号 */
    private String mchId;

    /** 前台支付网关 */
    private String frontPayUrl;

    /** 后台支付网关 */
    private String bgPayUrl;

    /** 二维码支付网关 */
    private String qrPayUrl;

    /** 支付版本号 */
    private String payVersion;

    /** privateKey */
    private String privateKey;

    /** 私钥密码 */
    private String privatePwd;

    /** alipayPublicKey */
    private String chinaPayPublicKey;

    /** 算法 **/
    private String secret;

    /** 私钥证书 (.sm2格式）**/
    private String chinaPayPrivateCert;

    /** 银联公钥证书（.cer格式） **/
    private String chinaPayPublicCert;


    @Override
    public String deSenData() {
        UnionPayNormalMchParams mchParams = this;
        if (StringUtils.isNotBlank(this.privateKey)) {
            mchParams.setPrivateKey(StringKit.str2Star(this.privateKey, 4, 4, 6));
        }
        if (StringUtils.isNotBlank(this.chinaPayPublicKey)) {
            mchParams.setChinaPayPublicKey(StringKit.str2Star(this.chinaPayPublicKey, 4, 4, 6));
        }
        return ((JSONObject) JSON.toJSON(mchParams)).toJSONString();
    }
}
