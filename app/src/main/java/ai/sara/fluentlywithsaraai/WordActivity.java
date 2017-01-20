package ai.sara.fluentlywithsaraai;

import android.os.Handler;
import android.speech.tts.UtteranceProgressListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.speech.tts.TextToSpeech;

import java.util.ArrayList;
import java.util.Collections;

import ai.sara.fluentlywithsaraai.data.User;

public class WordActivity extends AppCompatActivity {
    private User mUser;
    private String word = "First Word";
    private TextView randWord;
    private TextView score;
    private ImageView abc;
    private GridLayout keyboardGrid;
    float x1, x2;
    float y1, y2;
    long timeHolder;
    int sessionScore;
    private Toast toast;
    int milestone = 16;
    boolean lockSwipe = false;
    private TextToSpeech tts;
    private ArrayList<Character> keyboard = new ArrayList<>();
    private ArrayList<Character> targetLetters = new ArrayList<>();
    private Runnable hideWord;
    private int RECOGNIZED = 0;
    private int TAUGHT = 1;
    private int SKIPPED = 2;
    private Runnable taught = new Runnable() {
        @Override
        public void run() {
            nextWord(TAUGHT,-1);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUser = new User(this, getIntent().getStringExtra("USER_ID"));
        mUser.loadFluencyModel();
        setContentView(R.layout.activity_word);
        randWord = (TextView) findViewById(R.id.randomWord);
        score = (TextView) findViewById(R.id.score);
        abc = (ImageView) findViewById(R.id.flashcard_deconstruction);
        keyboardGrid = (GridLayout) findViewById(R.id.deconstruction_buttons);
        keyboardGrid.setVisibility(View.INVISIBLE);
        hideWord = new Runnable() {
            @Override
            public void run() {
                randWord.setVisibility(View.INVISIBLE);
            }
        };
        word = mUser.getRandWord();
        randWord.setText(word);
        randWord.postDelayed(hideWord,1500);
        sessionScore = mUser.getScore();
        if (sessionScore > milestone) {
            while (milestone <= sessionScore) milestone = milestone *2;
        }
        String scoreText = "Score: " + Integer.toString(sessionScore);
        score.setText(scoreText);
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
            }
        });
    }

    public boolean onTouchEvent(MotionEvent touchevent) {
        switch (touchevent.getAction()) {
            // when user first touches the screen we get x and y coordinate
            case MotionEvent.ACTION_DOWN: {
                x1 = touchevent.getX();
                y1 = touchevent.getY();
                break;
            }
            case MotionEvent.ACTION_UP: {
                x2 = touchevent.getX();
                y2 = touchevent.getY();

                // if left to right sweep event on screen
                if (x1 < x2 && !lockSwipe) {
                    long now = System.currentTimeMillis();
                    long ms = now - timeHolder;
                    int rate = (int)ms;
                    nextWord(RECOGNIZED,rate);
                }

                // if right to left sweep event on screen
                if (x1 > x2) {
                    nextWord(SKIPPED,0);
                }
            }
        }
        return false;
    }
    public void nextWord(int type, int rate) {
        randWord.removeCallbacks(hideWord);
        keyboardGrid.setVisibility(View.GONE);
        randWord.setVisibility(View.VISIBLE);
        abc.setVisibility(View.VISIBLE);
        if (type == RECOGNIZED) word = mUser.recognizedWord(word, rate);
        else if (type == TAUGHT) word = mUser.taughtWord(word);
        else word = mUser.getRandWord();
        int newscore = mUser.getScore();
        randWord.setText(word);
        timeHolder = System.currentTimeMillis();
        lockSwipe = false;
        randWord.postDelayed(hideWord, 1500);
        if (newscore != sessionScore) {
            if (toast != null) toast.cancel();
            String incrScore = "+" + Integer.toString(newscore - sessionScore);
            int duration = Toast.LENGTH_SHORT;
            if (newscore >= milestone && sessionScore < milestone) {
                incrScore = "You passed a milestone! " + Integer.toString(milestone) + " points!";
                milestone = milestone * 2;
                duration = Toast.LENGTH_LONG;
            }
            toast = Toast.makeText(this, incrScore, duration);
            toast.show();
            sessionScore = newscore;
            String scoreText = "Score: " + Integer.toString(sessionScore);
            score.setText(scoreText);

        }
    }
    public void deconstructionStart(View view) {
        lockSwipe = true;
        randWord.removeCallbacks(hideWord);
        randWord.setVisibility(View.VISIBLE);
        view.setVisibility(View.GONE);
        keyboardGrid.setVisibility(View.VISIBLE);
        getKeyboard(word);
        tts.speak(word,TextToSpeech.QUEUE_FLUSH,null);
    }
    private void getKeyboard(String unknown) {

        // letter buttons are lower case and alpha only, so strip word of all non-alpha
        String strippedUnknown = unknown.replaceAll("[^a-zA-Z]", "").toLowerCase();
        int c = 9;
        int i = 0;
        char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        char[] letters = strippedUnknown.toCharArray();
        ArrayList<Character> abc = new ArrayList<>();
        keyboard.clear();
        targetLetters.clear();

        // shuffle the alphabet
        for (char a : alphabet) {
            abc.add(a);
        }
        Collections.shuffle(abc);

        // add unique letters to keyboard
        for (char l : letters) {
            if (!keyboard.contains(l)) keyboard.add(l);
            if (!targetLetters.contains(l)) targetLetters.add(l);
        }
        i = letters.length;

        // determine number of buttons
        if (letters.length > 9) c = 12;
        if (letters.length > 12) c = 16;
        if (letters.length > 16) c = 20;
        if (letters.length > 20) c = 26;

        // and add random fillers to keyboard
        while (keyboard.size() < c) {
            if (!keyboard.contains(abc.get(i))) {
                keyboard.add(abc.get(i));
            }
            i++;
        }
        Collections.shuffle(keyboard);

        // create keyboard
        keyboardGrid.removeAllViews();
        keyboardGrid.setColumnCount(3);
        if (c > 15) keyboardGrid.setColumnCount(4);
        if (c > 19) keyboardGrid.setColumnCount(5);
        for (Character k : keyboard) {
            TextView letterKey = (TextView) View.inflate(this,R.layout.letter_key,null);
            letterKey.setText(k.toString());
            letterKey.setContentDescription(k.toString());
            letterKey.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArrayList<Character> m = new ArrayList<>();
                    m.add(view.getContentDescription().charAt(0));
                    if (targetLetters.contains(m.get(0))) view.setBackground(getDrawable(R.drawable.letter_key_pressed));
                    targetLetters.removeAll(m);
                    targetLetters.trimToSize();
                    if (targetLetters.size() == 0) {
                        tts.speak(word,TextToSpeech.QUEUE_FLUSH,null);
                        randWord.removeCallbacks(taught);
                        randWord.postDelayed(taught, 1500);
                    }
                }
            });
            keyboardGrid.addView(letterKey);
        }
    }
}
