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
import com.example.jiuwei.datetimeselect.DateUtil;
import com.example.jiuwei.http.IDataListener;
import com.example.jiuwei.http.Volley;
import com.example.jiuwei.http.bean.ResponseSign;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MineFragment extends Fragment implements View.OnClickListener{
    public int page =1;
    private View view=null;
    private MySQLiteOpenHelper mySQLiteOpenHelper;
    private List<Activity> activity = new ArrayList<Activity>();
    private PullToRefreshListView mPullToRefreshListView;
    BaseAdapter adapter;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String systemTime = simpleDateFormat.format(new Date());

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //View view=inflater.inflate(R.layout.fragment_history,container,false);
        view=inflater.inflate(R.layout.new_fragment_myactivity_activitylist,container,false);
        // 创建下拉刷新的ListView
        mPullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.pull_listview);
        initView(view);
        //两端刷新
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefreshListView.setAdapter(adapter);
        //adapter.notifyDataSetChanged();
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
                            activity.removeAll(activity);
                            postRequest(1);
                            initData();
                            adapter.notifyDataSetChanged();
                            mPullToRefreshListView.setAdapter(adapter);
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

    //通过数据库查询 orderby实现 故该方法暂时没用
    private void dateSort(){
        Collections.sort(activity, new Comparator<Activity>() {
            @Override
            public int compare(Activity t0, Activity t1) {
                Date date1 = DateUtil.stringToDate(t0.getStartDate());
                Date date2 = DateUtil.stringToDate(t1.getStartDate());
                // 对日期字段进行升序，如果欲降序可采用after方法
                if (date1.before(date2)) {
                    return 1;
                }
                return -1;
            }
        });

    }

    private void postRequest(final int page){
        String pages = String.valueOf(page);
        Map<String,String> map = new HashMap<String, String>();
        map.put("page",pages);
        String select = String.format(getString(R.string.baseURL));
        String url = select+"activity/mineActivity";
        Volley.sendJSONRequest(map, url, ResponseSign.class, new IDataListener<ResponseSign>() {
            @Override
            public void onSuccess(ResponseSign responseSign)  {
                String responseMsg = responseSign.msg;
                //如果不为空
                if(responseMsg.equals("numErr_Empty")){
                    Toast.makeText(getActivity(), "没有更多了", Toast.LENGTH_SHORT).show();
                }else if(responseMsg.equals("paginatorSuc")){
                    //需要解析的活动键值对  key 为活动序号
                    String responseAc = responseSign.activities;
                    JSONObject json = JSON.parseObject(responseAc);
                    for (Map.Entry<String, Object> entry : json.entrySet()) {
                        mySQLiteOpenHelper.insertData(mySQLiteOpenHelper.getReadableDatabase(),
                                entry.getKey(), "tb_activityMine", "activityMine",
                                entry.getValue().toString());

                        JSONObject jsonDate = JSON.parseObject(entry.getValue().toString());
                        //将时间单独从JSON串中提出 作为排序用的标识
                        String date = jsonDate.get("activity_time").toString()
                                .replace("T"," ").replace("Z","");

                        mySQLiteOpenHelper.insertData(mySQLiteOpenHelper.getReadableDatabase(),
                                entry.getKey(), "tb_activityMine", "Date",
                                date);
                        //将是否过期状态从JSON串提出
                        String state =jsonDate.get("activity_status").toString();
                        mySQLiteOpenHelper.insertData(mySQLiteOpenHelper.getReadableDatabase(),
                                entry.getKey(), "tb_activityMine", "activityStatus",
                                state);

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
        initData();


        mPullToRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Log.i("项目序号",position+"  "+id);
                Intent intent = new Intent(MineFragment.this.getActivity(),Activity_detailedInformation.class);
                //将点击项目的活动ID作为变量传给打开的activity
                intent.putExtra("whichFragment","Mine");
                //活动是否过期
                if(activity.get(position-1).getActivityState()){
                    intent.putExtra("acStatus","1");
                }else intent.putExtra("acStatus","0");
                intent.putExtra("isOverdue","Mine");
                intent.putExtra("MineAcId", activity.get(position-1).getActivityId());
                intent.putExtra("isMine","1");
                startActivity(intent);
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
                LayoutInflater inflater = MineFragment.this.getLayoutInflater();
                View view;

                if (convertView==null) {
                    //因为getView()返回的对象，adapter会自动赋给ListView
                    view = inflater.inflate(R.layout.item_myactivity, null);
                }else{
                    view=convertView;
                    //Log.i("info","有缓存，不需要重新生成"+position);
                }

                final ImageView iV = (ImageView)view.findViewById(R.id.itemState);
                final ImageView iV2 = (ImageView)view.findViewById(R.id.itemCrown);
                iV2.setImageDrawable(getResources().getDrawable(R.mipmap.crown));
                //true ：活动未过期     false：过期
                if(activity.get(position).getActivityState()){
                    iV.setImageDrawable(getResources().getDrawable(R.mipmap.active));
                    activity.get(position).setActivityState(true);
                }else {
                    iV.setImageDrawable(getResources().getDrawable(R.mipmap.overdue));
                    activity.get(position).setActivityState(false);
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
            page =1;
            //查询tb_activityMine表中，所有的活动ID
            String id_list[] = mySQLiteOpenHelper.queryDataALL("tb_activityMine", "_id","activityStatus desc,Date asc");
            int i = 0;
           int min = id_list.length>=4?4:id_list.length;
           // int min =id_list.length;
            for (i = 0; i <min; i++) {
                //依次从依据活动id从Mine表中中查询存储的JSON字符串
                String acJSON = mySQLiteOpenHelper.queryData("tb_activityMine",
                        "activityMine","_id="+id_list[i]);
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
                //0 过期  1 未过期
                if (json.get("activity_status").toString().equals("1")){
                    al.setActivityState(true);
                }else {
                    al.setActivityState(false);
                }
                activity.add(al);

            }



        }catch(NullPointerException e){
        }
    }

    public void initMoreData(){

        try {
            //测试取数据
            //查询tb_activityMine表中，所有的活动ID
            String id_list[] = mySQLiteOpenHelper.queryDataALL("tb_activityMine", "_id","activityStatus desc,Date asc");
            int i = 0;
            int min = id_list.length>=4*page?4*page:id_list.length;
            Date system = DateUtil.stringToDate(systemTime);
            for (i = (page-1)*4; i <min; i++) {
                //依次从依据活动id从Mine表中中查询存储的JSON字符串
                String acJSON = mySQLiteOpenHelper.queryData("tb_activityMine",
                        "activityMine","_id="+id_list[i]);
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
//                if(system.before(start)){
//                    al.setActivityState(true);
//                }else {
//                    al.setActivityState(false);
//                }
                //0 过期  1 未过期
                if (json.get("activity_status").toString().equals("1")){
                    al.setActivityState(true);
                }else {
                    al.setActivityState(false);
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