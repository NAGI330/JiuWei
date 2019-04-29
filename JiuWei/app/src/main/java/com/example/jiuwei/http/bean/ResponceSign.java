package com.example.jiuwei.http.bean;

public class ResponceSign{
    public String msg;
    public String cookie;
    public String activities;


    @Override
    public String toString() {
        return "ResponceSign{"+
                "msg='"+msg+'\''+
                "Cookie='"+cookie+'\''+
                "activities='"+activities+'\''+
                '}';
    }
}
