package com.awa.iocframework.context;

import com.awa.iocframework.factory.BeanFactory;

/**
 * 抽象应用程序上下文
 *
 * @author awa
 */
public abstract class AbstractApplicationContext implements ApplicationContext {

    BeanFactory beanFactory;

    @Override
    public Object getBean(String name) throws Exception {
        return beanFactory.getBean(name);
    }

    @Override
    public Object getBean(Class clazz) throws Exception {
        return beanFactory.getBean(clazz);
    }
}
