package com.example.jiuwei.personalInfo;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jiuwei.R;


public class UserMenu_Fragment extends Fragment implements View.OnClickListener{

    public static final String  SELECTED_ITEM = "selected_item" ;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_usermenu,container,false);

        initView(view);
        initEvent(view);
        Log.d("tag", "进入me了");
        return view;
    }




    private void initView(View view) {

    }
    private void initEvent(View view) {

    }

    @Override
    public void onClick(View v) {

    }
}