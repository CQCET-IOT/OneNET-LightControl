package com.onenet.config;

import com.onenet.exception.NBStatus;
import com.onenet.exception.OnenetNBException;

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
    static {
        Properties properties = new Properties();
        try {
            properties.load(Config.class.getClassLoader().getResourceAsStream("config.properties"));
            domainName = (String)properties.get("domainName");
            onenetDomain=(String)properties.get("onenetDomain");
        } catch (IOException e) {
            throw new OnenetNBException(NBStatus.LOAD_CONFIG_ERROR);
        }
    }
    public static String getDomainName() {
        return domainName;
    }
    public static String getOneNETDomainName() {
        return onenetDomain;
    }
}
