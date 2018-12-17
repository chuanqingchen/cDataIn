package com.trendy.bigdata.modular.system.dao;

import com.google.common.collect.Maps;
import com.trendy.bigdata.common.persistence.dao.DataWareJdbcDao;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by test on 2018/12/11.
 */
@Repository
public class CsvImportDao extends DataWareJdbcDao{

    /**
     * 新建csv相关表
     * @param fields
     */
    public void  addTable(String[] fields,String tableName){
        StringBuilder sb = new StringBuilder();
        sb.append(" create table stg."+ tableName +" (  ");
        for(int i = 0; i< fields.length ;i++){
            if(i == fields.length - 1){
                sb.append("\"" + fields[i] + "\" text,");
            }else{
                sb.append("\"" +fields[i] + "\" text,");
            }
        }
        sb.append("\"文件路径\" text");
        sb.append(")");
        MapSqlParameterSource param = new MapSqlParameterSource();
        getTrendyNpJdbcTemplate().update(sb.toString(),param);
    }

    /**
     * 判断表是否存在
     * @param tableName
     * @return
     */
    public int existsTable(String tableName){
        StringBuilder sb = new StringBuilder();
        sb.append("select count(*) from " +
                " information_schema.tables " +
                " where table_schema='stg' " +
                " and table_type='BASE TABLE' " +
                "  and table_name=:tableName ");
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("tableName",tableName);
        return getTrendyNpJdbcTemplate().queryForObject(sb.toString(),param,Integer.class);
    }

    /**
     * 插入CSV数据
     * @param fields
     * @param val
     * @param tableName
     * @return
     */
    public int insertCsvData(String[] fields, String[] val,String tableName,String path){
        StringBuilder sb = new StringBuilder(), sbVal = new StringBuilder();
        sb.append("insert into stg."+ tableName + "(" );
        int count = 0 ;
        MapSqlParameterSource param = new MapSqlParameterSource();
        for(int i = 0; i< fields.length ;i++){
            if(i == fields.length - 1){
                sb.append("\"" + fields[i] + "\",");
                sbVal.append(":var" + count + ",");
            }else{
                sb.append("\"" +fields[i] + "\",");
                sbVal.append(":var" + count + ",");
            }
            param.addValue("var"+count,val[count]);
            count ++;
        }

        sb.append("\"文件路径\"");
        sbVal.append(":var" + count);
        param.addValue("var"+count,path);

        sb.append(") values(").append(sbVal.toString()).append(")");
        return getTrendyNpJdbcTemplate().update(sb.toString(),param);
    }

    /**
     * 批量插入CSV数据
     * @param fields
     * @param val
     * @param tableName
     * @param path
     * @return
     */
    public int[] insertBatchCsvData(String[] fields, List<String[]> val, String tableName, String path){
        StringBuilder sb = new StringBuilder(), sbVal = new StringBuilder();
        sb.append("insert into stg."+ tableName + "(" );
        int count = 0 ;
        MapSqlParameterSource param = new MapSqlParameterSource();
        for(int i = 0; i< fields.length ;i++){
            if(i == fields.length - 1){
                sb.append("\"" + fields[i] + "\",");
                sbVal.append(":var" + count + ",");
            }else{
                sb.append("\"" +fields[i] + "\",");
                sbVal.append(":var" + count + ",");
            }
            count ++;
        }
        sb.append("\"文件路径\"");
        sbVal.append(":var" + count);
        sb.append(") values(").append(sbVal.toString()).append(")");

        Map[] batchValues = new HashMap[val.size()];
        for(int i=0;i<val.size();i++){
            Map v = Maps.newHashMap();
            batchValues[i] = v;
            count = 0 ;
            for(int j = 0; j< fields.length ;j++){
                batchValues[i].put("var"+count,val.get(i)[j]);
                count ++;
            }
            batchValues[i].put("var"+count,path);
        }

        return getTrendyNpJdbcTemplate().batchUpdate(sb.toString(),batchValues);
    }


    /**
     * 通过文件名删除CSV数据
     * @param tableName
     * @param path
     * @return
     */
    public int delCsvDataByPath(String tableName,String path){
        StringBuilder sb = new StringBuilder(), sbVal = new StringBuilder();
        sb.append("delete from stg." + tableName + " where 1=1 and " +
                "\"文件路径\"=:path" );
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("path",path);
        return getTrendyNpJdbcTemplate().update(sb.toString(),param);
    }


}
