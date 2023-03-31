package com.jeequan.jeepay.mgr.util;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlEngine;
import org.jxls.command.CellDataUpdater;
import org.jxls.common.CellData;
import org.jxls.common.CellRef;
import org.jxls.common.Context;
import org.jxls.expression.JexlExpressionEvaluator;
import org.jxls.formula.StandardFormulaProcessor;
import org.jxls.transform.Transformer;
import org.jxls.transform.poi.PoiTransformer;
import org.jxls.util.JxlsHelper;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;



public class JxlsUtils {


    public static class TotalCellUpdater implements CellDataUpdater {

        public void updateCellData(CellData cellData, CellRef targetCell, Context context) {
            if( cellData.isFormulaCell() && cellData.getFormula().equals("SUM(C4)")){
                String resultFormula = String.format("SUM(C4:E%d)", targetCell.getRow());
                cellData.setEvaluationResult(resultFormula);
            }
        }

    }
    public static void exportExcel(InputStream is, OutputStream os, Map<String, Object> model) throws IOException {

        Context context = PoiTransformer.createInitialContext();
        if (model != null) {
            for (String key : model.keySet()) {
                context.putVar(key, model.get(key));
            }
        }

        JxlsHelper jxlsHelper = JxlsHelper.getInstance();
        Transformer transformer = jxlsHelper.createTransformer(is, os);
        //获得配置
        JexlExpressionEvaluator evaluator = (JexlExpressionEvaluator) transformer.getTransformationConfig().getExpressionEvaluator();

        //设置静默模式，不报警告
        //evaluator.getJexlEngine().setSilent(true);
        Map<String, Object> funcs = new HashMap<String, Object>();
        funcs.put("utils", new JxlsUtils());

        //添加自定义功能
        JexlEngine customJexlEngine = new JexlBuilder().namespaces(funcs).create();
        evaluator.setJexlEngine(customJexlEngine);

        //必须要这个，否者表格函数统计会错乱
        jxlsHelper.setProcessFormulas(true);
        jxlsHelper.setFormulaProcessor(new StandardFormulaProcessor()).setEvaluateFormulas(true);
        context.putVar("totalCellUpdater", new TotalCellUpdater());
        jxlsHelper.setUseFastFormulaProcessor(false).setEvaluateFormulas(true).processTemplate(context,transformer);
        jxlsHelper.setDeleteTemplateSheet(true);

    }

    public static void exportExcel(File xls, File out, Map<String, Object> model) throws FileNotFoundException, IOException {
        exportExcel(new FileInputStream(xls), new FileOutputStream(out), model);
    }

    public static void exportExcel(String templatePath, OutputStream os, Map<String, Object> model) throws Exception {
        File template = getTemplate(templatePath);
        if (template != null) {
            exportExcel(new FileInputStream(template), os, model);
        } else {
            throw new Exception("Excel 模板未找到。");
        }
    }

    //获取jxls模版文件
    public static File getTemplate(String path) {
        File template = new File(path);
        if (template.exists()) {
            return template;
        }
        return null;
    }

    // 日期格式化
    public String dateFmt(Date date, String fmt) {
        if (date == null) {
            return "";
        }
        try {
            SimpleDateFormat dateFmt = new SimpleDateFormat(fmt);
            return dateFmt.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    // if判断
    public Object ifelse(boolean b, Object o1, Object o2) {
        return b ? o1 : o2;
    }
}