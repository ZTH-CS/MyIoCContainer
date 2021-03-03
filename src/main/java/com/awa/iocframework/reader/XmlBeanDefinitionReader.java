package com.awa.iocframework.reader;

import com.awa.iocframework.annotation.*;
import com.awa.iocframework.entity.BeanDefinition;
import com.awa.iocframework.entity.BeanReference;
import com.awa.iocframework.entity.Property;
import com.awa.iocframework.entity.Properties;
import com.awa.iocframework.io.ResourceLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 通过XML文件读取Bean信息的读取器
 *
 * @author awa
 */
public class XmlBeanDefinitionReader extends AbstractBeanDefinitionReader{

    public XmlBeanDefinitionReader(ResourceLoader resourceLoader) {
        super(resourceLoader);
    }

    @Override
    public void loadBeanDefinitions(String location) throws Exception {
        InputStream inputStream = getResourceLoader().getResource(location).getInputStream();
        doLoadBeanDefinitions(inputStream);
    }

    /**
     * 实际通过输入流加载Bean信息
     * @param inputStream 输入流
     * @throws Exception 可能发生的异常
     */
    private void doLoadBeanDefinitions(InputStream inputStream) throws Exception{
        try{
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            // 解析xml document并注册bean
            registerBeanDefinitions(document);
        }finally {
            inputStream.close();
        }
    }

    /**
     * 注册Bean信息
     *
     * @param document 文档类型数据
     */
    private void registerBeanDefinitions(Document document){
        Element root = document.getDocumentElement();
        // 从根路径递归解析
        parseBeanDefinitions(root);
    }

    /**
     * 通过节点信息解析Bean信息
     *
     * @param root 节点
     */
    private void parseBeanDefinitions(Element root){
        NodeList nodeList = root.getChildNodes();
        String basePackage = null;
        for(int i = 0; i < nodeList.getLength(); i++){
            if(nodeList.item(i) instanceof Element){
                Element ele = (Element) nodeList.item(i);
                if(ele.getTagName().equals("component-scan")){
                    basePackage = ele.getAttribute("base-package");
                    continue;
                }
                if(ele.getTagName().equals("bean")){
                    processBeanDefinitions(ele);
                }
            }
        }
        if(basePackage != null){
            parseAnnotation(basePackage);
        }
    }

    /* ================================================================
     * ====================解析XML文件中的Bean==========================
     * ================================================================*/

    private void processBeanDefinitions(Element element){
        String name= element.getAttribute("id");
        String className = element.getAttribute("class");
        boolean isSingleton = true;
        if(element.hasAttribute("scope") && "prototype".equals(element.getAttribute("scope"))){
            isSingleton = false;
        }

        BeanDefinition bd = new BeanDefinition();
        processProperties(element, bd);
        bd.setBeanClassName(className);
        bd.setSingleton(isSingleton);
        getRegistry().put(name, bd);
    }

    private void processProperties(Element element, BeanDefinition bd){
        NodeList list = element.getElementsByTagName("property");
        Properties properties = bd.getProperties();
        for(int i = 0; i < list.getLength(); i++){
            Node node = list.item(i);
            if(node instanceof Element){
                Element ele = (Element) node;
                String name = ele.getAttribute("name");
                String value = ele.getAttribute("value");
                if(value != null && value.length() > 0){
                    properties.addProperty(new Property(name, value));
                }else{
                    String ref = ele.getAttribute("ref");
                    if(ref == null || ref.equals("")){
                        throw new IllegalArgumentException("Configuration problem: <property> element for property '" + name + "' must specify a ref or value");
                    }
                    BeanReference br = new BeanReference(ref);
                    properties.addProperty(new Property(name, br));
                }
            }
        }
    }

    /*================================================================
     * ======================根据注解解析Bean==========================
     * ===============================================================*/

    /**
     * 解析注解
     * @param basePackage 注解包名
     */
    private void parseAnnotation(String basePackage){
        Set<Class<?>> classes = getClasses(basePackage);
        for(Class clazz : classes){
            processAnnotationBeanDefinition(clazz);
        }
    }

    private void processAnnotationBeanDefinition(Class<?> clazz){
        if(clazz.isAnnotationPresent(Component.class)){
            String name = clazz.getAnnotation(Component.class).name();
            if(name.equals("") || name.length() == 0){
                name = clazz.getName();
            }
            String className = clazz.getName();
            boolean isSingleton = true;
            if(clazz.isAnnotationPresent(Scope.class) && "prototype".equals(clazz.getAnnotation(Scope.class).value())){
                isSingleton = false;
            }

            BeanDefinition bd = new BeanDefinition();
            processAnnotationProperty(clazz, bd);
            bd.setBeanClassName(className);
            bd.setSingleton(isSingleton);
            getRegistry().put(name, bd);
        }
    }

