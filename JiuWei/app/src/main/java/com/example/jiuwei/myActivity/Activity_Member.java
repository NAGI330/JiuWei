package com.example.jiuwei.myActivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.jiuwei.LocalSQLite.MySQLiteOpenHelper;
import com.example.jiuwei.R;

import java.util.Map;

public class Activity_Member extends AppCompatActivity implements View.OnClickListener{
    private TextView memberTv;
    private TextView backtoAcDeInTv;
    private MySQLiteOpenHelper mySQLiteOpenHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);
        mySQLiteOpenHelper = new MySQLiteOpenHelper(Activity_Member.this, "db_jiuwei", null, 1);

        //初始化页面
        initView();
    }
    private void initView(){
        memberTv = (TextView)findViewById(R.id.AcMemberMsg);
        backtoAcDeInTv = (TextView)findViewById(R.id.backtoacdein);
        backtoAcDeInTv.setOnClickListener(this);
        String memberList = getIntent().getStringExtra("acMemberList")
                .replace("[","").replace("]","")
                .replace("{","").replace("}","");
        memberList = "{"+memberList+"}";
        JSONObject json = JSON.parseObject(memberList);
        String content =  memberTv.getText().toString();
        for (Map.Entry<String, Object> entry : json.entrySet()) {
            content =content+"\n"+"ID:"+entry.getKey()+"  昵称:"+entry.getValue().toString();
        }

        memberTv.setText(content);
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
