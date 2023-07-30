package com.yikolemon.client;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import java.io.FileReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.Vector;


/**
 * @author yikolemon
 * @date 2023/7/30 20:49
 * @description
 */
public class Client {




    /**
     * 去个人中心获取
     */
    private URL cnblogsUrl;

    private String username;

    private String token;

    private XmlRpcClient xmlRpcClient;

    private static Client instance;

    private void setCnblogsUrl(String cnblogsUrl) {
        try {
            this.cnblogsUrl = new URL(cnblogsUrl);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private void setUsername(String username) {
        this.username = username;
    }

    private void setToken(String token) {
        this.token = token;
    }

    private void setXmlRpcClient(XmlRpcClient xmlRpcClient) {
        this.xmlRpcClient = xmlRpcClient;
    }

    private URL getCnblogsUrl() {
        return cnblogsUrl;
    }

    private String getUsername() {
        return username;
    }

    private String getToken() {
        return token;
    }

    public synchronized static Client getInstance() {
        if(instance == null ) { // 一重检查
            synchronized (Client.class) {
                if(instance == null) { // 二重检查
                    instance = new Client();
                    ClassLoader classLoader = instance.getClass().getClassLoader();
                    URL resource = classLoader.getResource("application.properties");
                    //path 为父项目target/classes的路径，classes下面就是父项目的class文件以及所有的配置文件
                    String path = resource.getPath();
                    try {
                        Properties properties = new Properties();
                        properties.load(new FileReader(resource.getPath()));
                        String tempUrl = properties.getProperty("cnblogs.url");
                        instance.setCnblogsUrl(tempUrl);
                        instance.setUsername(properties.getProperty("cnblogs.username"));
                        instance.setToken(properties.getProperty("cnblogs.token"));
                        XmlRpcClient tempRpcClient= new XmlRpcClient();
                        XmlRpcClientConfigImpl xmlRpcClientConfig = new XmlRpcClientConfigImpl();
                        xmlRpcClientConfig.setServerURL(instance.getCnblogsUrl());
                        tempRpcClient.setConfig(xmlRpcClientConfig);
                        instance.setXmlRpcClient(tempRpcClient);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
        return instance;
    }

    /**
     * blogger.getUsersBlogs获取用户博客信息
     * @return
     */
    public Object getUserBlogs(){
        Vector params = new Vector();
        //appid
        params.addElement("metaWeblog");
        //username
        params.addElement(username);
        params.addElement(token);
        try {
            Object execute = xmlRpcClient.execute("blogger.getUsersBlogs", params);
            return execute;
        } catch (XmlRpcException e) {
            throw new RuntimeException(e);
        }
    }



}
