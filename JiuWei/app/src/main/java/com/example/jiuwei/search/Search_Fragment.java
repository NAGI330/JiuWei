package com.example.jiuwei.search;

import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.jiuwei.LocalSQLite.MySQLiteOpenHelper;
import com.example.jiuwei.R;
import com.example.jiuwei.http.IDataListener;
import com.example.jiuwei.http.Volley;
import com.example.jiuwei.http.bean.ResponseSign;
import com.example.jiuwei.myActivity.Activity;
import com.example.jiuwei.personalInfo.User;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Search_Fragment extends Fragment {

    private ListView listView;
    private Button searchBtn;
    private EditText searchEt = null;
    private MySQLiteOpenHelper mySQLiteOpenHelper;
    BaseAdapter adapter;
    private List<Activity> activity = new ArrayList<Activity>();
    private List<User>  user = new ArrayList<User>();
    private String joinInAcId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_search,container,false);
        listView = view.findViewById(R.id.lvList);
        searchBtn = (Button) view.findViewById(R.id.searchBtn);

        searchEt =(EditText) view.findViewById(R.id.searchEt);
        initView(view);
        Log.d("tag", "进入search了");
        return view;
    }




    private void initView(View view) {
        //实例化 （，数据库名称，工厂，版本号）
        mySQLiteOpenHelper = new MySQLiteOpenHelper(this.getActivity(), "db_jiuwei", null,1);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String text =searchEt.getText().toString();
                activity.removeAll(activity);
                user.removeAll(user);

                if(text.equals("")){
                    Toast.makeText(getActivity(), "请输入要搜索的内容", Toast.LENGTH_SHORT).show();
                }else {
                    new Thread(){
                        @Override
                        public void run() {
                            String select = String.format(getString(R.string.baseURL));
                            String url = select+"activity/search";
                            final String searchResult =sendGet(url,text);
                            Log.i("aaaaaaaaaaa",searchResult);
                            JSONObject json = JSON.parseObject(searchResult);
                            //将返回结果的第一层 response拆开
                            try {
                                String jsonContent = json.get("response").toString();
                                if(jsonContent.equals("query null")){
                                    Looper.prepare();
                                    Toast.makeText(getActivity(), "抱歉，未能找到相关活动", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }else{
                                    JSONObject json2 = JSON.parseObject(jsonContent);
                                    //开始遍历得到的结果并存库
                                    for (Map.Entry<String, Object> entry : json2.entrySet()) {
                                        mySQLiteOpenHelper.insertData(mySQLiteOpenHelper.getReadableDatabase(),
                                                entry.getKey(), "tb_activitySearch", "activitySearch",
                                                entry.getValue().toString());
                                }

                                }
                            }catch (NullPointerException e){

                            }



                        }
                    }.start();
                    initData();
                    adapter.notifyDataSetChanged();
                    listView.setAdapter(adapter);
                    mySQLiteOpenHelper.deleteDataALL("tb_activitySearch");
                }

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {


            }
        });

        adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                // TODO Auto-generated method stub
                return activity.size();//数目
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = Search_Fragment.this.getLayoutInflater();
                View view;

                if (convertView==null) {
                    //因为getView()返回的对象，adapter会自动赋给ListView
                    view = inflater.inflate(R.layout.item_searchactivity, null);
                }else{
                    view=convertView;
                    //Log.i("info","有缓存，不需要重新生成"+position);
                }


                final TextView tv1 = (TextView) view.findViewById(R.id.itemAcId);
                tv1.setText(activity.get(position).getActivityId());//设置参数

                final TextView tv2 = (TextView) view.findViewById(R.id.itemAcName);
                tv2.setText(activity.get(position).getActivityName());//设置参数

                final TextView tv3 = (TextView) view.findViewById(R.id.itemUserId);
                tv3.setText(user.get(position).getUserId());//设置参数

                final TextView tv4 = (TextView) view.findViewById(R.id.itemUserNickname);
                tv4.setText(user.get(position).getNickname());//设置参数

                final TextView tv5 = (TextView) view.findViewById(R.id.itemAcType);
                tv5.setText(activity.get(position).getActivityType());//设置参数

                final TextView tv6 = (TextView) view.findViewById(R.id.itemAcDes);
                tv6.setText(activity.get(position).getActivityDescribe());//设置参数

                final TextView tv7 = (TextView) view.findViewById(R.id.itemAcTime);
                tv7.setText(activity.get(position).getStartDate());//设置参数
                String select = String.format(getString(R.string.baseURL));
                final String url = select+"activity/joinInActivity";
                final Map<String,String> map = new HashMap<String, String>();
                map.put("activity_id",activity.get(position).getActivityId());
                final Button btn = view.findViewById(R.id.joinIn);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Volley.sendJSONRequest(map, url, ResponseSign.class, new IDataListener<ResponseSign>() {
                            @Override
                            public void onSuccess(ResponseSign responseSign) {
                                if(responseSign.msg.equals("join successfully")){
                                    Toast.makeText(getActivity(), "加入活动成功", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure() {

                            }
                        });
                    }
                });
                return view;
            }
            @Override
            public long getItemId(int position) {//取在列表中与指定索引对应的行id
                return 0;
            }
            @Override
            public Object getItem(int position) {//获取数据集中与指定索引对应的数据项
                return null;
            }
        };


    }
    public void initData(){

        try {
            //查询tb_activityMine表中，所有的活动ID
            String id_list[] = mySQLiteOpenHelper.queryDataALL("tb_activitySearch", "_id",null);
            int i = 0;
            for (i = 0; i <id_list.length; i++) {
                //依次从依据活动id从Mine表中中查询存储的JSON字符串
                String acJSON = mySQLiteOpenHelper.queryData("tb_activitySearch",
                        "activitySearch","_id="+id_list[i]);
                JSONObject json = JSON.parseObject(acJSON);
                //给实体类赋值
                Activity al = new Activity();
                User ul = new User();
                //设置活动ID
                al.setActivityId(id_list[i]);
                //将字符串转化为JSON对象
                //设置要展示的活动属性
                al.setActivityName("" + json.get("activity_name"));
                al.setStartDate("" + json.get("activity_time").toString()
                        .replace("T"," ").replace("Z",""));
                al.setActivityType("" + json.get("activity_type"));

                ul.setNickname(json.get("owner_nickname").toString());
                ul.setUserId(json.get("owner_id").toString());
                activity.add(al);
                user.add(ul);

            }



        }catch(NullPointerException e){
        }
    }

    public static String sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "/q=" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }





}