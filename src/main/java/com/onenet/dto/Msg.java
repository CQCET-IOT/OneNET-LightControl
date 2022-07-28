package com.onenet.dto;

/**
 * 结果返回类对象
 * @version  链式编程
 */
public class Msg {
    private String title;
    private String content;
    private String etraInfo;

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getEtraInfo() {
        return etraInfo;
    }

    private Msg() {
    }
    public static Msg build(){
        return new Msg();
    }
    public Msg title(String title) {
        this.title = title;
        return this;
    }
    public Msg content(String content) {
        this.content = content;
        return this;
    }
    public Msg etraInfo(String etraInfo) {
        this.etraInfo = etraInfo;
        return this;
    }

    @Override
    public String toString() {
        return "Msg{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", etraInfo='" + etraInfo + '\'' +
                '}';
    }
}
