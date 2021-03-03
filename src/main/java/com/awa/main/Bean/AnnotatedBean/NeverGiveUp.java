package com.awa.main.Bean.AnnotatedBean;


import com.awa.iocframework.annotation.Component;

@Component(name = "ngu")
public class NeverGiveUp implements DoSomething {
    @Override
    public String doSomething() {
        return "Never Give Up!";
    }
}
