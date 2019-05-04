package com.example.jiuwei.http;

//不是java.util.logging.Handler

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.alibaba.fastjson.JSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

//同样用到泛型
public class JsonHttpListener<M>implements IHttpListener {
    //字节码
    Class<M> responceClass;
    IDataListener<M> dataListener;

    //用于切换线程
    Handler handler = new Handler(Looper.getMainLooper());
    //构造方法
    public JsonHttpListener(Class<M> responceClass,IDataListener dataListener){
        this.responceClass = responceClass;
        this.dataListener = dataListener;


    }

    @Override
    public void onSuccess(InputStream inputStream) {
        //获取响应结果，将byte数据转化为string数据(Json字符串)

        String content = getContent(inputStream);
        Log.i("content",content);

        //结果(Json字符串)转换成对象
         final M responce = JSON.parseObject(content,responceClass);


//        JSONObject json = JSON.parseObject(content);
//        Log.i("content.get'cookie'",json.get("Cookie").toString());
//
//        JSONObject cookie = (JSONObject)json.get("Cookie");
//       Log.i("get session_id", cookie.get("session_id").toString());
//        String cookie_value = "session_id=" + cookie.get("session_id");
//        Log.i("cookie_value", cookie_value);


        //把结果传送到调用层,handler用于将子线程切换到主线程
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(dataListener!=null){
                    dataListener.onSuccess(responce);
                }
            }
        });

    }

    @Override
    public void onFailure() {
        //把结果传送到调用层
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(dataListener!=null){
                    dataListener.onFailure();
                }
            }
        });
    }
    /**
     * 将流转换成字符串
     * @param inputStream
     * @return
     */
    private String getContent(InputStream inputStream) {
        String content = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String line = null;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    inputStream.close();
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }
}
