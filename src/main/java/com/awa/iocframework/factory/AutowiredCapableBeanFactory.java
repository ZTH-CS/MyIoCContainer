package com.awa.iocframework.factory;

import com.awa.iocframework.entity.BeanDefinition;
import com.awa.iocframework.entity.BeanReference;
import com.awa.iocframework.entity.Property;
import com.awa.iocframework.exception.BeanNotFoundException;

import java.lang.reflect.Field;

/**
 * 自动注入的Bean工厂
 *
 * @author awa
 */
public class AutowiredCapableBeanFactory extends AbstractBeanFactory{

    @Override
    Object doCreateBean(BeanDefinition beanDefinition) throws Exception {
        if(beanDefinition.isSingleton() && beanDefinition.getBean() != null){
            return beanDefinition.getBean();
        }

        Object bean = beanDefinition.getBeanClass().newInstance();
        if(beanDefinition.isSingleton()){
            beanDefinition.setBean(bean);
        }

        applyPropertyList(bean, beanDefinition);

        return bean;
    }

    /**
     * 对Bean进行依赖注入
     * @param bean 需要依赖注入的Bean
     * @param beanDefinition Bean的信息
     * @throws Exception 可能发生的异常
     */
    private void applyPropertyList(Object bean, BeanDefinition beanDefinition) throws Exception{
        for(Property property : beanDefinition.getProperties().getPropertyList()){
            Field field = bean.getClass().getDeclaredField(property.getName());
            Object value = property.getValue();

            if(value instanceof BeanReference){
                BeanReference beanReference = (BeanReference) value;
                BeanDefinition refDefinition = beanDefinitionMap.get(beanReference.getName());
                if(refDefinition == null){
                    throw new BeanNotFoundException(beanReference.getName());
                }
                if(refDefinition.getBean() == null){
                    value = doCreateBean(refDefinition);
                }
            }

            field.setAccessible(true);
            field.set(bean, value);
        }
    }
}
