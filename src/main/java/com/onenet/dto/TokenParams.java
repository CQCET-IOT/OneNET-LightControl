package com.onenet.dto;

public class TokenParams {
    String sourcetype;
    String userid;
    String projectid;
    String groupid;
    String productid;
    String deviceid;
    String apikey;
    int et;
    String version;
    String signmethod;
    String token;

    @Override
    public String toString() {
        return "TokenParams{" +
                "sourcetype='" + sourcetype + '\'' +
                ", userid='" + userid + '\'' +
                ", projectid='" + projectid + '\'' +
                ", groupid='" + groupid + '\'' +
                ", productid='" + productid + '\'' +
                ", deviceid='" + deviceid + '\'' +
                ", apikey='" + apikey + '\'' +
                ", et=" + et +
                ", version='" + version + '\'' +
                ", signmethod='" + signmethod + '\'' +
                ", token='" + token + '\'' +
                '}';
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public String getSourcetype() {
        return sourcetype;
    }

    public void setSourcetype(String sourcetype) {
        this.sourcetype = sourcetype;
    }

    public String getProjectid() {
        return projectid;
    }

    public void setProjectid(String projectid) {
        this.projectid = projectid;
    }

    public String getProductid() {
        return productid;
    }

    public void setProductid(String productid) {
        this.productid = productid;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public int getEt() {
        return et;
    }

    public void setEt(int et) {
        this.et = et;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSignmethod() {
        return signmethod;
    }

    public void setSignmethod(String signmethod) {
        this.signmethod = signmethod;
    }
}
