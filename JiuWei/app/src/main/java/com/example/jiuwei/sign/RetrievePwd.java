package com.example.jiuwei.sign;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.jiuwei.R;
import com.example.jiuwei.http.IDataListener;
import com.example.jiuwei.http.Volley;
import com.example.jiuwei.http.bean.ResponseSign;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/*
 *      找回密码界面
 */
public class RetrievePwd extends AppCompatActivity implements View.OnClickListener {
    //实例化
    Button btnChangePwd = null;
    Button btnBacktomain = null;
    EditText etNameText = null;
    EditText etPasswordText = null;
    EditText etPasswordConfrimText =null;
    //private String baseURL = "http://10.0.2.2:8000/";
    //正则表达式：验证密码
    public static final String REGEX_PASSWORD = "^[\\S]{6,20}$";
    //判断注册信息字符串是否合法

    public static boolean isPassword(String password) {
        return Pattern.matches(REGEX_PASSWORD, password);
    }




    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.retrieve_pwd);
        //绑定
        btnChangePwd = (Button) findViewById(R.id.changePwd);
        btnBacktomain =(Button) findViewById(R.id.backtomain);
        etNameText = (EditText) findViewById(R.id.username);
        etPasswordText = (EditText) findViewById(R.id.userPwd);
        etPasswordConfrimText = (EditText) findViewById(R.id.userPwdConfirm);


        btnChangePwd.setOnClickListener(RetrievePwd.this);
        btnBacktomain.setOnClickListener(RetrievePwd.this);
        showDialog("修改密码完成后，请在注册邮箱中确认修改，否则修改失败");


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
    public void onClick(View v){
        // 获取用户输入的用户名和两边密码，并进行判断
        String name = etNameText.getText().toString();
        String password = etPasswordText.getText().toString();
        String password2 =etPasswordConfrimText.getText().toString();
        switch (v.getId()) {
            case R.id.changePwd:
                if (name.equals("") || password.equals("")) {
                    Toast.makeText(this,
                            "账号或者新密码不能为空",
                            Toast.LENGTH_SHORT).show();
                }else if(!isPassword(password)){
                    showDialog("新密码长度在7~21之间，且不能有空格");
                }else if(!password2.equals(password)){
                    showDialog("两次输入密码不一致，请重新输入");
                }
                else{
                    String select = String.format(getString(R.string.baseURL));
                    String url1 = select+"personal/RetrievePwd";
                    Log.i("tg",url1);
                    Map<String,String> map = new HashMap<String, String>();
                    map.put("username",name);
                    map.put("password",password);
                    //调用com.example.jiuwei.http包下的volley接口
                    Volley.sendJSONRequest(map, url1, ResponseSign.class, new IDataListener<ResponseSign>() {
                        @Override
                        public void onSuccess(ResponseSign responseSign) {
                            String response = responseSign.msg;
                            if (response.equals("retrieve successfully")) {
                                Toast.makeText(RetrievePwd.this, "在邮箱确认后即可修改成功", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RetrievePwd.this,
                                        SignIn.class);
                                startActivity(intent);
                            } else if (response.equals("userErr_notExist")) {
                                showDialog("用户名不存在");
                            }

                        }

                        @Override
                        public void onFailure() {

                        }
                    });
                }
                break;
            case R.id.backtomain:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("确定要返回登录界面吗？");
                builder.setNegativeButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(RetrievePwd.this,
                                        SignIn.class);
                                startActivity(intent);
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
                break;
        }
    }


}
