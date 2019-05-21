package com.example.jiuwei.personalInfo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.jiuwei.LocalSQLite.MySQLiteOpenHelper;
import com.example.jiuwei.R;
import com.example.jiuwei.http.IDataListener;
import com.example.jiuwei.http.Volley;
import com.example.jiuwei.http.bean.ResponseSign;

import java.util.HashMap;
import java.util.Map;

public class UserMsg extends AppCompatActivity {
    private ImageView avatar;
    private EditText nicknameEt,ageEt,signatureEt;
    private TextView userIdTv,emailTv,creditTv,backToMainTv;
    private Spinner genderSpn;
    private Button editBtn;
    private MySQLiteOpenHelper mySQLiteOpenHelper;
    String genderItem;
    String genderStr;
    String gender;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usermenu_usermsg);
        editBtn = findViewById(R.id.button_user_edit);
        backToMainTv =findViewById(R.id.backto);
        userIdTv = findViewById(R.id.text_user_account);
        nicknameEt = findViewById(R.id.text_user_nickname);
        creditTv = findViewById(R.id.text_user_credit);
        ageEt = findViewById(R.id.text_user_age);
        emailTv = findViewById(R.id.text_user_email);
        signatureEt = findViewById(R.id.text_user_signature);
        genderSpn =findViewById(R.id.text_user_gender);
        avatar = findViewById(R.id.image_user_avatar);
        mySQLiteOpenHelper = new MySQLiteOpenHelper(UserMsg.this, "db_jiuwei", null, 1);
        String userInfoJSON = mySQLiteOpenHelper.queryData("tb_userCookie",
                "userInfo",null);
        try{
            JSONObject json = JSON.parseObject(userInfoJSON);
            userIdTv.setText(json.get("id").toString());
            nicknameEt.setText(json.get("nickname").toString());
            ageEt.setText(json.get("age").toString());
            emailTv.setText(json.get("email").toString());
            signatureEt.setText(json.get("self_desc").toString());
            creditTv.setText(json.get("credit_score").toString());
            if (json.get("gender").toString().equals("true")){
                genderSpn.setSelection(1);
            }else genderSpn.setSelection(0);
            Log.i("用户性别:",json.get("gender").toString());
        }catch (java.lang.NullPointerException e){

        }
        backToMainTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        genderSpn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //拿到被选择项的值
                genderItem = (String) genderSpn.getSelectedItem();
                if (genderItem.equals("男")){
                    genderStr = "1";
                    gender = "true";
                }else {
                    genderStr = "0";
                    gender ="false";
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editBtn.getText().toString().equals("编辑")){
                    editBtn.setText("确定修改");
                    nicknameEt.setEnabled(true);
                    ageEt.setEnabled(true);
                    signatureEt.setEnabled(true);
                    ageEt.setEnabled(true);
                    genderSpn.setClickable(true);
                }else {
                    Map<String,String> map = new HashMap<String, String>();
                    map.put("nickname",nicknameEt.getText().toString());
                    map.put("gender",genderStr);
                    map.put("age",ageEt.getText().toString());
                    map.put("self_desc",signatureEt.getText().toString());
                    String select = String.format(getString(R.string.baseURL));
                    String url = select+"personal/changeUserMsg";
                    Volley.sendJSONRequest(map, url, ResponseSign.class, new IDataListener<ResponseSign>() {
                        @Override
                        public void onSuccess(ResponseSign responseSign) {
                            if(responseSign.msg.equals("change successfully")){
                                Toast.makeText(UserMsg.this, "个人信息修改成功", Toast.LENGTH_SHORT).show();
                                String user_info = "{\"id\":\"" + userIdTv.getText().toString()+"\"," +
                                        "\"nickname\":\"" + nicknameEt.getText().toString()+"\"," +
                                        //注意 gender 是以 字符串 true false 存在数据库的，故不需要反转义引号
                                        "\"gender\":" + gender+"," +
                                        "\"email\":\"" + emailTv.getText().toString()+"\"," +
                                        "\"self_desc\":\"" + signatureEt.getText().toString()+"\"," +
                                        "\"credit_score\":\"" + creditTv.getText().toString()+"\"," +
                                        "\"age\":\""+ageEt.getText().toString()+"\"}";
                                mySQLiteOpenHelper.insertData(mySQLiteOpenHelper.getReadableDatabase(),"1",
                                        "tb_userCookie","userInfo",user_info);
                                nicknameEt.setEnabled(false);
                                ageEt.setEnabled(false);
                                signatureEt.setEnabled(false);
                                ageEt.setEnabled(false);
                                editBtn.setText("编辑");


                            }
                        }

                        @Override
                        public void onFailure() {

                        }
                    });
                }



            }
        });
        final Bitmap avatarBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_friend);
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserMsg.this, ChangePicture.class);
                intent.putExtra("avatar", avatarBitmap);
                startActivity(intent);
            }
        });
    }
}
