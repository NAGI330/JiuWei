package com.example.jiuwei.personalInfo;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jiuwei.R;
import com.example.jiuwei.http.IDataListener;
import com.example.jiuwei.http.Volley;
import com.example.jiuwei.http.bean.ResponseSign;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ChangePwd extends AppCompatActivity {

    private EditText pwdEt,pwdEt2;
    private TextView backToTv;
    private Button changePwdBtn;
    //正则表达式：验证密码
    public static final String REGEX_PASSWORD = "^[\\S]{6,20}$";
    //判断注册信息字符串是否合法

    public static boolean isPassword(String password) {
        return Pattern.matches(REGEX_PASSWORD, password);
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
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usermenu_changepwd);
        pwdEt= findViewById(R.id.userPwd);
        pwdEt2 = findViewById(R.id.userPwdConfirm);
        changePwdBtn= findViewById(R.id.changePwd);
        backToTv = findViewById(R.id.backto);
        backToTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        changePwdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String select = String.format(getString(R.string.baseURL));
                String url = select+"personal/changePwd";
                Map<String,String> map = new HashMap<String, String>();
                String password = pwdEt.getText().toString();
                String password2 = pwdEt2.getText().toString();
                if (password.equals("")) {
                    showDialog("账号或者新密码不能为空");
                }else if(!isPassword(password)){
                    showDialog("新密码长度在7~21之间，且不能有空格");
                }else if(!password2.equals(password)){
                    showDialog("两次输入密码不一致，请重新输入");
                }else{
                    map.put("password",password);
                    Volley.sendJSONRequest(map, url, ResponseSign.class, new IDataListener<ResponseSign>() {
                        @Override
                        public void onSuccess(ResponseSign responseSign) {
                            if(responseSign.msg.equals("change successfully")){
                                Toast.makeText(ChangePwd.this, "修改成功", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }

                        @Override
                        public void onFailure() {

                        }
                    });
                }

            }
        });

    }
}
