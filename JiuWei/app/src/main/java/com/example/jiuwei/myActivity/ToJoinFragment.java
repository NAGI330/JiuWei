package com.example.jiuwei.myActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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


public class ToJoinFragment extends Fragment implements View.OnClickListener{
    public int page =1;
    private View view;
    private MySQLiteOpenHelper mySQLiteOpenHelper;
    final List<Activity> activity = new ArrayList<Activity>();//实体类
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //View view=inflater.inflate(R.layout.fragment_history,container,false);
        view=inflater.inflate(R.layout.fragment_myactivity_activitylist,container,false);
        initView(view);
        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.CART_BROADCAST");
        BroadcastReceiver mItemViewListClickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent){
                String msgR = intent.getStringExtra("data");
                if("refresh".equals(msgR)){
                    refresh();
                }
            }
        };
        broadcastManager.registerReceiver(mItemViewListClickReceiver, intentFilter);
    }

    private void refresh() {
        //getActivity().onAttachFragment(this);
        initData();

    }


    private void initView(View view) {
        ListView lv;//activity_main.xml里的ListView
        //实例化 （，数据库名称，工厂，版本号）
        mySQLiteOpenHelper = new MySQLiteOpenHelper(this.getActivity(), "db_jiuwei", null, 1);
        BaseAdapter adapter;//要实现的类
        lv = (ListView)view.findViewById(R.id.lvList);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Log.i("项目序号",position+"  "+id);
                Intent intent = new Intent(ToJoinFragment.this.getActivity(),Activity_detailedInformation.class);
                //告诉它 这是哪个Fragment传过来的
                intent.putExtra("whichFragment", "ToJoin");
                //将点击项目的活动ID作为变量传给打开的activity
                intent.putExtra("ToJoinAcId", activity.get(position).getActivityId());
                startActivity(intent);
            }
        });


        String pages = String.valueOf(page);
        Map<String,String> map = new HashMap<String, String>();
        map.put("page",pages);
        String select = String.format(getString(R.string.baseURL));
        String url = select+"activity/tojoinActivity";
        Volley.sendJSONRequest(map, url, ResponceSign.class, new IDataListener<ResponceSign>() {
            @Override
            public void onSuccess(ResponceSign responceSign)  {
                //需要解析的活动键值对  key 为活动序号
                String responseAc = responceSign.activities;
                JSONObject json = JSON.parseObject(responseAc);
                for (Map.Entry<String, Object> entry : json.entrySet()) {
                    mySQLiteOpenHelper.insertData(mySQLiteOpenHelper.getReadableDatabase(),
                            entry.getKey(),"tb_activityToJoin","activityToJoin",
                            entry.getValue().toString());

                }
            }

            @Override
            public void onFailure() {

            }
        });

        initData();
        adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                // TODO Auto-generated method stub
                return activity.size();//数目
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = ToJoinFragment.this.getLayoutInflater();
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

                final TextView tv3 = (TextView) view.findViewById(R.id.itemtype);//itemage
                tv3.setText(activity.get(position).getActivityType());//设置参数
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

    public void initData(){
        //NullPointerException
        try {
            //测试取数据
            //查询tb_activityMine表中，所有的活动ID
            String id_list[] = mySQLiteOpenHelper.queryDataALL("tb_activityToJoin", "_id");
            int i = 0;
            for (i = 0; i < id_list.length; i++) {
                //依次从依据活动id从Mine表中中查询存储的JSON字符串
                String acJSON = mySQLiteOpenHelper.queryData("tb_activityToJoin",
                        "activityToJoin","_id="+id_list[i]);
                Log.i("aaaaaaaaaaaa",acJSON);
                JSONObject json = JSON.parseObject(acJSON);
                //给实体类赋值
                Activity al = new Activity();
                //设置活动ID
                al.setActivityId(id_list[i]);
                //将字符串转化为JSON对象
                //设置要展示的活动属性
                al.setActivityName("" + json.get("activity_name"));
                al.setStartDate("" + json.get("activity_time").toString()
                        .replace("T"," ").replace("Z",""));
                al.setActivityType("" + json.get("activity_type"));
                activity.add(al);

            }

        }catch(NullPointerException e){
        }
    }
    @Override
    public void onClick(View v) {

    }
}