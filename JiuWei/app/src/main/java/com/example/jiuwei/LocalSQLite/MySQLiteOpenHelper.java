package com.example.jiuwei.LocalSQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {

    private static final String name = "db_jiuwei"; //数据库名称
    final String CREATE_TABLE_USER_COOKIE="create table tb_userCookie(_id integer primary key autoincrement,cookie)";
    final String CREATE_TABLE_ACTIVITY_JSON="create table tb_activityJSON(_id integer primary key autoincrement,activityJSON)";
    private static final int version = 1; //数据库版本
    public MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {

        //第三个参数CursorFactory指定在执行查询时获得一个游标实例的工厂类,设置为null,代表使用系统默认的工厂类

        super(context, name, null, version);


    }



    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE_USER_COOKIE);
        db.execSQL(CREATE_TABLE_ACTIVITY_JSON);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.i("cookie","版本更新"+oldVersion+"-->"+newVersion);

    }

    //插入数据的方法
    public void insertData(SQLiteDatabase sqLiteDatabase,int id, String tables,String dataname,String data){
        Log.i("调用插入数据方法了",tables+","+dataname+","+data);
        ContentValues values=new ContentValues();
        values.put("_id",id);
        values.put(dataname,data);
        sqLiteDatabase.insertWithOnConflict(tables,null,values,SQLiteDatabase.CONFLICT_REPLACE);
        sqLiteDatabase.close();

    }

    //查询数据的方法
    public String queryData(String tables,String dataname,String selecion){
        SQLiteDatabase db = getReadableDatabase();
        String data =null;
        //创建游标对象
        Cursor cursor = db.query(tables, new String[]{dataname}, selecion, null, null, null, null);
        //利用游标遍历所有数据对象
        while(cursor.moveToNext()){
            data = cursor.getString(cursor.getColumnIndex(dataname));
        }
        return data;
    }

}




