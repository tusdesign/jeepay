package com.jeequan.jeepay.mgr.ctrl.anon;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jeequan.jeepay.core.entity.PayOrder;
import com.jeequan.jeepay.core.model.ApiRes;
import com.jeequan.jeepay.mgr.ctrl.CommonCtrl;
import com.jeequan.jeepay.mgr.rqrs.AccountForTenantRq;
import com.jeequan.jeepay.mgr.service.FlowOrderService;
import com.jeequan.jeepay.mgr.service.Page;
import com.jeequan.jeepay.mgr.service.PageService;
import com.jeequan.jeepay.mgr.service.ReportingService;
import com.jeequan.jeepay.mgr.util.JxlsUtils;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 报表下载
 *
 * @author chengzw
 * @site https://www.jeequan.com
 * @date 2023/03/17 17:09
 */
@RestController
@RequestMapping("/api/anon/report")
public class ReportController extends CommonCtrl {

    private final ReportingService reportingService;

    public ReportController(ReportingService reportingService) {
        this.reportingService = reportingService;
    }

    //下载文件名
    private final String FILENAMEPREFIX = "集团物业入驻企业%s月份费用账单[%s]";

    //企业账单模板文件
    private final String TENANTTEMPLATE = "TEMPLATE_TENANT-1-1.xlsx";

    //商户账单模板文件
    private final String MECHANTTEMPLATE = "TEMPLATE_TENANT-2.xlsx";

    //订单流水模块文件
    private final String ORDERFLOWTEMPLATE = "TEMPLATE_ORDER_FLOW.xlsx";


    @RequestMapping(value = "/tenant/{month}", method = RequestMethod.GET)
    public void excelExport(HttpServletResponse response, @PathVariable int month) throws IOException {

        List<AccountForTenantRq> accountForTenantRqs = reportingService.getAccountForTenants(month);
        if (accountForTenantRqs.size() > 0) {
            accountForTenantRqs = accountForTenantRqs.stream().sorted(Comparator.comparing(AccountForTenantRq::getGroupName)).collect(Collectors.toList());
        }
        List<String> sheetNames = accountForTenantRqs.stream().map(item -> item.getGroupName()).collect(Collectors.toList());

        //数据分页转换
        List<Page> page1 = PageService.individual(accountForTenantRqs, sheetNames);
        List<Page> page2 = page1.stream().filter(item -> ((AccountForTenantRq) item.getOnlyOne()).getAccountForDepartmentRqs().stream()
                        .filter(s -> s.getOrgAccountDetailMap().size() > 0 || s.getTypeDetailMap().size() > 0)
                        .findAny().isPresent())
                .collect(Collectors.toList());

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("month", month);
        model.put("pages_one", page1);
        model.put("pages_two", page2);
        model.put("sheetNames_1", getSheetMain(page1, month));
        model.put("sheetNames_2", getSheetSlave(page2, month));

        // 模板位置，输出流
        ServletOutputStream outputStream = null;
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("templates/" + TENANTTEMPLATE);

        try {

            SimpleDateFormat simpleDate = new SimpleDateFormat("yyyyMMdd");
            Date date = new Date();
            String str = simpleDate.format(date);
            Random rand = new Random();
            int rannum = (int) (rand.nextDouble() * (99999 - 10000 + 1) + 10000);

            String fileName = String.format(FILENAMEPREFIX, month, str + rannum) + ".xlsx";

            //设置响应头信息header，下载时以文件附件下载
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            outputStream = response.getOutputStream();

            JxlsUtils.exportExcel(in, outputStream, model);

        }finally {

            if (in != null) {
                in.close();
            }
            if (outputStream != null) {
                outputStream.flush();
                outputStream.close();
            }
        }
    }

    /**
     * 设置excel的主表sheetname
     */
    public ArrayList<String> getSheetMain(List<Page> page, int month) {
        ArrayList<String> al = new ArrayList<String>();
        for (int i = 0; i < page.size(); i++) {
            al.add(String.format("%s-%d月汇总账单", page.get(i).getSheetName(), month));
        }
        return al;
    }


    /**
     * 设置excel的副表sheetname
     */
    public ArrayList<String> getSheetSlave(List<Page> page, int month) {
        ArrayList<String> al = new ArrayList<String>();
        for (int i = 0; i < page.size(); i++) {
            al.add(String.format("%s-%d月部门费用单", page.get(i).getSheetName(), month));
        }
        return al;
    }

}