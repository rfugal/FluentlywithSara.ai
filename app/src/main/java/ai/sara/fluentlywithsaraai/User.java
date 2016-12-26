package ai.sara.fluentlywithsaraai;

import android.content.Context;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by russ.fugal on 12/14/2016.
 */

public class User {
    private String mUserId;
    private String mUserName;
    private boolean hasMetrics;
    private SightWords mWordModel;

    public User (String name, Context context) {
        mUserName = name;
        mUserId = createUserId();
        mWordModel = new SightWords(mUserId, context);
    }

    public int getRANcount() {
        if (hasMetrics) return mWordModel.getRANcount();
        else return 0;
    }
    public int getScore() {
        if (hasMetrics) return mWordModel.getScore();
        else return 0;
    }
    public ArrayList<String> getRANwords() {
        if (hasMetrics) return mWordModel.getRANwords();
        else return null;
    }
    public String getId() {
        return mUserId;
    }
    public boolean hasMetrics() {
        if (hasMetrics) return true;
        else return false;
    }
    public String getRandWord() {
        return mWordModel.getRandWord();
    }
    public String recognizedWord(String word) {
        String newWord = mWordModel.recognizedWord(word);
        hasMetrics = true;
        return newWord;
    }
    public int taughtWord(String word) {
        mWordModel.taughtWord(word);
        return getScore();
    }
    public int encounterWord(String word) {
        mWordModel.encounterWord(word);
        return getScore();
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
            userId += charSpace[ThreadLocalRandom.current().nextInt(0, 54)];
        }
        return userId;
    }
}
