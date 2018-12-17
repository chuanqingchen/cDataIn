package com.trendy.bigdata.modular.system.controller;

import com.trendy.bigdata.modular.system.component.CsvExecutorsComponent;
import com.trendy.bigdata.modular.system.model.Status;
import com.trendy.bigdata.modular.system.service.CsvImportService;
import com.trendy.core.common.logcommon.TrendyLog;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by test on 2018/12/11.
 */
@Controller
@RequestMapping("/data")
public class CsvImportController {

    @Resource
    private CsvExecutorsComponent csvExecutorsComponent;


    @RequestMapping("/csvImport")
    @ResponseBody
    public Map<String,Object> csvImport(@RequestParam("path") String path,
                                        @RequestParam("tableName") String tableName) {
        HashMap<String,Object> result = new HashMap<String,Object>();
        if(csvExecutorsComponent.getDealingQueue().containsKey(path)){
            result.put("message","该文件已在处理队列中, 请耐心等待.....");
            result.put("statusCode", Status.IN_QUEUE);
        }else{
            try {
                csvExecutorsComponent.executeHandler(path,tableName);
                result.put("status","请求已提交, 数据处理中.....");
                result.put("statusCode", Status.SUCCESS);
            } catch (IOException e) {
                e.printStackTrace();
                csvExecutorsComponent.getDealingQueue().remove(path);
                result.put("status","文件不存在，请检查!!!");
                result.put("statusCode", Status.FILE_NOT_EXISTS);
            } catch (Exception e) {
                e.printStackTrace();
                csvExecutorsComponent.getDealingQueue().remove(path);
                result.put("status","服务繁忙，请稍后再试!!!");
                result.put("statusCode", Status.FAIL);
            }
        }
        return result;
    }

}
