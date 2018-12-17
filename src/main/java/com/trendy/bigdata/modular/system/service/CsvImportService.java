package com.trendy.bigdata.modular.system.service;

import java.util.List;

/**
 * Created by test on 2018/12/11.
 */
public interface CsvImportService {

    int addTable(String tableName,String[] headers);

    int insertCsvData(String[] fields, String[] val,String tableName,String path);

    int[] insertBatchCsvData(String[] fields, List<String[]> val, String tableName, String path);

    int delCsvDataByPath(String tableName,String path);

}
