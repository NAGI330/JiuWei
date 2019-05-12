package com.example.jiuwei.LocalSQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Arrays;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {

    private static final String name = "db_jiuwei"; //数据库名称
    final String CREATE_TABLE_USER_COOKIE="create table tb_userCookie(_id integer primary key autoincrement,cookie)";
    final String CREATE_TABLE_ACTIVITY_Mine="create table tb_activityMine(_id integer primary key ,activityMine,Date timestamp)";
    final String CREATE_TABLE_ACTIVITY_ToJoin="create table tb_activityToJoin(_id integer primary key ,activityToJoin,Date timestamp)";
    final String CREATE_TABLE_ACTIVITY_History="create table tb_activityHistory(_id integer primary key ,activityHistory,Date timestamp)";
    private static final int version = 1; //数据库版本
    public MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {

        //第三个参数CursorFactory指定在执行查询时获得一个游标实例的工厂类,设置为null,代表使用系统默认的工厂类

        super(context, name, null, version);


    }



    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE_USER_COOKIE);
        db.execSQL(CREATE_TABLE_ACTIVITY_Mine);
        db.execSQL(CREATE_TABLE_ACTIVITY_ToJoin);
        db.execSQL(CREATE_TABLE_ACTIVITY_History);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.i("cookie","版本更新"+oldVersion+"-->"+newVersion);

    }

    //插入数据的方法
    public void insertData(SQLiteDatabase sqLiteDatabase,String id, String tables,String dataname,String data){
        Log.i("调用插入数据方法了",tables+","+dataname+","+data);
        ContentValues values=new ContentValues();
        values.put("_id",id);
        values.put(dataname,data);
        //判断该数据是否存在
        String[] array = queryDataALL(tables,"_id",null);
        boolean flag = Arrays.asList(array).contains(id);
        //如果存在，就更新
        if(flag){
            sqLiteDatabase.update(tables,values,"_id="+id,null);
        }else{
            sqLiteDatabase.insert(tables,null,values);
        }
        sqLiteDatabase.close();

    }

    //查询数据的方法
    public String queryData(String tables,String dataname,String selecion){
        Log.i("调用查询数据方法了",tables+" "+dataname+" "+selecion);
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
    //查询数据某一字段所有数据的方法
    public String[] queryDataALL(String tables,String id,String orderBy){
        SQLiteDatabase db = getReadableDatabase();
        String data[] = new String[40];
        //创建游标对象
        Cursor cursor = db.query(tables, new String[]{id}, null, null, null, null, orderBy);
        //利用游标遍历所有数据对象
        int i=0;
       // Log.i("while前",cursor.getString(cursor.getColumnIndex(id)));
        while(cursor.moveToNext()){
            data[i] = cursor.getString(cursor.getColumnIndex(id));
            Log.i("queryDataALL",data[i]);
            i++;

        }
        return data;
    }

    public void deleteData(String tables,String id){

        SQLiteDatabase db = getReadableDatabase();
        if(db.isOpen()) {
            db.delete(tables, "_id=?",new String[]{id});
            db.close();
        }

    }

}




