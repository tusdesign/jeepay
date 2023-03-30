package com.jeequan.jeepay.mgr.ctrl.anon;

import com.jeequan.jeepay.mgr.rqrs.AccountForDepartRq;
import com.jeequan.jeepay.mgr.service.ReportingService;
import org.apache.commons.lang3.StringUtils;
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
import java.util.List;
import java.util.UUID;

/*
 * 报表下载
 *
 * @author czw
 * @site https://www.jeequan.com
 * @date 2023/03/17 17:09
 */
@Controller
@RequestMapping("/api/anon/excel")
public class ReportController {

    @Autowired
    private ReportingService reportingService;

    @RequestMapping(value = "/export/{month}", method = RequestMethod.GET)
    public void export(HttpServletResponse response, @PathVariable int month) throws Exception {

        List<AccountForDepartRq> accountRQList = reportingService.getAccountList(month);
        Workbook workbook = new XSSFWorkbook();

        accountRQList.forEach(item -> {

            Sheet sheet = workbook.createSheet("账单-" + (StringUtils.isEmpty(item.getCompanyName()) ? "未知" : item.getCompanyName()));

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

            int rowIndex = 0;
            Row row = sheet.createRow(rowIndex++);
            Cell cell = row.createCell(0);
            cell.setCellValue(String.format("久旺物业入驻企业%s月度账单", month));
            cell.setCellStyle(getStyle(false, 2, workbook));

            CellRangeAddress range_2 = new CellRangeAddress(1, 1, 1, 3);
            sheet.addMergedRegion(range_2);

            CellRangeAddress range_3 = new CellRangeAddress(2, 2, 1, 3);
            sheet.addMergedRegion(range_3);

            Row row1 = sheet.createRow(rowIndex++);
            Cell cell1 = row1.createCell(0);
            cell1.setCellValue("企业");
            Cell cell2 = row1.createCell(1);
            cell2.setCellValue(item.getCompanyName());
            cell2.setCellStyle(getStyle(false, 0, workbook));

            Row row2 = sheet.createRow(rowIndex++);
            Cell cell3 = row2.createCell(0);
            cell3.setCellValue("总账");
            cell2.setCellStyle(getStyle(false, 0, workbook));

            Cell cell4 = row2.createCell(1);
            cell4.setCellValue(item.getTotalAccountForCompany());
            cell2.setCellStyle(getStyle(false, -1, workbook));

            CellStyle cellStylex = setDefaultStyle2(workbook);
            setRegionStyle(sheet, range_2, cellStylex);

            CellStyle cellStyley = setDefaultStyle2(workbook);
            setRegionStyle(sheet, range_3, cellStyley);

            CellRangeAddress range_4 = new CellRangeAddress(3, 3, 0, 3);
            sheet.addMergedRegion(range_4);
            Row range_4_row = sheet.createRow(rowIndex++);
            Cell range_4_cell = range_4_row.createCell(0);
            range_4_cell.setCellValue("货币形式：CNY   单位：元");
            range_4_cell.setCellStyle(getStyle(false, 0, workbook));

            CellStyle cellStyle = setDefaultStyle(workbook);
            setRegionStyle(sheet, range_4, cellStyle);

            for (AccountForDepartRq.DepartMentAccountRQ rq : item.getDepartMentAccountRQList()) {

                int r = rowIndex++;

                Row for_row = sheet.createRow(r);
                Cell for_cell = for_row.createCell(0);
                for_cell.setCellValue(rq.getDeptName() + "：" +String.format("%.2f", rq.getTotalAccountForDept()));
                for_cell.setCellStyle(getStyle(false, 2, workbook));

                CellRangeAddress range = new CellRangeAddress(r, r, 0, 3);
                sheet.addMergedRegion(range);

                for (String key : rq.getAccountDetail().keySet()) {
                    Double value = rq.getAccountDetail().get(key);

                    Row app_row = sheet.createRow(rowIndex++);

                    Cell acell = app_row.createCell(0);
                    acell.setCellValue("费用类别");
                    acell.setCellStyle(getStyle(true, 0, workbook));

                    Cell key_cell = app_row.createCell(2);
                    key_cell.setCellValue(key);
                    key_cell.setCellStyle(getStyle(false, 0, workbook));

                    Cell value_cell = app_row.createCell(3);
                    value_cell.setCellValue(value.doubleValue());
                    value_cell.setCellStyle(getStyle(false, -1, workbook));
                }

                CellRangeAddress range_type = new CellRangeAddress(r + 1, rq.getAccountDetail().size() + r, 0, 1);
                sheet.addMergedRegion(range_type);

                CellStyle cellStylen = setDefaultStyle2(workbook);
                setRegionStyle(sheet, range_type, cellStylen);
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

        if (styleIndex == -1) {
            //style.setDataFormat(book.createDataFormat().getFormat("##.##"));
            style.setDataFormat(book.createDataFormat().getFormat("0.00"));
        }

        style.setBorderBottom(BorderStyle.THIN); //下边框
        style.setBorderLeft(BorderStyle.THIN);//左边框
        style.setBorderTop(BorderStyle.THIN);//上边框
        style.setBorderRight(BorderStyle.THIN);//右边框

        Font font = book.createFont();
        font.setFontName("新宋体");
        font.setFontHeightInPoints((short) 12);//设置字体大小
        style.setFont(font);
        return style;
    }


    /**
     * 为合并的单元格设置样式（可根据需要自行调整）
     */
    @SuppressWarnings("deprecation")
    public void setRegionStyle(Sheet sheet, CellRangeAddress region, CellStyle cs) {
        for (int i = region.getFirstRow(); i <= region.getLastRow(); i++) {
            Row row = sheet.getRow(i);
            if (null == row) row = sheet.createRow(i);
            for (int j = region.getFirstColumn(); j <= region.getLastColumn(); j++) {
                Cell cell = row.getCell(j);
                if (null == cell) cell = row.createCell(j);
                cell.setCellStyle(cs);
            }
        }
    }

    /**
     * 带边框的样式+
     */
    public CellStyle setDefaultStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        // 边框
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        return cellStyle;
    }

    public CellStyle setDefaultStyle2(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        // 边框
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        return cellStyle;
    }
}