package ai.sara.fluentlywithsaraai.data;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ai.sara.fluentlywithsaraai.R;

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
    List<String> readings;
    public static final Pattern wordEx = Pattern.compile("([a-zA-Z']+)");
    ProgressDialog pd;

    public User(Context c, String userId) {
        this.context = c.getApplicationContext();
        mUserId = userId;
    }
    public User(Context c, String username, GregorianCalendar birthdate, int gender) {
        CreateNewUser(c,username,birthdate,gender);
    }
    public User(Context c, String username, GregorianCalendar birthdate, int gender, String knownwords) {
        if (CreateNewUser(c,username,birthdate,gender)) {
            loadFluencyModel();
            int input = UserListContract.WordEncounters.INPUT_USER_CREATION;
            if (username.equals(this.context.getResources().getString(R.string.example_user_name))) {
                input = UserListContract.WordEncounters.INPUT_IGNORE;
                for (int i=0; i<100; i++) {
                    recognizedWord(mWordModel.wordList[i],0,input);
                }
            }
            Matcher m = wordEx.matcher(knownwords);
            while (m.find()) {
                recognizedWord(m.group(),0,input);
            }
        }
    }
    private boolean CreateNewUser(Context c, String username, GregorianCalendar birthdate, int gender) {
        this.context = c.getApplicationContext();
        db = new UsersDb(this.context);
        mUserId = createUserId();
        while (db.hasUserProfile(mUserId)) mUserId = createUserId();
        ContentValues values = new ContentValues();
        values.put(UserListContract.UserList.COLUMN_USER_ID, mUserId);
        values.put(UserListContract.UserList.COLUMN_USER_NAME, username);
        values.put(UserListContract.UserList.COLUMN_BIRTHDATE, birthdate.toString());
        values.put(UserListContract.UserList.COLUMN_GENDER, gender);
        db.insert(UserListContract.UserList.TABLE_NAME, values);
        db.close();
        Name = username;
        return true;
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
        readings = Arrays.asList(context.getResources().getStringArray(R.array.readings));
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
    public boolean allowTwitter() {
        return true;
    }
    public String getRandWord() {
        if (hasMetrics) return mWordModel.getRandWord();
        else return "error";
    }
    public String recognizedWord(String word, int rate, int input) {
        if (hasMetrics) {
            String newWord = mWordModel.recognizedWord(word,rate,input);
            return newWord;
        } else return "error";
    }
    public String taughtWord(String word) {
        if (hasMetrics) {
            String newWord = mWordModel.taughtWord(word);
            //if (newWord == "error") newWord = mWordModel.getRandWord();
            return newWord;
        } else return "error";
    }
    public String encounterWord(String word, int rate) {
        if (hasMetrics) {
            String newWord = mWordModel.encounterWord(word, rate);
            //if (newWord == "error") newWord = mWordModel.getRandWord();
            return newWord;
        } else return "error";
    }
    public boolean getWordFluency(String word) {
        if (hasMetrics) {
            Matcher m = wordEx.matcher(word);
            if (m.find()) {
                String test = m.group().toLowerCase();
                return mWordModel.getWordFluency(test);
            }
        }
        return false;
    }

    // TODO: Make getRandomReading() an AsyncTask
    public ArrayList<String> getRandomReading() {
        if (hasMetrics) {
            Collections.shuffle(readings);
            ArrayList<String> words = mWordModel.getFluentWords();
            for (int i=0; i < words.size(); i++) {
                words.set(i,words.get(i).toLowerCase());
            }
            ArrayList<String> matchedPassages = new ArrayList<>();
            int ratio = 15;
            int fluentCount = mWordModel.getFluentCount();
            if (fluentCount < 25) ratio = 2;
            else if (fluentCount < 100) ratio = 3;
            else if (fluentCount < 200) ratio = 4;
            else if (fluentCount < 400) ratio = 6;
            else if (fluentCount < 800) ratio = 9;
            else if (fluentCount < 1600) ratio = 12;
            int break_loop = 0;
            for (int i = 0; i < readings.size(); i++) {
                break_loop++;
                if (checkPassage(readings.get(i), words, ratio) != null) {
                    break_loop = 0;
                    matchedPassages.add(readings.get(i));
                    if (matchedPassages.size() > 12) break;
                }
                if (break_loop > 2000) break;
            }
            Collections.shuffle(matchedPassages);
            return matchedPassages;
        }
        return new ArrayList<String>();
    }
    private String checkPassage(String passage, ArrayList<String> words, int ratio) {
        int unknowns = 0;
        int count = 0;
        Matcher m = wordEx.matcher(passage.toLowerCase());
        while (m.find()) {
            count++;
            if (!words.contains(m.group())) unknowns++;
        }
        int min = 1;
        if (ratio < 3) min = 0;
        if (unknowns >= min && unknowns < Math.max((count / ratio),2)) {
            return passage;
        }
/*
        String[] sentences = passage.replace("\"","").split("[.?!]");
        if (sentences.length > 1 && count - unknowns > 12) {
            for (String sentence : sentences) {
                if (checkPassage(sentence, words, ratio) != null) return sentence;
            }
        }
*/
        return null;
    }
    public String getDefinition(String word) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        String reply = null;
        try {
            URL url = new URL("https://api.pearson.com/v2/dictionaries/ldoce5/entries?headword=" + word.replace(" ","+"));
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            InputStream stream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));
            StringBuffer buffer = new StringBuffer();
            String line = "";
            while ((line = reader.readLine()) != null) {
                buffer.append(line+"\n");
                Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)
            }
            reply = buffer.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (reply != null) {
            try {
                JSONObject dict_entry = new JSONObject(reply);
                JSONArray entries = dict_entry.getJSONArray("results");
                for (int i = 0; i < entries.length(); i++) {
                    JSONObject entry = entries.getJSONObject(i);
                    if (entry.get("headword") == word) {
                        JSONArray senses = entry.getJSONArray("senses");
                        return senses.getJSONObject(0).getString("definition");
                    }
                }
            } catch (JSONException e) {
            }
        }
        return null;
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
        return userId;
    }
    public void deleteUser() {
        db = new UsersDb(this.context);
        db.deleteUser(mUserId);
        db.close();
        if (hasMetrics) mWordModel.close();
    }
    public void close() {
        if (hasMetrics) mWordModel.close();
    }
}