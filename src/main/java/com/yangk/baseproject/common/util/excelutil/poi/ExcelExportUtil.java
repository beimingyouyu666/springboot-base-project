
package com.yangk.baseproject.common.util.excelutil.poi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 导出excel工具类
 *
 * @date 2017年11月29日
 */
public class ExcelExportUtil {

    /**
     * 默认缓存中存放的数量
     */
    private static final int DEFAULT_ROWACCESSWINDOWSIZE = 500;

    /**
     * 默认分页大小
     */
    public static final int DEFAULT_PAGE_SIZE = DEFAULT_ROWACCESSWINDOWSIZE;

    /**
     * 序号列属性
     */
    public static final String ROW_NUM_CELL = "rowNumber";

    /**
     * 默认的sheet名称
     */
    public static final String DEFAULT_SHEET_NAME = "sheet1";

    /**
     * 根据excel模板获取工作簿
     *
     * @param input excel文件xlsx格式
     * @return
     * @throws RuntimeException
     */
    public static Workbook getSXSSFWorkbookByTemplate(InputStream input) throws RuntimeException {
        try {
            return new XSSFWorkbook(input);
        } catch (IOException e) {
            throw new RuntimeException("根据模板创建失败，请检查文件路径或格式是否正确");
        } finally {
            IOUtils.close(input);
        }
    }

    /**
     * 根据excel模板获取工作簿
     *
     * @param resource 资源路径
     * @return
     * @throws RuntimeException
     */
    public static Workbook getSXSSFWorkbookByTemplate(String resource) throws RuntimeException {
        return getSXSSFWorkbookByTemplate(ExcelExportUtil.class.getClassLoader().getResourceAsStream(resource));
    }

    /**
     * 获取workbook
     *
     * @return
     */
    public static SXSSFWorkbook getSXSSFWorkbook() {
        return new SXSSFWorkbook(DEFAULT_ROWACCESSWINDOWSIZE);
    }

    /**
     * 获取属性值
     *
     * @param obj
     * @param property
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private static String getPropertyValue(Object obj, String property) throws Exception {
        if (obj instanceof Map) {
            Object val = ((Map<String, Object>) obj).get(property);
            if (val == null) {
                return "";
            }
            return val.toString();
        }
        Object result = null;
        String str = "";
        Class<?> clazz = obj.getClass();
        if (property == null || "".equals(property)) {
            return "";
        }
        Method readMethod = clazz.getMethod("get" + property.substring(0, 1).toUpperCase() + property.substring(1));
        if (readMethod != null) {
            result = readMethod.invoke(obj);
        }
        if (result != null) {
            if (result.getClass() == Date.class) {
                str = DateFormatUtils.format((Date) result, "yyyy-MM-dd hh:mm:ss");
            } else {
                str = result.toString();
            }
        } else {
            str = "";
        }
        return str;
    }

    /**
     * 填充excel数据(会自动填充到末尾一行) 序号从默认从第二行开始数
     *
     * @param workbook
     * @param sheetName
     * @param cellMapList
     * @param dataList
     * @return
     * @throws Exception
     */
    public static int fillExcel_2007_SXSSF(Workbook workbook, String sheetName, List<CellMap> cellMapList,
                                           List<?> dataList) throws Exception {
        return fillExcel_2007_SXSSF(workbook, sheetName, cellMapList, dataList, 0);
    }

    /**
     * 填充excel数据(会自动填充到末尾一行)
     *
     * @param workbook
     * @param sheetName
     * @param cellMapList
     * @param dataList
     * @param serialNumberOffset
     * @return
     * @throws Exception
     */
    public static int fillExcel_2007_SXSSF(Workbook workbook, String sheetName, List<CellMap> cellMapList,
                                           List<?> dataList, int serialNumberOffset)
            throws Exception {
        CellStyle dataCellStyle = workbook.createCellStyle();
//        dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        dataCellStyle.setAlignment(HorizontalAlignment.CENTER);
        Sheet sheet = workbook.getSheet(sheetName);
        int rowIndex = -1;
        if (null == sheet) {
            // sheet第一次生成会进来
            sheet = workbook.createSheet(sheetName);
            rowIndex = 0;
        } else {
            rowIndex = sheet.getLastRowNum() + 1;
        }
        if (dataList == null || dataList.isEmpty()) {
            return 0;
        }
        Row row = null;
        Cell cell = null;
        int rowSize = (dataList == null) ? 0 : dataList.size();
        for (int i = rowIndex; i < rowSize + rowIndex; i++) {
            Object obj = dataList == null || dataList.isEmpty() ? null : dataList.get(i - rowIndex);
            row = sheet.createRow(i);
            if (obj == null) {
                continue;
            }
            for (int j = 0; j < cellMapList.size(); j++) {
                CellMap cellMap = cellMapList.get(j);
                cell = row.createCell(j);
                cell.setCellStyle(dataCellStyle);
                String property = cellMap.getProperty();
                if (property.equals(ROW_NUM_CELL) || StringUtils.isEmpty(property)) {
                    cell.setCellValue(i - serialNumberOffset);
                } else {
                    String propertyValue = getPropertyValue(obj, property);
                    cell.setCellValue(propertyValue);
                    if (propertyValue != null) {
                        int columnWidth = sheet.getColumnWidth(j);
                        int propertyValueLength = propertyValue.getBytes().length * 2 * 172;
                        if (columnWidth < propertyValueLength) {
                            // sheet.setColumnWidth(j, propertyValueLength);
                        }
                    }
                }
            }
        }
        return rowSize + rowIndex;
    }

