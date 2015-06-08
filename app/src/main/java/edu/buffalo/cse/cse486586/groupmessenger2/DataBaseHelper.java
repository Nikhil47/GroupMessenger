package edu.buffalo.cse.cse486586.groupmessenger2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by nikhil on 2/16/15.
 */
public class DataBaseHelper extends SQLiteOpenHelper {
    public static final String TABLE = "kvs";
    public static final String KEY = "key";
    public static final String VALUE = "value";

    private static final String DBNAME = "kvs.db";
    private static final int DATABASE_VERSION = 2;

    private static final String CREATE_DATABASE = "create table " + TABLE + "(" +
            KEY + " text primary key,  " +
            VALUE + " text not null);";

    public DataBaseHelper(Context ctx)
    {
        super(ctx, DBNAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //db.execSQL("drop table if exists " + TABLE);
        db.execSQL(CREATE_DATABASE);
        return;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE);
        return;
    }

    public SQLiteDatabase getDB()
    {
        return this.getWritableDatabase();
    }
}
