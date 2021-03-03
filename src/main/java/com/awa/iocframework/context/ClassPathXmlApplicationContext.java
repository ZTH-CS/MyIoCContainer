package com.awa.iocframework.context;

import com.awa.iocframework.entity.BeanDefinition;
import com.awa.iocframework.factory.AbstractBeanFactory;
import com.awa.iocframework.factory.AutowiredCapableBeanFactory;
import com.awa.iocframework.io.ResourceLoader;
import com.awa.iocframework.reader.XmlBeanDefinitionReader;

import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 根据类路径的配置文件建立应用程序上下文
 *
 * @author awa
 */
public class ClassPathXmlApplicationContext extends AbstractApplicationContext {

    private final ReentrantLock startupShutdownLock = new ReentrantLock();

    private String location;

    public ClassPathXmlApplicationContext(String location) throws Exception {
        super();
        this.location = location;
        refresh();
    }

    /**
     * 刷新或重建应用程序上下文
     * @throws Exception 可能出现的异常
     */
    private void refresh() throws Exception{
        try{
            startupShutdownLock.lock();
            AbstractBeanFactory beanFactory = obtainBeanFactory();
            prepareBeanFactory(beanFactory);
            this.beanFactory = beanFactory;
        }finally {
            startupShutdownLock.unlock();
        }
    }

    /**
     * 获取Bean工厂
     * @return 抽象Bean工厂
     * @throws Exception 可能发生的异常
     */
    private AbstractBeanFactory obtainBeanFactory() throws Exception{
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(new ResourceLoader());
        beanDefinitionReader.loadBeanDefinitions(location);
        AbstractBeanFactory beanFactory = new AutowiredCapableBeanFactory();

        for(Map.Entry<String, BeanDefinition> entry : beanDefinitionReader.getRegistry().entrySet()){
            beanFactory.registerBeanDefinition(entry.getKey(), entry.getValue());
        }
        return beanFactory;
    }

    /**
     * 初始化Bean工厂
     * @param beanFactory bean工厂
     * @throws Exception 可能发生的异常
     */
    private void prepareBeanFactory(AbstractBeanFactory beanFactory) throws Exception{
        beanFactory.populateBeans();
    }

}
