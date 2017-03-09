package ai.sara.fluentlywithsaraai.data;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.twitter.Regex;

import org.json.JSONArray;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.LogRecord;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ai.sara.fluentlywithsaraai.R;

/**
 * Created by russ.fugal on 12/14/2016.
 */

public class SightWords {
    public String[] wordList;
    private int[] wordWeights;
    private boolean[] wordsFluent;
    private int[] wordEncounters;
    private String mUserId;
    private int mScore;
    private int readCount;
    private ArrayList<String> additionalWordList;
    private ArrayList<Boolean> additionalWordsFluent;
    private ArrayList<Integer> additionalWordEncounters;
    private Context context;
    private UsersDb db;
    private String lastWord = "be";
    private boolean[] UPDATE_ALL = {true,true,true,true,true};
    private boolean[] UPDATE_FLUENCY = {true,true,false,true,false};
    private boolean[] UPDATE_WEIGHT = {true,false,false,true,false};
    private boolean[] UPDATE_COUNTS = {false,false,false,true,false};
    private boolean[] UPDATE_ADDITIONS = {false,false,false,true,true};

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
            additionalWordList = db.getAdditionalWords(mUserId);
            additionalWordsFluent = db.getAdditionalFluency(mUserId);
            additionalWordEncounters = db.getAdditionalEncounters(mUserId);
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
            additionalWordList = new ArrayList<>();
            additionalWordList.add("Sara");
            additionalWordsFluent = new ArrayList<>();
            additionalWordsFluent.add(false);
            additionalWordEncounters = new ArrayList<>();
            additionalWordEncounters.add(0);
            dbSave(UPDATE_ALL);
        }
    }

    private void dbSave(boolean[] type) {
        int fluentCount = getFluentCount();
        JSONArray weights = new JSONArray();
        JSONArray fluent = new JSONArray();
        JSONArray encounters = new JSONArray();
        JSONArray addWords = new JSONArray();
        JSONArray addFluent = new JSONArray();
        JSONArray addEncounters = new JSONArray();
        for (int i=0; i < wordList.length; i++) {
            weights.put(wordWeights[i]);
            fluent.put(wordsFluent[i]);
            encounters.put(wordEncounters[i]);
        }
        for (int i=0; i < additionalWordList.size(); i++) {
            addWords.put(additionalWordList.get(i));
            addFluent.put(additionalWordsFluent.get(i));
            addEncounters.put(additionalWordEncounters.get(i));
        }
        ContentValues values = new ContentValues();
        if (db.hasFluencyModel(mUserId)) {
            if (type[0]) {
                values.put(UserListContract.UserFluency.COLUMN_WORD_WEIGHTS, weights.toString());
            }
            if (type[1]) {
                values.put(UserListContract.UserFluency.COLUMN_WORDS_FLUENT, fluent.toString());
            }
            if (type[2]) {
                values.put(UserListContract.UserFluency.COLUMN_ENCOUNTER_COUNTS, encounters.toString());
            }
            if(type[3]) {
                values.put(UserListContract.UserFluency.COLUMN_FLUENT_COUNT, fluentCount);
                values.put(UserListContract.UserFluency.COLUMN_USER_SCORE, mScore);
                values.put(UserListContract.UserFluency.COLUMN_READ_COUNT, readCount);
            }
            if (type[4]) {
                values.put(UserListContract.UserFluency.COLUMN_ADDITIONAL_WORDS,addWords.toString());
                values.put(UserListContract.UserFluency.COLUMN_ADDITIONAL_FLUENT,addFluent.toString());
                values.put(UserListContract.UserFluency.COLUMN_ADDITIONAL_ENCOUNTER,addEncounters.toString());
            }
            db.update(UserListContract.UserFluency.TABLE_NAME,values,UserListContract.UserFluency.COLUMN_USER_ID + " = ? ",new String[]{mUserId});
        }
        else {
            values.put(UserListContract.UserFluency.COLUMN_WORD_WEIGHTS, weights.toString());
            values.put(UserListContract.UserFluency.COLUMN_WORDS_FLUENT, fluent.toString());
            values.put(UserListContract.UserFluency.COLUMN_ENCOUNTER_COUNTS, encounters.toString());
            values.put(UserListContract.UserFluency.COLUMN_FLUENT_COUNT, fluentCount);
            values.put(UserListContract.UserFluency.COLUMN_USER_SCORE, mScore);
            values.put(UserListContract.UserFluency.COLUMN_READ_COUNT, readCount);
            values.put(UserListContract.UserFluency.COLUMN_USER_ID, mUserId);
            values.put(UserListContract.UserFluency.COLUMN_ADDITIONAL_WORDS,addWords.toString());
            values.put(UserListContract.UserFluency.COLUMN_ADDITIONAL_FLUENT,addFluent.toString());
            values.put(UserListContract.UserFluency.COLUMN_ADDITIONAL_ENCOUNTER,addEncounters.toString());
            db.insert(UserListContract.UserFluency.TABLE_NAME, values);
        }
    }
    public int getFluentCount() {
        int fluentCount = 0;
        for (int i = 0; i < wordsFluent.length; i++) {
            if (wordsFluent[i]) fluentCount++;
        }
        for (int i = 0; i < additionalWordsFluent.size(); i++) {
            if (additionalWordsFluent.get(i)) fluentCount++;
        }
        return fluentCount;
    }
    public int getScore() {
        return mScore;
    }
    public ArrayList<String> getFluentWords() {
        ArrayList<String> fluentWords = new ArrayList<>();
        for (int index = 0; index < wordsFluent.length; index++) {
            if (wordsFluent[index]) fluentWords.add(wordList[index]);
        }
        for (int i = 0; i < additionalWordsFluent.size(); i++) {
            if (additionalWordsFluent.get(i)) fluentWords.add(additionalWordList.get(i));
        }
        return fluentWords;
    }
    public String getUserId() {
        return mUserId;
    }

    public String getRandWord() {
        int sumWeights = 0;
        Random random = new Random();
        int index = 0;
        int targetSpace = 8;
        int fluentCount = getFluentCount();
        if (fluentCount > 3) targetSpace = fluentCount * 2;
        while (index < wordWeights.length && index < targetSpace) {
            sumWeights = sumWeights + wordWeights[index];
            index++;
        }
        int additionlist = 0;
        for (int i = 0; i < additionalWordList.size(); i++) {
            if (additionalWordsFluent.get(i)) additionlist++;
            else additionlist += 125;
        }
        sumWeights = sumWeights + additionlist;
        if (sumWeights == 0) return "";
        int target = random.nextInt(sumWeights);
        sumWeights = 0;
        index = 0;
        if (target >= additionlist) {
            sumWeights = additionlist;
            while (sumWeights < target && index < wordList.length) {
                sumWeights = sumWeights + wordWeights[index];
                if (sumWeights < target && index < wordList.length - 1) index++;
            }
            if (lastWord.equals(wordList[index])) {
                return getRandWord();
            } else {
                lastWord = wordList[index];
                return lastWord;
            }
        } else {
            while (sumWeights < target && index < additionalWordList.size() - 1) {
                index++;
                if (additionalWordsFluent.get(index)) sumWeights++;
                else sumWeights += 125;
            }
            if (lastWord.equals(additionalWordList.get(index))) {
                return getRandWord();
            } else {
                lastWord = additionalWordList.get(index);
                return lastWord;
            }
        }
    }

    private void logEncounter(String word, int type) {
        ContentValues wordEncounter = new ContentValues();
        wordEncounter.put(UserListContract.WordEncounters.COLUMN_USER_ID,mUserId);
        wordEncounter.put(UserListContract.WordEncounters.COLUMN_WORD,word);
        wordEncounter.put(UserListContract.WordEncounters.COLUMN_TYPE,type);
        wordEncounter.put(UserListContract.WordEncounters.COLUMN_USER_SCORE,mScore);
        db.insert(UserListContract.WordEncounters.TABLE_NAME, wordEncounter);
    }
    private void logEncounter(String word, int type, int rate) {
        ContentValues wordEncounter = new ContentValues();
        wordEncounter.put(UserListContract.WordEncounters.COLUMN_USER_ID,mUserId);
        wordEncounter.put(UserListContract.WordEncounters.COLUMN_WORD,word);
        wordEncounter.put(UserListContract.WordEncounters.COLUMN_RATE,rate);
        wordEncounter.put(UserListContract.WordEncounters.COLUMN_TYPE,type);
        wordEncounter.put(UserListContract.WordEncounters.COLUMN_USER_SCORE,mScore);
        db.insert(UserListContract.WordEncounters.TABLE_NAME, wordEncounter);
    }
    private void logEncounter(String word, int type, int rate, int input) {
        ContentValues wordEncounter = new ContentValues();
        wordEncounter.put(UserListContract.WordEncounters.COLUMN_USER_ID,mUserId);
        wordEncounter.put(UserListContract.WordEncounters.COLUMN_WORD,word);
        wordEncounter.put(UserListContract.WordEncounters.COLUMN_RATE,rate);
        wordEncounter.put(UserListContract.WordEncounters.COLUMN_INPUT,input);
        wordEncounter.put(UserListContract.WordEncounters.COLUMN_TYPE,type);
        wordEncounter.put(UserListContract.WordEncounters.COLUMN_USER_SCORE,mScore);
        if (input != UserListContract.WordEncounters.INPUT_IGNORE) db.insert(UserListContract.WordEncounters.TABLE_NAME, wordEncounter);
    }

    //Known Known Encounter
    public String recognizedWord (String word, int rate, int input) {
        int index = findWordIndex(word);
        if (index >= 0 && index < wordList.length) {
            wordWeights[index] = wordWeights[index] / 2;
            if (wordEncounters[index] > 0) wordEncounters[index]++;
            else if (rate < 1500){
                wordEncounters[index] = 1;
                wordWeights[index] = wordWeights[index] / 16;
                wordsFluent[index] = true;
                mScore += 2;
            } else wordEncounters[index] = 1;
            if (wordsFluent[index]) mScore += 2;
            if (wordsFluent[index] && wordEncounters[index] > 2 && rate < 1000) {
                wordWeights[index] = wordWeights[index] / 4;
                mScore +=5;
            }
            if (wordEncounters[index] > 2 && rate < 1500) wordsFluent[index] = true;
            mScore += 1;
            dbSave(UPDATE_FLUENCY);
            logEncounter(word,UserListContract.WordEncounters.TYPE_FLUENT,rate,input);
            return getRandWord();
        } else if (index >= wordList.length) {
            index = index - wordList.length;
            int newCount = additionalWordEncounters.get(index) + 1;
            if (newCount == 1 && rate < 1500) {
                additionalWordsFluent.set(index,true);
            }
            additionalWordEncounters.set(index,newCount);
            if (additionalWordsFluent.get(index)) mScore += 2;
            if (additionalWordsFluent.get(index) && newCount > 2 && rate < 1000) mScore += 5;
            if (newCount > 2 && rate < 1500) additionalWordsFluent.set(index,true);
            mScore += 1;
            dbSave(UPDATE_ADDITIONS);
            logEncounter(word,UserListContract.WordEncounters.TYPE_FLUENT,rate,input);
            return getRandWord();
        }
        return "error";
    }

    //Known Unknown Encounter
    public String taughtWord (String word) {
        int index = findWordIndex(word);
        if (index >= 0 && index < wordList.length) {
            wordWeights[index] += 125;
            if (wordEncounters[index] > 0) wordEncounters[index]++;
            else wordEncounters[index] = 1;
            if (wordEncounters[index] < 8) wordsFluent[index] = false;
            mScore += 1;
            dbSave(UPDATE_WEIGHT);
            return getRandWord();
        } else if (index >= wordList.length) {
            index = index - wordList.length;
            int newCount = additionalWordEncounters.get(index) + 1;
            additionalWordEncounters.set(index,newCount);
            if (newCount < 8) additionalWordsFluent.set(index,false);
            mScore +=1;
            dbSave(UPDATE_ADDITIONS);
            logEncounter(word,UserListContract.WordEncounters.TYPE_DECONSTRUCTED);
            return getRandWord();
        }
        return "error";
    }

    //Fuzzy Encounter
    public String encounterWord (String word, int rate) {
        int index = findWordIndex(word);
        if (index >= 0 && index < wordList.length) {
            if (wordEncounters[index] > 0) wordEncounters[index]++;
            else wordEncounters[index] = 1;
            if (wordEncounters[index] > 3 && rate < 600) wordsFluent[index] = true;
            else if (wordEncounters[index] > 8) wordsFluent[index] = true;
            else if (!wordsFluent[index]) wordWeights[index] += 125;
            mScore += 1;
            dbSave(UPDATE_COUNTS);
            return getRandWord();
        } else if (index >= wordList.length) {
            index = index - wordList.length;
            int newCount = additionalWordEncounters.get(index) + 1;
            additionalWordEncounters.set(index,newCount);
            if (newCount > 3 && rate < 600) additionalWordsFluent.set(index,true);
            else if (wordEncounters[index] > 8) wordsFluent[index] = true;
            mScore += 1;
            dbSave(UPDATE_ADDITIONS);
            logEncounter(word,UserListContract.WordEncounters.TYPE_FUZZY,rate);
            return getRandWord();
        }
        return "error";
    }

    public boolean getWordFluency(String word) {
        int i = findWordIndex(word);
        if (i >= 0 && i < wordsFluent.length) return wordsFluent[i];
        else if (i >= wordsFluent.length) {
            i = i - wordsFluent.length;
            return additionalWordsFluent.get(i);
        }
        else return false;
    }

    private int findWordIndex(String word) {
        for (int index = 0; index < wordList.length; index++) {
            if (wordList[index].toLowerCase().equals(word.toLowerCase())) {
                return index;
            }
        }
        for (int index = 0; index < additionalWordList.size(); index++) {
            if (additionalWordList.get(index).toLowerCase().equals(word.toLowerCase())) {
                return index + wordList.length;
            }
        }
        additionalWordList.add(word.toLowerCase());
        additionalWordsFluent.add(false);
        additionalWordEncounters.add(0);
        return wordList.length + additionalWordList.size() - 1;
    }
    public void close() {
        //dbSave(UPDATE_ALL);
        db.close();
    }
}
