package com.awa.iocframework.reader;

import com.awa.iocframework.entity.BeanDefinition;
import com.awa.iocframework.io.ResourceLoader;

import java.util.HashMap;
import java.util.Map;

/**
 * 抽象Bean信息读取器
 *
 * @author awa
 */
public abstract class AbstractBeanDefinitionReader implements BeanDefinitionReader {

    private Map<String, BeanDefinition> registry;

    private ResourceLoader resourceLoader;

    public AbstractBeanDefinitionReader(ResourceLoader resourceLoader) {
        this.registry = new HashMap<>();
        this.resourceLoader = resourceLoader;
    }

    public Map<String, BeanDefinition> getRegistry() {
        return registry;
    }

    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }
}
