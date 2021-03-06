package com.example.jiuwei.myActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
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
import android.widget.ImageView;
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
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ToJoinFragment extends Fragment implements View.OnClickListener{
    public int page =1;
    private View view=null;
    private MySQLiteOpenHelper mySQLiteOpenHelper;
    private List<Activity> activity ;
    private PullToRefreshListView mPullToRefreshListView;
    BaseAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = new ArrayList<Activity>();
        //View view=inflater.inflate(R.layout.fragment_history,container,false);
        view=inflater.inflate(R.layout.new_fragment_myactivity_activitylist,container,false);
        // 创建下拉刷新的ListView
        mPullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.pull_listview);
        initView(view);
        //两端刷新
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefreshListView.setAdapter(adapter);
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            //下拉刷新
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {


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
                        Log.i("onPostExecute方法","111");
                        activity.removeAll(activity);
                        adapter.notifyDataSetChanged();
                        mPullToRefreshListView.setAdapter(adapter);
                        postRequest(1);
                        initData();
                        mPullToRefreshListView.onRefreshComplete();
                    }
                }.execute();

            }
            //上拉加载更多
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

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
                        Log.i("上拉onPostExecute方法","222");
//                            activity.removeAll(activity);
//                            adapter.notifyDataSetChanged();
//                            mPullToRefreshListView.setAdapter(adapter);
                        page++;
                        Log.i("page的具体数字",String.valueOf(page));
                        postRequest(page);
                        initMoreData();
                        mPullToRefreshListView.onRefreshComplete();
                    }
                }.execute();
            }


        });

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

        activity.removeAll(activity);
        adapter.notifyDataSetChanged();
        mPullToRefreshListView.setAdapter(adapter);
        initData();
    }

    private void postRequest(final int page){
        String pages = String.valueOf(page);
        Map<String,String> map = new HashMap<String, String>();
        map.put("page",pages);
        String select = String.format(getString(R.string.baseURL));
        String url = select+"activity/toJoinActivity";
        Volley.sendJSONRequest(map, url, ResponseSign.class, new IDataListener<ResponseSign>() {
            @Override
            public void onSuccess(ResponseSign responseSign)  {
                String responseMsg = responseSign.msg;
                //如果不为空
                if(responseMsg.equals("numErr_Empty")){
                    Toast.makeText(getActivity(), "没有更多了", Toast.LENGTH_SHORT).show();
                }else if(responseMsg.equals("paginatorSuc")) {

                    String responseAc = responseSign.activities;
                    JSONObject json = JSON.parseObject(responseAc);
                    for (Map.Entry<String, Object> entry : json.entrySet()) {
                        mySQLiteOpenHelper.insertData(mySQLiteOpenHelper.getReadableDatabase(),
                                entry.getKey(), "tb_activityToJoin", "activityToJoin",
                                entry.getValue().toString());
                        JSONObject jsonDate = JSON.parseObject(entry.getValue().toString());
                        //将时间单独从JSON串中提出 作为排序用的标识
                        String date = jsonDate.get("activity_time").toString()
                                .replace("T"," ").replace("Z","");

                        mySQLiteOpenHelper.insertData(mySQLiteOpenHelper.getReadableDatabase(),
                                entry.getKey(), "tb_activityToJoin", "Date",
                                date);
                        //将是否是自己的从JSON串提出
                        String isMine =jsonDate.get("activity_isMine").toString();
                        mySQLiteOpenHelper.insertData(mySQLiteOpenHelper.getReadableDatabase(),
                                entry.getKey(), "tb_activityToJoin", "activityIsMine",
                                isMine);

                    }

                }
            }

            @Override
            public void onFailure() {

            }
        });
    }

    private void initView(View view) {
        //实例化 （，数据库名称，工厂，版本号）
        mySQLiteOpenHelper = new MySQLiteOpenHelper(this.getActivity(), "db_jiuwei", null,1);
        //要实现的类

        postRequest(page);

        mPullToRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Log.i("项目序号",position+"  "+id);
                Intent intent = new Intent(ToJoinFragment.this.getActivity(),Activity_detailedInformation.class);
                //将点击项目的活动ID作为变量传给打开的activity
                intent.putExtra("whichFragment","ToJoin");
                intent.putExtra("acStatus","1");
                intent.putExtra("ToJoinAcId", activity.get(position-1).getActivityId());
                if (activity.get(position-1).getActivityIsMine()){
                    intent.putExtra("isMine","1");
                }else intent.putExtra("isMine","0");


                startActivity(intent);
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


                final ImageView iV = (ImageView)view.findViewById(R.id.itemState);
                iV.setImageDrawable(getResources().getDrawable(R.mipmap.active));

                if(activity.get(position).getActivityIsMine()) {
                    final ImageView iV2 = (ImageView) view.findViewById(R.id.itemCrown);
                    iV2.setImageDrawable(getResources().getDrawable(R.mipmap.crown));
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


    }

    public void initData(){

        try {
            //测试取数据
            //查询tb_activityToJoin表中，所有的活动ID
            String id_list[] = mySQLiteOpenHelper.queryDataALL("tb_activityToJoin", "_id","Date asc");
            int i = 0;
            int min = id_list.length>=4?4:id_list.length;
            for (i = 0; i < min; i++) {
                //依次从依据活动id从Mine表中中查询存储的JSON字符串
                String acJSON = mySQLiteOpenHelper.queryData("tb_activityToJoin",
                        "activityToJoin","_id="+id_list[i]);
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

                if(json.get("activity_isMine").toString().equals("1")){
                    al.setActivityIsMine(true);
                }else if(json.get("activity_isMine").toString().equals("0")){
                    al.setActivityIsMine(false);
                }
                activity.add(al);

            }



        }catch(NullPointerException e){
        }
    }
    public void initMoreData(){

        try {
            //测试取数据

            String id_list[] = mySQLiteOpenHelper.queryDataALL("tb_activityToJoin", "_id","Date asc");
            int i = 0;
            int min = id_list.length>=4*page?4*page:id_list.length;
            for (i = (page-1)*4; i <min; i++) {
                //依次从依据活动id从Mine表中中查询存储的JSON字符串
                String acJSON = mySQLiteOpenHelper.queryData("tb_activityToJoin",
                        "activityToJoin","_id="+id_list[i]);
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
                if(json.get("activity_isMine").toString().equals("1")){
                    al.setActivityIsMine(true);
                }else {
                    al.setActivityIsMine(false);
                }
                activity.add(al);

            }



        }catch(NullPointerException e){
        }
    }
    @Override
    public void onClick(View v) {

    }
}