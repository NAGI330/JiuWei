package com.example.jiuwei.myActivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jiuwei.R;


public class CreateActivity_Fragment extends Fragment implements View.OnClickListener{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.createactivity,container,false);
        initView(view);
        Log.d("tag", "进入plus了");
        return view;
    }



    private void initView(View view) {

    }


    @Override
    public void onClick(View v) {

    }
}