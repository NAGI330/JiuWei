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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jiuwei.MainActivity;
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
                }else loginGet(name, password);
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

    private void loginGet(final String name, final String password) {
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
                    //connection.setRequestMethod("GET");
                    // 设置是否向httpUrlConnection输出，因为这个是post请求，参数要放在
                    // http正文内，因此需要设为true, 默认情况下是false;
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
                    if (response.toString().equals("用户不存在")) {
                        Looper.prepare();
                        showDialog("用户不存在，请修改用户名或者注册新的用户");
                        Looper.loop();
                    } else if (response.toString().equals("登录成功")) {
//                        Looper.prepare();
//                        showDialog("登录成功！");
//                        Looper.loop();
                        Intent intent = new Intent(SignIn.this,
                                MainActivity.class);
                        startActivity(intent);

                    } else if (response.toString().equals("密码错误")) {
                        Looper.prepare();
                        showDialog("密码错误,请重新输入");
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
