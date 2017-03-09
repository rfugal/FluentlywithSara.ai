package ai.sara.fluentlywithsaraai;

import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

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
import java.util.Locale;

public class TranslateActivity extends AppCompatActivity {
    JSONObject TypingHMM;
    int init;
    private TextToSpeech tts;
    private int saraIndex = 0;
    private String[] saraSays;
    private TextView typed_word;
    private Locale spanish = new Locale("es", "ES");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_builder);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        openSpellerHMM();
        saraSays = getResources().getStringArray(R.array.sara_translate);
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
            }
        });
        ImageView ear = (ImageView) findViewById(R.id.word_builder_tts);
        TextView more = (TextView) findViewById(R.id.more_letters);
        TextView back = (TextView) findViewById(R.id.restart_letters);
        TextView backspace = (TextView) findViewById(R.id.backspace);
        typed_word = (TextView) findViewById(R.id.built_word);
        typed_word.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tts.setLanguage(spanish);
                if (!tts.isSpeaking() && typed_word.getText().length()>0) tts.speak((String) typed_word.getText(),TextToSpeech.QUEUE_FLUSH,null);
            }
        });
        typed_word.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                tts.setLanguage(spanish);
                if (tts.isSpeaking()) return false;
                String word = (String) typed_word.getText();
                if (word != null && !word.equals("")) {
                    new AsyncJsonCall().execute("https://api.pearson.com/v2/dictionaries/laes/entries?headword=" + word.replace(" ","+") + "&limit=25");
                }
                return true;
            }
        });
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
        FloatingActionButton sara = (FloatingActionButton) findViewById(R.id.sara_word_builder);
        sara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!tts.isSpeaking()) {
                    tts.speak(saraSays[saraIndex],TextToSpeech.QUEUE_FLUSH,null);
                    saraIndex++;
                    if (saraIndex == saraSays.length) saraIndex = 0;
                }
            }
        });

        ear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!tts.isSpeaking()) {
                    String word = (String) typed_word.getText();
                    tts.speak(word, TextToSpeech.QUEUE_FLUSH,null);
                }
            }
        });
    }

    private void openSpellerHMM() {
        InputStream is = getResources().openRawResource(R.raw.typing_model);
        try {
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
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
        String word = (String)typed_word.getText();
        tts.setLanguage(Locale.ENGLISH);
        tts.speak(word,TextToSpeech.QUEUE_FLUSH,null);
        try {
            JSONObject dict_entry = new JSONObject(result);
            JSONArray entries = dict_entry.getJSONArray("results");
            int count = 0;
            if (entries != null) count = entries.length();
            for (int i = 0; i < count; i++) {
                JSONObject entry = entries.getJSONObject(i);
                if (word.equals(entry.getString("headword"))) {
                    tts.playSilence(750,TextToSpeech.QUEUE_ADD,null);
                    getTranslation(entry);
                }
            }
        } catch (JSONException e) {
        }
    }
    private void getTranslation(JSONObject entry) {
        try {
            JSONArray senses = entry.getJSONArray("senses");
            for (int i = 0; i < senses.length(); i++) {
                speakTranslation(senses.getJSONObject(i).getJSONArray("translations"));
            }
        } catch (JSONException e) {}
    }
    private void speakTranslation(JSONArray translations) {
        for (int i = 0; i < translations.length(); i++) {
            try {
                JSONObject translation = translations.getJSONObject(i);
                tts.setLanguage(spanish);
                tts.speak(translation.getString("text"),TextToSpeech.QUEUE_ADD,null);
                tts.playSilence(750,TextToSpeech.QUEUE_ADD,null);
                speakExamples(translation.getJSONArray("example"));
            } catch (JSONException e) {}
        }
    }
    private void speakExamples(JSONArray examples) {
        for (int i=0;i<examples.length(); i++) {
            try {
                tts.setLanguage(spanish);
                tts.speak(examples.getJSONObject(i).getJSONObject("translation").getString("text"),TextToSpeech.QUEUE_ADD,null);
                tts.playSilence(750,TextToSpeech.QUEUE_ADD,null);
                tts.setLanguage(Locale.ENGLISH);
                tts.speak(examples.getJSONObject(i).getString("text"),TextToSpeech.QUEUE_ADD,null);
                tts.playSilence(750,TextToSpeech.QUEUE_ADD,null);
            } catch (JSONException e) {}
        }

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        tts.stop();
    }
}