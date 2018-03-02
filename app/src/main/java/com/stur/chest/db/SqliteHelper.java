package com.stur.chest.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.stur.chest.dto.CategoryDTO;


public class SqliteHelper extends SQLiteOpenHelper{
    public static final String TB_NAME= "CategoryDTO";  //table name
    public static final int COL_ID = 0;
    public static final int COL_NAME = 1;
    public static final int COL_PARAID = 2;
    public static final int COL_IMG = 3;
    
    public SqliteHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS "+
                TB_NAME+ "("+
                CategoryDTO.ID+ " INTEGER,"+
                CategoryDTO.NAME+ " TEXT,"+
                CategoryDTO.PARENT_ID+ " INTEGER,"+
                CategoryDTO.IMG+ " TEXT"+
                ");"
                );
        Log.d("Database" ,"onCreate" );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL( "DROP TABLE IF EXISTS " + TB_NAME );
        onCreate(db);
        Log.d("Database" ,"onUpgrade" );
    }

    public void updateColumn(SQLiteDatabase db, String oldColumn, String newColumn, String typeColumn){
        try{
            db.execSQL( "ALTER TABLE " +
                    TB_NAME + " CHANGE " +
                    oldColumn + " "+ newColumn +
                    " " + typeColumn
            );
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
