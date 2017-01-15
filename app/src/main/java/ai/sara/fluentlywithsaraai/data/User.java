package ai.sara.fluentlywithsaraai.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by russ.fugal on 12/14/2016.
 */

public class User {
    private String mUserId = null;
    private boolean hasMetrics = false;
    private SightWords mWordModel;
    private Context context;
    private UsersDb db;
    private String Name;

    public User(Context c, String userId) {
        this.context = c.getApplicationContext();
        mUserId = userId;
    }
    public User(Context c, String username, int year, int gender) {
        this.context = c.getApplicationContext();
        db = new UsersDb(this.context);
        mUserId = createUserId();
        while (db.hasUserProfile(mUserId)) mUserId = createUserId();
        ContentValues values = new ContentValues();
        values.put(UserListContract.UserList.COLUMN_USER_ID, mUserId);
        values.put(UserListContract.UserList.COLUMN_USER_NAME, username);
        values.put(UserListContract.UserList.COLUMN_BIRTHDATE, year);
        values.put(UserListContract.UserList.COLUMN_GENDER, gender);
        db.insert(UserListContract.UserList.TABLE_NAME, values);
        db.close();
        Name = username;
    }
    public String getUserName(){
        if (Name == null) {
            db = new UsersDb(this.context);
            Cursor c = db.rawQuery("SELECT " + UserListContract.UserList.COLUMN_USER_NAME +
                    " FROM " + UserListContract.UserList.TABLE_NAME +
                    " WHERE " + UserListContract.UserList.COLUMN_USER_ID +
                    " = ?", new String[] {mUserId});
            if (c.moveToFirst()) {
                Name = c.getString(c.getColumnIndex(UserListContract.UserList.COLUMN_USER_NAME));
            }
            c.close();
            db.close();
        }
        return Name;
    }
    public void loadFluencyModel() {
        mWordModel = new SightWords(this.context, mUserId);
        hasMetrics = true;
    }
    public int getFluentCount() {
        if (hasMetrics) return mWordModel.getFluentCount();
        else return -1;
    }
    public int getScore() {
        if (hasMetrics) return mWordModel.getScore();
        else return -1;
    }
    public ArrayList<String> getFluentWords() {
        if (hasMetrics) return mWordModel.getFluentWords();
        else return null;
    }
    public String getId() {
        return mUserId;
    }
    public String getRandWord() {
        if (hasMetrics) return mWordModel.getRandWord();
        else return "error";
    }
    public String recognizedWord(String word) {
        if (hasMetrics) {
            String newWord = mWordModel.recognizedWord(word);
            hasMetrics = true;
            //if (newWord == "error") newWord = mWordModel.getRandWord();
            return newWord;
        } else return "error";
    }
    public String taughtWord(String word) {
        if (hasMetrics) {
            String newWord = mWordModel.taughtWord(word);
            hasMetrics = true;
            //if (newWord == "error") newWord = mWordModel.getRandWord();
            return newWord;
        } else return "error";
    }
    public String encounterWord(String word) {
        if (hasMetrics) {
            String newWord = mWordModel.encounterWord(word);
            hasMetrics = true;
            //if (newWord == "error") newWord = mWordModel.getRandWord();
            return newWord;
        } else return "error";
    }

/*    public boolean syncProfile() {
        //sync user account
    }
    public boolean[] createProfileRemote() {
        //create user account at Sara.ai server
        //return [ageKnown,emailKnown,policyAgreement,passwordSet,userIdUnique]
        //if all are true, create an account at Sara.ai
    }
    public void pushAnonData() {
        //push anonymous mWordModel to Sara.ai server
    }
    public void saveProfileLocal() {
        //save metrics and user info
    }
*/
    private String createUserId() {
        String userId = "";
        String[] charSpace = new String[54];
        charSpace[0] = "A";
        charSpace[1] = "a";
        charSpace[2] = "B";
        charSpace[3] = "b";
        charSpace[4] = "C";
        charSpace[5] = "c";
        charSpace[6] = "D";
        charSpace[7] = "d";
        charSpace[8] = "E";
        charSpace[9] = "e";
        charSpace[10] = "F";
        charSpace[11] = "f";
        charSpace[12] = "G";
        charSpace[13] = "g";
        charSpace[14] = "H";
        charSpace[15] = "h";
        charSpace[16] = "J";
        charSpace[17] = "K";
        charSpace[18] = "k";
        charSpace[19] = "L";
        charSpace[20] = "M";
        charSpace[21] = "m";
        charSpace[22] = "N";
        charSpace[23] = "n";
        charSpace[24] = "P";
        charSpace[25] = "p";
        charSpace[26] = "Q";
        charSpace[27] = "q";
        charSpace[28] = "R";
        charSpace[29] = "r";
        charSpace[30] = "S";
        charSpace[31] = "s";
        charSpace[32] = "T";
        charSpace[33] = "t";
        charSpace[34] = "U";
        charSpace[35] = "u";
        charSpace[36] = "V";
        charSpace[37] = "v";
        charSpace[38] = "W";
        charSpace[39] = "w";
        charSpace[40] = "X";
        charSpace[41] = "x";
        charSpace[42] = "Y";
        charSpace[43] = "y";
        charSpace[44] = "Z";
        charSpace[45] = "z";
        charSpace[46] = "2";
        charSpace[47] = "3";
        charSpace[48] = "4";
        charSpace[49] = "5";
        charSpace[50] = "6";
        charSpace[51] = "7";
        charSpace[52] = "8";
        charSpace[53] = "9";
        for (int i = 0; i < 8; i++) {
            userId += charSpace[new Random().nextInt(54)];
        }
        //Check for conflicting UserId locally
        return userId;
    }
}
