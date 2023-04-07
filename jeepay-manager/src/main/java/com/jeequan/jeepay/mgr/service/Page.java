package com.jeequan.jeepay.mgr.service;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
public class Page {
    /**
     * 页面信息
     */
    private String sheetName; // 每个sheet名字
    private String currentPage; // 当前页
    private String tolalPage; // 总页
    private List<?> data; //每页数据集
    private Object onlyOne; //一页一条数据

    /**
     * 页面遍历的数据 List 的泛型自行设置，如果所有数据都来着同一个类就写那个类，
     * 不是同一个类有继承就写继承类的泛型，没有就写问号。
     */

    public Page() {
    }

    public Page(String sheetName, String currentPage, String tolalPage, List<?> data) {
        super();
        this.sheetName = sheetName;
        this.currentPage = currentPage;
        this.tolalPage = tolalPage;
        this.data = data;
    }
    /**
     * 省略构造器和其他get/set方法
     * @return Object
     */
    public Object getOnlyOne() {
        return onlyOne;
    }
    public void setOnlyOne(Object onlyOne) {
        this.onlyOne = onlyOne;
    }

}
