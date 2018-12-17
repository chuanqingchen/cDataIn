package com.trendy.bigdata.core.util;

/**
 * Created by test on 2018/12/11.
 */
public class ToolUtil {

    public static  String[] dealSpecialCharacter(String[] strs){
        for(int i =0 ; i <strs.length ;i++){
            strs[i]  =  strs[i].replace("\uFEFF","");
        }
        return strs;
    }

}
