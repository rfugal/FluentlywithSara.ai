package ai.sara.fluentlywithsaraai;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class WordBuilderActivity extends AppCompatActivity {
    JSONObject TypingHMM;
    int init;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_builder);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        openSpellerHMM();
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
            }
        });
        TextView more = (TextView) findViewById(R.id.more_letters);
        TextView back = (TextView) findViewById(R.id.restart_letters);
        TextView backspace = (TextView) findViewById(R.id.backspace);
        ImageView ear = (ImageView) findViewById(R.id.word_builder_tts);
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshKeyboard(true);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshKeyboard(false);
            }
        });
        backspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView built_word = (TextView) findViewById(R.id.built_word);
                String m = (String) built_word.getText();
                if (m.length() > 0) {
                    m = m.substring(0,m.length()-1);
                    built_word.setText(m);
                    refreshKeyboard(false);
                }
            }
        });
        ear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!tts.isSpeaking()) {
                    TextView built_word = (TextView) findViewById(R.id.built_word);
                    String word = (String) built_word.getText();
                    tts.speak(word, TextToSpeech.QUEUE_FLUSH,null);
                }
            }
        });
    }

    private void openSpellerHMM() {
        InputStream is = getResources().openRawResource(R.raw.five_k_simple);
        try {
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");
            TypingHMM = new JSONObject(json);
            is.close();
            refreshKeyboard(false);
        } catch (IOException e) {
        } catch (JSONException e) {
        }
    }

    private void refreshKeyboard(boolean extend) {
        final TextView built_word = (TextView) findViewById(R.id.built_word);
        GridLayout word_builder_buttons = (GridLayout) findViewById(R.id.word_builder_buttons);
        word_builder_buttons.setColumnCount(4);
        String word = (String) built_word.getText();
        JSONArray children = new JSONArray();
        try {
            children = TypingHMM.getJSONArray(word);
        } catch (JSONException e) {
        }
        if (extend) {
            if (init < children.length() - 4) init += 4;
            word_builder_buttons.removeAllViews();
        } else {
            word_builder_buttons.removeAllViews();
            init = 0;
        }
        int next = Math.min(children.length(), init + 4);
        for (int i = init; i < next; i++) {
            TextView letterKey = (TextView) View.inflate(this, R.layout.letter_key, null);
            try {
                letterKey.setText(children.getString(i));
                letterKey.setContentDescription(children.getString(i));
            } catch (JSONException e) {
            }
            letterKey.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String m = (String) built_word.getText();
                    String n = (String) view.getContentDescription();
                    m = m + n;
                    built_word.setText(m);
                    refreshKeyboard(false);
                }
            });
            word_builder_buttons.addView(letterKey);
        }
    }
}
