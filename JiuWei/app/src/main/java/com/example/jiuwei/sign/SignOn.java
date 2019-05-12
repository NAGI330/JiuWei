package com.example.jiuwei.sign;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import com.example.jiuwei.http.IDataListener;
import com.example.jiuwei.http.Volley;
import com.example.jiuwei.http.bean.ResponseSign;

import java.util.HashMap;
import java.util.Map;
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
        showDialog("注册完成后，请在所填写的邮箱中确认注册，否则注册失败");

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
                }
                else{
                    String select = String.format(getString(R.string.baseURL));
                    String url1 = select+"personal/SignOn";
                    Log.i("tg",url1);
                    Map<String,String> map = new HashMap<String, String>();
                    map.put("username",name);
                    map.put("password",password);
                    map.put("email",emall);
                    //调用com.example.jiuwei.http包下的volley接口
                    Volley.sendJSONRequest(map, url1, ResponseSign.class, new IDataListener<ResponseSign>() {
                        @Override
                        public void onSuccess(ResponseSign responseSign) {
                            String response = responseSign.msg;
                            if (response.equals("signOn successfully")) {
                                Toast.makeText(SignOn.this, "注册完成，请去邮箱中确认", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignOn.this,
                                        SignIn.class);
                                startActivity(intent);
                            } else if (response.equals("userErr_exist")) {
                                showDialog("用户名重复，请更换一个用户名");
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


}
