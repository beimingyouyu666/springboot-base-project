package com.yangk.baseproject.common.util.excelutil.poi;

import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * 分页模板导出大数据表格工具类，支持title集中在表的上面行，数据写在title下面行的简单导出。<br>
 * 本工具类没有测试表格中含有合并单元格的情况，如果需要可先测试再使用<br>
 * 本工具支持模板中包含公式函数<br>
 * 本工具结合业务代码，支持多sheet的导出
 * @date 2018年11月15日
 */
public class ExportExcelForModuleUtil {
    
    public static final int DEFAULT_PAGESIZE = 10000;
    
    /**
     * 替换sql中的参数
     * @param sql
     * @param paramName
     * @param value
     * @return
     */
    public static String replaceExportSqlParam(String sql, String paramName, Object value) {
        String val = (value instanceof Integer || value instanceof Long || value instanceof Double || value instanceof Float || value instanceof Short) ? 
                String.valueOf(value):("'"+ value +"'");
        
        try {
            return sql.replaceAll(paramName, val);
        } catch (Exception e) {
            return sql.replace(paramName, val);
        }
    }
    
    /**
     * 根据模板构建SXSSFWorkbook对象（为分页导出准备）
     * @param stencilPath
     * @return
     */
    public static ExportExcelModuleWorkbookDTO buildSXSSFWorkbook(String stencilPath) throws Exception {
        ExportExcelModuleWorkbookDTO dto = new ExportExcelModuleWorkbookDTO();
        Map<Integer, ExportExcelModuleSheetDTO> sheetMap = new HashMap<Integer, ExportExcelModuleSheetDTO>();
        SXSSFWorkbook wb = null;
        URL url = Thread.currentThread().getContextClassLoader().getResource(stencilPath);
        String excelPath = url.getPath();
        File f = new File(excelPath);
        try (FileInputStream fis = new FileInputStream(f)) {
            if (f != null) {
                @SuppressWarnings("resource") 
                XSSFWorkbook xwb = new XSSFWorkbook(fis);
                wb = new SXSSFWorkbook(DEFAULT_PAGESIZE);
                dto.setSheetNum(xwb.getNumberOfSheets());
                dto.setWorkbook(wb);
                for (int i = 0; i < xwb.getNumberOfSheets(); i++) {
                    XSSFSheet sheet = xwb.getSheetAt(i);
                    SXSSFSheet xsheet = (SXSSFSheet) wb.getSheet(sheet.getSheetName());
                    xsheet = (xsheet == null)?(SXSSFSheet) wb.createSheet(sheet.getSheetName()):xsheet;
                    // 从批注里获取起始行数 
                    Comment comment = sheet.getCellComment(CellAddress.A1);
                    String commentStr = comment.getString().getString();
                    commentStr = commentStr.substring(commentStr.indexOf("lastCell=")+"lastCell=".length());
                    commentStr = commentStr.substring(0, commentStr.indexOf(")"));
                    commentStr = commentStr.split("\"")[1];
                    char maxCellNum = commentStr.substring(0,1).toUpperCase().toCharArray()[0];
                    int maxRowNum = Integer.valueOf(commentStr.substring(1));
                    copyMergedRegions(sheet, xsheet);
                    ExportExcelModuleSheetDTO sheetVal = copyTitleRow(maxCellNum, maxRowNum, sheet, xsheet);
                    sheetMap.put(i, sheetVal);
                }
                dto.setSheetMap(sheetMap);
            }
        } catch (Exception e) {
            throw e;
        }
        return dto;
    }
    
    /**
     * 复制单元格合并
     * @param sheet
     * @param xsheet
     */
    private static void copyMergedRegions(XSSFSheet sheet, SXSSFSheet xsheet) {
        int totalNum = sheet.getNumMergedRegions();
        for (int i = 0; i < totalNum; i++) {
            CellRangeAddress region = sheet.getMergedRegion(i);
            xsheet.addMergedRegion(region);
            
        }
    }
    
