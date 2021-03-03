package com.awa.iocframework.entity;

/**
 * @author awa
 *
 * Bean中对其他Bean的引用
 */
public class BeanReference {

    // bean的名称
    private String name;

    // bean引用的对象
    private Object reference;

    public BeanReference(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getReference() {
        return reference;
    }

    public void setReference(Object reference) {
        this.reference = reference;
    }
}
