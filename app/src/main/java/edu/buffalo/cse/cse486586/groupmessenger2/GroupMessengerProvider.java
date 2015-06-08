package edu.buffalo.cse.cse486586.groupmessenger2;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

/**
 * GroupMessengerProvider is a key-value table. Once again, please note that we do not implement
 * full support for SQL as a usual ContentProvider does. We re-purpose ContentProvider's interface
 * to use it as a key-value table.
 * 
 * Please read:
 * 
 * http://developer.android.com/guide/topics/providers/content-providers.html
 * http://developer.android.com/reference/android/content/ContentProvider.html
 * 
 * before you start to get yourself familiarized with ContentProvider.
 * 
 * There are two methods you need to implement---insert() and query(). Others are optional and
 * will not be tested.
 * 
 * @author stevko
 *
 */
public class GroupMessengerProvider extends ContentProvider {

    private DataBaseHelper dbh = new DataBaseHelper(getContext());
    private SQLiteQueryBuilder sqb = new SQLiteQueryBuilder();
    private SQLiteDatabase sqd;
    private Cursor c_;

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // You do not need to implement this.
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        // You do not need to implement this.
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        /*
         * TODO: You need to implement this method. Note that values will have two columns (a key
         * column and a value column) and one row that contains the actual (key, value) pair to be
         * inserted.
         * 
         * For actual storage, you can use any option. If you know how to use SQL, then you can use
         * SQLite. But this is not a requirement. You can use other storage options, such as the
         * internal storage option that we used in PA1. If you want to use that option, please
         * take a look at the code for PA1.
         */

        String key = values.getAsString("key");
        String value = values.getAsString("value");

        String selection = dbh.KEY + " = '" + key + "'";
        sqd = dbh.getDB();

        //sqd.insert(dbh.TABLE, null, values);

        sqb.setTables(dbh.TABLE);
        c_ = sqb.query(sqd, null, selection, null, null, null, null);

        if(c_.getCount() == 0){
            //insert the data
            sqd.insert(dbh.TABLE, null, values);
        }
        else{
            /*c_.moveToFirst();
            //delete all the rows and insert data
            while(!c_.isAfterLast()){
                sqd.delete(dbh.TABLE, dbh.KEY + " = '" + c_.getString(0) + "'", null);
                c_.moveToNext();
            }

            sqd.insert(dbh.TABLE, null, values);*/
            sqd.execSQL("update " + dbh.TABLE + " set value = '" + value + "' where key = '" + key +"';" );
        }

        Log.v("insert", values.toString());
        return uri;
    }

    @Override
    public boolean onCreate() {
        // If you need to perform any one-time initialization task, please do it here.

        dbh = new DataBaseHelper(getContext());

        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        /*
         * TODO: You need to implement this method. Note that you need to return a Cursor object
         * with the right format. If the formatting is not correct, then it is not going to work.
         * 
         * If you use SQLite, whatever is returned from SQLite is a Cursor object. However, you
         * still need to be careful because the formatting might still be incorrect.
         * 
         * If you use a file storage option, then it is your job to build a Cursor * object. I
         * recommend building a MatrixCursor described at:
         * http://developer.android.com/reference/android/database/MatrixCursor.html
         */

        String clause = dbh.KEY + " = '" + selection + "'";
        sqd = dbh.getDB();

        sqb.setTables(dbh.TABLE);
        c_ = sqb.query(sqd, null, clause, null, null, null, null);

        //Log.v("query", selection);

        if(c_.getCount() == 0)
            return null;
        else
            return c_;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // You do not need to implement this.
        return 0;
    }
}
