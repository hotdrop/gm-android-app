package jp.hotdrop.gmapp.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Date;

import jp.hotdrop.gmapp.util.DateUtil;

public abstract class AbstractDao {

    private DatabaseHelper dbHelper;
    protected SQLiteDatabase db;

    public AbstractDao(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void beginTran() {
        db = dbHelper.getWritableDatabase();
        db.beginTransaction();
    }

    public void commit() {
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    public void rollback() {
        db.endTransaction();
        db.close();
    }

    protected boolean isBeginTransaction() {
        return (db != null)? true : false;
    }

    protected void readableDatabase() {
        if(db == null) {
            db = dbHelper.getReadableDatabase();
        }
    }

    protected Cursor execSelect(String sql, String[] selectionArgs) {
        return db.rawQuery(sql, selectionArgs);
    }

    protected void execUpdate(String sql, Object[] bindArgs) {
        db.execSQL(sql, bindArgs);
    }

    protected void execInsert(String sql, Object[] bindArgs) {
        db.execSQL(sql, bindArgs);
    }

    protected void execDelete(String sql, Object[] bindArgs) {
        db.execSQL(sql, bindArgs);
    }

    protected String getCursorString(Cursor cursor, String itemName) {
        return cursor.getString(cursor.getColumnIndex(itemName));
    }

    protected int getCursorInt(Cursor cursor, String itemName) {
        return cursor.getInt(cursor.getColumnIndex(itemName));
    }

    protected Date getCursorDate(Cursor cursor, String itemName) {
        long unixEpoch = cursor.getLong(cursor.getColumnIndex(itemName));
        return DateUtil.longToDate(unixEpoch);
    }


}
