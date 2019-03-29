package com.example.jiuwei.sign;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.example.jiuwei.R;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.regex.Pattern;

/*
 *      注册界面
 */
public class SignOn extends AppCompatActivity implements View.OnClickListener {
    //实例化
    Button btnSignup = null;
    Button btnBacktomain = null;
    CheckBox cbManual =null;
    EditText etNameText = null;
    EditText etPasswordText = null;
    EditText etPasswordConfirmText = null;
    EditText etEmall = null;
    //private String baseURL = "http://10.0.2.2:8000/";
    SignIn si =new SignIn();
    //正则表达式：验证用户名
    public static final String REGEX_USERNAME = "^[a-zA-Z]\\w{5,20}$";
    //正则表达式：验证密码
    public static final String REGEX_PASSWORD = "^[\\S]{6,20}$";
    //正则表达式：验证邮箱
    public static final String REGEX_EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
    //判断注册信息字符串是否合法
    public static boolean isUsername(String username) {
        return Pattern.matches(REGEX_USERNAME, username);
    }
    public static boolean isPassword(String password) {
        return Pattern.matches(REGEX_PASSWORD, password);
    }
    public static boolean isEmail(String email) {

        return Pattern.matches(REGEX_EMAIL, email);
    }



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_on);
        //绑定
        btnSignup = (Button) findViewById(R.id.signup);
        btnBacktomain =(Button) findViewById(R.id.backtomain);
        etNameText = (EditText) findViewById(R.id.username);
        etPasswordText = (EditText) findViewById(R.id.userpassword);
        etPasswordConfirmText = (EditText) findViewById(R.id.userpasswordconfirm);
        etEmall = (EditText) findViewById(R.id.email);
        cbManual = (CheckBox) findViewById(R.id.manual);


        btnSignup.setOnClickListener(SignOn.this);
        btnBacktomain.setOnClickListener(SignOn.this);
        cbManual.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                // TODO Auto-generated method stub
            }
        });


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
        String password2 = etPasswordConfirmText.getText().toString();
        String emall = etEmall.getText().toString();
        switch (v.getId()) {
            case R.id.signup:
                if (name.equals("") || password.equals("")||emall.equals("")) {
                    Toast.makeText(this,
                            "账号、密码或者邮箱不能为空",
                            Toast.LENGTH_SHORT).show();
                }else if(!isUsername(name)){
                    showDialog("用户名必须以字母开头，长度在6~21之间，且不能有！@空格等特殊符号");
                }else if(!isPassword(password)){
                    showDialog("密码长度在7~21之间，且不能有空格");
                }else if(!password2.equals(password)){
                    showDialog("两次输入密码不一致，请重新输入");
                }else if (!isEmail(emall)){
                    showDialog("邮箱格式不正确！请重新输入");
                }else if (!cbManual.isChecked()){
                    showDialog("请阅读用户手册");
                }else signupGet(name, password);
                break;
            case R.id.backtomain:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("确定要返回登录界面吗？");
                builder.setNegativeButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(SignOn.this,
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
    private void signupGet(final String name, final String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                String url ="http://10.0.2.2:8000/SignIn/";
                // 生成请求对象
                try {

                    URL urlobject = new URL(url);
                    connection = (HttpURLConnection) urlobject.openConnection();
                    // 设置请求方式
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    connection.setUseCaches(false);
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    DataOutputStream dataOutputStream =
                            new DataOutputStream(connection.getOutputStream());
                    dataOutputStream.writeBytes("username=" + name + "&password=" + password);
                    dataOutputStream.flush();
                    dataOutputStream.close();
                    InputStream inputStream = connection.getInputStream();
                    //读取服务器返回的信息
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null)
                        response.append(line);
                    Log.d("tag", response.toString());
                    if (response.toString().equals("注册成功")) {
                        Looper.prepare();
                        showDialog("注册成功，账号为："
                                + name + "。密码为：" + password + "。你可以登录了");
                        Looper.loop();
                    } else if (response.toString().equals("用户名重复")) {
                        Looper.prepare();
                        showDialog("用户名重复，请更换一个用户名");
                        Looper.loop();
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }


            }
        }).start();
    }


}