    private void processAnnotationProperty(Class<?> clazz, BeanDefinition bd){
        Field[] fields = clazz.getDeclaredFields();

        for(Field field : fields){
            String name = field.getName();
            if(field.isAnnotationPresent(Value.class)){
                String value = field.getAnnotation(Value.class).value();
                bd.getProperties().addProperty(new Property(name, value));
            }else if(field.isAnnotationPresent(Autowired.class)){
                if(field.isAnnotationPresent(Qualifier.class)){
                    String ref = field.getAnnotation(Qualifier.class).value();
                    if(ref == null || ref.equals("")){
                        throw new IllegalArgumentException("the value of Qualifier should not be null!");
                    }
                    BeanReference br = new BeanReference(ref);
                    bd.getProperties().addProperty(new Property(name, br));
                }else{
                    String ref = field.getType().getName();
                    BeanReference br = new BeanReference(ref);
                    bd.getProperties().addProperty(new Property(name, br));
                }
            }
        }
    }

    /**
     * 根据包路径获得类属性集合
     * @param packageName 包路径
     * @return 类属性集合
     */
    private Set<Class<?>> getClasses(String packageName){
        Set<Class<?>> classes = new HashSet<>();
        boolean recursive = true;
        String directory = packageName.replace('.', '/');
        Enumeration<URL> dirs = null;
        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(
                    directory);
            // 循环迭代下去
            while (dirs.hasMoreElements()) {
                // 获取下一个元素
                URL url = dirs.nextElement();
                // 得到协议的名称
                String protocol = url.getProtocol();
                // 如果是以文件的形式保存在服务器上
                if ("file".equals(protocol)) {
                    // 获取包的物理路径
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    // 以文件的方式扫描整个包下的文件 并添加到集合中
                    findAndAddClassesInPackageByFile(packageName, filePath,
                            recursive, classes);
                } else if ("jar".equals(protocol)) {
                    // 如果是jar包文件
                    findAndAddClassesInPackageByJar(packageName, directory,
                            ((JarURLConnection) url.openConnection()).getJarFile(), // 获取jar
                            recursive, classes);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return classes;
    }

    // 根据类文件扫描并加载包内的类
    private void findAndAddClassesInPackageByFile(String packageName,
                                                  String packagePath, final boolean recursive, Set<Class<?>> classes) {
        // 获取此包的目录 建立一个File
        File dir = new File(packagePath);
        // 如果不存在或者 也不是目录就直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            // log.warn("用户定义包名 " + packageName + " 下没有任何文件");
            return;
        }
        // 如果存在 就获取包下的所有文件 包括目录
        File[] dirfiles = dir.listFiles(new FileFilter() {
            // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
            public boolean accept(File file) {
                return (recursive && file.isDirectory())
                        || (file.getName().endsWith(".class"));
            }
        });
        // 循环所有文件
        assert dirfiles != null;
        for (File file : dirfiles) {
            // 如果是目录 则继续扫描
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(packageName + "."
                                + file.getName(), file.getAbsolutePath(), recursive,
                        classes);
            } else {
                // 如果是java类文件 去掉后面的.class 只留下类名
                String className = file.getName().substring(0,
                        file.getName().length() - 6);
                try {
                    // 添加到集合中去
                    classes.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + '.' + className));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 根据jar文件扫描并加载类
    private void findAndAddClassesInPackageByJar(String packageName,
                                                 String directory, JarFile jar, boolean recursive, Set<Class<?>> classes){
        // 从此jar包 得到一个枚举类
        Enumeration<JarEntry> entries = jar.entries();
        // 同样的进行循环迭代
        while (entries.hasMoreElements()) {
            // 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            // 如果是以/开头的
            if (name.charAt(0) == '/') {
                // 获取后面的字符串
                name = name.substring(1);
            }
            // 如果前半部分和定义的包名相同
            if (name.startsWith(directory)) {
                int idx = name.lastIndexOf('/');
                // 如果以"/"结尾 是一个包
                if (idx != -1) {
                    // 获取包名 把"/"替换成"."
                    packageName = name.substring(0, idx)
                            .replace('/', '.');
                }
                // 如果可以迭代下去 并且是一个包
                if ((idx != -1) || recursive) {
                    // 如果是一个.class文件 而且不是目录
                    if (name.endsWith(".class")
                            && !entry.isDirectory()) {
                        // 去掉后面的".class" 获取真正的类名
                        String className = name.substring(
                                packageName.length() + 1, name
                                        .length() - 6);
                        try {
                            // 添加到classes
                            classes.add(Class
                                    .forName(packageName + '.'
                                            + className));
                        } catch (ClassNotFoundException e) {
                            // log
                            // .error("添加用户自定义视图类错误 找不到此类的.class文件");
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
