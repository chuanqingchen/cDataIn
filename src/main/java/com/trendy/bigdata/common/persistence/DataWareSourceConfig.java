package com.trendy.bigdata.common.persistence;

import com.alibaba.druid.pool.DruidDataSource;
import com.trendy.core.common.db.PostgreSqlDialect;
import com.trendy.core.common.db.TrendyNamedParamJdbcTemplate;
import com.trendy.core.common.logcommon.TrendyLog;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.sql.SQLException;


/**
 * Created by pipeng on 2016/8/17.
 */
@Configuration
@EnableTransactionManagement
public class DataWareSourceConfig {


    private String url = "jdbc:postgresql://ip:port/dataware";

    private String username = "username";

    private String password = "password";

    private String driverClassName = "org.postgresql.Driver";

    private Integer initialSize = 2;

    private Integer minIdle = 1;

    private Integer maxActive = 10;

    private Integer maxWait = 60000;

    private Integer timeBetweenEvictionRunsMillis = 60000;

    private Integer minEvictableIdleTimeMillis = 300000;

    private String validationQuery = "SELECT 'x'";

    private Boolean testWhileIdle = true;

    private Boolean testOnBorrow = false;

    private Boolean testOnReturn = false;

    private Boolean poolPreparedStatements = true;

    private Integer maxPoolPreparedStatementPerConnectionSize = 20;

    private String filters = "stat,wall";


    @Bean(name = "dataWareDataSource", destroyMethod = "close", initMethod = "init")
    @Qualifier("dataWareDataSource")
    @ConfigurationProperties(prefix = "dataWare.datasource")
    public DataSource metaDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        dataSource.setDriverClassName(driverClassName);
        dataSource.setInitialSize(initialSize);     //定义初始连接数
        dataSource.setMinIdle(minIdle);             //最小空闲
        dataSource.setMaxActive(maxActive);         //定义最大连接数
        dataSource.setMaxWait(maxWait);             //最长等待时间

        // 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
        dataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);

        // 配置一个连接在池中最小生存的时间，单位是毫秒
        dataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        dataSource.setValidationQuery(validationQuery);
        dataSource.setTestWhileIdle(testWhileIdle);
        dataSource.setTestOnBorrow(testOnBorrow);
        dataSource.setTestOnReturn(testOnReturn);

        // 打开PSCache，并且指定每个连接上PSCache的大小
        dataSource.setPoolPreparedStatements(poolPreparedStatements);
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);

        try {
            dataSource.setFilters(filters);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dataSource;
       //return  DataSourceBuilder.create().build();
    }



    @Bean(name = "dataWareNamedParameterJdbcTemplate")
    public TrendyNamedParamJdbcTemplate metaNamedParameterJdbcTemplate(
            @Qualifier("dataWareDataSource") DataSource dataSource) {
        TrendyLog.log.info("dataWareNamedParameterJdbcTemplate init ....dataWareJdbcTemplate");
        return new TrendyNamedParamJdbcTemplate(dataSource,new PostgreSqlDialect());
    }




}
