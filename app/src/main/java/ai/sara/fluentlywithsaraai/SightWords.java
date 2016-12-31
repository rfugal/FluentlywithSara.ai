package ai.sara.fluentlywithsaraai;

import android.content.Context;
import android.content.res.Resources;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by russ.fugal on 12/14/2016.
 */

public class SightWords implements Serializable {
    private String[] wordList;
    private int[] wordWeights;
    private boolean[] wordRAN;
    private int[] wordEncounters;
    private String mUserId;
    private int mScore;

    //Constructor
    public SightWords (String userId, String[] frequentWords, int[] initialWeights) {
        wordList = frequentWords;
        wordWeights = new int[wordList.length];
        wordRAN = new boolean[wordList.length];
        wordEncounters = new int[wordList.length];
        mUserId = userId;
        mScore = 0;
        for (int index = 0; index < wordList.length; index ++) {
            try {
                wordWeights[index] = initialWeights[index];
            }
            catch (Exception e) {
                wordWeights[index] = 1;
            }
        }
    }

    public int getRANcount() {
        int RANcount = 0;
        for (int index = 0; index < wordRAN.length; index++) {
            if (wordRAN[index]) RANcount++;
        }
        return RANcount;
    }
    public int getScore() {
        return mScore;
    }
    public ArrayList<String> getRANwords() {
        ArrayList<String> RANwords = new ArrayList<String>();
        for (int index = 0; index < wordRAN.length; index++) {
            if (wordRAN[index]) RANwords.add(wordList[index]);
        }
        return RANwords;
    }
    public String getUserId() {
        return mUserId;
    }
    public void setUserId(String userId) {
        mUserId = userId;
    }

    public String getRandWord () {
        int sumWeights = 0;
        int index = 0;
        while (index < wordWeights.length && sumWeights < 1000000) {
            sumWeights = sumWeights + wordWeights[index];
            index++;
        }
        if (sumWeights == 0) return "";
        int target = ThreadLocalRandom.current().nextInt(1, sumWeights + 1);
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
                wordRAN[index] = true;
            }
            if (wordRAN[index]) mScore += 4;
            if (wordRAN[index] && wordEncounters[index] > 4) wordWeights[index] = wordWeights[index] / 4;
            if (wordEncounters[index] > 2) wordRAN[index] = true;
            mScore += 1;
            return getRandWord();
        }
        return "error";
    }

    //Known Unknown Encounter
    public void taughtWord (String word) {
        int index = findWordIndex(word);
        if (index != -1) {
            wordWeights[index] += 125;
            if (wordEncounters[index] > 0) wordEncounters[index]++;
            else wordEncounters[index] = 1;
            if (wordEncounters[index] < 8) wordRAN[index] = false;
            mScore += 1;
        }
    }

    //Fuzzy Encounter
    public void encounterWord (String word) {
        int index = findWordIndex(word);
        if (index != -1) {
            if (wordEncounters[index] > 0) wordEncounters[index]++;
            else wordEncounters[index] = 1;
            if (wordEncounters[index] > 12) wordRAN[index] = true;
            else if (!wordRAN[index]) wordWeights[index] += 125;
            mScore += 1;
        }
    }

    private int findWordIndex (String word) {
        for (int index = 0; index < wordList.length; index++) {
            if (wordList[index] == word) {
                return index;
            }
        }
        return -1;
    }
}
