package com.example.jiuwei.http;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class GetCookie extends SQLiteOpenHelper {

    private static final String name = "db_userCookie"; //数据库名称
    final String CREATE_TABLE_SQL="create table tb_userCookie(_id integer primary key autoincrement,cookie)";
    private static final int version = 1; //数据库版本
    public GetCookie(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {

        //第三个参数CursorFactory指定在执行查询时获得一个游标实例的工厂类,设置为null,代表使用系统默认的工厂类

        super(context, name, null, version);


    }



    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE_SQL);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.i("cookie","版本更新"+oldVersion+"-->"+newVersion);

    }

    //插入数据的方法
    public void insertData(SQLiteDatabase sqLiteDatabase,int id, String cookie){

        ContentValues values=new ContentValues();
        values.put("_id",id);
        values.put("cookie",cookie);
        //sqLiteDatabase.insert("tb_userCookie",null,values);
        sqLiteDatabase.insertWithOnConflict("tb_userCookie",null,values,SQLiteDatabase.CONFLICT_REPLACE);
        sqLiteDatabase.close();

    }

    //查询数据的方法
    public String queryData(){
        SQLiteDatabase db = getReadableDatabase();
        String cookie =null;
        //创建游标对象
        Cursor cursor = db.query("tb_userCookie", new String[]{"cookie"}, null, null, null, null, null);
        //利用游标遍历所有数据对象
        while(cursor.moveToNext()){
            cookie = cursor.getString(cursor.getColumnIndex("cookie"));
        }
        return cookie;
    }

}




