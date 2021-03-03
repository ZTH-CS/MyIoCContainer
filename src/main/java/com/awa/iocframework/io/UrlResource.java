package com.awa.iocframework.io;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * 根据url获取资源
 *
 * @author awa
 */
public class UrlResource implements Resource {

    private URL url;

    public UrlResource(URL url) {
        this.url = url;
    }

    @Override
    public InputStream getInputStream() throws Exception {
        URLConnection urlConnection = url.openConnection();
        urlConnection.connect();
        return urlConnection.getInputStream();
    }
}
