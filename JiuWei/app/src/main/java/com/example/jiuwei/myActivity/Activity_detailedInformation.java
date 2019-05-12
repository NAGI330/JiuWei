package com.example.jiuwei.myActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.jiuwei.LocalSQLite.MySQLiteOpenHelper;
import com.example.jiuwei.R;
import com.example.jiuwei.datetimeselect.CustomDatePicker;
import com.example.jiuwei.datetimeselect.DateFormatUtils;
import com.example.jiuwei.http.IDataListener;
import com.example.jiuwei.http.Volley;
import com.example.jiuwei.http.bean.ResponseSign;
import com.example.jiuwei.mapService.LBSService.MapService;

import java.util.HashMap;
import java.util.Map;

public class Activity_detailedInformation extends AppCompatActivity implements View.OnClickListener{
    private TextView acIdTv = null,acPlaceTv = null,numMaxTv = null,acTimeTv = null,
            ownNameTv = null,acMemberTv = null,acTypeTv =null,ownIdTv = null;
    private String member = "1";
    private String canJiaZhe = "";
    private TextView backToAcTv = null;
    private EditText acNameTv = null,acDesTv = null;
    private Button moreOperation = null;
    private Button deleteAcBtn = null;
    private Button timeSelectBtn = null;
    private Button placeSelectBtn = null;
    private MySQLiteOpenHelper mySQLiteOpenHelper;
    private String acJSON = "null",acId = "null";
    private CustomDatePicker mTimerPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_information);

        //初始化页面
        initView();
        initTimerPicker();
    }
    private void initTimerPicker() {
        String beginTime = DateFormatUtils.long2Str(System.currentTimeMillis(), true);
        String endTime = "2020-12-31 23:59";
        //acTimeTv.setText(beginTime);
        // 通过日期字符串初始化日期，格式请用：yyyy-MM-dd HH:mm
        mTimerPicker = new CustomDatePicker(this, new CustomDatePicker.Callback() {
            @Override
            public void onTimeSelected(long timestamp) {
                acTimeTv.setText(DateFormatUtils.long2Str(timestamp, true));
            }
        }, beginTime, endTime);
        // 允许点击屏幕或物理返回键关闭
        mTimerPicker.setCancelable(true);
        // 显示时和分
        mTimerPicker.setCanShowPreciseTime(true);
        // 允许循环滚动
        mTimerPicker.setScrollLoop(true);
        // 允许滚动动画
        mTimerPicker.setCanShowAnim(true);
    }

    private void initView(){
        acNameTv = (EditText)findViewById(R.id.activityName);
        acIdTv =(TextView) findViewById(R.id.activityId);
        acDesTv = (EditText)findViewById(R.id.activityDescribe);
        acPlaceTv = (TextView)findViewById(R.id.activityPlace);
        acTimeTv = (TextView)findViewById(R.id.activityTime);
        acTypeTv = (TextView) findViewById(R.id.activityType);
        acMemberTv = (TextView)findViewById(R.id.activityMember);
        numMaxTv = (TextView)findViewById(R.id.numberMax);
        ownNameTv = (TextView)findViewById(R.id.ownName);
        ownIdTv = (TextView) findViewById(R.id.ownId);
        backToAcTv = (TextView) findViewById(R.id.backtoactivity);
        moreOperation = (Button) findViewById(R.id.moreOperation);
        deleteAcBtn = (Button) findViewById(R.id.deleteActivity);
        timeSelectBtn = (Button) findViewById(R.id.timeSelect);
        placeSelectBtn = (Button) findViewById(R.id.placeSelect);

        backToAcTv.setOnClickListener(this);
        moreOperation.setOnClickListener(this);
        acMemberTv.setOnClickListener(this);
        deleteAcBtn.setOnClickListener(this);
        timeSelectBtn.setOnClickListener(this);
        placeSelectBtn.setOnClickListener(this);

        mySQLiteOpenHelper = new MySQLiteOpenHelper(this, "db_jiuwei", null, 1);

        //获得打开这个activity时传来的变量。
        //首先判断是哪个Fragment传来的
        if(getIntent().getStringExtra("whichFragment").equals("History")){
            acId = getIntent().getStringExtra("HistoryAcId");
            acJSON = mySQLiteOpenHelper.queryData("tb_activityHistory",
                "activityHistory","_id="+acId);
        }else if(getIntent().getStringExtra("whichFragment").equals("Mine")){
            acId = getIntent().getStringExtra("MineAcId");
            acJSON = mySQLiteOpenHelper.queryData("tb_activityMine",
                    "activityMine","_id="+acId);
            //活动未过期时，让解散活动和修改活动按钮可见
            if(getIntent().getStringExtra("acStatus").equals("1")){
                deleteAcBtn.setText("解散活动");
                deleteAcBtn.setVisibility(View.VISIBLE);
                moreOperation.setVisibility(View.VISIBLE);
            }


        }else if(getIntent().getStringExtra("whichFragment").equals("ToJoin")){
            acId = getIntent().getStringExtra("ToJoinAcId");
            acJSON = mySQLiteOpenHelper.queryData("tb_activityToJoin",
                    "activityToJoin","_id="+acId);
            if(getIntent().getStringExtra("isMine").equals("1")){
                deleteAcBtn.setText("解散活动");
            }else deleteAcBtn.setText("退出活动");
            deleteAcBtn.setVisibility(View.VISIBLE);
        }
        //转化为json对象
        JSONObject json = JSON.parseObject(acJSON);
        acNameTv.setText(""+json.get("activity_name"));
        acIdTv.setText(""+acId);
        acDesTv.setText(""+json.get("activity_desc"));
        acPlaceTv.setText(""+json.get("activity_site"));
        acTimeTv.setText(""+json.get("activity_time").toString()
                .replace("T"," ").replace("Z",""));
        numMaxTv.setText(""+json.get("limit_num"));
        acTypeTv.setText(""+json.get("activity_type"));
        ownNameTv.setText(""+json.get("owner_name"));
        ownIdTv.setText(""+json.get("owner_id"));

        member =json.get("participant_num").toString();
        acMemberTv.setText("共"+member+"人  >");
        canJiaZhe = json.get("participant").toString();


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 1:
                if (resultCode==RESULT_OK){
                    acPlaceTv.setText(data.getStringExtra("data_return"));
                }
                break;
            default:
        }
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.backtoactivity:
                finish();
                break;
            case R.id.moreOperation:
                if(moreOperation.getText().equals("修改活动")){
                    moreOperation.setText("完成");
                    timeSelectBtn.setVisibility(View.VISIBLE);
                    placeSelectBtn.setVisibility(View.VISIBLE);
                    acNameTv.setEnabled(true);
                    acDesTv.setEnabled(true);
                    numMaxTv.setEnabled(true);

                }else if(moreOperation.getText().equals("完成")){
                    String select = String.format(getString(R.string.baseURL));
                    String url = select+"activity/changeActivity";
                    Map<String,String> map = new HashMap<String, String>();
                    map.put("id",acId);
                    map.put("activity_name",acNameTv.getText().toString());
                    map.put("activity_desc",acDesTv.getText().toString());
                    map.put("activity_site",acPlaceTv.getText().toString());
                    map.put("activity_time",acTimeTv.getText().toString());
                    map.put("limit_num",numMaxTv.getText().toString());
                    Volley.sendJSONRequest(map, url, ResponseSign.class, new IDataListener<ResponseSign>() {
                        @Override
                        public void onSuccess(ResponseSign responseSign) {
                            String responseMsg = responseSign.msg;
                            if (responseMsg.equals("change activity successfully")){
                                Toast.makeText(Activity_detailedInformation.this, "活动修改成功", Toast.LENGTH_SHORT).show();
                                String s="{\"activity_name\":\"" + acNameTv.getText().toString()+"\"," +
                                        "\"activity_desc\":\"" + acDesTv.getText().toString()+"\"," +
                                        "\"activity_site\":\"" + acPlaceTv.getText().toString()+"\"," +
                                        "\"activity_time\":\"" + acTimeTv.getText().toString()+"\"," +
                                        "\"activity_type\":\"" + acTypeTv.getText().toString()+"\"," +
                                        "\"activity_status\":\"" + getIntent().getStringExtra("acStatus")+"\"," +
                                        "\"activity_isMine\":\"" +getIntent().getStringExtra("isMine")+"\"," +
                                        "\"limit_num\":\"" + numMaxTv.getText().toString()+"\"," +
                                        "\"owner_id\":\"" + ownIdTv.getText().toString()+"\"," +
                                        "\"owner_name\":\"" + ownNameTv.getText().toString()+"\"," +
                                        "\"participant_num\":\"" + member+"\"," +
                                        "\"participant\":\""+canJiaZhe+"\"}";
                                mySQLiteOpenHelper.insertData(mySQLiteOpenHelper.getReadableDatabase(),
                                       acId,"tb_activityMine","activityMine",
                                       s );
                                mySQLiteOpenHelper.insertData(mySQLiteOpenHelper.getReadableDatabase(),
                                        acId,"tb_activityToJoin","activityToJoin",
                                        s );

                            }

                        }

                        @Override
                        public void onFailure() {

                        }
                    });

                }
                break;
            case R.id.activityMember:
                Intent intent1 = new Intent(Activity_detailedInformation.this,
                        Activity_Member.class);
                intent1.putExtra("acMemberList",canJiaZhe);
                startActivity(intent1);
                break;
            case R.id.timeSelect:
                mTimerPicker.show(acTimeTv.getText().toString());
                break;
            case R.id.placeSelect:
                Intent intent = new Intent(Activity_detailedInformation.this,
                        MapService.class);
                startActivityForResult(intent,1);
                break;
            case R.id.deleteActivity:
                if (deleteAcBtn.getText().equals("解散活动")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("这是你发起的活动，该操作会扣除10点信用分，确定要解散么？");
                    builder.setNegativeButton("确定",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String select = String.format(getString(R.string.baseURL));
                                    String url = select+"activity/quitActivity";
                                    Map<String,String> map = new HashMap<String, String>();
                                    map.put("id",acId);

                                    Volley.sendJSONRequest(map, url, ResponseSign.class, new IDataListener<ResponseSign>() {
                                        @Override
                                        public void onSuccess(ResponseSign responseSign) {
                                            String responseMsg = responseSign.msg;
                                            Log.i("quit",responseMsg);
                                            //删除成功时，本地数据库删除
                                            //解散活动成功
                                            if (responseMsg.equals("dissolution successfully")){
                                                mySQLiteOpenHelper.deleteData("tb_activityMine",acId);
                                                mySQLiteOpenHelper.deleteData("tb_activityToJoin",acId);
                                                Toast.makeText(Activity_detailedInformation.this, "退出活动成功", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        }

                                        @Override
                                        public void onFailure() {

                                        }
                                    });


                                }
                            });
                    builder.setPositiveButton("取消",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }else if (deleteAcBtn.getText().equals("退出活动")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("退出活动会扣除5点信用分，确定要退出么？");
                    builder.setNegativeButton("确定",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String select = String.format(getString(R.string.baseURL));
                                    String url = select+"activity/quitActivity";
                                    Map<String,String> map = new HashMap<String, String>();
                                    map.put("id",acId);

                                    Volley.sendJSONRequest(map, url, ResponseSign.class, new IDataListener<ResponseSign>() {
                                        @Override
                                        public void onSuccess(ResponseSign responseSign) {
                                            String responseMsg = responseSign.msg;
                                            Log.i("quit",responseMsg);
                                            //删除成功时，本地数据库删除
                                            //退出活动成功
                                           if(responseMsg.equals("quit successfully")){
                                                mySQLiteOpenHelper.deleteData("tb_activityMine",acId);
                                                mySQLiteOpenHelper.deleteData("tb_activityToJoin",acId);
                                                Toast.makeText(Activity_detailedInformation.this, "解散活动成功", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        }

                                        @Override
                                        public void onFailure() {

                                        }
                                    });
                                    Intent intent2 = new Intent("android.intent.action.CART_BROADCAST");
                                    LocalBroadcastManager.getInstance(Activity_detailedInformation.this).sendBroadcast(intent2);
                                    sendBroadcast(intent2);
                                    intent2.putExtra("data","refresh");
                                    Log.i("到finish了了！！！！！！！！","a");
                                    finish();

                                }
                            });
                    builder.setPositiveButton("取消",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

                break;

        }

    }

}
