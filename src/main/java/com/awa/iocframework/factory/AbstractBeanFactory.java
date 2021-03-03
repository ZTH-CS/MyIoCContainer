package com.awa.iocframework.factory;

import com.awa.iocframework.entity.BeanDefinition;
import com.awa.iocframework.exception.BeanNotFoundException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author awa
 *
 * 抽象Bean工厂，实现了部分基本方法
 */
public abstract class AbstractBeanFactory implements BeanFactory {

    final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, BeanDefinition>();

    @Override
    public Object getBean(String name) throws Exception {
        BeanDefinition beanDefinition = beanDefinitionMap.get(name);

        if(beanDefinition == null){
            throw new BeanNotFoundException(name);
        }

        if(!beanDefinition.isSingleton() || beanDefinition.getBean() == null){
            return doCreateBean(beanDefinition);
        }

        return beanDefinition.getBean();
    }

    @Override
    public Object getBean(Class clazz) throws Exception {
        BeanDefinition beanDefinition = null;

        for(Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()){
            Class tmpClass = entry.getValue().getBeanClass();

            if(tmpClass == clazz || clazz.isAssignableFrom(tmpClass)){
                beanDefinition = entry.getValue();
            }
        }

        if(beanDefinition == null){
            throw new BeanNotFoundException(clazz.getName());
        }

        if(!beanDefinition.isSingleton() || beanDefinition.getBean() == null){
            return doCreateBean(beanDefinition);
        }

        return beanDefinition.getBean();
    }

    @Override
    public void registerBeanDefinition(String name, BeanDefinition beanDefinition) {
        beanDefinitionMap.put(name, beanDefinition);
    }

    /**
     * 创建Bean的实例
     * @param beanDefinition Bean在容器中的定义
     * @return Bean的实例对象
     * @throws Exception 可能出现的异常
     */
    abstract Object doCreateBean(BeanDefinition beanDefinition) throws Exception;

    /**
     * 在工厂中初始化BeanDefinition
     * @throws Exception 可能产生的异常
     */
    public void populateBeans() throws Exception{
        for(Map.Entry<String, BeanDefinition> entry: beanDefinitionMap.entrySet()){
            doCreateBean(entry.getValue());
        }
    }

}
