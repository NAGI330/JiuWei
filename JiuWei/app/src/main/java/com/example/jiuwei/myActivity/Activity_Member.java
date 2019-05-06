package com.example.jiuwei.myActivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.jiuwei.R;

public class Activity_Member extends AppCompatActivity implements View.OnClickListener{
    private TextView memberTv;
    private TextView backtoAcDeInTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);

        //初始化页面
        initView();
    }
    private void initView(){
        memberTv = (TextView)findViewById(R.id.AcMemberMsg);
        backtoAcDeInTv = (TextView)findViewById(R.id.backtoacdein);
        backtoAcDeInTv.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.backtoacdein:
                finish();
                break;
        }
    }
}
