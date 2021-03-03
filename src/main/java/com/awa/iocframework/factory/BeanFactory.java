package com.awa.iocframework.factory;

import com.awa.iocframework.entity.BeanDefinition;

/**
 * Bean工厂接口
 *
 * @author awa
 */
public interface BeanFactory {

    /**
     * 根据Bean名获取Bean
     * @param name Bean的名称
     * @return Bean的实例
     * @throws Exception 可能发生的异常
     */
    Object getBean(String name) throws Exception;

    /**
     * 根据Bean的类型获取Bean
     * @param clazz Bean的类型
     * @return Bean的实例
     * @throws Exception 可能发生的异常
     */
    Object getBean(Class clazz) throws Exception;

    /**
     * 向工厂中注入Bean的信息
     * @param name Bean的名称
     * @param beanDefinition Bean的信息
     */
    void registerBeanDefinition(String name, BeanDefinition beanDefinition);
}
