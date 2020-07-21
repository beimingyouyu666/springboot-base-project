package com.yangk.baseproject.common.util.excelutil.poi;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.util.Map;

/**
 */
public class ExportExcelModuleWorkbookDTO {

    /** 表格对象 */
    private SXSSFWorkbook workbook;
    /** 表格下所有表 */
    private Map<Integer, ExportExcelModuleSheetDTO> sheetMap;
    /** 拥有的表格数 */
    private int sheetNum = 1;

    public SXSSFWorkbook getWorkbook() {
        return workbook;
    }

    public void setWorkbook(SXSSFWorkbook workbook) {
        this.workbook = workbook;
    }

    public Map<Integer, ExportExcelModuleSheetDTO> getSheetMap() {
        return sheetMap;
    }

    public void setSheetMap(Map<Integer, ExportExcelModuleSheetDTO> sheetMap) {
        this.sheetMap = sheetMap;
    }

    public int getSheetNum() {
        return sheetNum;
    }

    public void setSheetNum(int sheetNum) {
        this.sheetNum = sheetNum;
    }

    @Override
    public String toString() {
        return "ExportExcelModuleWorkbookDTO{" +
                "workbook=" + workbook +
                ", sheetMap=" + sheetMap +
                ", sheetNum=" + sheetNum +
                '}';
    }
}
