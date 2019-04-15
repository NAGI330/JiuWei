package com.example.jiuwei.http;

import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class JsonHttpService implements IHttpService{
    private String url;
    private byte[] requestData;
    IHttpListener httpListener;
    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void setRequestData(byte[] requestData) {
        this.requestData = requestData;
    }

    @Override
    public void setHttpCallBack(IHttpListener httpListerner) {
        this.httpListener = httpListerner;
    }
    //实现真实的网络操作
    @Override
    public void execute() {
        httpUrlconnPost();
    }
    HttpURLConnection urlConnection = null;
    public void httpUrlconnPost(){
        URL url = null;
        try{
            url = new URL(this.url);
            //打开http连接
            urlConnection = (HttpURLConnection) url.openConnection();
            //设置连接的超时时间
            urlConnection.setConnectTimeout(6000);
            //不使用缓存
            urlConnection.setUseCaches(false);
            urlConnection.setInstanceFollowRedirects(true);
            //响应的超时时间
            urlConnection.setReadTimeout(3000);
            //设置这个连接是否可以写入数据
            urlConnection.setDoInput(true);
            //设置这个连接是否可以输出数据
            urlConnection.setDoOutput(true);
            //设置请求的方式
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");

            urlConnection.setRequestProperty("csrf-token","AL9VLCPiysJ56dnfcjmsO4M98WZuVSRrhHWGjJINT8ihHOJIv9BqXKv5UcLJJtDm");
            urlConnection.setRequestProperty("NMSL", "WSND");

            urlConnection.connect();



            //使用字节流发送数据
            OutputStream out = urlConnection.getOutputStream();
            BufferedOutputStream bos = new BufferedOutputStream(out);
            if( requestData != null ){
                //把字节数组的数据(请求数据)写入缓冲区
                bos.write(requestData);
            }
//报错
//            Map<String, List<String>> cookies = urlConnection.getHeaderFields();
//            List<String> setCookies = cookies.get("Set-Cookie");
//            System.out.println(setCookies);

            //刷新缓冲区，发送数据
            bos.flush();
            out.close();
            bos.close();

            //获得服务器响应
            if( urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                InputStream in = urlConnection.getInputStream();
                //回调监听器的listener方法
                httpListener.onSuccess(in);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }



}
