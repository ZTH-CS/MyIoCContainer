package com.awa.iocframework.reader;

/**
 * Bean信息读取器接口
 *
 * @author awa
 */
public interface BeanDefinitionReader {

    /**
     * 根据位置加载Bean的信息
     * @param location 文件位置
     * @throws Exception 可能发生的异常
     */
    void loadBeanDefinitions(String location) throws Exception;

}
