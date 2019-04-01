package com.example.jiuwei.http;


import com.alibaba.fastjson.JSON;

import java.io.UnsupportedEncodingException;

/*
 * 主要流程:
 *  1.一个HttpTask的封装由 httpService，httpListener合成
 *  2.当把一个任务丢到线程池时(ThreadPoolManager)，首先JsonHttpService在工作——将请求
 *      发出去，如果结果为200，通过httpListener.onSuccess(in)传给 JsonHttpListener。
 *  3.JsonHttpListener把拿回来的信息变成了Json字符串，再变成对象(alibabaJson包提供的方法)
 *  4.变成对象后，通过线程切换(Handler)切换到主线程操作，结果传给调用者，之后调用者在主线
 *      程写代码即可。
 */


//不知道调用者给进来的类型，所以用泛型处理
public class HttpTask<T> implements Runnable {
    private IHttpService httpService;
    private IHttpListener httpListener;
    public<T> HttpTask(T requestInfo, String url,
                       IHttpService httpService, IHttpListener httpListerner){
        this.httpService = httpService;
        this.httpListener = httpListerner;
        httpService.setUrl(url);
        httpService.setHttpCallBack(httpListerner);
        if(requestInfo!=null){
            String requestContent = JSON.toJSONString(requestInfo);
            try {
                httpService.setRequestData(requestContent.getBytes("utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void run() {
        httpService.execute();
    }
}
