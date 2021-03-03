package com.awa.main.Bean.AnnotatedBean;

import com.awa.iocframework.annotation.Autowired;
import com.awa.iocframework.annotation.Component;
import com.awa.iocframework.annotation.Qualifier;
import com.awa.iocframework.annotation.Value;

@Component
public class Woman {

    @Autowired
    @Qualifier("ngu")
    private NeverGiveUp neverGiveUp;

    @Value("BestOne")
    private String name;

    public void doSomething(){
        System.out.println("The woman " + name + " do " + neverGiveUp.doSomething());
    }

}
