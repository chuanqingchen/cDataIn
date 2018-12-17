package com.trendy.bigdata.callback;

/**
 * Created by test on 2018/12/11.
 */
public class B {

    /*
	 * 回调函数
	 */
    public void call(CallBack a){
		/*
		 * b help a solve the priblem
		 */
        System.out.println("b help a solve the problem!");
		/*
		 * call back
		 */
        a.slove();

    }
}
