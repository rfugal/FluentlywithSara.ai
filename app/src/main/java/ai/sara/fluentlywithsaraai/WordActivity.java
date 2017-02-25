package ai.sara.fluentlywithsaraai;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.speech.tts.TextToSpeech;

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
import java.util.Collections;

import ai.sara.fluentlywithsaraai.data.User;
import ai.sara.fluentlywithsaraai.data.UserListContract;

public class WordActivity extends AppCompatActivity {
    private User mUser;
    private String word = "First Word";
    private TextView randWord;
    private TextView score;
    private ImageView abc;
    private GridLayout keyboardGrid;
    private float x1, x2;
    private float y1, y2;
    private long timeHolder = System.currentTimeMillis();
    private long timeToSpeech;
    private int sessionScore;
    private Toast toast;
    private int milestone = 16;
    boolean lockSwipe = false;
    private TextToSpeech tts;
    private ArrayList<Character> keyboard = new ArrayList<>();
    private ArrayList<Character> targetLetters = new ArrayList<>();
    private Runnable hideWord;
    private Runnable stopListening;
    private int RECOGNIZED = 0;
    private int TAUGHT = 1;
    private int SKIPPED = 2;
    private int SPECIFIC = 3;
    private Runnable taught = new Runnable() {
        @Override
        public void run() {
            nextWord(TAUGHT,-1,UserListContract.WordEncounters.INPUT_IGNORE);
        }
    };
    private RecognitionListener sara;
    private SpeechRecognizer listen;
    private AudioManager audioManager;
    private SharedPreferences sharedpreferences;
    private final String USER_SESSION = "CurrentUser";
    private final String USER_ID = "UserId";
    private final String USER_NAME = "UserName";
    private String username;
    private String userId;
    private int saraIndex = 0;
    private String[] saraSays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        saraSays = getResources().getStringArray(R.array.sara_fluency);
        sharedpreferences = getSharedPreferences(USER_SESSION, Context.MODE_PRIVATE);
        username = sharedpreferences.getString(USER_NAME,null);
        userId = sharedpreferences.getString(USER_ID,null);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            listen = SpeechRecognizer.createSpeechRecognizer(this);
            sara = new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle bundle) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_UNMUTE,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                }

                @Override
                public void onBeginningOfSpeech() {
                    long now = System.currentTimeMillis();
                    timeToSpeech = now - timeHolder;
                }

                @Override
                public void onRmsChanged(float v) {

                }

                @Override
                public void onBufferReceived(byte[] bytes) {

                }

                @Override
                public void onEndOfSpeech() {

                }

                @Override
                public void onError(int i) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_UNMUTE,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                }

                @Override
                public void onResults(Bundle bundle) {
                    ArrayList<String> results = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    float[] confidence = bundle.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES);
                    Log.d("SARA", "onResults() returned: " + results.size());
                    if (results.size() > 0) {
                        Log.d("SARA", "onResults() returned: " + word);
                        for (int i = 0; i < results.size(); i++) {
                            Log.d("SARA", "onResults() returned: " + results.get(i) + ":" + confidence[i]);
                            if (results.get(i).trim().toLowerCase().equals(word.trim().toLowerCase()) && confidence[i] > .3) {
                                Log.d("SARA", "onResults() returned: MATCH");
                                int rate = (int)timeToSpeech;
                                nextWord(RECOGNIZED,rate,UserListContract.WordEncounters.INPUT_SPEECH_RECOGNIZER);
                                break;
                            }
                        }
                    }
                }

                @Override
                public void onPartialResults(Bundle bundle) {

                }

                @Override
                public void onEvent(int i, Bundle bundle) {

                }
            };
            listen.setRecognitionListener(sara);
        }
        mUser = new User(this, userId);
        mUser.loadFluencyModel();
        setContentView(R.layout.activity_word);
        randWord = (TextView) findViewById(R.id.randomWord);
        score = (TextView) findViewById(R.id.score);
        abc = (ImageView) findViewById(R.id.flashcard_deconstruction);
        keyboardGrid = (GridLayout) findViewById(R.id.deconstruction_buttons);
        keyboardGrid.setVisibility(View.INVISIBLE);
        stopListening = new Runnable() {
            @Override
            public void run() {
                listen.stopListening();
            }
        };
        hideWord = new Runnable() {
            @Override
            public void run() {
                randWord.setVisibility(View.INVISIBLE);
                randWord.postDelayed(stopListening, 1500);
            }
        };
        word = mUser.getRandWord();
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
        if (getIntent().getStringExtra("WORD") != null) {
            word = getIntent().getStringExtra("WORD");
            nextWord(SPECIFIC,0,UserListContract.WordEncounters.INPUT_IGNORE);
            deconstructionStart(abc);
        } else nextWord(SKIPPED,0,UserListContract.WordEncounters.INPUT_IGNORE);
        FloatingActionButton sara = (FloatingActionButton) findViewById(R.id.sara_home);
        sara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!tts.isSpeaking()) {
                    tts.speak(saraSays[saraIndex], TextToSpeech.QUEUE_FLUSH,null);
                    saraIndex++;
                    if (saraIndex == saraSays.length) saraIndex = 0;
                }
            }
        });
        sara.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (tts.isSpeaking()) return false;
                new AsyncJsonCall().execute("https://api.pearson.com/v2/dictionaries/ldoce5/entries?headword=" + word.replace(" ","+") + "&limit=25");
                return true;
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
                    nextWord(RECOGNIZED,rate,UserListContract.WordEncounters.INPUT_USER_TOUCH);
                }

                // if right to left sweep event on screen
                if (x1 > x2) {
                    nextWord(SKIPPED,0,UserListContract.WordEncounters.INPUT_USER_TOUCH);
                }
            }
        }
        return false;
    }
    public void nextWord(int type, int rate, int input) {
        randWord.removeCallbacks(hideWord);
        randWord.removeCallbacks(stopListening);
        keyboardGrid.setVisibility(View.GONE);
        randWord.setVisibility(View.VISIBLE);
        abc.setVisibility(View.VISIBLE);
        if (type == RECOGNIZED) word = mUser.recognizedWord(word, rate, input);
        else if (type == TAUGHT) word = mUser.taughtWord(word);
        else if (type == SPECIFIC);
        else word = mUser.getRandWord();
        int newscore = mUser.getScore();
        randWord.setText(word);
/*
        if (true) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_MUTE,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
            startSaraListener();
        }
        else timeHolder = System.currentTimeMillis();
*/
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
        listen.cancel();
        randWord.removeCallbacks(hideWord);
        randWord.setVisibility(View.VISIBLE);
        view.setVisibility(View.GONE);
        keyboardGrid.setVisibility(View.VISIBLE);
        getKeyboard(word);
        if (!tts.isSpeaking()) tts.speak(word,TextToSpeech.QUEUE_FLUSH,null);
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
                    if (!tts.isSpeaking()) {
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
                }
            });
            keyboardGrid.addView(letterKey);
        }
    }
    private boolean startSaraListener() {
        if (sara == null) return false;
        /*Intent hearWords = new Intent();
        hearWords.setAction(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        hearWords.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,"en-US");
        hearWords.putExtra(RecognizerIntent.EXTRA_RESULTS,true);
        hearWords.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"ai.sara.fluentlywithsaraai");
        if (android.os.Build.VERSION.SDK_INT >= 23) hearWords.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE,true);
        listen.cancel();
        timeHolder = System.currentTimeMillis();
        listen.startListening(hearWords);
        return true;*/
        return false;
    }
    public void onDestroy() {
        super.onDestroy();
        tts.shutdown();
        listen.destroy();
        mUser.close();
    }
    public void onPause() {
        mUser.close();
        super.onPause();
    }
    public void onResume() {
        sharedpreferences = getSharedPreferences(USER_SESSION, Context.MODE_PRIVATE);
        username = sharedpreferences.getString(USER_NAME,null);
        userId = sharedpreferences.getString(USER_ID,null);
        mUser = new User(this, userId);
        mUser.loadFluencyModel();
        super.onResume();
    }

    private class AsyncJsonCall extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
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

                return buffer.toString();


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
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            iterateDefinitions(result);
        }
    }
    private void iterateDefinitions(String result) {
        if (result == null) return;
        tts.speak(word,TextToSpeech.QUEUE_FLUSH,null);
        if (abc.getVisibility() != View.GONE) deconstructionStart(abc);
        try {
            JSONObject dict_entry = new JSONObject(result);
            JSONArray entries = dict_entry.getJSONArray("results");
            int count = 0;
            if (entries != null) count = entries.length();
            for (int i = 0; i < count; i++) {
                JSONObject entry = entries.getJSONObject(i);
                if (word.equals(entry.getString("headword"))) {
                    tts.playSilence(750,TextToSpeech.QUEUE_ADD,null);
                    speakDefinition(entry);
                }
            }
        } catch (JSONException e) {
        }
    }
    private void speakDefinition(JSONObject entry) {
        try {
            JSONArray senses = entry.getJSONArray("senses");
            for (int i = 0; i < senses.length(); i++) {
                tts.speak(senses.getJSONObject(i).getJSONArray("definition").getString(0),TextToSpeech.QUEUE_ADD,null);
                tts.playSilence(750,TextToSpeech.QUEUE_ADD,null);
                try {
                    tts.speak(senses.getJSONObject(i).getJSONArray("examples").getJSONObject(0).getString("text"),TextToSpeech.QUEUE_ADD,null);
                    tts.playSilence(750,TextToSpeech.QUEUE_ADD,null);
                } catch (JSONException e) {}
            }
        } catch (JSONException e) {}
    }

}
