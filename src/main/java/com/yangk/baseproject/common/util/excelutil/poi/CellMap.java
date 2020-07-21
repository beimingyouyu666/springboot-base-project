
package com.yangk.baseproject.common.util.excelutil.poi;

/**
 *
 * @date 2017年11月30日
 */
public class CellMap {

    private String title;// 标题
    private String property;// 属性

    public CellMap(String title, String property) {
        this.title = title;
        this.property = property;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }
}
