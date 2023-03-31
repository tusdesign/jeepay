package com.jeequan.jeepay.mgr.service;

import cn.hutool.core.collection.CollectionUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 此类用于分页,数据转化成分页对象
 *
 * @author chengzhengwen
 */
@Service
public class PageService {

    //分页大小
    static int pageSize = 20; // 每页记录数

    /**
     * 根据每页显示多少条数据计算总页数
     *
     * @param dataList 数据库查询的数据
     */
    public static int countPages(List<?> dataList) {
        // 总记录数
        int recordCount = dataList.size();
        return (recordCount + pageSize - 1) / pageSize;
    }

    /**
     * 将数据集转化成分页对象
     *
     * @param dataList
     * @return List<Page>
     */
    public static List<Page> byPage(List<?> dataList, List<String> sheetNames) {

        // 总页数
        int pageCount;
        //读取到接收的哪一条数据
        int nowDataListPoint = 0;

        // 计算页码
        pageCount = countPages(dataList);
        // 页面分页
        List<Page> pageList = new ArrayList<Page>();
        for (int i = 0; i < pageCount; i++) {

            List<Object> pageData = new ArrayList<Object>();
            while (nowDataListPoint < dataList.size()) {
                pageData.add(dataList.get(nowDataListPoint));
                nowDataListPoint += 1;
                if (nowDataListPoint != 0 && nowDataListPoint % pageSize == 0) {
                    break;
                }
            }
            String sheetName = "page_" + (i + 1);
            if (CollectionUtil.isEmpty(sheetNames) && sheetNames.size() > 0) {
                sheetName = sheetNames.get(i);
            }
            Page page = new Page(sheetName, (i + 1) + "", pageCount + "", pageData);
            pageList.add(page);
        }
        return pageList;
    }


    /**
     * 每页一条
     *
     * @param dataList
     * @return List<Page>
     */
    public static List<Page> individual(List<?> dataList, List<String> sheetNames) {
        List<Page> pages = new ArrayList<Page>();
        for (int i = 0; i < dataList.size(); i++) {
            Page p = new Page();
            p.setOnlyOne(dataList.get(i));

            String sheetName = "page_" + (i + 1);
            if (CollectionUtil.isEmpty(sheetNames) && sheetNames.size() > 0) {
                sheetName = sheetNames.get(i);
            }
            p.setSheetName(sheetName);
            pages.add(p);
        }
        return pages;
    }
}