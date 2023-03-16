package com.jeequan.jeepay.mgr.ctrl.anon;

import cn.hutool.core.date.DateTime;
import com.jeequan.jeepay.mgr.rqrs.AccountRQ;
import com.jeequan.jeepay.mgr.service.ReportingService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/api/anon/excel")
public class ReportController {

    @Autowired
    private ReportingService reportingService;

    @RequestMapping(value="/export/{month}",method = RequestMethod.GET)
    public void export(HttpServletResponse response,@PathVariable int month) throws Exception {

        List<AccountRQ> accountRQList= reportingService.getAccountList(month);
        Workbook workbook = new XSSFWorkbook();

        accountRQList.forEach(item->{

            Sheet sheet = workbook.createSheet( "账单统计"+System.currentTimeMillis());

            for (int i = 0; i < 4; i++) {
                if (i == 0) {
                    sheet.setColumnWidth(i, 20 * 256);
                } else {
                    sheet.setColumnWidth(i, 30 * 256);
                    //sheet.autoSizeColumn(i,true);
                }
            }
            CellRangeAddress range_1 = new CellRangeAddress(0, 0, 0, 3);
            sheet.addMergedRegion(range_1);

            int rowIndex=0;
            Row row = sheet.createRow(rowIndex++);
            Cell cell = row.createCell(0);
            cell.setCellValue(String.format("久旺物业入驻企业%s月度账单",month));

            CellRangeAddress range_2 = new CellRangeAddress(1, 1, 1, 3);
            sheet.addMergedRegion(range_2);

            CellRangeAddress range_3 = new CellRangeAddress(2, 2, 1, 3);
            sheet.addMergedRegion(range_3);

            Row row1 = sheet.createRow(rowIndex++);
            Cell cell1 = row1.createCell(0);
            cell1.setCellValue("企业");
            Cell cell2 = row1.createCell(1);
            cell2.setCellValue(item.getCompanyName());

            Row row2 = sheet.createRow(rowIndex++);
            Cell cell3 = row2.createCell(0);
            cell3.setCellValue("总账");
            Cell cell4 = row2.createCell(1);
            cell4.setCellValue(item.getTotalAccountForCompany());

            CellRangeAddress range_4 = new CellRangeAddress(3, 3, 0, 3);
            sheet.addMergedRegion(range_4);
            Row range_4_row = sheet.createRow(rowIndex++);
            Cell range_4_cell = range_4_row.createCell(0);
            range_4_cell.setCellValue("货币:人民币 单位：元");

            for(AccountRQ.DepartMentAccountRQ rq : item.getDepartMentAccountRQList()){

                int r=rowIndex++;
                CellRangeAddress range = new CellRangeAddress(r, r, 0, 3);
                sheet.addMergedRegion(range);

                Row for_row = sheet.createRow(r);
                Cell for_cell = for_row.createCell(0);
                for_cell.setCellValue(rq.getDeptName()+"："+rq.getTotalAccountForDept());

                for(String key : rq.getAccountDetail().keySet()){
                    Double value = rq.getAccountDetail().get(key);

                    Row app_row = sheet.createRow(rowIndex++);

                    Cell acell= app_row.createCell(0);
                    acell.setCellValue("费用类别");

                    Cell key_cell= app_row.createCell(2);
                    key_cell.setCellValue(key);

                    Cell value_cell= app_row.createCell(3);
                    value_cell.setCellValue(value);
                }

                CellRangeAddress range_type = new CellRangeAddress(r+1, rq.getAccountDetail().size()+r, 0, 1);
                sheet.addMergedRegion(range_type);
            }

        });

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String name = new String(UUID.randomUUID().toString().getBytes("GBK"), "ISO8859_1") + ".xlsx";
        response.addHeader("Content-Disposition", "attachment;filename=" + name);
        response.addHeader("Pargam", "no-cache");
        response.addHeader("Cache-Control", "no-cache");
        ServletOutputStream out = response.getOutputStream();
        workbook.write(out);
        out.flush();
        out.close();
    }


    private CellStyle getStyle(boolean isLeft, int styleIndex, Workbook book) {
        CellStyle style = book.createCellStyle();
        if (isLeft) {
            style.setAlignment(HorizontalAlignment.LEFT);
        } else {
            style.setAlignment(HorizontalAlignment.CENTER);
        }
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        if (styleIndex == 1) { //标绿色
            style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        } else if (styleIndex == 2) {
            style.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        } else if (styleIndex == 3) {
            style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        } else if (styleIndex == 4) {
            style.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        } else if (styleIndex == 5) {
            style.setFillForegroundColor(IndexedColors.CORAL.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        Font font = book.createFont();
        font.setFontName("宋体");
        font.setFontHeightInPoints((short) 12);//设置字体大小
        style.setFont(font);
        return style;
    }
}