package com.trendy.bigdata.modular.system.service.impl;

import com.trendy.bigdata.modular.system.dao.CsvImportDao;
import com.trendy.bigdata.modular.system.service.CsvImportService;
import com.trendy.core.common.logcommon.TrendyLog;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 *
 * @author test
 * @date 2018/12/11
 */

@Service
public class CsvImportServiceImpl implements CsvImportService {

    @Resource
    private CsvImportDao csvImportDao;

    @Override
    public int addTable(String tableName,String[] headers){
        int isExist = csvImportDao.existsTable(tableName.toLowerCase());
        if(isExist > 0){
            TrendyLog.log.info("The table '{}' is already exist !!!",tableName);
        }else{
            csvImportDao.addTable(headers,tableName.toLowerCase());
            TrendyLog.log.info("Create table '{}' successfully.",tableName);
        }
        return isExist;
    }

    @Override
    public int insertCsvData(String[] fields, String[] val,String tableName,String path){
        return  csvImportDao.insertCsvData(fields,val,tableName,path);

    }

    @Override
    public int[] insertBatchCsvData(String[] fields, List<String[]> val, String tableName, String path) {
        return csvImportDao.insertBatchCsvData(fields,val,tableName,path);
    }

    @Override
    public int delCsvDataByPath(String tableName,String path){
        return csvImportDao.delCsvDataByPath(tableName,path);
    }


}