    /**
     * 填充标题
     *
     * @param workbook
     * @param sheetName
     * @param cellMapList
     */
    public static void fillTitleRow(SXSSFWorkbook workbook, String sheetName, List<CellMap> cellMapList) {
        fillTitleRow(workbook, sheetName, cellMapList, true, true);
    }

    /**
     * 填充标题
     *
     * @param cell
     */
    public static String handleCellValue(XSSFCell cell) {
        switch (cell.getCellTypeEnum()) {
            case STRING:// 字符串型
                return StringUtils.upperCase(cell.getStringCellValue());
            case NUMERIC:// 数值型
                return String.valueOf(cell.getRawValue());
            default:
                return StringUtils.EMPTY;
        }
    }

    /**
     * 重载填充标题方法
     *
     * @param workbook
     * @param sheetName
     * @param cellMapList
     * @param isDoubleWidth 列宽是否为双倍（原方法列宽为内容的双倍宽）
     * @param fromFirstRow  是否从第一行开始（原方法标题在sheet的第一行）
     */
    public static void fillTitleRow(SXSSFWorkbook workbook, String sheetName, List<CellMap> cellMapList,
                                    boolean isDoubleWidth, boolean fromFirstRow) {
        Sheet sheet = workbook.getSheet(sheetName);
        if (null == sheet) {
            sheet = workbook.createSheet(sheetName);
        }
        Row row = null;
        Cell cell = null;
        // header 标题
        Font titleFont = workbook.createFont();
//        titleFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        titleFont.setBold(true);
        CellStyle titleCellStyle = workbook.createCellStyle();
        titleCellStyle.setAlignment(HorizontalAlignment.CENTER);
//        titleCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        titleCellStyle.setFont(titleFont);
        if (fromFirstRow) {
            row = sheet.createRow(0);
        } else {
            row = sheet.createRow(sheet.getLastRowNum() + 1);
        }
        // 数据列数
        int cellSize = cellMapList.size();
        for (int i = 0; i < cellSize; i++) {
            CellMap cellMap = cellMapList.get(i);
            String title = cellMap.getTitle();
            cell = row.createCell(i);
            cell.setCellStyle(titleCellStyle);
            cell.setCellValue(title);
            if (title != null) {
                int colLength = title.getBytes().length * 234;
                if (isDoubleWidth) {
                    colLength *= 2;
                }
                sheet.setColumnWidth(i, colLength);
            }
        }
    }

    @SuppressWarnings("deprecation")
    public static void main(String[] args) {
        SXSSFWorkbook sxssfWorkbook = new SXSSFWorkbook(1000);
        CellStyle dataCellStyle = sxssfWorkbook.createCellStyle();
        dataCellStyle.setAlignment(HorizontalAlignment.CENTER);
        Sheet sheet = sxssfWorkbook.getSheet("aa");
        if (null == sheet) {
            sheet = sxssfWorkbook.createSheet("aa");
        }
        CellStyle cellStyle = sxssfWorkbook.createCellStyle();
        Font font = sxssfWorkbook.createFont();
        // 此处改动了
//        font.setColor(HSSFColor.BLUE.index);
        font.setColor(HSSFColor.HSSFColorPredefined.BLUE.getIndex());
        cellStyle.setFont(font);
        Row row1 = sheet.createRow(0);
        Cell c1 = row1.createCell(0);
        c1.setCellStyle(cellStyle);
        c1.setCellValue("DHL Global Mail");

        Row row2 = sheet.createRow(1);
        Cell c2 = row2.createCell(0);
        c2.setCellValue("Registered  Pre-Alert");
        c2.setCellStyle(cellStyle);

        Row row3 = sheet.createRow(2);
        Cell c3 = row3.createCell(0);
        c3.setCellValue("Customer Name");
        Cell c31 = row3.createCell(1);
        c31.setCellValue("4PX");

        Row row4 = sheet.createRow(3);
        Cell c4 = row4.createCell(0);
        c4.setCellValue("Origin Ship Date");

        Row row5 = sheet.createRow(4);
        Cell c5 = row5.createCell(0);
        c5.setCellValue("Airway Bill No.");

        List<CellMap> cellMapList = new ArrayList<CellMap>();
        cellMapList.add(new CellMap("单元格1", "bsId"));
        cellMapList.add(new CellMap("单元格2", "baggingTime"));
        fillTitleRow(sxssfWorkbook, "aa", cellMapList, true, false);

        try {
            OutputStream os = new FileOutputStream(new File("e:/test.xlsx"));
            sxssfWorkbook.write(os);
            sxssfWorkbook.close();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据json字符串构建列头
     *
     * @param columns json字符串：["sTitle":"编号","mData":"id"]
     * @return List<CellMap>
     */
    public static List<CellMap> buildColumnsHead(String columns) {
        JSONArray array = JSON.parseArray(columns);
        List<CellMap> cellMapList = new ArrayList<>(array.size());
        for (int i = 0; i < array.size(); i++) {
            JSONObject json = array.getJSONObject(i);
            cellMapList.add(new CellMap(json.getString("sTitle"), json.getString("mData")));
        }
        return cellMapList;
    }

    public static void sendHttpResponse(HttpServletResponse response, SXSSFWorkbook sxssfWorkbook, String fileName) throws IOException {
        fileName += ".xlsx";
        fileName = new String(fileName.getBytes(), "ISO8859-1");
        response.setContentType("application/octet-stream;charset=ISO8859-1");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        response.addHeader("Pargam", "no-cache");
        response.addHeader("Cache-Control", "no-cache");
        OutputStream os = response.getOutputStream();
        sxssfWorkbook.write(os);
        os.flush();
        os.close();
        sxssfWorkbook.dispose();
        sxssfWorkbook.close();
    }
}
