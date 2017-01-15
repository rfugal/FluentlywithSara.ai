package ai.sara.fluentlywithsaraai.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by russ.fugal on 1/12/2017.
 */

public class UsersDb {
    private Context context;
    private DbHelper mDbHelper;
    private SQLiteDatabase db;

    public UsersDb(Context c) {
        this.context = c.getApplicationContext();
        mDbHelper = new DbHelper(context);
        db = mDbHelper.getWritableDatabase();
    }

    public void close(){
        db.close();
        mDbHelper.close();
    }
    public boolean hasFluencyModel (String userId) {
        Cursor c = db.rawQuery("SELECT " + UserListContract.UserFluency.COLUMN_USER_ID +
                " FROM " + UserListContract.UserFluency.TABLE_NAME +
                " WHERE " + UserListContract.UserFluency.COLUMN_USER_ID +
                " = ?", new String[] {userId});
        if (c.getCount() > 0) {
            c.close();
            return true;
        }
        else {
            c.close();
            return false;
        }
    }
    public boolean hasUserProfile (String userId) {
        Cursor c = db.rawQuery("SELECT " + UserListContract.UserList.COLUMN_USER_ID +
                " FROM " + UserListContract.UserFluency.TABLE_NAME +
                " WHERE " + UserListContract.UserFluency.COLUMN_USER_ID +
                " = ?", new String[] {userId});
        if (c.getCount() > 0) {
            c.close();
            return true;
        }
        else {
            c.close();
            return false;
        }
    }
    public void update(String tableName, ContentValues values, String where, String[] arg) {
        db.update(tableName,values,where,arg);
    }
    public void insert (String tableName, ContentValues values) {
        db.insert(tableName, null, values);
    }
    public int[] getWeights(String userId){
        int[] wordWeights;
        Cursor c = db.rawQuery("SELECT " + UserListContract.UserFluency.COLUMN_WORD_WEIGHTS +
                " FROM " + UserListContract.UserFluency.TABLE_NAME +
                " WHERE " + UserListContract.UserFluency.COLUMN_USER_ID +
                " = ?", new String[] {userId});
        if (c.moveToFirst()) {
            JSONArray weights;
            try {
                weights = new JSONArray(c.getString(c.getColumnIndex(UserListContract.UserFluency.COLUMN_WORD_WEIGHTS)));
            } catch (JSONException e) {
                return null;
            }
            wordWeights = new int[weights.length()];
            for (int i=0; i < weights.length(); i++) {
                try {
                    wordWeights[i] = weights.getInt(i);
                } catch (JSONException e) {
                    return null;
                }
            }
            return wordWeights;
        } else return null;
    }
    public boolean[] getFluency(String userId){
        boolean[] wordFluency;
        Cursor c = db.rawQuery("SELECT " + UserListContract.UserFluency.COLUMN_WORDS_FLUENT +
                " FROM " + UserListContract.UserFluency.TABLE_NAME +
                " WHERE " + UserListContract.UserFluency.COLUMN_USER_ID +
                " = ?", new String[] {userId});
        if (c.moveToFirst()) {
            JSONArray fluency;
            try {
                fluency = new JSONArray(c.getString(c.getColumnIndex(UserListContract.UserFluency.COLUMN_WORDS_FLUENT)));
            } catch (JSONException e) {
                return null;
            }
            wordFluency = new boolean[fluency.length()];
            for (int i=0; i < fluency.length(); i++) {
                try {
                    wordFluency[i] = fluency.getBoolean(i);
                } catch (JSONException e) {
                    return null;
                }
            }
            return wordFluency;
        } else return null;
    }
    public int[] getEncounters(String userId){
        int[] wordEncounters;
        Cursor c = db.rawQuery("SELECT " + UserListContract.UserFluency.COLUMN_ENCOUNTER_COUNTS +
                " FROM " + UserListContract.UserFluency.TABLE_NAME +
                " WHERE " + UserListContract.UserFluency.COLUMN_USER_ID +
                " = ?", new String[] {userId});
        if (c.moveToFirst()) {
            JSONArray encounters;
            try {
                encounters = new JSONArray(c.getString(c.getColumnIndex(UserListContract.UserFluency.COLUMN_ENCOUNTER_COUNTS)));
            } catch (JSONException e) {
                return null;
            }
            wordEncounters = new int[encounters.length()];
            for (int i=0; i < encounters.length(); i++) {
                try {
                    wordEncounters[i] = encounters.getInt(i);
                } catch (JSONException e) {
                    return null;
                }
            }
            c.close();
            return wordEncounters;
        } else return null;
    }
    public int getScore(String userId){
        int score;
        Cursor c = db.rawQuery("SELECT " + UserListContract.UserFluency.COLUMN_USER_SCORE +
                " FROM " + UserListContract.UserFluency.TABLE_NAME +
                " WHERE " + UserListContract.UserFluency.COLUMN_USER_ID +
                " = ?", new String[] {userId});
        if (c.moveToFirst()) {
            score = c.getInt(c.getColumnIndex(UserListContract.UserFluency.COLUMN_USER_SCORE));
            return score;
        } else return -1;
    }
    public int getReadCount(String userId){
        int readCount;
        Cursor c = db.rawQuery("SELECT " + UserListContract.UserFluency.COLUMN_READ_COUNT +
                " FROM " + UserListContract.UserFluency.TABLE_NAME +
                " WHERE " + UserListContract.UserFluency.COLUMN_USER_ID +
                " = ?", new String[] {userId});
        if (c.moveToFirst()) {
            readCount = c.getInt(c.getColumnIndex(UserListContract.UserFluency.COLUMN_READ_COUNT));
            return readCount;
        } else return -1;
    }
    public int getFluentCount(String userId){
        int fluentCount;
        Cursor c = db.rawQuery("SELECT " + UserListContract.UserFluency.COLUMN_FLUENT_COUNT +
                " FROM " + UserListContract.UserFluency.TABLE_NAME +
                " WHERE " + UserListContract.UserFluency.COLUMN_USER_ID +
                " = ?", new String[] {userId});
        if (c.moveToFirst()) {
            fluentCount = c.getInt(c.getColumnIndex(UserListContract.UserFluency.COLUMN_FLUENT_COUNT));
            return fluentCount;
        } else return -1;
    }
    public Cursor rawQuery(String sql, String[] args) {
        Cursor c = db.rawQuery(sql,args);
        return c;
    }
    private static final String SQL_CREATE_USER_TABLE =
            "CREATE TABLE " + UserListContract.UserList.TABLE_NAME + " (" +
                    UserListContract.UserList.COLUMN_USER_ID + " TEXT PRIMARY KEY NOT NULL," +
                    UserListContract.UserList.COLUMN_USER_NAME + " TEXT NOT NULL," +
                    UserListContract.UserList.COLUMN_BIRTHDATE + " INT NOT NULL," +
                    UserListContract.UserList.COLUMN_GENDER + " INT DEFAULT " + UserListContract.UserList.GENDER_OTHER + "," +
                    UserListContract.UserList.COLUMN_SYNC + " INT DEFAULT " + UserListContract.UserList.SYNC_FALSE + "," +
                    UserListContract.UserList.COLUMN_AUTHENTICATION + " INT)";

