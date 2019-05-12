package com.example.jiuwei.push;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
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
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Push_Fragment extends Fragment implements View.OnClickListener{

    private PullToRefreshListView pullToRefreshListView;
    private MySQLiteOpenHelper mySQLiteOpenHelper;
    BaseAdapter adapter;
    private List<Activity> activity = new ArrayList<Activity>();
    private List<User>  user = new ArrayList<User>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_push,container,false);
        Log.d("tag", "进入push了");
        pullToRefreshListView = view.findViewById(R.id.pull_listview);
        initView(view);

        pullToRefreshListView.setAdapter(adapter);
        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                //这里用异步任务来模拟从网络上获取数据
                new AsyncTask<Void, Void, Void>() {



                    @Override
                    protected Void doInBackground(Void... arg0) {
                        try {
                            Thread.sleep(500);//线程休眠
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        return null;
                    }

                    protected void onPostExecute(Void result) {
                        //添加新的数据
                        user.removeAll(user);
                        activity.removeAll(activity);
                        postRequest();
                        initData();
                        adapter.notifyDataSetChanged();
                        pullToRefreshListView.setAdapter(adapter);
                        mySQLiteOpenHelper.deleteDataALL("tb_activityPush");
                        pullToRefreshListView.onRefreshComplete();
                    }
                }.execute();
            }
        });
        return view;
    }



    private void initView(View view) {
        mySQLiteOpenHelper = new MySQLiteOpenHelper(this.getActivity(), "db_jiuwei", null,1);
        activity.removeAll(activity);
        user.removeAll(user);
        postRequest();
        initData();

//        mySQLiteOpenHelper.deleteDataALL("tb_activityPush");
        adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                // TODO Auto-generated method stub
                return activity.size();//数目
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = Push_Fragment.this.getLayoutInflater();
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
    private void postRequest(){
        String select = String.format(getString(R.string.baseURL));
        String url = select+"activity/pushActivity";
        Volley.sendJSONRequest(null, url, ResponseSign.class, new IDataListener<ResponseSign>() {
            @Override
            public void onSuccess(ResponseSign responseSign)  {
                //将返回结果的第一层 response拆开
                String pushResult = responseSign.response;
                Log.i("hhhhhhhhhhhhhhhhhhhhh",pushResult);
                JSONObject json = JSON.parseObject(pushResult);
                try {
                    int i =0;
                    //开始遍历得到的结果并存库
                    for (Map.Entry<String, Object> entry : json.entrySet()) {
                        mySQLiteOpenHelper.insertPushData(mySQLiteOpenHelper.getReadableDatabase(),
                                i,entry.getKey(), "tb_activityPush", "activityPush",
                                entry.getValue().toString());
                        i++;

                    }
                }catch (NullPointerException e){

                }

            }

            @Override
            public void onFailure() {

            }
        });

    }
    public void initData(){

        try {
            //查询tb_activityMine表中，所有的活动ID
            String id_list[] = mySQLiteOpenHelper.queryDataALL("tb_activityPush", "_id",null);
            int i = 0;
//            Log.i("ssssssssssssssssssssss",String.valueOf(id_list.length));
            for (i = 0; i <id_list.length; i++) {
                //依次从依据活动id从Mine表中中查询存储的JSON字符串
                String acJSON = mySQLiteOpenHelper.queryData("tb_activityPush",
                        "activityPush","_id="+id_list[i]);
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

    @Override
    public void onClick(View v) {

    }
}