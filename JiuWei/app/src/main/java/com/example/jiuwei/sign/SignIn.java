package com.example.jiuwei.sign;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import com.example.jiuwei.MainActivity;
import com.example.jiuwei.R;
import com.example.jiuwei.LocalSQLite.MySQLiteOpenHelper;
import com.example.jiuwei.http.IDataListener;
import com.example.jiuwei.http.Volley;
import com.example.jiuwei.http.bean.ResponseSign;

import java.util.HashMap;
import java.util.Map;
/*
 *     登录界面
 */

public class SignIn extends AppCompatActivity implements View.OnClickListener {

    //实例化
    Button btnLogin = null;
    TextView tvSignup = null;
    TextView tvFindPwd = null;
    EditText etNameText = null;
    EditText etPasswordText = null;


    private static Context mContext;
    public static Context getmContext(){
        return mContext;
    }

    private MySQLiteOpenHelper mySQLiteOpenHelper;
    //private String baseURL = "http://127.0.0.1:8000/";
    //private String baseURL = "http://10.0.2.2:8000/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext =getApplicationContext();
        setContentView(R.layout.sign_in);

        //实例化 （，数据库名称，工厂，版本号）
        mySQLiteOpenHelper = new MySQLiteOpenHelper(SignIn.this, "db_jiuwei", null, 1);
        //绑定
        btnLogin =(Button) findViewById(R.id.login);
        tvSignup = (TextView) findViewById(R.id.signup);
        etNameText = (EditText) findViewById(R.id.username);
        etPasswordText = (EditText) findViewById(R.id.userpassword);
        tvFindPwd =(TextView) findViewById(R.id.findpwd);


        btnLogin.setOnClickListener(SignIn.this);
        tvSignup.setOnClickListener(SignIn.this);
        tvFindPwd.setOnClickListener(SignIn.this);



    }



    public void onClick(View v) {
        // 获取用户输入的用户名和密码
        String name = etNameText.getText().toString();
        String password = etPasswordText.getText().toString();
        switch (v.getId()) {
            case R.id.login:
                if (name.equals("") || password.equals("")) {
                    Toast.makeText(this,
                            "账号或者密码不能为空",
                            Toast.LENGTH_SHORT).show();
                }
                else{
                    String select = String.format(getString(R.string.baseURL));
                    String url1 = select+"personal/SignIn";
                    Map<String,String> map = new HashMap<String, String>();
                    map.put("username",name);
                    map.put("password",password);
                    //String mapNew = JSON.toJSONString(map);
                    //Log.i("tag",mapNew);

                    //调用com.example.jiuwei.http包下的volley接口
                    Volley.sendJSONRequest(map,
                            url1, ResponseSign.class, new IDataListener<ResponseSign>() {
                        @Override
                        public void onSuccess(ResponseSign responseSign) {
                            Log.i("tostring", responseSign.toString());
                            String response = responseSign.msg;
                            if (response.equals("userErr_notExist")) {
                                showDialog("用户不存在，或密码错误");
                            } else if (response.equals("signIn successfully")) {
                                String responseCookie = responseSign.cookie;
                                JSONObject json =  JSON.parseObject(responseCookie);
                                String cookie_value = json.get("session_id").toString();
                                String user_info = responseSign.info;
                                /*
                                 * 查询本地储存的cookies并与cookie_value对比，如果不同，则说明是新的用户
                                 * 登录,这时删除本地所有库内容
                                 */
                                String lastUser = mySQLiteOpenHelper.queryData("tb_userCookie","cookie","_id=1");
                                if(!cookie_value.equals(lastUser)){
                                    //将本地数据库全部清空
                                    mySQLiteOpenHelper.deleteDataALL("tb_userCookie");
                                    mySQLiteOpenHelper.deleteDataALL("tb_activityMine");
                                    mySQLiteOpenHelper.deleteDataALL("tb_activityToJoin");
                                    mySQLiteOpenHelper.deleteDataALL("tb_activityHistory");
                                    mySQLiteOpenHelper.deleteDataALL("tb_activitySearch");
                                    mySQLiteOpenHelper.deleteDataALL("tb_activityPush");
                                }

                                //将cookie存入数据库
                                mySQLiteOpenHelper.insertData(mySQLiteOpenHelper.getReadableDatabase(),"1",
                                        "tb_userCookie","cookie",cookie_value);
                                //将userMsg存入数据库
                                mySQLiteOpenHelper.insertData(mySQLiteOpenHelper.getReadableDatabase(),"1",
                                        "tb_userCookie","userInfo",user_info);
                                //测试取数据
                                String cookie = mySQLiteOpenHelper.queryData("tb_userCookie","cookie","_id=1");
                                Log.i("测试读数据",cookie);
                                Intent intent = new Intent(SignIn.this,
                                        MainActivity.class);
                                startActivity(intent);
//                                Log.i("signin",responseSign.msg);
//                                Log.i("signin",responseSign.cookie);


                            } else if (response.equals("userErr_notActive")) {
                                showDialog("请先激活邮箱");
                            } else showDialog("异常情况");

                        }

                        @Override
                        public void onFailure() {

                        }
                    });
                }
                break;

            case R.id.signup:
                Intent intent = new Intent(SignIn.this,
                        SignOn.class);
                startActivity(intent);
                break;
            case R.id.findpwd:
                Intent intent2 = new Intent(SignIn.this,
                        RetrievePwd.class);
                startActivity(intent2);
                break;
        }

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

}