    private static final String SQL_CREATE_USER_FLUENCY_TABLE =
            "CREATE TABLE " + UserListContract.UserFluency.TABLE_NAME + " (" +
                    UserListContract.UserFluency.COLUMN_USER_ID + " TEXT PRIMARY KEY NOT NULL," +
                    UserListContract.UserFluency.COLUMN_WORD_WEIGHTS + " TEXT NOT NULL," +
                    UserListContract.UserFluency.COLUMN_WORDS_FLUENT + " TEXT NOT NULL," +
                    UserListContract.UserFluency.COLUMN_ENCOUNTER_COUNTS + " TEXT NOT NULL," +
                    UserListContract.UserFluency.COLUMN_USER_SCORE + " INT DEFAULT 0," +
                    UserListContract.UserFluency.COLUMN_FLUENT_COUNT + " INT DEFAULT 0," +
                    UserListContract.UserFluency.COLUMN_READ_COUNT + " INT DEFAULT 0," +
                    UserListContract.UserFluency.COLUMN_VERSION + " INT DEFAULT 1," +
                    "FOREIGN KEY(" + UserListContract.UserFluency.COLUMN_USER_ID + ") REFERENCES " +
                    UserListContract.UserList.TABLE_NAME + "(" + UserListContract.UserList.COLUMN_USER_ID + "))";

    private static final String SQL_CREATE_WORD_ENCOUNTERS_TABLE =
            "CREATE TABLE " + UserListContract.WordEncounters.TABLE_NAME + " (" +
                    UserListContract.WordEncounters.COLUMN_USER_ID + " TEXT NOT NULL," +
                    UserListContract.WordEncounters.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    UserListContract.WordEncounters.COLUMN_WORD + " TEXT NOT NULL," +
                    UserListContract.WordEncounters.COLUMN_TYPE + " INT NOT NULL," +
                    UserListContract.WordEncounters.COLUMN_USER_SCORE + " INT," +
                    UserListContract.WordEncounters.COLUMN_RATE + " INT," +
                    UserListContract.WordEncounters.COLUMN_CONTEXT + " INT," +
                    "FOREIGN KEY(" + UserListContract.WordEncounters.COLUMN_USER_ID + ") REFERENCES " +
                    UserListContract.UserList.TABLE_NAME + "(" + UserListContract.UserList.COLUMN_USER_ID + "))";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS ";

    private class DbHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "SaraLocalUsers.db";

        public DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_USER_TABLE);
            db.execSQL(SQL_CREATE_USER_FLUENCY_TABLE);
            db.execSQL(SQL_CREATE_WORD_ENCOUNTERS_TABLE);
        }
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_ENTRIES + UserListContract.UserList.TABLE_NAME);
            db.execSQL(SQL_DELETE_ENTRIES + UserListContract.UserFluency.TABLE_NAME);
            db.execSQL(SQL_DELETE_ENTRIES + UserListContract.WordEncounters.TABLE_NAME);
            onCreate(db);
        }
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }
}
