package com.example.jiuwei.myActivity;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jiuwei.LocalSQLite.MySQLiteOpenHelper;
import com.example.jiuwei.R;
import com.example.jiuwei.adapter.MyActivityAdapter;


public class MyActivity_Fragment extends Fragment implements View.OnClickListener{
    TabLayout myacTabLayout;
    ViewPager myacViewPager;
    private TabLayout.Tab tab_Mine,tab_ToJoin,tab_History;
    private MyActivityAdapter myActivityAdapter;
    TextView morePTv;
    HistoryFragment historyFragment =new HistoryFragment();
    ToJoinFragment toJoinFragment = new ToJoinFragment();
    MineFragment mineFragment = new MineFragment();
    MySQLiteOpenHelper mySQLiteOpenHelper;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_myactivity,container,false);
        Log.d("tag", "进入MyAc了");
        initView(view);
        initEvents(view);
        return view;
    }



    private void initView(View view) {
        myacTabLayout= (TabLayout) view.findViewById(R.id.tabLayout_home_fragment);
        myacViewPager= (ViewPager) view.findViewById(R.id.viewPager_home_fragment);
        morePTv = (TextView) view.findViewById(R.id.morePage);
        morePTv.setOnClickListener(this);
        //getSupportFragmentManager()方法在Activity中使用
        //嵌套Fragment拿到FragmentManager要用这个方法 getChildFragmentManager()
        myActivityAdapter =new MyActivityAdapter(getChildFragmentManager());
        myacViewPager.setAdapter(myActivityAdapter);
        myacTabLayout.setupWithViewPager(myacViewPager);
        //初始化tab
        //tab_Mine,tab_ToJoin,tab_History;
        tab_Mine=myacTabLayout.getTabAt(0);
        tab_ToJoin=myacTabLayout.getTabAt(1);
        tab_History=myacTabLayout.getTabAt(2);
        //初始化第一个
        myacViewPager.setCurrentItem(0);

        setIndicator(myacTabLayout, 10, 10);


    }

    private void initEvents(View view) {
        myacTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // if (tab == myacTabLayout.getTabAt(0)) {

                if (tab == tab_Mine) {
                    myacViewPager.setCurrentItem(0);
                    Log.d("tag", "进入数字0了");
                    // } else if (tab == myacTabLayout.getTabAt(1)) {
                } else if (tab == tab_ToJoin) {
                    myacViewPager.setCurrentItem(1);
                    Log.d("tag", "进入数字1了");
                    //} else if (tab == myacTabLayout.getTabAt(2)) {
                } else if (tab == tab_History) {
                    myacViewPager.setCurrentItem(2);
                    Log.d("tag", "进入数字2了");
                }

            }


            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
//                if (tab == mTablayout.getTabAt(0)) {
//                    tablayoutHome.setIcon(getResources().getDrawable(R.drawable.img_home_normal));
//                } else if (tab == mTablayout.getTabAt(1)) {
//                    tablayoutShopping.setIcon(getResources().getDrawable(R.drawable.img_shop_normal));
//                } else if (tab == mTablayout.getTabAt(2)) {
//                    tablayoutUseDetail.setIcon(getResources().getDrawable(R.drawable.img_use_detail_normal));
//                }else if (tab == mTablayout.getTabAt(3)){
//                    tablayoutMe.setIcon(getResources().getDrawable(R.drawable.img_me_normal));
//                }
//                else if (tab == mTablayout.getTabAt(4)){
//                    tablayoutMe.setIcon(getResources().getDrawable(R.drawable.img_me_normal));
//
//                }
            }


            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //加载更多活动
            case R.id.morePage:
                //我发起的
                if (myacViewPager.getCurrentItem()==0){
                    mineFragment.page++;
                    Toast.makeText(this.getActivity(),""+mineFragment.page, Toast.LENGTH_SHORT).show();
                    //mineFragment.getActivityList(mineFragment.page);
                }
                //待参加的
                else if (myacViewPager.getCurrentItem()==1){
                    toJoinFragment.page++;
                    Toast.makeText(this.getActivity(),""+toJoinFragment.page, Toast.LENGTH_SHORT).show();
                    //toJoinFragment.getActivityList(toJoinFragment.page);
                }
                //历史活动
                else if (myacViewPager.getCurrentItem()==2){
                    historyFragment.page++;
                    Toast.makeText(this.getActivity(),""+historyFragment.page, Toast.LENGTH_SHORT).show();
                    //historyFragment.getActivityList(historyFragment.page);
                }
                break;
        }


    }

    //设置TabLayout下划线长度
//    注意1：Tablayout的Mode必须得设置为fixed,否则会滑动
//    示例：app:tabMode=”fixed”
//
//    注意2：Tablayout的宽不能写死，得设置为包裹内容
    public void setIndicator(TabLayout tabs, int leftDip, int rightDip) {
        Class<?> tabLayout = tabs.getClass();
        java.lang.reflect.Field tabStrip = null;
        try {
            tabStrip = tabLayout.getDeclaredField("mTabStrip");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        tabStrip.setAccessible(true);
        LinearLayout llTab = null;
        try {
            llTab = (LinearLayout) tabStrip.get(tabs);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        int left = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, leftDip, Resources.getSystem().getDisplayMetrics());
        int right = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, rightDip, Resources.getSystem().getDisplayMetrics());
        for (int i = 0; i < llTab.getChildCount(); i++) {
            View child = llTab.getChildAt(i);
            child.setPadding(0, 0, 0, 0);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
            params.leftMargin = left;
            params.rightMargin = right;
            child.setLayoutParams(params);
            child.invalidate();
        }


    }

}