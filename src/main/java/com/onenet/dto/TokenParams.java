package com.onenet.dto;

public class TokenParams {
    String userid;
    String apikey;
    int et;
    String version;
    String signmethod;
    String token;

    @Override
    public String toString() {
        return "TokenParams{" +
                "userid='" + userid + '\'' +
                ", apikey='" + apikey + '\'' +
                ", et=" + et +
                ", version='" + version + '\'' +
                ", signmethod='" + signmethod + '\'' +
                ", token='" + token + '\'' +
                '}';
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
