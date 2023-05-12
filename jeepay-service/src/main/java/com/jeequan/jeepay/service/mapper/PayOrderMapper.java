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
package com.jeequan.jeepay.service.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.jeequan.jeepay.core.entity.OrderStatisticsDept;
import com.jeequan.jeepay.core.entity.PayOrder;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.session.ResultHandler;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 支付订单表 Mapper 接口
 * </p>
 *
 * @author [mybatis plus generator]
 * @since 2021-04-27
 */
public interface PayOrderMapper extends BaseMapper<PayOrder> {

    Map payCount(Map param);

    List<Map> payTypeCount(Map param);

    List<Map> selectOrderCount(Map param);

    /** 更统计企业在各个档口的消费情况 **/
    List<OrderStatisticsDept> selectOrderCountByDept(Map param);

    /** 更新订单状态为已完成 **/
    int updateOrderStateBatch(List<PayOrder> orderList);

    /** 更新订单退款金额和次数 **/
    int updateRefundAmountAndCount(@Param("payOrderId") String payOrderId, @Param("currentRefundAmount") Long currentRefundAmount);


    @Select("select * from t_pay_order ${ew.customSqlSegment}")
    @Options(resultSetType = ResultSetType.FORWARD_ONLY, fetchSize = Integer.MIN_VALUE)
    @ResultType(PayOrder.class)
    void streamQuery(@Param(Constants.WRAPPER) Wrapper<PayOrder> queryWrapper, ResultHandler<PayOrder> handler);
}
