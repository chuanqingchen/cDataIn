package com.trendy.bigdata;

import com.csvreader.CsvReader;
import com.trendy.bigdata.core.util.POIUtil;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by test on 2018/8/23.
 */
public class JunitTest {

    @Test
    public void testPoi() {
        String path = "C:\\Users\\test\\Desktop\\OCH_sku.xls";
        try {
            POIUtil.readExcel(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCsv(){
        String filePath = "C:\\Users\\test\\Desktop\\test.csv";
        try {
            // 创建CSV读对象
            CsvReader csvReader = new CsvReader(filePath,',', Charset.forName("UTF-8"));
            // 读表头
            csvReader.readHeaders();
            String[] headers = csvReader.getHeaders();
            for(int i = 0 ; i<headers.length ;i++){
                System.out.println(i+" : "+headers[i]);
            }
            int count = 0 ;
            while (csvReader.readRecord() && count < 100){
//                // 读一整行
//                System.out.println(csvReader.getRawRecord());
//                // 读这行的某一列
//                count ++;
//                System.out.println(csvReader.get(headers[0]));
                String[] record = csvReader.getValues();
                for(int i =0 ; i < record.length; i++){
                    System.out.print(record[i] + " , ");
                }
                System.out.println();
            }
            System.out.println("数据总条数 ："+csvReader.getHeaderCount());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testMap() {
        ConcurrentHashMap<String,Integer> DealingQueue = new ConcurrentHashMap<String, Integer>(100);
        DealingQueue.put("1",1);
        DealingQueue.put("1",2);
        for(Map.Entry<String, Integer> entrySet : DealingQueue.entrySet()){
            System.out.println("" + entrySet.getKey() + " : " + entrySet.getValue() );
        }
    }

    @Test
    public void testIndexOf(){
        String fName="D:/EREI\\OCH_sku.txt";
        fName = fName.substring(fName.lastIndexOf("/")+1);
        fName = fName.substring(fName.lastIndexOf("\\")+1);
        System.out.println(fName.substring(0,fName.lastIndexOf("."))) ;
    }



}
