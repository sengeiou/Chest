package com.stur.lib.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.stur.lib.Log;
import com.stur.lib.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class LoggerSQLiteHelper extends SQLiteOpenHelper {

    /**
     * 数据库变化监听回调
     */
    public interface LoggerDataChangeListener {
        /**
         * 当数据库发生变化时,回调该方法
         * @param table  发生数据变化的表名
         */
        void onDataChanged(String table);
    }

    public static final String TABLE_NAME_WARNNING = "warnning";
    private static final String DATABASE_NAME = "logger.db";
    private static final int DATABASE_VERSION = 1;
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_EVENT = "event";
    private static final String COLUMN_TIME = "time";
    private static LoggerSQLiteHelper mSQLiteHelper;

    private static List<LoggerDataChangeListener> dataChangeListeners = new ArrayList<LoggerDataChangeListener>();

    public synchronized static LoggerSQLiteHelper getInstance(Context context) {
        if (mSQLiteHelper == null) {
            mSQLiteHelper = new LoggerSQLiteHelper(context.getApplicationContext());
        }
        return mSQLiteHelper;
    }

    /**
     * 添加数据库变化监听,必须在不需要监听的情况下调用
     */
    public static void registerInterceptDataChangeListener(LoggerDataChangeListener listener) {
        if (!dataChangeListeners.contains(listener)) {
            dataChangeListeners.add(listener);
        }
    }

    public static void unregisterInterceptDataChangeListener(LoggerDataChangeListener listener) {
        if (dataChangeListeners.contains(listener)) {
            dataChangeListeners.remove(listener);
        }
    }

    public static long addWarnningRecord(Context context, int type, int event) {
        // TODO: 后台网络事件同步暂无接口，用本地时间代替
        String time = DateTime.FORMATTER.format(new java.util.Date());
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TYPE, type);
        cv.put(COLUMN_EVENT, event);
        cv.put(COLUMN_TIME, time);
        return insert(context, TABLE_NAME_WARNNING, cv);
    }

    public static long insert(Context context, String table, ContentValues values) {
        SQLiteDatabase db = getInstance(context).getWritableDatabase();
        long insert = db.replace(table, null, values);
        if (insert > -1) {
            notifyDataChanged(table);
        }
        return insert;
    }

    /**
     * 批量插入数据
     */
    public static void insertBatch(Context context, String table, List<ContentValues> datas) {
        SQLiteDatabase db = getInstance(context).getWritableDatabase();
        db.beginTransaction();
        for (ContentValues contentValues : datas) {
            db.replace(table, null, contentValues);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        notifyDataChanged(table);
    }

    public static int delete(Context context, String table, String whereClause, String[] whereArgs) {
        SQLiteDatabase db = getInstance(context).getWritableDatabase();
        int delete = db.delete(table, whereClause, whereArgs);
        if (delete > 0) {
            notifyDataChanged(table);
        }
        return delete;
    }

    public static void deleteBatch(Context context, String table, String whereClause, List<String> args) {
        SQLiteDatabase db = getInstance(context).getWritableDatabase();
        int n = 0;
        db.beginTransaction();
        String[] whereArgs = new String[1];
        for (String string : args) {
            whereArgs[0] = string;
            n += db.delete(table, whereClause, whereArgs);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        if (n > 0) {
            notifyDataChanged(table);
        }
    }

    public static int update(Context context, String table, ContentValues values, String whereClause,
            String[] whereArgs) {
        SQLiteDatabase db = getInstance(context).getWritableDatabase();
        int update = db.update(table, values, whereClause, whereArgs);
        if (update > 0) {
            notifyDataChanged(table);
        }
        return update;
    }

    public static Cursor query(Context context, String table, String[] columns, String selection,
            String[] selectionArgs, String orderBy) {
        SQLiteDatabase db = getInstance(context).getReadableDatabase();
        Cursor cursor = db.query(table, columns, selection, selectionArgs, null, null, orderBy);
        return cursor;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        Log.d(this, "onCreate E:");
        // create table member
        String loggerSqlStr = "CREATE TABLE " + TABLE_NAME_WARNNING + " ( " +
                COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_TYPE + " INTEGER, " +
                COLUMN_EVENT + " INTEGER, " +
                COLUMN_TIME + " TEXT )";
        database.execSQL(loggerSqlStr);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

    }

    private static void notifyDataChanged(String table) {
        if (dataChangeListeners.size() > 0) {
            for (LoggerDataChangeListener listener : dataChangeListeners) {
                listener.onDataChanged(table);
            }
        }
    }

    private LoggerSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
}