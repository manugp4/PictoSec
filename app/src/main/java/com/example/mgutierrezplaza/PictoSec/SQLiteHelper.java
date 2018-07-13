package com.example.mgutierrezplaza.PictoSec;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class SQLiteHelper extends SQLiteOpenHelper{

    public SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public void queryData(String sql){
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sql);
    }

    public void deleteData(String sec, String name, String p, String p1, String p2, String p3, String aud){
        SQLiteDatabase database = getWritableDatabase();
        database.delete("PRUEBA", "secuencia=? AND name=? AND path=? AND path1=? AND path2=? AND path3=? AND audio=?",
                new String[] {sec,name,p,p1,p2,p3,aud});
    }

    public void deleteSec(String sec){
        SQLiteDatabase database = getWritableDatabase();
        database.delete("PRUEBA", "secuencia=?",
                new String[] {sec});
    }

    public void insertData(String secuencia, String name, String path, String path1, String path2, String path3, String audio){

        SQLiteDatabase database = getWritableDatabase();
        String sql = "INSERT INTO PRUEBA VALUES (NULL, ?, ?, ?, ?, ?, ?, ?)";

        SQLiteStatement statement = database.compileStatement(sql);

        statement.clearBindings();
        statement.bindString(1, secuencia);
        statement.bindString(2, name);
        statement.bindString(3, path);
        statement.bindString(4, path1);
        statement.bindString(5, path2);
        statement.bindString(6, path3);
        statement.bindString(7, audio);

        statement.executeInsert();
    }

    public Cursor getData(String sql){
        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery(sql, null);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