    /**
     * 复制标题行
     * @param maxCellNum
     * @param maxRowNum
     * @param sheet
     * @param xsheet
     */
    private static ExportExcelModuleSheetDTO copyTitleRow(char maxCellNum, int maxRowNum, XSSFSheet sheet, SXSSFSheet xsheet) {
        ExportExcelModuleSheetDTO dto = new ExportExcelModuleSheetDTO();
        dto.setSheet(xsheet);
        dto.setDataWriteStartRow(maxRowNum - 1);
        Map<Integer, CellStyle> styleMap = new HashMap<Integer, CellStyle>();
        Map<Integer, CellType> typeMap = new HashMap<Integer, CellType>();
        Map<Integer, String> keyMap = new HashMap<Integer, String>();
        for (int rowNum = 0; rowNum < maxRowNum; rowNum ++) {
            XSSFRow row = sheet.getRow(rowNum);
            if (row == null) {
                xsheet.createRow(rowNum);
                continue;
            }
            SXSSFRow xrow = (SXSSFRow) xsheet.getRow(rowNum);
            xrow = (xrow==null)?(SXSSFRow) xsheet.createRow(rowNum):xrow;
            xrow.setHeight(row.getHeight());
            for (int cellNum = 0; cellNum <= maxCellNum - 'A'; cellNum ++) {
                XSSFCell cell = row.getCell(cellNum);
                // 空行跳过
                if (cell == null) {
                    xrow.createCell(cellNum);
                    continue;
                }
                SXSSFCell xcell = (SXSSFCell) xrow.getCell(cellNum);
                xcell = (xcell == null)?(SXSSFCell) xrow.createCell(cellNum, cell.getCellTypeEnum()):xcell;
                if (rowNum != maxRowNum - 1) {
                    switch (cell.getCellTypeEnum()) {
                    case NUMERIC:
                        xcell.setCellValue(HSSFDateUtil.getJavaDate(cell.getNumericCellValue()));
                        break;
                    case STRING:
                        xcell.setCellValue(cell.getStringCellValue());
                        break;
                    case BOOLEAN:
                        xcell.setCellValue(cell.getBooleanCellValue());
                        break;
                    case FORMULA:
                        xcell.setCellFormula(cell.getCellFormula());
                        break;
                    default:
                        xcell.setCellValue("");
                    }
                }
                if (rowNum == maxRowNum - 1) {
                    typeMap.put(cellNum, cell.getCellTypeEnum());
                    keyMap.put(cellNum, dealKeyName(getCellVal(cell)));
                }
                CellStyle type = cell.getCellStyle();
                if (type != null) {
                    if (rowNum == maxRowNum - 1) {
                        styleMap.put(cellNum, type);
                    }
                    xcell.setCellStyle(type);
                }
            }
        }
        dto.setKeyMap(keyMap);
        dto.setStyleMap(styleMap);
        dto.setTypeMap(typeMap);
        return dto;
    }
    
    private static String dealKeyName(String cval) {
        cval = cval == null ? "" : cval;
        // 公式保留
        if (cval.startsWith("=")) {
        } else if (cval.indexOf("/") != -1) {
            String[] cvalArr = cval.split("/");
            cval = "";
            for (String val : cvalArr) {
                cval += ","+(val.indexOf(".") != -1 ? val.substring(val.indexOf(".") + 1, val.indexOf("}")) : val);
            }
            cval = cval.substring(1);
        } else {
            cval = cval.indexOf(".") != -1 ? cval.substring(cval.indexOf(".") + 1, cval.indexOf("}")) : cval;
        }
        return cval;
    }
    /**
     * 默认格式
     * @param wb
     * @return
     */
    private static CellStyle getDefaultCellStyle(Workbook wb) {
        CellStyle defaultCellStyle = wb.createCellStyle();
        defaultCellStyle.setWrapText(true);
        Font font = wb.createFont();
        font.setFontHeightInPoints((short) 9);
        defaultCellStyle.setFont(font);
        return defaultCellStyle;
    }
    
