package ai.sara.fluentlywithsaraai.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.GregorianCalendar;

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
    public ArrayList<String> getAdditionalWords(String userId) {
        ArrayList<String> wordList = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT " + UserListContract.UserFluency.COLUMN_ADDITIONAL_WORDS +
                " FROM " + UserListContract.UserFluency.TABLE_NAME +
                " WHERE " + UserListContract.UserFluency.COLUMN_USER_ID +
                " = ?", new String[] {userId});
        if (c.moveToFirst()) {
            JSONArray words;
            try {
                words = new JSONArray(c.getString(c.getColumnIndex(UserListContract.UserFluency.COLUMN_ADDITIONAL_WORDS)));
            } catch (JSONException e) {
                return wordList;
            }
            for (int i=0; i < words.length(); i++) {
                try {
                    wordList.add(words.getString(i));
                } catch (JSONException e) {
                    return wordList;
                }
            }
            c.close();
        }
        return wordList;
    }
    public ArrayList<Boolean> getAdditionalFluency(String userId) {
        ArrayList<Boolean> fluencyList = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT " + UserListContract.UserFluency.COLUMN_ADDITIONAL_FLUENT +
                " FROM " + UserListContract.UserFluency.TABLE_NAME +
                " WHERE " + UserListContract.UserFluency.COLUMN_USER_ID +
                " = ?", new String[] {userId});
        if (c.moveToFirst()) {
            JSONArray words;
            try {
                words = new JSONArray(c.getString(c.getColumnIndex(UserListContract.UserFluency.COLUMN_ADDITIONAL_FLUENT)));
            } catch (JSONException e) {
                return fluencyList;
            }
            for (int i=0; i < words.length(); i++) {
                try {
                    fluencyList.add(words.getBoolean(i));
                } catch (JSONException e) {
                    return fluencyList;
                }
            }
            c.close();
        }
        return fluencyList;
    }
    public ArrayList<Integer> getAdditionalEncounters(String userId) {
        ArrayList<Integer> encounterList = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT " + UserListContract.UserFluency.COLUMN_ADDITIONAL_ENCOUNTER +
                " FROM " + UserListContract.UserFluency.TABLE_NAME +
                " WHERE " + UserListContract.UserFluency.COLUMN_USER_ID +
                " = ?", new String[] {userId});
        if (c.moveToFirst()) {
            JSONArray words;
            try {
                words = new JSONArray(c.getString(c.getColumnIndex(UserListContract.UserFluency.COLUMN_ADDITIONAL_ENCOUNTER)));
            } catch (JSONException e) {
                return encounterList;
            }
            for (int i=0; i < words.length(); i++) {
                try {
                    encounterList.add(words.getInt(i));
                } catch (JSONException e) {
                    return encounterList;
                }
            }
            c.close();
        }
        return encounterList;
    }
    public Cursor rawQuery(String sql, String[] args) {
        Cursor c = db.rawQuery(sql,args);
        return c;
    }
    public void deleteUser(String userId) {
        db.delete(UserListContract.UserList.TABLE_NAME, UserListContract.UserList.COLUMN_USER_ID + " = ?", new String[] {userId});
        db.delete(UserListContract.UserFluency.TABLE_NAME, UserListContract.UserFluency.COLUMN_USER_ID + " = ?", new String[] {userId});
    }
    private static final String SQL_CREATE_USER_TABLE =
            "CREATE TABLE " + UserListContract.UserList.TABLE_NAME + " (" +
                    UserListContract.UserList.COLUMN_USER_ID + " TEXT PRIMARY KEY NOT NULL," +
                    UserListContract.UserList.COLUMN_USER_NAME + " TEXT NOT NULL," +
                    UserListContract.UserList.COLUMN_BIRTHDATE + " TEXT NOT NULL," +
                    UserListContract.UserList.COLUMN_GENDER + " INT DEFAULT " + UserListContract.UserList.GENDER_OTHER + "," +
                    UserListContract.UserList.COLUMN_SYNC + " INT DEFAULT " + UserListContract.UserList.SYNC_FALSE + "," +
                    UserListContract.UserList.COLUMN_AUTHENTICATION + " INT," +
                    UserListContract.UserList.COLUMN_PREFERENCES + "TEXT)";

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
                    UserListContract.UserFluency.COLUMN_ADDITIONAL_WORDS + " TEXT, " +
                    UserListContract.UserFluency.COLUMN_ADDITIONAL_FLUENT + " TEXT, " +
                    UserListContract.UserFluency.COLUMN_ADDITIONAL_ENCOUNTER + " TEXT, " +
                    "FOREIGN KEY(" + UserListContract.UserFluency.COLUMN_USER_ID + ") REFERENCES " +
                    UserListContract.UserList.TABLE_NAME + "(" + UserListContract.UserList.COLUMN_USER_ID + "))";

    private static final String SQL_CREATE_WORD_ENCOUNTERS_TABLE =
            "CREATE TABLE " + UserListContract.WordEncounters.TABLE_NAME + " (" +
                    UserListContract.WordEncounters.COLUMN_USER_ID + " TEXT NOT NULL," +
                    UserListContract.WordEncounters.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    UserListContract.WordEncounters.COLUMN_WORD + " TEXT NOT NULL," +
                    UserListContract.WordEncounters.COLUMN_TYPE + " INT NOT NULL," +
                    UserListContract.WordEncounters.COLUMN_INPUT + " INT," +
                    UserListContract.WordEncounters.COLUMN_USER_SCORE + " INT," +
                    UserListContract.WordEncounters.COLUMN_RATE + " INT," +
                    UserListContract.WordEncounters.COLUMN_CONTEXT + " INT," +
                    "FOREIGN KEY(" + UserListContract.WordEncounters.COLUMN_USER_ID + ") REFERENCES " +
                    UserListContract.UserList.TABLE_NAME + "(" + UserListContract.UserList.COLUMN_USER_ID + "))";

    private static final String SQL_CREATE_LOCAL_READINGS_TABLE =
            "CREATE TABLE " + UserListContract.LocalReadings.TABLE_NAME + " (" +
                    UserListContract.LocalReadings.COLUMN_USER_ID + " TEXT NOT NULL, " +
                    UserListContract.LocalReadings.COLUMN_TEXT + " INT NOT NULL, " +
                    UserListContract.LocalReadings.COLUMN_SOURCE + " INT NOT NULL, " +
                    UserListContract.LocalReadings.COLUMN_TITLE + " TEXT, " +
                    UserListContract.LocalReadings.COLUMN_TEXT_ID + " INT, " +
                    UserListContract.LocalReadings.COLUMN_TWEET + " INT, " +
                    UserListContract.LocalReadings.COLUMN_URL + " TEXT, " +
                    "UNIQUE (" + UserListContract.LocalReadings.COLUMN_USER_ID + "," + UserListContract.LocalReadings.COLUMN_TEXT + "))";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS ";

    private class DbHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 2;
        public static final String DATABASE_NAME = "SaraLocalUsers.db";

        public DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_USER_TABLE);
            db.execSQL(SQL_CREATE_USER_FLUENCY_TABLE);
            db.execSQL(SQL_CREATE_WORD_ENCOUNTERS_TABLE);
            db.execSQL(SQL_CREATE_LOCAL_READINGS_TABLE);
        }
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (oldVersion == 1) {
                try {
                    Cursor c = db.rawQuery("SELECT ?,? FROM " + UserListContract.UserList.TABLE_NAME,new String[] {UserListContract.UserList.COLUMN_USER_ID, UserListContract.UserList.COLUMN_BIRTHDATE});
                    while (c.moveToNext()) {
                        int year = c.getInt(c.getColumnIndex(UserListContract.UserList.COLUMN_BIRTHDATE));
                        GregorianCalendar birthdate = new GregorianCalendar(year,12,31);
                        ContentValues values = new ContentValues();
                        values.put(UserListContract.UserList.COLUMN_BIRTHDATE, birthdate.toString());
                        String where = UserListContract.UserFluency.COLUMN_USER_ID + " = ? ";
                        String[] args = {c.getString(c.getColumnIndex(UserListContract.UserList.COLUMN_USER_ID))};
                        db.update(UserListContract.UserList.TABLE_NAME,values,where,args);
                    }
                    c.close();
                    String alter = "ALTER TABLE ? ADD COLUMN ? TEXT";
                    db.execSQL(alter,new String[]{UserListContract.UserFluency.TABLE_NAME, UserListContract.UserFluency.COLUMN_ADDITIONAL_FLUENT});
                    db.execSQL(alter,new String[]{UserListContract.UserFluency.TABLE_NAME, UserListContract.UserFluency.COLUMN_ADDITIONAL_ENCOUNTER});
                    db.execSQL(alter,new String[]{UserListContract.WordEncounters.TABLE_NAME, UserListContract.WordEncounters.COLUMN_INPUT});
                    db.execSQL(SQL_CREATE_LOCAL_READINGS_TABLE);
                } catch (Exception e) {
                    db.execSQL(SQL_DELETE_ENTRIES + UserListContract.UserList.TABLE_NAME);
                    db.execSQL(SQL_DELETE_ENTRIES + UserListContract.UserFluency.TABLE_NAME);
                    db.execSQL(SQL_DELETE_ENTRIES + UserListContract.WordEncounters.TABLE_NAME);
                    onCreate(db);
                }
            } else {
                db.execSQL(SQL_DELETE_ENTRIES + UserListContract.UserList.TABLE_NAME);
                db.execSQL(SQL_DELETE_ENTRIES + UserListContract.UserFluency.TABLE_NAME);
                db.execSQL(SQL_DELETE_ENTRIES + UserListContract.WordEncounters.TABLE_NAME);
                onCreate(db);
            }
        }
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(SQL_DELETE_ENTRIES + UserListContract.UserList.TABLE_NAME);
            db.execSQL(SQL_DELETE_ENTRIES + UserListContract.UserFluency.TABLE_NAME);
            db.execSQL(SQL_DELETE_ENTRIES + UserListContract.WordEncounters.TABLE_NAME);
            onCreate(db);
        }
    }
}
