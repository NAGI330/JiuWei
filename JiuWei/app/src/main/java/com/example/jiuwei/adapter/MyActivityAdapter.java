package com.example.jiuwei.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import com.example.jiuwei.MainActivity;
import com.example.jiuwei.myActivity.HistoryFragment;
import com.example.jiuwei.myActivity.MineFragment;
import com.example.jiuwei.myActivity.ToJoinFragment;

public class MyActivityAdapter extends FragmentPagerAdapter {
    private String[] myacTitles = new String[]{"我发起的", "待参加的", "历史活动"};
    MainActivity mainActivity;




    @Override
    public Fragment getItem(int position) {

        if (position == 1) {
            Log.i("testttt", "进入数字0了");
            return new ToJoinFragment();
        } else if (position == 2) {
            Log.i("testttt", "进入数字1了");
            return new HistoryFragment();
        }else return new MineFragment();
    }

    @Override
    public int getCount() {
        return myacTitles.length;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return myacTitles[position];
    }

    public MyActivityAdapter(FragmentManager fm) {
        super(fm);
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Fragment fragment = ((Fragment)object);
    }
}
