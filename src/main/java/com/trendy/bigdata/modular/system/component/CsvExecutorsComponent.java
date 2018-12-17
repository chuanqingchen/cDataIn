package com.trendy.bigdata.modular.system.component;

import com.csvreader.CsvReader;
import com.trendy.bigdata.core.util.SectionUtil;
import com.trendy.bigdata.core.util.ToolUtil;
import com.trendy.bigdata.modular.system.dao.CsvImportDao;
import com.trendy.bigdata.modular.system.service.CsvImportService;
import com.trendy.bigdata.modular.system.service.impl.CsvImportServiceImpl;
import com.trendy.core.common.logcommon.TrendyLog;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by test on 2018/12/11.
 */
@Component
public class CsvExecutorsComponent {

    public static  int POOL_SIZE  = 10,BATCH_SIZE = 5000 ;

    private static ExecutorService extractPool =  Executors.newFixedThreadPool(POOL_SIZE);

    @Resource
    private CsvImportService csvImportService;

    @Value("${csv.tmp.dir}")
    private String outDir;

    private static ConcurrentHashMap<String,Integer> DealingQueue = new ConcurrentHashMap<String, Integer>(100);

    public ConcurrentHashMap<String,Integer> getDealingQueue(){
        return DealingQueue;
    }

    public ExecutorService getExtractPool(){
        if(extractPool.isShutdown() || extractPool == null){
            extractPool = Executors.newFixedThreadPool(POOL_SIZE);
        }
        return  extractPool;
    }

    public String createAndGetOutDir (String outDir) {
        File dir = new File(outDir);
        if (dir.exists()) {
            if (dir.isDirectory()) {
                TrendyLog.log.info("dir exists");
            } else {
                TrendyLog.log.info("the same name file exists, can not create dir");
                return "";
            }
        } else {
            TrendyLog.log.info("dir not exists, create it ...");
            dir.mkdir();
        }
        return outDir;
    }

    public void  executeHandler( String path, String tableName) throws Exception {
        DealingQueue.put(path,0);
        String headers[]  = this.getHeadersWithCreateTable(path,tableName);
        SectionUtil s = new SectionUtil();
        List<File> files = s.splitByChannel(path, POOL_SIZE, createAndGetOutDir(outDir) + this.getFileNamePrefix(path)+"_");
        DealingQueue.put(path,files.size());
        //根据文件名删除历史数据
        csvImportService.delCsvDataByPath(tableName,path);
        ExecutorService extractPool = getExtractPool();
        files.parallelStream().forEach(n->{

            Runnable run = () -> {
                try {
                    csvImport(n.getAbsolutePath(), tableName, headers, path);
                }finally {
                    if(DealingQueue.get(path)-1 == 0){
                        DealingQueue.remove(path);
                    }else {
                        DealingQueue.put(path, DealingQueue.get(path) - 1);
                    }
                }
            };
            extractPool.execute(run);
        });
    }

    public String[] getHeadersWithCreateTable(String path,String tableName) throws IOException {
        // 创建CSV读对象
        CsvReader csvReader = new CsvReader(path,',', Charset.forName("UTF-8"));
        // 读表头
        csvReader.readHeaders();
        String[] headers = ToolUtil.dealSpecialCharacter(csvReader.getHeaders());
        csvImportService.addTable(tableName,headers);
        return headers;
    }

    public String getFileNamePrefix(String fName){
        //通过文件路径获取文件名+后缀(例如:1.txt,2.txt)
        String fileName = fName.substring(fName.lastIndexOf("/")+1);
        fileName = fileName.substring(fName.lastIndexOf("\\")+1);
        //截取文件名(例如:1,2)
        return fileName.substring(0,fileName.lastIndexOf("."));
    }


    public void csvImport(String path , String tableName
            ,String[] headers,String originPath){
        try {
            CsvReader csvReader = new CsvReader(path,',', Charset.forName("UTF-8"));
            if(path.endsWith("_0_byChannel.txt")){
                // 读表头
                csvReader.readHeaders();
            }
            int count = 0,total=0;
            List<String []> list = new ArrayList<String []>(BATCH_SIZE);
            while (csvReader.readRecord()){
                total++;
                list.add(csvReader.getValues());
                if(list.size() == BATCH_SIZE ){
                    csvImportService.insertBatchCsvData(headers,list,tableName,originPath);
                    TrendyLog.log.info("线程{}在执行:{}文件数据({}条)入库",
                            Thread.currentThread().getName(), path,list.size());

                    list.clear();

                }
            }
            if(list.size()>0){
                csvImportService.insertBatchCsvData(headers,list,tableName,originPath);
                TrendyLog.log.info("线程{}在执行:{}文件数据({}条)入库",
                        Thread.currentThread().getName(), path,list.size());

                list.clear();
            }
            TrendyLog.log.info("The csv section data[{}] import table '{}' finish({}条).",
                    path,tableName,total);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
