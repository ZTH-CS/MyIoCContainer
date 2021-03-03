package com.awa.main;

import com.awa.iocframework.context.ApplicationContext;
import com.awa.iocframework.context.ClassPathXmlApplicationContext;
import com.awa.main.Bean.AnnotatedBean.Woman;
import com.awa.main.Bean.Man;

public class Main {

    public static void main(String[] args) throws Exception {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("application.xml");
        Man aMan = (Man) applicationContext.getBean(Man.class);
        aMan.speakLoud();
        Woman woman = (Woman) applicationContext.getBean("com.awa.main.Bean.AnnotatedBean.Woman");
        woman.doSomething();
    }

}
