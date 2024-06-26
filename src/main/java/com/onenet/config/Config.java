package com.onenet.config;

import com.onenet.exception.OnenetStatus;
import com.onenet.exception.OnenetException;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by zhuocongbin
 * date 2018/3/15
 * Loading global properties
 */
public class Config {
    public static String domainName;
    public static String onenetDomain;
    public static String appVersion;
    public static String fileBase;
    public static String allowUrls;
    static {
        Properties properties = new Properties();
        try {
            properties.load(Config.class.getClassLoader().getResourceAsStream("config.properties"));
            domainName = (String)properties.get("domainName");
            onenetDomain=(String)properties.get("onenetDomain");
            appVersion=(String)properties.getOrDefault("appVersion","2020-05-29");
            fileBase=(String)properties.getOrDefault("fileBase","/home");
            allowUrls=(String)properties.getOrDefault("allowUrls","/");
        } catch (IOException e) {
            throw new OnenetException(OnenetStatus.LOAD_CONFIG_ERROR);
        }
    }
    public static String getDomainName() {
        return domainName;
    }
    public static String getOneNETDomainName() {
        return onenetDomain;
    }
    public static String getAppVersion() { return appVersion; }
    public static String getFileBase() {
        return fileBase;
    }
    public static String getAllowUrls() { return allowUrls; }
}
