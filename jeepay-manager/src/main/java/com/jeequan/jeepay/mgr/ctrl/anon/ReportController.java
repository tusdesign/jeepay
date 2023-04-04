package com.jeequan.jeepay.mgr.ctrl.anon;

import com.jeequan.jeepay.mgr.rqrs.AccountForDepartRq;
import com.jeequan.jeepay.mgr.rqrs.AccountForTenantRq;
import com.jeequan.jeepay.mgr.service.Page;
import com.jeequan.jeepay.mgr.service.PageService;
import com.jeequan.jeepay.mgr.service.ReportingService;
import com.jeequan.jeepay.mgr.util.JxlsUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.ss.util.CellRangeAddress;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
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
public class ReportController {

    private final ReportingService reportingService;

    public ReportController(ReportingService reportingService) {
        this.reportingService = reportingService;
    }

    //下载文件名
    private final String FILENAMEPREFIX = "玖旺物业入驻企业%s月份费用账单[%s]";

    //企业账单模板文件
    private final String TENANTTEMPLATE = "TEMPLATE_TENANT-1.xlsx";

    //商户账单模板文件
    private final String MECHANTTEMPLATE = "TEMPLATE_TENANT-2.xlsx";

    //订单流水模块文件
    private final String ORDERFLOWTEMPLATE = "TEMPLATE_ORDER_FLOW.xlsx";


//    @RequestMapping(value = "/export/{month}", method = RequestMethod.GET)
//    public void export(HttpServletResponse response, @PathVariable int month) throws Exception {
//
//        List<AccountForDepartRq> accountRQList = reportingService.getAccountList(month);
//        Workbook workbook = new XSSFWorkbook();
//
//        accountRQList.forEach(item -> {
//
//            Sheet sheet = workbook.createSheet("账单-" + (StringUtils.isEmpty(item.getCompanyName()) ? "未知" : item.getCompanyName()));
//
//            for (int i = 0; i < 4; i++) {
//                if (i == 0) {
//                    sheet.setColumnWidth(i, 20 * 256);
//                } else {
//                    sheet.setColumnWidth(i, 30 * 256);
//                    //sheet.autoSizeColumn(i,true);
//                }
//            }
//            CellRangeAddress range_1 = new CellRangeAddress(0, 0, 0, 3);
//            sheet.addMergedRegion(range_1);
//
//            int rowIndex = 0;
//            Row row = sheet.createRow(rowIndex++);
//            Cell cell = row.createCell(0);
//            cell.setCellValue(String.format("久旺物业入驻企业%s月度账单", month));
//            cell.setCellStyle(getStyle(false, 2, workbook));
//
//            CellRangeAddress range_2 = new CellRangeAddress(1, 1, 1, 3);
//            sheet.addMergedRegion(range_2);
//
//            CellRangeAddress range_3 = new CellRangeAddress(2, 2, 1, 3);
//            sheet.addMergedRegion(range_3);
//
//            Row row1 = sheet.createRow(rowIndex++);
//            Cell cell1 = row1.createCell(0);
//            cell1.setCellValue("企业");
//            Cell cell2 = row1.createCell(1);
//            cell2.setCellValue(item.getCompanyName());
//            cell2.setCellStyle(getStyle(false, 0, workbook));
//
//            Row row2 = sheet.createRow(rowIndex++);
//            Cell cell3 = row2.createCell(0);
//            cell3.setCellValue("总账");
//            cell2.setCellStyle(getStyle(false, 0, workbook));
//
//            Cell cell4 = row2.createCell(1);
//            cell4.setCellValue(item.getTotalAccountForCompany());
//            cell2.setCellStyle(getStyle(false, -1, workbook));
//
//            CellStyle cellStylex = setDefaultStyle2(workbook);
//            setRegionStyle(sheet, range_2, cellStylex);
//
//            CellStyle cellStyley = setDefaultStyle2(workbook);
//            setRegionStyle(sheet, range_3, cellStyley);
//
//            CellRangeAddress range_4 = new CellRangeAddress(3, 3, 0, 3);
//            sheet.addMergedRegion(range_4);
//            Row range_4_row = sheet.createRow(rowIndex++);
//            Cell range_4_cell = range_4_row.createCell(0);
//            range_4_cell.setCellValue("货币形式：CNY   单位：元");
//            range_4_cell.setCellStyle(getStyle(false, 0, workbook));
//
//            CellStyle cellStyle = setDefaultStyle(workbook);
//            setRegionStyle(sheet, range_4, cellStyle);
//
//            for (AccountForDepartRq.DepartMentAccountRQ rq : item.getDepartMentAccountRQList()) {
//
//                int r = rowIndex++;
//
//                Row for_row = sheet.createRow(r);
//                Cell for_cell = for_row.createCell(0);
//                for_cell.setCellValue(rq.getDeptName() + "：" + String.format("%.2f", rq.getTotalAccountForDept()));
//                for_cell.setCellStyle(getStyle(false, 2, workbook));
//
//                CellRangeAddress range = new CellRangeAddress(r, r, 0, 3);
//                sheet.addMergedRegion(range);
//
//                for (String key : rq.getAccountDetail().keySet()) {
//                    Double value = rq.getAccountDetail().get(key);
//
//                    Row app_row = sheet.createRow(rowIndex++);
//
//                    Cell acell = app_row.createCell(0);
//                    acell.setCellValue("费用类别");
//                    acell.setCellStyle(getStyle(true, 0, workbook));
//
//                    Cell key_cell = app_row.createCell(2);
//                    key_cell.setCellValue(key);
//                    key_cell.setCellStyle(getStyle(false, 0, workbook));
//
//                    Cell value_cell = app_row.createCell(3);
//                    value_cell.setCellValue(value.doubleValue());
//                    value_cell.setCellStyle(getStyle(false, -1, workbook));
//                }
//
//                CellRangeAddress range_type = new CellRangeAddress(r + 1, rq.getAccountDetail().size() + r, 0, 1);
//                sheet.addMergedRegion(range_type);
//
//                CellStyle cellStylen = setDefaultStyle2(workbook);
//                setRegionStyle(sheet, range_type, cellStylen);
//            }
//
//        });
//
//        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//        response.setCharacterEncoding("utf-8");
//        String name = new String(UUID.randomUUID().toString().getBytes("GBK"), "ISO8859_1") + ".xlsx";
//        response.addHeader("Content-Disposition", "attachment;filename=" + name);
//        response.addHeader("Pargam", "no-cache");
//        response.addHeader("Cache-Control", "no-cache");
//        ServletOutputStream out = response.getOutputStream();
//        workbook.write(out);
//        out.flush();
//        out.close();
//    }


