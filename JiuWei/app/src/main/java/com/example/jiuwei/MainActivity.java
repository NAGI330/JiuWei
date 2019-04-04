package com.example.jiuwei;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jiuwei.adapter.PersonalAdapter;
import com.example.jiuwei.friend.Friend_Fragment;
import com.example.jiuwei.message.Msg_Fragment;
import com.example.jiuwei.myActivity.CreateActivity_Fragment;
import com.example.jiuwei.myActivity.MyActivity_Fragment;
import com.example.jiuwei.personalInfo.ChangePwd;
import com.example.jiuwei.personalInfo.Setting;
import com.example.jiuwei.personalInfo.UserMenu_Fragment;
import com.example.jiuwei.personalInfo.UserMsg;
import com.example.jiuwei.push.Push_Fragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private RadioGroup radioGroup;
    private RadioButton radio_home, radio_friend, radio_myac, radio_msg;
    private ImageView radio_plus;
    private RadioButton radio_me;
    //hide_show方法使用
    private Fragment currentFragment;
    private LinearLayout lL;
    private Fragment fragment_push;
    private Fragment fragment_friend;
    private Fragment fragment_createActivity;
    private Fragment fragment_myActivity;
    private Fragment fragment_msg;
    private Fragment fragment_userMenu;
    private TextView textView;
    private List<Fragment> list;
    private FrameLayout frameLayout1;
    private FrameLayout frameLayout2;

    ListView lvDrawer; //侧滑菜单视图
    PersonalAdapter personalAdapter; // 侧滑菜单ListView的Adapter
    DrawerLayout mDrawerLayout; // DrawerLayout组件
    ActionBarDrawerToggle mDrawerToggle; //侧滑菜单状态监听器
    Fragment fragment;
    //private CoordinatorLayout right;
    //private NavigationView left;

    //点击返回键时的时间
    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化页面
        initView();

    }

    //重写手机物理按键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //如果此时按下返回键
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //创建退出方法exit()
    public void exit() {
        //判断两次按下返回键的时间差
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            //finish();
            //System.exit(0);
            moveTaskToBack(true);
        }
    }

    private void initView() {

        lL =(LinearLayout) findViewById(R.id.LL);

        frameLayout1 = (FrameLayout) findViewById(R.id.framelayout1);
        frameLayout2 = (FrameLayout) findViewById(R.id.framelayout3);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        textView = (TextView) findViewById(R.id.tvTitle);

        //找到按钮
        radio_home = (RadioButton) findViewById(R.id.btnPush);
        radio_friend = (RadioButton) findViewById(R.id.btnFriend);
        radio_plus = (ImageView) findViewById(R.id.btnCreateActivity);
        radio_myac = (RadioButton) findViewById(R.id.btnMyActivity);
        radio_msg = (RadioButton) findViewById(R.id.btnMsg);
        radio_me = (RadioButton) findViewById(R.id.btnPersonalInfo);

        //初始化当前fragment,hide_show方法使用
        currentFragment = new CreateActivity_Fragment();

        //创建Fragment对象及集合
        fragment_push = new Push_Fragment();
        fragment_friend = new Friend_Fragment();
        fragment_createActivity = new CreateActivity_Fragment();
        fragment_myActivity = new MyActivity_Fragment();
        fragment_msg = new Msg_Fragment();
        fragment_userMenu = new UserMenu_Fragment();


        //将Fragment对象添加到list中
        list = new ArrayList<>();
        list.add(fragment_push);
        list.add(fragment_friend);
        list.add(fragment_createActivity);
        list.add(fragment_myActivity);
        list.add(fragment_msg);
        list.add(fragment_userMenu);

        //设置RadioGroup开始时设置的按钮，设置第一个按钮为默认值
        radioGroup.check(R.id.btnPush);

        //设置按钮点击监听
        radio_home.setOnClickListener(this);
        radio_friend.setOnClickListener(this);
        radio_plus.setOnClickListener(this);
        radio_myac.setOnClickListener(this);
        radio_msg.setOnClickListener(this);
        radio_me.setOnClickListener(this);
        //lvDrawer.setOnItemClickListener

        //初始时向容器中添加第一个Fragment对象
        replaceFragment(fragment_push);

        //hide_showFragment(fragment_push);
        //为侧滑菜单设置Adapter，并为ListView添加单击事件监听器
        lvDrawer = (ListView) findViewById(R.id.left_drawer);
        personalAdapter = new PersonalAdapter(this);
        lvDrawer.setAdapter(personalAdapter);
        //为DrawerLayout注册状态监听器
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                Log.d("tag", "打开了");
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                Log.d("tag", "关闭了");
                //removeFrag();
                invalidateOptionsMenu();


            }
        };
        //mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                //获取屏幕的宽高
                WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                Display display = manager.getDefaultDisplay();
                //设置右面的布局位置  根据左面菜单的right作为右面布局的left   左面的right+屏幕的宽度（或者right的宽度这里是相等的）为右面布局的right
                lL.layout(lvDrawer.getRight(), 0, lvDrawer.getRight() + display.getWidth(), display.getHeight());
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });


        //mDrawerLayout.openDrawer(Gravity.LEFT);//侧滑打开  不设置则不会默认打开
        lvDrawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                switch (position) {
                    case 1:
                        Log.i("tag","第一个项目");
                        Intent intent1 = new Intent(MainActivity.this,
                                UserMsg.class);
                        startActivity(intent1);
                        break;
                    case 2:
                        Log.i("tag","第二个项目");
                        Intent intent2 = new Intent(MainActivity.this,
                                ChangePwd.class);
                        startActivity(intent2);
                        break;
                    case 3:
                        Log.i("tag","第三个项目");
                        Intent intent3 = new Intent(MainActivity.this,
                                Setting.class);
                        startActivity(intent3);
                        break;
                    case 4:
                        Log.i("tag","第四个项目");
                    default:
                        break;
                }
            }
        });


    }


    //向Activity中添加Fragment的方法
    public void replaceFragment(Fragment fragment) {

        //获得Fragment管理器
        FragmentManager fragmentManager = getSupportFragmentManager();
        //使用管理器开启事务
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //使用事务替换Fragment容器中Fragment对象
        fragmentTransaction.replace(R.id.framelayout1, fragment);
        //提交事务，否则事务不生效
        fragmentTransaction.commit();
    }

    public void hide_showFragment(Fragment fragment) {
        if (currentFragment != fragment) {   //获得Fragment管理器
            FragmentManager fragmentManager = getSupportFragmentManager();
            //使用管理器开启事务
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if (!fragment.isAdded()) {
                // 隐藏当前的fragment，将 下一个fragment 添加进去
                fragmentTransaction.hide(currentFragment)
                        .add(R.id.framelayout1, fragment, "").commit();
            } else {
                // 隐藏当前的fragment，显示下一个fragment
                fragmentTransaction.hide(currentFragment).show(fragment).commit();
            }
            currentFragment = fragment;
        }
    }


    @Override
    public void onClick(View v) {
        //我们根据参数的id区别不同按钮
        //不同按钮对应着不同的Fragment对象页面
        switch (v.getId()) {
            case R.id.btnPush:
                replaceFragment(fragment_push);
                textView.setText("活动推送");
                Log.d("tag", "点击了home");
                if (fragment_push.isHidden()) {
                    Log.d("tag", "home隐藏了！");
                }
                break;
            case R.id.btnFriend:
                replaceFragment(fragment_friend);
                textView.setText("好友");
                Log.d("tag", "点击了friend");
                break;
            case R.id.btnCreateActivity:
                replaceFragment(fragment_createActivity);
                textView.setText("添加活动");
                Log.d("tag", "点击了plus");
                break;
            case R.id.btnMyActivity:
                replaceFragment(fragment_myActivity);
                textView.setText("我的活动");
                Log.d("tag", "点击了myac");
                break;
            case R.id.btnMsg:
                replaceFragment(fragment_msg);
                textView.setText("消息");
                Log.d("tag", "点击了msg");
                break;
            case R.id.btnPersonalInfo:
                Log.d("tag", "点击了me ");
                mDrawerLayout.openDrawer(Gravity.LEFT);//侧滑打开  不设置则不会默认打开
                //replaceFragment(fragment_userMenu);
        }
    }


    @Override
    public void finish() {
        ViewGroup viewGroup = (ViewGroup) getWindow().getDecorView();
        viewGroup.removeAllViews();
        super.finish();
    }
}


