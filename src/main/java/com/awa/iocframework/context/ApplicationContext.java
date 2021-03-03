package com.awa.iocframework.context;


/**
 * @author awa
 *
 * 应用程序上下文
 */
public interface ApplicationContext {

    /**
     * 根据Bean名字获取bean
     * @param name bean的名字
     * @return bean实例
     * @throws Exception 可能发生的异常
     */
    Object getBean(String name) throws Exception;

    /**
     * 根据Bean类型获取bean
     * @param clazz bean的类型
     * @return bean实例
     * @throws Exception 可能发生的异常
     */
    Object getBean(Class clazz) throws Exception;

}
