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
import android.widget.TextView;
import android.widget.Toast;

import com.example.jiuwei.MainActivity;
import com.example.jiuwei.R;
import com.example.jiuwei.http.IDataListener;
import com.example.jiuwei.http.Volley;
import com.example.jiuwei.http.bean.sign.ResponceSign;

import java.util.HashMap;
import java.util.Map;
/*
 *     登录界面
 */

public class SignIn extends AppCompatActivity implements View.OnClickListener {

    //实例化
    Button btnLogin = null;
    TextView tvSignup = null;
    EditText etNameText = null;
    EditText etPasswordText = null;

    //private String baseURL = "http://127.0.0.1:8000/";
    //private String baseURL = "http://10.0.2.2:8000/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.sign_in);
        setContentView(R.layout.sign_in);
        //绑定
        btnLogin =(Button) findViewById(R.id.login);
        tvSignup = (TextView) findViewById(R.id.signup);
        etNameText = (EditText) findViewById(R.id.username);
        etPasswordText = (EditText) findViewById(R.id.userpassword);


        btnLogin.setOnClickListener(SignIn.this);
        tvSignup.setOnClickListener(SignIn.this);



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
                }//else loginGet(name, password);
                else{
                    String url = "http://10.0.2.2:8000/signin/";
                    Map<String,String> map = new HashMap<String, String>();
                    map.put("username",name);
                    map.put("password",password);
                    //String mapNew = JSON.toJSONString(map);
                    //Log.i("tag",mapNew);

                    //调用com.example.jiuwei.http包下的volley接口
                    Volley.sendJSONRequest(map,
                            url, ResponceSign.class, new IDataListener<ResponceSign>() {
                        @Override
                        public void onSuccess(ResponceSign responceSign) {
                            String response = responceSign.msg;
                            if (response.equals("用户不存在")) {
                                Log.i("tag","进入用户不存在了");
                                showDialog("用户不存在，请修改用户名或者注册新的用户");
                            } else if (response.equals("登录成功")) {
                                Intent intent = new Intent(SignIn.this,
                                        MainActivity.class);
                                startActivity(intent);

                            } else if (response.equals("密码错误")) {
                                showDialog("密码错误,请重新输入");
                            }

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
