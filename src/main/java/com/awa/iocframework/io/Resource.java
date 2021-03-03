package com.awa.iocframework.io;

import java.io.InputStream;

/**
 * 获取资源（配置文件）的接口
 *
 * @author awa
 */
public interface Resource {

    InputStream getInputStream() throws Exception;

}