    @RequestMapping(value = "/tenant/{month}", method = RequestMethod.GET)
    public void excelExport(HttpServletResponse response, @PathVariable int month) throws Exception {

        List<AccountForTenantRq> accountForTenantRqs = reportingService.getAccountForTenants(month);
        if (accountForTenantRqs.size() > 0) {
            accountForTenantRqs = accountForTenantRqs.stream().sorted(Comparator.comparing(AccountForTenantRq::getGroupName)).collect(Collectors.toList());
        }
        List<String> sheetNames = accountForTenantRqs.stream().map(item -> item.getGroupName()).collect(Collectors.toList());

        //数据分页转换
        List<Page> page1 = PageService.individual(accountForTenantRqs, sheetNames);
        List<Page> page2 = PageService.individual(accountForTenantRqs, sheetNames);
        page2 = page2.stream().filter(item -> ((AccountForTenantRq) item.getOnlyOne()).getAccountForDepartmentRqs().stream()
                        .filter(s->s.getOrgAccountDetailMap().size()>0)
                        .findAny().isPresent())
                .collect(Collectors.toList());

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("month", month);
        model.put("pages_one", page1);
        model.put("pages_two", page2);
        model.put("sheetNames_1", getSheetMain(page1, month));
        model.put("sheetNames_2", getSheetSlave(page2, month));

        // 模板位置，输出流
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("templates/"+TENANTTEMPLATE);
        ServletOutputStream outputStream=null;

        try {

            response.setContentType("application/octet-stream;charset=UTF-8");
            //设置响应头信息header，下载时以文件附件下载
            SimpleDateFormat simpleDate = new SimpleDateFormat("yyyyMMdd");
            Date date = new Date();
            String str = simpleDate.format(date);
            Random rand = new Random();
            int rannum = (int) (rand.nextDouble() * (99999 - 10000 + 1) + 10000);
            String fileName = String.format(FILENAMEPREFIX, month, str + rannum) + ".xlsx";
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);

            outputStream = response.getOutputStream();

            JxlsUtils.exportExcel(in, outputStream, model);

            outputStream.flush();
            outputStream.close();

        }catch (Exception e){
            if(in!=null){
                in.close();
            }
            if(outputStream!=null){
                outputStream.flush();
                outputStream.close();
            }
            e.printStackTrace();
        }
        System.out.println("完成");
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