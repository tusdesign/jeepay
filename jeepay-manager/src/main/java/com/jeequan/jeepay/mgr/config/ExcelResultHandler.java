package com.jeequan.jeepay.mgr.config;

import com.jeequan.jeepay.core.exception.BizException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.Reflector;
import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import sun.reflect.MethodAccessor;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;


@Slf4j
public abstract class ExcelResultHandler<T> implements ResultHandler<T> {

    private AtomicInteger currentRowNumber = new AtomicInteger(0);//记录当前excel行号，从0开始
    private Sheet sheet = null;

    private List<String> headerArray; //表头
    private List<String> fieldArray; //对应的字段

    //定义totalCellNumber变量，
    private int totalCellNumber;

    //定义导出成zip格式的还是原始的xlsx格式
    private boolean isExportZip = true;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //定义要导出的excel文件名,不带xlsx后缀,默认为uuID,也可以通过构造函数传进来进行改变。
    private String exportFileName = UUID.randomUUID().toString().replace("-", "");

    public ExcelResultHandler(List<String> headerArray, List<String> fieldArray) {

        this.headerArray = headerArray;
        this.fieldArray = fieldArray;
        this.totalCellNumber = headerArray.size();
    }

    public ExcelResultHandler(List<String> headerArray, List<String> fieldArray, boolean isExportZip) {

        this(headerArray, fieldArray);
        this.isExportZip = isExportZip;

    }

    public ExcelResultHandler(List<String> headerArray, List<String> fieldArray, String exportFileName) {

        this(headerArray, fieldArray);
        this.exportFileName = exportFileName;

    }

    public ExcelResultHandler(List<String> headerArray, List<String> fieldArray, String exportFileName, boolean isExportZip) {

        this(headerArray, fieldArray, exportFileName);
        this.isExportZip = isExportZip;

    }

    //出象方法，提供给子类进行实现，遍历写入数据到excel
    public abstract void tryFetchDataAndWriteToExcel();

    @SneakyThrows
    public void handleResult(ResultContext<? extends T> resultContext) {
        Object aRowData = resultContext.getResultObject();
        callBackWriteRowDataToExcel(aRowData);
    }

    /**
     * 导出
     */
    public void ExportExcel() throws IOException {

        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        ZipOutputStream zos = null;
        OutputStream os = null;

        try {

            SimpleDateFormat simpleDate = new SimpleDateFormat("yyyyMMdd");
            Date date = new Date();
            String str = simpleDate.format(date);
            Random rand = new Random();
            int rannum = (int) (rand.nextDouble() * (99999 - 10000 + 1) + 10000);

            response.setContentType("application/x-download");
            if (isExportZip) {
                response.setHeader("Content-Disposition", "attachment;filename=" + new String((exportFileName + str + rannum + ".zip").replaceAll(" ", "").getBytes("GBK"), "iso8859-1"));
            } else {
                response.addHeader("Content-Disposition", "attachment;filename=" + new String((exportFileName + str + rannum + ".xlsx").replaceAll(" ", "").getBytes("GBK"), "iso8859-1"));
            }
            os = new BufferedOutputStream(response.getOutputStream());

            //如果设置成了导出成Zip则进行Zip的处理
            if (isExportZip) {
                zos = new ZipOutputStream(os);
                ZipEntry zipEntry = new ZipEntry(new String((exportFileName + ".xlsx").replaceAll(" ", "")));
                zos.putNextEntry(zipEntry);
            }

            Workbook wb = new XSSFWorkbook();
            sheet = wb.createSheet("Sheet 1");
            Row row = sheet.createRow(0);

            for (int cellNumber = 0; cellNumber < totalCellNumber; cellNumber++) {
                Cell cell = row.createCell(cellNumber);
                cell.setCellValue(headerArray.get(cellNumber)); //写入表头数据
            }

            //获取数据进行遍历并写入excel
            tryFetchDataAndWriteToExcel();

            //最后打印一下最终写入的行数
            //log.info("--------->>>> write to excel size now is {}", currentRowNumber.get());

            if (isExportZip) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                wb.write(bos);
                bos.writeTo(zos);
            } else {
                wb.write(os);
            }
            wb.close();
        } finally {

            if (isExportZip) {
                try {
                    if (zos != null) zos.closeEntry();
                    zos.close();
                } catch (IOException e1) {
                    throw new BizException("下载订单流水出错:" + e1.getMessage());
                }
            } else {
                try {
                    if (os != null) os.close();
                } catch (IOException e1) {
                    throw new BizException("下载订单流水出错:" + e1.getMessage());
                }
            }

        }
    }

    public void callBackWriteRowDataToExcel(Object RowData) throws IllegalAccessException {
        Field[] fields = RowData.getClass().getDeclaredFields();

        currentRowNumber.incrementAndGet();
        Row row = sheet.createRow(currentRowNumber.get());
        for (int cellNumber = 0; cellNumber < totalCellNumber; cellNumber++) {

            Object value = null;
            if (RowData instanceof Map) {
                value = ((Map) RowData).get(fieldArray.get(cellNumber));
            } else {
                String a = fieldArray.get(cellNumber);
                if (Arrays.stream(fields).anyMatch(item -> item.getName().equals(a))) {
                    Field field = Arrays.stream(fields).filter(item -> item.getName().equals(a)).findFirst().get();
                    field.setAccessible(true);
                    value = field.get(RowData);
                }
            }

            Cell cell = row.createCell(cellNumber);

            if (value != null && value instanceof Date) {
                cell.setCellValue(sdf.format(value));
            } else {
                cell.setCellValue(value == null ? "" : value.toString());
            }
        }
//        //测试:每写入5000条就打印一下
//        if (currentRowNumber.get() % 5000 == 0) {
//            log.info("--------->>>> write to excel size now is {}", currentRowNumber.get());
//        }
    }

}