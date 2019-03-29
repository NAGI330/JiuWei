package com.example.jiuwei.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.jiuwei.myActivity.HistoryFragment;
import com.example.jiuwei.myActivity.MineFragment;
import com.example.jiuwei.myActivity.ToJoinFragment;

public class MyActivityAdapter extends FragmentPagerAdapter {
    private String[] myacTitles = new String[]{"我发起的", "待参加的", "历史活动"};

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new MineFragment();
        } else if (position == 1) {
            return new ToJoinFragment();
        } else if(position == 2){
            return new HistoryFragment();
        }
        return new MineFragment();
    }

    @Override
    public int getCount() {
        return myacTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return myacTitles[position];
    }

    public MyActivityAdapter(FragmentManager fm) {
        super(fm);
    }
}
