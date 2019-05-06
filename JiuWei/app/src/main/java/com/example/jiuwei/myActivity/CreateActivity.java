package com.example.jiuwei.myActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jiuwei.R;
import com.example.jiuwei.datetimeselect.CustomDatePicker;
import com.example.jiuwei.datetimeselect.DateFormatUtils;
import com.example.jiuwei.http.IDataListener;
import com.example.jiuwei.http.Volley;
import com.example.jiuwei.http.bean.ResponceSign;
import com.example.jiuwei.mapService.search.BaiduMapMainActivity;

import java.util.HashMap;
import java.util.Map;

public class CreateActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText acNameEt = null,acDesEt = null,acPlaceEt = null,numMaxEt = null;
    private TextView  acTimeTv = null;
    private Spinner acTypeSpn=null;
    private String activityType;
    private CustomDatePicker mTimerPicker;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createactivity);


        TextView canaleTv = (TextView) findViewById(R.id.createCancel);
        acNameEt =(EditText) findViewById(R.id.activityName);
        acDesEt =(EditText) findViewById(R.id.activityDescribe);
        acPlaceEt =(EditText) findViewById(R.id.activityPlace);

        acTimeTv =(TextView) findViewById(R.id.activityTime);
        initTimerPicker();

        numMaxEt =(EditText) findViewById(R.id.numberMax);
        acTypeSpn =(Spinner) findViewById(R.id.activityType);
        Button reAcBtn = (Button) findViewById(R.id.releaseActivity);
        Button spaceSelectBtn = (Button) findViewById(R.id.spaceSelect);
        Button timeSelectBtn = (Button) findViewById(R.id.timeSelect);


        canaleTv.setOnClickListener(CreateActivity.this);
        spaceSelectBtn.setOnClickListener(CreateActivity.this);
        timeSelectBtn.setOnClickListener(CreateActivity.this);
        reAcBtn.setOnClickListener(CreateActivity.this);

    }

    public void showDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //重写手机物理按键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //如果此时按下返回键
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //为了让退出键效果和默认退出效果不一样
            finish();
            overridePendingTransition(R.anim.pop_enter_anim, R.anim.pop_exit_anim);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }



    private void initTimerPicker() {
        String beginTime = DateFormatUtils.long2Str(System.currentTimeMillis(), true);
        String endTime = "2020-12-31 23:59";
        acTimeTv.setText(beginTime);
        //acTimeTv.setText(endTime);
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




    @Override
    public void onClick(View view) {
        activityType = (String) acTypeSpn.getSelectedItem();
        String activityName =acNameEt.getText().toString();
        String activityDescribe = acDesEt.getText().toString();
        String activityPlaceSelect = acPlaceEt.getText().toString();
        String activityTimeSelect = acTimeTv.getText().toString();
        String activityNumMax = numMaxEt.getText().toString();
        //获取选择的活动类型
        acTypeSpn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //拿到被选择项的值
                activityType = (String) acTypeSpn.getSelectedItem();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        switch (view.getId()){
            case R.id.createCancel:
                finish();
                overridePendingTransition(R.anim.pop_enter_anim, R.anim.pop_exit_anim);
                break;
            case R.id.spaceSelect:
                //acPlaceEt.setText("西安");
                Intent intent = new Intent(CreateActivity.this,
                        BaiduMapMainActivity.class);
                startActivity(intent);
                break;
            case R.id.timeSelect:
                mTimerPicker.show(acTimeTv.getText().toString());
                break;

            case R.id.releaseActivity:
                //对输入进行合法判断
                if(activityName.equals("")){
                    showDialog("活动名不能为空");
                    //Toast.makeText(BaiduMapMainActivity.this, "活动名长度需在8~20字之间", Toast.LENGTH_SHORT).show();
                }else if(activityDescribe.equals("")){
                    showDialog("活动描述不能为空");
                    //Toast.makeText(BaiduMapMainActivity.this, "活动描述长度需在20~100个字之间", Toast.LENGTH_SHORT).show();
                }else if(activityPlaceSelect.equals("")) {
                    showDialog("活动地点不能为空");
                    //Toast.makeText(BaiduMapMainActivity.this, "活动地点与活动时间不能为空", Toast.LENGTH_SHORT).show();
                }else if(activityTimeSelect.equals("")){
                    showDialog("活动时间不能为空");
                }
                else{
                    String select = String.format(getString(R.string.baseURL));
                    String url = select+"activity/createActivity";
                    Map<String,String> map = new HashMap<String, String>();
                    map.put("activity_name",activityName);
                    map.put("activity_desc",activityDescribe);
                    map.put("activity_site",activityPlaceSelect);
                    map.put("activity_time",activityTimeSelect);
                    map.put("limit_num",activityNumMax);
                    map.put("activity_type",activityType);
                    Volley.sendJSONRequest(map, url, ResponceSign.class, new IDataListener<ResponceSign>() {
                        @Override
                        public void onSuccess(ResponceSign responceSign) {
                            String response = responceSign.msg;
                            if (response.equals("createActivity Successfully")) {
                                Toast.makeText(CreateActivity.this, "活动创建成功！", Toast.LENGTH_SHORT).show();
                                finish();
                            }else if(response.equals("fieldErr_lose")){
                                showDialog("字段缺失");
                            }
                        }

                        @Override
                        public void onFailure() {

                        }
                    });

                }
                break;
        }
    }
}
