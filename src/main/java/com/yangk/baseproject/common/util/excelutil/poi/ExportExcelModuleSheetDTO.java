package com.yangk.baseproject.common.util.excelutil.poi;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.streaming.SXSSFSheet;

import java.util.Map;

/**
 */
public class ExportExcelModuleSheetDTO {

    /** 表格下所有表 */
    private SXSSFSheet sheet;
    /** 数据填写开始行数 */
    private int dataWriteStartRow;
    /** 字段标题键, key:列序号 */
    private Map<Integer, String> keyMap;
    /** 格式，空代表没有设定特殊格式, key:列序号 */
    private Map<Integer, CellStyle> styleMap;
    /** 类型，空代表没有设置特殊类型，可默认字符串, key:列序号 */
    private Map<Integer, CellType> typeMap;

    public SXSSFSheet getSheet() {
        return sheet;
    }

    public void setSheet(SXSSFSheet sheet) {
        this.sheet = sheet;
    }

    public int getDataWriteStartRow() {
        return dataWriteStartRow;
    }

    public void setDataWriteStartRow(int dataWriteStartRow) {
        this.dataWriteStartRow = dataWriteStartRow;
    }

    public Map<Integer, String> getKeyMap() {
        return keyMap;
    }

    public void setKeyMap(Map<Integer, String> keyMap) {
        this.keyMap = keyMap;
    }

    public Map<Integer, CellStyle> getStyleMap() {
        return styleMap;
    }

    public void setStyleMap(Map<Integer, CellStyle> styleMap) {
        this.styleMap = styleMap;
    }

    public Map<Integer, CellType> getTypeMap() {
        return typeMap;
    }

    public void setTypeMap(Map<Integer, CellType> typeMap) {
        this.typeMap = typeMap;
    }

    @Override
    public String toString() {
        return "ExportExcelModuleSheetDTO{" +
                "sheet=" + sheet +
                ", dataWriteStartRow=" + dataWriteStartRow +
                ", keyMap=" + keyMap +
                ", styleMap=" + styleMap +
                ", typeMap=" + typeMap +
                '}';
    }
}
