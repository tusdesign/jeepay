package com.jeequan.jeepay.mgr.config;

import com.jeequan.jeepay.core.exception.BizException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
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

    //抽象方法，提供给子类进行实现，遍历写入数据到excel
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
            //写入文件
            SimpleDateFormat simpleDate = new SimpleDateFormat("yyyyMMdd");
            Date date = new Date();
            String str = simpleDate.format(date);

            Random rand = new Random();
            int ranNum = (int) (rand.nextDouble() * (99999 - 10000 + 1) + 10000);

            String fileName = isExportZip ? exportFileName + str + ranNum + ".zip" : exportFileName + str + ranNum + ".xlsx";

            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(fileName.replaceAll(" ", "").getBytes("utf-8"), "iso8859-1"));
            os = new BufferedOutputStream(response.getOutputStream());

            //如果true则进行zip压缩处理
            if (isExportZip) {
                zos = new ZipOutputStream(os);
                ZipEntry zipEntry = new ZipEntry(new String((exportFileName + ".xlsx").replaceAll(" ", "")));
                zos.putNextEntry(zipEntry);
            }

            SXSSFWorkbook wb = new SXSSFWorkbook();
            wb.setCompressTempFiles(false);
            sheet = wb.createSheet("数据列表");

            //写入表头，Rows从0开始.
            Row row = sheet.createRow(0);
            for (int cellNumber = 0; cellNumber < totalCellNumber; cellNumber++) {
                Cell cell = row.createCell(cellNumber);
                cell.setCellValue(headerArray.get(cellNumber));
            }

            //写入数据
            //调用具体的实现子类进行遍历并写入excel
            tryFetchDataAndWriteToExcel();

            //Write excel to a file
            if (isExportZip) {
                wb.write(zos);
            } else {
                wb.write(os);
            }

            if (wb != null) {
                wb.dispose();
            }
            wb.close();
        } finally {
            if (isExportZip) {
                try {
                    if (zos != null) { zos.flush(); zos.close();}
                } catch (IOException e1) {
                    throw new BizException("下载订单流水出错:" + e1.getMessage());
                }
            } else {
                try {
                    if (os != null){  os.flush();os.close();}
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