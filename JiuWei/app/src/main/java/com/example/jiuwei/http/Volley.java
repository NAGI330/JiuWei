package com.example.jiuwei.http;

//外部通过该接口调用com.example.jiuwei.http内的相关内容

public class Volley {
    //请求信息，返回信息
    public static<T,M> void sendJSONRequest(T requestInfo, String url, Class<M> responce,
                                             IDataListener<M> dataListener)
    {
        IHttpService httpService = new JsonHttpService();
        IHttpListener httpListener = new JsonHttpListener<>(responce,dataListener);
        HttpTask<T> httpTask = new HttpTask<T>(requestInfo,url,httpService,httpListener);
        ThreadPoolManager.getOurInstance().execute(httpTask);

    }




}
