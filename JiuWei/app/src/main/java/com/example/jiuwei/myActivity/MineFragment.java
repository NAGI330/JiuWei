package com.example.jiuwei.myActivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.jiuwei.LocalSQLite.MySQLiteOpenHelper;
import com.example.jiuwei.R;
import com.example.jiuwei.http.IDataListener;
import com.example.jiuwei.http.Volley;
import com.example.jiuwei.http.bean.ResponceSign;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MineFragment extends Fragment implements View.OnClickListener{
    public int page =1;
    private View view;
    private MySQLiteOpenHelper mySQLiteOpenHelper;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //View view=inflater.inflate(R.layout.fragment_history,container,false);
        view=inflater.inflate(R.layout.fragment_mine,container,false);
            initView(view);
        return view;
    }


    private void initView(View view) {
        ListView lv;//activity_main.xml里的ListView
        //实例化 （，数据库名称，工厂，版本号）
        mySQLiteOpenHelper = new MySQLiteOpenHelper(this.getActivity(), "db_jiuwei", null, 1);
        BaseAdapter adapter;//要实现的类
        final List<Activity> activity = new ArrayList<Activity>();//实体类
        lv = (ListView)view.findViewById(R.id.lvList);

        String pages = String.valueOf(page);
        Map<String,String> map = new HashMap<String, String>();
        map.put("page",pages);
        String select = String.format(getString(R.string.baseURL));
        String url = select+"activity/myActivity";
        Volley.sendJSONRequest(map, url, ResponceSign.class, new IDataListener<ResponceSign>() {
            @Override
            public void onSuccess(ResponceSign responceSign)  {
                //需要解析的活动键值对  key 为活动序号
                String responseAc = responceSign.activities;
                mySQLiteOpenHelper.insertData(mySQLiteOpenHelper.getReadableDatabase(),1,"tb_activityJSON","activityJSON",responseAc);
            }

            @Override
            public void onFailure() {

            }
        });
        //NullPointerException
        try {
            //测试取数据
            String s = mySQLiteOpenHelper.queryData("tb_activityJSON", "activityJSON", "_id=1");
            //Log.i("测试读数据",s);
            JSONObject json = JSON.parseObject(s);
            JSONObject valueCurrent = new JSONObject();
            for (Map.Entry<String, Object> entry : json.entrySet()) {
                //给实体类赋值
                Activity al = new Activity();
                //设置活动ID
                al.setActivityId(entry.getKey());
                //将字符串转化为JSON对象
                valueCurrent = JSON.parseObject(entry.getValue().toString());
                //设置要展示的活动属性
                al.setActivityName("活动名称：" + valueCurrent.get("activity_name"));
                al.setStartDate("活动时间：" + valueCurrent.get("activity_time"));
                al.setActivityType("活动类型：" + valueCurrent.get("activity_type"));
                al.setNumMax("最大人数：" + valueCurrent.get("limit_num"));
                activity.add(al);
            }
        }catch (NullPointerException e){
            Log.i("NullPointerException","NullPointerException");
        }


        adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                // TODO Auto-generated method stub
                return activity.size();//数目
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = MineFragment.this.getLayoutInflater();
                View view;

                if (convertView==null) {
                    //因为getView()返回的对象，adapter会自动赋给ListView
                    view = inflater.inflate(R.layout.item_myactivity, null);
                }else{
                    view=convertView;
                    //Log.i("info","有缓存，不需要重新生成"+position);
                }
                final TextView tv1 = (TextView) view.findViewById(R.id.itemname);//itemname
                tv1.setText(activity.get(position).getActivityName());//设置参数

                final TextView tv2 = (TextView) view.findViewById(R.id.itemtime);//itemage
                tv2.setText(activity.get(position).getStartDate());//设置参数

                final TextView tv3 = (TextView) view.findViewById(R.id.itemmax);//itemname
                tv3.setText(activity.get(position).getNumMax());//设置参数

                final TextView tv4 = (TextView) view.findViewById(R.id.itemtype);//itemage
                tv4.setText(activity.get(position).getActivityType());//设置参数
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
        lv.setAdapter(adapter);
        //获取当前ListView点击的行数，并且得到该数据

    }


    @Override
    public void onClick(View v) {

    }
}