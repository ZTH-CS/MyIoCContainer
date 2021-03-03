package com.awa.iocframework.io;

import java.net.URL;

/**
 * 默认资源加载器
 *
 * @author awa
 */
public class ResourceLoader {

    public Resource getResource(String location){
        // 根据当前类路径获取url地址
        URL url = this.getClass().getClassLoader().getResource(location);
        return new UrlResource(url);
    }

}
