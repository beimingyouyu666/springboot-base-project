package com.yangk.baseproject.common.util.excelutil.easyexcel;

import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.BaseRowModel;
import com.alibaba.excel.metadata.Font;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.metadata.Table;
import com.alibaba.excel.metadata.TableStyle;
import com.alibaba.excel.support.ExcelTypeEnum;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * excel工具类
 *
 * @date 2019-06-18
 */
public class ExcelUtil {
    /**
     * 读取 Excel(多个 sheet)
     *
     * @param excel    文件
     * @param rowModel 实体类映射，继承 BaseRowModel 类
     * @return Excel 数据 list
     */
    public static List<Object> readExcel(MultipartFile excel, BaseRowModel rowModel) {
        ExcelListener excelListener = new ExcelListener();
        ExcelReader reader = getReader(excel, excelListener);
        if (reader == null) {
            return null;
        }
        for (Sheet sheet : reader.getSheets()) {
            if (rowModel != null) {
                sheet.setClazz(rowModel.getClass());
            }
            reader.read(sheet);
        }
        return excelListener.getDatas();
    }

    /**
     * 读取某个 sheet 的 Excel
     *
     * @param excel    文件
     * @param rowModel 实体类映射，继承 BaseRowModel 类
     * @param sheetNo  sheet 的序号 从1开始
     * @return Excel 数据 list
     */
    public static List<Object> readExcel(MultipartFile excel, BaseRowModel rowModel, int sheetNo) {
        return readExcel(excel, rowModel, sheetNo, 1);
    }

    /**
     * 读取某个 sheet 的 Excel
     *
     * @param excel       文件
     * @param rowModel    实体类映射，继承 BaseRowModel 类
     * @param sheetNo     sheet 的序号 从1开始
     * @param headLineNum 表头行数，默认为1
     * @return Excel 数据 list
     */
    public static List<Object> readExcel(MultipartFile excel, BaseRowModel rowModel, int sheetNo,
                                         int headLineNum) {
        ExcelListener excelListener = new ExcelListener();
        ExcelReader reader = getReader(excel, excelListener);

        if (reader == null) {
            return null;
        }
        reader.read(new Sheet(sheetNo, headLineNum, rowModel.getClass()));
        return excelListener.getDatas();
    }

    /**
     * 导出 Excel ：一个 sheet，带表头
     *
     * @param response  HttpServletResponse
     * @param userAgent 浏览器类型
     * @param list      数据 list，每个元素为一个 BaseRowModel
     * @param fileName  导出的文件名
     * @param sheetName 导入文件的 sheet 名
     * @param object    映射实体类，Excel 模型
     */
    public static void writeExcel(HttpServletResponse response, String userAgent, List<? extends BaseRowModel> list,
                                  String fileName, String sheetName, BaseRowModel object) {
        ExcelWriter writer = new ExcelWriter(getOutputStream(fileName, response, userAgent), ExcelTypeEnum.XLSX, true);
        Sheet sheet = new Sheet(1, 0, object.getClass());
        sheet.setSheetName(sheetName);
        Table table = new Table(1);
        table.setClazz(object.getClass());
        table.setTableStyle(createTableStyle());
        writer.write(list, sheet, table);
        writer.finish();
    }

    /**
     * 根据文件路径，导出模板文件
     *
     * @param templatePath 模板路径
     * @param response  HttpServletResponse
     * @param userAgent 浏览器类型
     */
    public static void exportExcelByTemplate(String templatePath, HttpServletResponse response, String userAgent,String fileName) throws IOException {
        ClassPathResource classPathResource =new ClassPathResource(templatePath);
        //创建本地文件
        InputStream inputStream = null;
        ServletOutputStream servletOutputStream = null;
        try {
            setHeader(response, userAgent, fileName);

            inputStream = classPathResource.getInputStream();
            servletOutputStream = response.getOutputStream();
            IOUtils.copy(inputStream, servletOutputStream);
            response.flushBuffer();
        } catch (IOException e) {
            throw new ExcelException("创建文件失败！");
        } finally {
            try {
                if (servletOutputStream != null) {
                    servletOutputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 导出文件时为Writer生成OutputStream
     */
    private static OutputStream getOutputStream(String fileName, HttpServletResponse response, String userAgent) {
        //创建本地文件
        fileName = fileName + ".xlsx";
        File dbfFile = new File(fileName);
        try {
            if (!dbfFile.exists() || dbfFile.isDirectory()) {
                dbfFile.createNewFile();
            }

            // 判断浏览器类型，用以处理文件名
            setHeader(response, userAgent, fileName);
            return response.getOutputStream();
        } catch (IOException e) {
            throw new ExcelException("创建文件失败！");
        } finally {
            dbfFile.delete();
        }
    }

    private static void setHeader(HttpServletResponse response, String userAgent, String fileName) throws UnsupportedEncodingException {
        // 判断浏览器类型，用以处理文件名
        if (/* IE 8 至 IE 10 */
                userAgent.toUpperCase().contains("MSIE") ||
                        /* IE 11 */
                        userAgent.contains("Trident/7.0")) {
            fileName = URLEncoder.encode(fileName, "UTF-8");
        } else if (userAgent.toUpperCase().contains("MOZILLA") ||
                userAgent.toUpperCase().contains("CHROME")) {
            fileName = new String(fileName.getBytes(), "ISO-8859-1");
        } else {
            fileName = URLEncoder.encode(fileName, "UTF-8");
        }

        response.setContentType("application/octet-stream");
        response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
        response.setCharacterEncoding("utf-8");
    }

    /**
     * 返回 ExcelReader
     *
     * @param excel         需要解析的 Excel 文件
     * @param excelListener new ExcelListener()
     */
    private static ExcelReader getReader(MultipartFile excel,
                                         ExcelListener excelListener) {
        String filename = excel.getOriginalFilename();
        if (filename == null || (!filename.toLowerCase().endsWith(".xls") && !filename.toLowerCase().endsWith(".xlsx"))) {
            throw new ExcelException("文件格式错误！");
        }
        InputStream inputStream;
        try {
            inputStream = new BufferedInputStream(excel.getInputStream());
            return new ExcelReader(inputStream, filename.toLowerCase().endsWith(".xls") ? ExcelTypeEnum.XLS : ExcelTypeEnum.XLSX, null, excelListener);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static TableStyle createTableStyle() {
        TableStyle tableStyle = new TableStyle();
        // 设置表头样式
        Font headFont = new Font();
        // 字体是否加粗
        headFont.setBold(true);
        // 字体大小
        headFont.setFontHeightInPoints((short)11);
        // 字体
        headFont.setFontName("宋体");
        tableStyle.setTableHeadFont(headFont);
        // 背景色
        tableStyle.setTableHeadBackGroundColor(IndexedColors.WHITE);


        // 设置表格主体样式
        Font contentFont = new Font();
        contentFont.setBold(false);
        contentFont.setFontHeightInPoints((short)11);
        contentFont.setFontName("宋体");
        tableStyle.setTableContentFont(contentFont);
        tableStyle.setTableContentBackGroundColor(IndexedColors.WHITE);
        return tableStyle;
    }
}
