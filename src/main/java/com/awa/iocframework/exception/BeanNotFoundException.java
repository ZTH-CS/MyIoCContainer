package com.awa.iocframework.exception;

/**
 * 当查找的Bean不存在时抛出异常
 *
 * @author awa
 */
public class BeanNotFoundException extends RuntimeException {

    public BeanNotFoundException() {
    }

    public BeanNotFoundException(String beanName) {
        super("Bean " + beanName + " not exists!");
    }
}