    /**
     * 动态生成表格并填值
     * @param wb 表格
     * @param sheetDto sheet参数
     * @param pageData 导出值
     * @param rowSartNum 当前填写开始行数
     * @throws Exception
     */
    public static void fillDataForDynamicCell(SXSSFWorkbook wb, ExportExcelModuleSheetDTO sheetDto, PageInfo<Map<String, Object>> pageData, int rowSartNum) throws Exception {
        CellStyle defaultCellStyle = getDefaultCellStyle(wb);
        
        for (int i = rowSartNum; i < pageData.getSize() + rowSartNum; i++) {
            Map<String, Object> valMap = pageData.getList().get(i - rowSartNum);
            Row row = sheetDto.getSheet().createRow(i);
            // 序号
            Cell scell = row.createCell(0);
            scell.setCellStyle(defaultCellStyle);
            scell.setCellValue(i);
            Iterator<Integer> ite = sheetDto.getKeyMap().keySet().iterator();
            while (ite.hasNext()) {
                int cellNum = ite.next();
                String keyName = sheetDto.getKeyMap().get(cellNum);
                // 序号跳过
                if (cellNum == 0 && keyName.toUpperCase().startsWith("=IF")) {
                    continue;
                }
                Cell valCell = row.createCell(cellNum);
                CellType cellType = sheetDto.getTypeMap().get(cellNum);
                cellType = cellType == null ? CellType.STRING:cellType;
                if (cellType == CellType.FORMULA) {
                    valCell.setCellFormula(keyName.replace("=", ""));
                    valCell.setCellType(cellType);
                } else {
                    String content = "";
                    if (keyName.contains(",")) {
                        String[] keyArr = keyName.split(",");
                        for (String key : keyArr) {
                            String val = getPropertyValue(valMap, key);
                            content += "/" + (val == null ? "" : val);
                        }
                        content = content.substring(1);
                    } else {
                        content = getPropertyValue(valMap, keyName);
                    }
                    valCell.setCellValue(content == null ? "" : content);
                    valCell.setCellStyle(sheetDto.getStyleMap().containsKey(cellNum) ? sheetDto.getStyleMap().get(cellNum) : defaultCellStyle);
                }
            }
        }
        
    }
    
    public static String getCellVal(Cell cell) {
        String value = null;
        switch (cell.getCellTypeEnum()) {
        case NUMERIC: // 数字 //如果为时间格式的内容
            if (HSSFDateUtil.isCellDateFormatted(cell)) { // 注：format格式
                // yyyy-MM-dd hh:mm:ss 中小时为12小时制，若要24小时制，则把小h变为H即可，yyyy-MM-dd HH:mm:ss
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                value = sdf.format(HSSFDateUtil.getJavaDate(cell.getNumericCellValue())).toString();
                break;
            } else {
                value = new DecimalFormat("0").format(cell.getNumericCellValue());
            }
            break;
        case STRING: // 字符串
            value = cell.getStringCellValue();
            break;
        case BOOLEAN: //
            value = cell.getBooleanCellValue() + "";
            break;
        case FORMULA:
            value = "="+cell.getCellFormula() + "";
            break;
        case BLANK:
            // 空值
            value = "";
            break;
        case ERROR: // 故障
            value = "";
            break;
        default:
            value = "";
            break;
        }

        return value;
    }
    
    /**
     * 取值
     * @param obj
     * @param property
     * @return
     * @throws Exception
     */
    private static String getPropertyValue(Object obj, String property) throws Exception {
        if (property == null) {
            return property;
        }
        if (obj instanceof Map) {
            @SuppressWarnings("unchecked")
            Object val = ((Map<String, Object>) obj).get(property);
            if (val == null) {
                return null;
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
}
