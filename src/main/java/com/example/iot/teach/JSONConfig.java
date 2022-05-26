package com.example.iot.teach;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "server.mockjson")
public class JSONConfig {

    private String filepath;
    private String term_auth;
    private String term_access;
    private String term_allslotinfo;

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getTerm_auth() {
        return term_auth;
    }

    public void setTerm_auth(String term_auth) {
        this.term_auth = term_auth;
    }

    public String getTerm_access() {
        return term_access;
    }

    public void setTerm_access(String term_access) {
        this.term_access = term_access;
    }

    public String getTerm_allslotinfo() {
        return term_allslotinfo;
    }

    public void setTerm_allslotinfo(String term_allslotinfo) {
        this.term_allslotinfo = term_allslotinfo;
    }
}
