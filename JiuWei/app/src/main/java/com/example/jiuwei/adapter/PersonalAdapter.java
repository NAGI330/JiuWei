package com.example.jiuwei.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.jiuwei.R;

import java.util.ArrayList;
import java.util.List;

/**定义菜单项类*/
class PersonMenuItem {
    String menuTitle ;
    int menuIcon ;

    //构造方法
    public PersonMenuItem(String menuTitle , int menuIcon ){
        this.menuTitle = menuTitle ;
        this.menuIcon = menuIcon ;
    }

}
/**自定义设置侧滑菜单ListView的Adapter*/
public class PersonalAdapter extends BaseAdapter {

    //存储侧滑菜单中的各项的数据
    List<PersonMenuItem> MenuItems = new ArrayList<PersonMenuItem>( ) ;
    //构造方法中传过来的activity
    Context context ;

    //构造方法
    public PersonalAdapter(Context context){

        this.context = context ;

        MenuItems.add(new PersonMenuItem("", R.mipmap.yuki)) ;
        MenuItems.add(new PersonMenuItem("个人信息", R.mipmap.personalmessage)) ;
        MenuItems.add(new PersonMenuItem("修改密码", R.mipmap.repassword)) ;
        MenuItems.add(new PersonMenuItem("设置", R.mipmap.setting)) ;
    }

    @Override
    public int getCount() {

        return MenuItems.size();

    }

    @Override
    public PersonMenuItem getItem(int position) {

        return MenuItems.get(position) ;
    }

    @Override
    public long getItemId(int position) {

        return position ;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView ;
        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.item_person, parent, false);
            ((TextView) view).setText(getItem(position).menuTitle);
            ((TextView) view).setCompoundDrawablesWithIntrinsicBounds(getItem(position).menuIcon,
                    0, 0, 0) ;
        }


        return view ;
    }


}