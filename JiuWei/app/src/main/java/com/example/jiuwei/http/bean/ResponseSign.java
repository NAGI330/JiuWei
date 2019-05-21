package com.example.jiuwei.http.bean;

public class ResponseSign {
    public String msg ;
    public String cookie ;
    public String activities;
    public String info;
    public String response;

    @Override
    public String toString() {
        return "ResponseSign{" +
                "msg='" + msg + '\'' +
                ", cookie='" + cookie + '\'' +
                ", activities='" + activities + '\'' +
                ", info='" + info + '\'' +
                ", response='" + response + '\'' +
                '}';
    }
}
