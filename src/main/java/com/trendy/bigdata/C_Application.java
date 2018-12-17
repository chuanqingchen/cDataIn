package com.trendy.bigdata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by test on 2018/12/10.
 */
@SpringBootApplication
@EnableScheduling
public class C_Application {

    protected final static Logger logger = LoggerFactory.getLogger(com.trendy.bigdata.C_Application.class);


    public static void main(String[] args) {

        SpringApplication.run(com.trendy.bigdata.C_Application.class, args);

        logger.info("CsvImportApi is sussess!");
    }

}
