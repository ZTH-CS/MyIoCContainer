package com.awa.iocframework.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author awa
 *
 * Bean的属性列表
 */
public class Properties {

    private final List<Property> propertyList = new ArrayList<Property>();

    public Properties() {
    }

    public void addProperty(Property property){
        propertyList.add(property);
    }

    public void addProperty(Property... properties){
        Collections.addAll(propertyList, properties);
    }

    public List<Property> getPropertyList(){
        return propertyList;
    }
}
