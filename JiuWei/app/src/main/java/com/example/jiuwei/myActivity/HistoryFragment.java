package com.example.jiuwei.myActivity;

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

import com.example.jiuwei.R;
import com.example.jiuwei.HistoryActivityListTest;

import java.util.ArrayList;
import java.util.List;


public class HistoryFragment extends Fragment implements View.OnClickListener{


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_myactivity_history,container,false);
        initView(view);
        initEvent(view);
        return view;
    }


    private void initView(View view) {

    }

    private void initEvent(View view) {
        Button bt;//activity_main.xml里的Button
        ListView lv;//activity_main.xml里的ListView
        BaseAdapter adapter;//要实现的类
        final List<HistoryActivityListTest> activityList = new ArrayList<HistoryActivityListTest>();//实体类
        lv = (ListView)view.findViewById(R.id.lvHistory);
        //模拟数据库
        for (int i = 0; i < 5; i++) {
            HistoryActivityListTest al = new HistoryActivityListTest();//给实体类赋值
            al.setActivityName("KTV"+i);
            al.setStartDate("2019-09-26");
            activityList.add(al);
        }

        /*  bt.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {


            }
        });*/

        adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                // TODO Auto-generated method stub
                return activityList.size();//数目
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = HistoryFragment.this.getLayoutInflater();
                View view;

                if (convertView==null) {
                    //因为getView()返回的对象，adapter会自动赋给ListView
                    view = inflater.inflate(R.layout.item_myactivity, null);
                }else{
                    view=convertView;
                    Log.i("info","有缓存，不需要重新生成"+position);
                }
                final TextView tv1 = (TextView) view.findViewById(R.id.itemname);//itemname
                tv1.setText(activityList.get(position).getActivityName());//设置参数

                final TextView tv2 = (TextView) view.findViewById(R.id.itemage);//itemage
                tv2.setText(activityList.get(position).getStartDate());//设置参数
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