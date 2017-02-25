package ai.sara.fluentlywithsaraai;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.apmem.tools.layouts.FlowLayout;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ai.sara.fluentlywithsaraai.data.User;
import ai.sara.fluentlywithsaraai.data.UserListContract;

public class RandomReadingActivity extends AppCompatActivity {
    private SharedPreferences sharedpreferences;
    private final String USER_SESSION = "CurrentUser";
    private final String USER_ID = "UserId";
    private final String USER_NAME = "UserName";
    private String username;
    private String userId;
    private int saraIndex = 0;
    private String[] saraSays;
    private TextToSpeech tts;
    private float x1, x2;
    private float y1, y2;
    private User mUser;
    private ArrayList<String> passages = new ArrayList<>();
    private int passage_index = 0;
    private String passage = "No Readings";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_reading);
        saraSays = getResources().getStringArray(R.array.sara_reader);
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        sharedpreferences = getSharedPreferences(USER_SESSION, Context.MODE_PRIVATE);
        username = sharedpreferences.getString(USER_NAME,null);
        userId = sharedpreferences.getString(USER_ID,null);
        mUser = new User(this, userId);
        mUser.loadFluencyModel();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NextReading();
        FloatingActionButton nextPassage = (FloatingActionButton) findViewById(R.id.next_passage);
        nextPassage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NextReading();
            }
        });
    }
    private void NextReading() {
        if (passage_index >= passages.size()) {
            passages = mUser.getRandomReading();
            passage_index = 0;
        }
        if (passages.size() > 0) {
            passage = passages.get(passage_index);
            passage_index++;
        } else {
            passage = null;
        }
        FlowLayout content = (FlowLayout) findViewById(R.id.randomReading);
        content.removeAllViews();
        if (passage!=null) {
            String[] spans = passage.split("\\s");
            for (String span : spans) {
                TextView text = (TextView) View.inflate(this, R.layout.reader_word_span,null);
                text.setText(span);
                text.setContentDescription(span);
                Matcher m = mUser.wordEx.matcher(span);
                if (m.find() && !mUser.getWordFluency(span)) {
                    text.setBackground(getDrawable(R.drawable.letter_key_back));
                    text.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String inspectWord = (String) view.getContentDescription();
                            Matcher m = User.wordEx.matcher(inspectWord);
                            if (m.find()) mUser.encounterWord(m.group(),1000);
                            view.setBackground(getDrawable(R.drawable.letter_key_pressed));
                        }
                    });
                    text.setOnLongClickListener(new View.OnLongClickListener(){
                        @Override
                        public boolean onLongClick(View view) {
                            String inspectWord = (String) view.getContentDescription();
                            Matcher m = User.wordEx.matcher(inspectWord);
                            Intent i = new Intent(view.getContext(), WordActivity.class);
                            if (m.find()) i.putExtra("WORD",m.group());
                            startActivity(i);
                            return true;
                        }
                    });
                } else {
                    text.setOnLongClickListener(new View.OnLongClickListener(){
                        @Override
                        public boolean onLongClick(View view) {
                            String inspectWord = (String) view.getContentDescription();
                            Matcher m = User.wordEx.matcher(inspectWord);
                            if (!tts.isSpeaking() && m.find()) {
                                view.setBackground(getDrawable(R.drawable.letter_key_red));
                                tts.speak(m.group(), TextToSpeech.QUEUE_FLUSH, null);
                                mUser.taughtWord(m.group());
                            }
                            return true;
                        }
                    });
                }
                content.addView(text);
            }
            FloatingActionButton sara = (FloatingActionButton) findViewById(R.id.sara_reader);
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
                    if (!tts.isSpeaking()) tts.speak(passage,TextToSpeech.QUEUE_FLUSH,null);
                    return true;
                }
            });
        } else {
            TextView text0 = (TextView) View.inflate(this,R.layout.reader_word_span,null);
            TextView text1 = (TextView) View.inflate(this,R.layout.reader_word_span,null);
            TextView text2 = (TextView) View.inflate(this,R.layout.reader_word_span,null);
            text0.setText("No Matched Readings.");
            text1.setText("Try:");
            text2.setText(R.string.open_deconstruction_activity);
            text2.setBackground(getDrawable(R.drawable.letter_key_back));
            text2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(view.getContext(), WordActivity.class);
                    startActivity(i);
                }
            });
            content.addView(text0);
            content.addView(text1);
            content.addView(text2);
            FloatingActionButton sara = (FloatingActionButton) findViewById(R.id.sara_reader);
            sara.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!tts.isSpeaking()) {
                        tts.speak(getString(R.string.sara_reading_0), TextToSpeech.QUEUE_FLUSH,null);
                    }
                }
            });
            sara.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    return false;
                }
            });
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUser.close();
        tts.shutdown();
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
}
