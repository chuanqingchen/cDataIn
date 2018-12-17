package com.trendy.bigdata.common.persistence.dao;

import com.trendy.core.common.db.TrendyNamedParamJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/**
 * Created by pipeng on 2016/8/17.
 */
@Repository
public class DataWareJdbcDao {



    @Resource(name="dataWareNamedParameterJdbcTemplate")
    private TrendyNamedParamJdbcTemplate namedJdbcTemplate;


    protected TrendyNamedParamJdbcTemplate getTrendyNpJdbcTemplate(){
        return this.namedJdbcTemplate;
    }



}
