package com.example.jiuwei.http;

/*
 *  回调用层的接口,提供传递功能
 */
public interface IDataListener<M> {
    void onSuccess(M m);
    void onFailure();
}
