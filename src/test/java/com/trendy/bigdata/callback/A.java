package com.trendy.bigdata.callback;

/**
 * Created by test on 2018/12/11.
 */
public class A implements CallBack {

    B b = new B();
    @Override
	/*
	 * (non-Javadoc)
	 * @see CallBack#slove()
	 * 响应回调函数
	 */
    public void slove() {
        System.out.println("the problem is solve!");
    }
    /*
     * 登记回调函数
     */
    public void askQuestion(){
        System.out.println("ask b solve the problem!");
		/*
		 * 自己去做其它事
		 */
        new Thread(new Runnable() {

            @Override
            public void run() {
                System.out.println("A want to do another thing!");
            }
        }).start();
		/*
		 * ask b to solve this problem
		 */
        this.b.call(this);
    }
    /*
     * test
     */
    public static void main(String[] args)  {
        A a = new A();
        a.askQuestion();
    }
}
