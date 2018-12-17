package com.trendy.bigdata.core.util;

import com.trendy.core.common.logcommon.TrendyLog;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by test on 2017/12/21.
 */
public class POIUtil {

    private final static String xls = "xls";
    private final static String xlsx = "xlsx";

    /**
     * 读入excel文件，解析后返回
     *
     * @param path
     * @throws IOException
     */
    public static List<String[]> readExcel(String path) throws IOException {
        //检查文件
        checkFile(path);
        //获得Workbook工作薄对象
        Workbook workbook = getWorkBook(path);
        //创建返回对象，把每行中的值作为一个数组，所有行作为一个集合返回
        List<String[]> list = new ArrayList<String[]>();
        if (workbook != null) {
            for (int sheetNum = 0; sheetNum < workbook.getNumberOfSheets(); sheetNum++) {
                //获得当前sheet工作表
                Sheet sheet = workbook.getSheetAt(sheetNum);
                if (sheet == null) {
                    continue;
                }
                //获得当前sheet的开始行
                int firstRowNum = sheet.getFirstRowNum();
                //获得当前sheet的结束行
                int lastRowNum = sheet.getLastRowNum();
                //循环除了第一行的所有行
                for (int rowNum = firstRowNum + 1; rowNum <= lastRowNum; rowNum++) {
                    //获得当前行
                    Row row = sheet.getRow(rowNum);
                    if (row == null) {
                        continue;
                    }
                    //获得当前行的开始列
                    int firstCellNum = row.getFirstCellNum();
                    //获得当前行的列数
                    int lastCellNum = row.getPhysicalNumberOfCells();
                    String[] cells = new String[row.getPhysicalNumberOfCells()];
                    //循环当前行
                    TrendyLog.log.info("============================");
                    for (int cellNum = firstCellNum; cellNum < lastCellNum; cellNum++) {
                        Cell cell = row.getCell(cellNum);
                        cells[cellNum] = getCellValue(cell);
                    }
                    list.add(cells);
                }
            }
            workbook.close();
        }
        return list;
    }


    public static String[] getColumnName(String path) throws IOException {
        //检查文件
        checkFile(path);
        //获得Workbook工作薄对象
        Workbook workbook = getWorkBook(path);
        if (workbook != null) {
            Sheet sheet = workbook.getSheetAt(0);

            Row row = sheet.getRow(0);
            //获得当前行的开始列
            int firstCellNum = row.getFirstCellNum();
            //获得当前行的列数
            int lastCellNum = row.getPhysicalNumberOfCells();
            String[] cells = new String[row.getPhysicalNumberOfCells()];
            //循环当前行
            TrendyLog.log.info("============================");
            for (int cellNum = firstCellNum; cellNum < lastCellNum; cellNum++) {
                Cell cell = row.getCell(cellNum);
                cells[cellNum] = getCellValue(cell);
            }
            workbook.close();
            return cells;
        }
        return null;
    }

    public static void checkFile(String path) throws IOException {
        //判断文件是否存在
        File file = new File(path);
        if (null == file) {
            TrendyLog.log.info("文件不存在！");
            throw new FileNotFoundException("文件不存在！");
        }
        //获得文件名
        String fileName = file.getName();
        //判断文件是否是excel文件
        if (!fileName.endsWith(xls) && !fileName.endsWith(xlsx)) {
            TrendyLog.log.error("{}不是excel文件", fileName);
            throw new IOException(fileName + "不是excel文件");
        }
    }

    public static Workbook getWorkBook(String path) {
        File file = new File(path);
        //获得文件名
        String fileName = file.getName();
        //创建Workbook工作薄对象，表示整个excel
        Workbook workbook = null;
        try {
            //获取excel文件的io流
            InputStream is = new FileInputStream(file);
            //根据文件后缀名不同(xls和xlsx)获得不同的Workbook实现类对象
            if (fileName.endsWith(xls)) {
                //2003
                workbook = new HSSFWorkbook(is);
            } else if (fileName.endsWith(xlsx)) {
                //2007
                workbook = new XSSFWorkbook(is);
            }
        } catch (IOException e) {
            TrendyLog.log.info(e.getMessage());
        }
        return workbook;
    }

    public static String getCellValue(Cell cell) {
        String cellValue = "";
        if (cell == null) {
            return cellValue;
        }
        //判断数据的类型
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_NUMERIC: //数字
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    Date date = cell.getDateCellValue();
                    cellValue = DateFormatUtils.format(date, "yyyy/MM/dd");
                    TrendyLog.log.info("Date:{}", cellValue);
                } else {
                    cellValue = String.valueOf(cell.getNumericCellValue());
                    TrendyLog.log.info("Numeric:{}", cellValue);
                }
                break;
            case Cell.CELL_TYPE_STRING: //字符串
                cellValue = String.valueOf(cell.getStringCellValue());

                TrendyLog.log.info("String:{}", cellValue);
                break;
            case Cell.CELL_TYPE_BOOLEAN: //Boolean
                cellValue = String.valueOf(cell.getBooleanCellValue());
                TrendyLog.log.info("Boolean:{}", cellValue);
                break;
            case Cell.CELL_TYPE_FORMULA: //公式
//                cellValue = String.valueOf(cell.getCellFormula());
                try {
                    cellValue = String.valueOf(cell.getNumericCellValue());
                } catch (IllegalStateException e) {
                    cellValue = String.valueOf(cell.getRichStringCellValue());
                }
                TrendyLog.log.info("Formula:{}", cellValue);
                break;
            case Cell.CELL_TYPE_BLANK: //空值
                cellValue = "";
                TrendyLog.log.info("null:{}", cellValue);
                break;
            case Cell.CELL_TYPE_ERROR: //故障
                cellValue = "非法字符";

                TrendyLog.log.info("{}", cellValue);
                break;
            default:
                cellValue = "未知类型";
                TrendyLog.log.info("{}", cellValue);
                break;
        }
        return cellValue;
    }

}
