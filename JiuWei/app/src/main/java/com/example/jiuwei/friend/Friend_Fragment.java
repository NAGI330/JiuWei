package com.example.jiuwei.friend;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jiuwei.R;


public class Friend_Fragment extends Fragment implements View.OnClickListener{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_friend,container,false);
        initView(view);
        Log.d("tag", "进入friend了");
        return view;
    }



    private void initView(View view) {

    }


    @Override
    public void onClick(View v) {

    }
}