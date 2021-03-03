package com.awa.iocframework.entity;

/**
 * @author awa
 *
 * 属性
 */
public class Property {

    // 属性名
    private final String name;

    // 值
    private final Object value;

    public Property(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }
}
