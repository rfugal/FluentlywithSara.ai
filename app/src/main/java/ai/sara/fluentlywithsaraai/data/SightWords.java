package ai.sara.fluentlywithsaraai.data;

import android.content.ContentValues;
import android.content.Context;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.Random;
import ai.sara.fluentlywithsaraai.R;

/**
 * Created by russ.fugal on 12/14/2016.
 */

public class SightWords {
    private String[] wordList;
    private int[] wordWeights;
    private boolean[] wordsFluent;
    private int[] wordEncounters;
    private String mUserId;
    private int mScore;
    private int readCount;
    private Context context;
    private UsersDb db;

    //Constructor
    public SightWords (Context c, String userId) {
        this.context = c.getApplicationContext();
        db = new UsersDb(this.context);
        wordList = this.context.getResources().getStringArray(R.array.frequent_words);
        int[] initialWeights = this.context.getResources().getIntArray(R.array.initial_weights);
        mUserId = userId;
        mScore = 0;
        readCount = 0;
        if (db.hasFluencyModel(mUserId)) {
            wordWeights = db.getWeights(mUserId);
            wordsFluent = db.getFluency(mUserId);
            wordEncounters = db.getEncounters(mUserId);
            mScore = db.getScore(mUserId);
            readCount = db.getReadCount(mUserId);
        } else {
            wordWeights = new int[wordList.length];
            wordsFluent = new boolean[wordList.length];
            wordEncounters = new int[wordList.length];
            for (int index = 0; index < wordList.length; index ++) {
                try {
                    wordWeights[index] = initialWeights[index];
                } catch (Exception e) {
                    wordWeights[index] = 1;
                }
            }
            dbSave();
        }
    }
    private void dbSave() {
        int fluentCount = getFluentCount();
        JSONArray weights = new JSONArray();
        JSONArray fluent = new JSONArray();
        JSONArray encounters = new JSONArray();
        for (int i=0; i < wordList.length; i++) {
            weights.put(wordWeights[i]);
            fluent.put(wordsFluent[i]);
            encounters.put(wordEncounters[i]);
        }
        ContentValues values = new ContentValues();
        values.put(UserListContract.UserFluency.COLUMN_WORD_WEIGHTS, weights.toString());
        values.put(UserListContract.UserFluency.COLUMN_WORDS_FLUENT, fluent.toString());
        values.put(UserListContract.UserFluency.COLUMN_FLUENT_COUNT, fluentCount);
        values.put(UserListContract.UserFluency.COLUMN_ENCOUNTER_COUNTS, encounters.toString());
        values.put(UserListContract.UserFluency.COLUMN_USER_SCORE, mScore);
        values.put(UserListContract.UserFluency.COLUMN_READ_COUNT, readCount);
        if (db.hasFluencyModel(mUserId)) {
            db.update(UserListContract.UserFluency.TABLE_NAME,values,UserListContract.UserFluency.COLUMN_USER_ID + " = ? ",new String[]{mUserId});
        }
        else {
            values.put(UserListContract.UserFluency.COLUMN_USER_ID, mUserId);
            db.insert(UserListContract.UserFluency.TABLE_NAME, values);
        }
    }
    public int getFluentCount() {
        int fluentCount = 0;
        for (int i = 0; i < wordsFluent.length; i++) {
            if (wordsFluent[i]) fluentCount++;
        }
        return fluentCount;
    }
    public int getScore() {
        return mScore;
    }
    public ArrayList<String> getFluentWords() {
        ArrayList<String> RANwords = new ArrayList<>();
        for (int index = 0; index < wordsFluent.length; index++) {
            if (wordsFluent[index]) RANwords.add(wordList[index]);
        }
        return RANwords;
    }
    public String getUserId() {
        return mUserId;
    }

    public String getRandWord() {
        int sumWeights = 0;
        Random random = new Random();
        int index = 0;
        while (index < wordWeights.length && sumWeights < 1000000) {
            sumWeights = sumWeights + wordWeights[index];
            index++;
        }
        if (sumWeights == 0) return "";
        int target = random.nextInt(sumWeights);
        sumWeights = 0;
        index = -1;
        while (sumWeights < target) {
            index++;
            sumWeights = sumWeights + wordWeights[index];
        }
        return wordList[index];
    }

    //Known Known Encounter
    public String recognizedWord (String word) {
        int index = findWordIndex(word);
        if (index != -1) {
            wordWeights[index] = wordWeights[index] / 4;
            if (wordEncounters[index] > 0) wordEncounters[index]++;
            else {
                wordEncounters[index] = 1;
                wordWeights[index] = wordWeights[index] / 4;
                wordsFluent[index] = true;
            }
            if (wordsFluent[index]) mScore += 4;
            if (wordsFluent[index] && wordEncounters[index] > 4) wordWeights[index] = wordWeights[index] / 4;
            if (wordEncounters[index] > 2) wordsFluent[index] = true;
            mScore += 1;
            dbSave();
            return getRandWord();
        }
        return "error";
    }

    //Known Unknown Encounter
    public String taughtWord (String word) {
        int index = findWordIndex(word);
        if (index != -1) {
            wordWeights[index] += 125;
            if (wordEncounters[index] > 0) wordEncounters[index]++;
            else wordEncounters[index] = 1;
            if (wordEncounters[index] < 8) wordsFluent[index] = false;
            mScore += 1;
            dbSave();
            return getRandWord();
        }
        return "error";
    }

    //Fuzzy Encounter
    public String encounterWord (String word) {
        int index = findWordIndex(word);
        if (index != -1) {
            if (wordEncounters[index] > 0) wordEncounters[index]++;
            else wordEncounters[index] = 1;
            if (wordEncounters[index] > 12) wordsFluent[index] = true;
            else if (!wordsFluent[index]) wordWeights[index] += 125;
            mScore += 1;
            dbSave();
            return getRandWord();
        }
        return "error";
    }

    private int findWordIndex (String word) {
        for (int index = 0; index < wordList.length; index++) {
            if (wordList[index] == word) {
                return index;
            }
        }
        return -1;
    }
    public void close() {
        db.close();
    }
}
