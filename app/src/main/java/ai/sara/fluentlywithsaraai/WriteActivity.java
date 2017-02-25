package ai.sara.fluentlywithsaraai;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.regex.Matcher;

import ai.sara.fluentlywithsaraai.data.User;
import ai.sara.fluentlywithsaraai.data.UserListContract;

public class WriteActivity extends AppCompatActivity {
    private SharedPreferences sharedpreferences;
    private final String USER_SESSION = "CurrentUser";
    private final String USER_ID = "UserId";
    private final String USER_NAME = "UserName";
    private String username;
    private String userId;
    private int saraIndex = 0;
    private String[] saraSays;
    private TextToSpeech tts;
    private User mUser;
    FlowLayout content;
    private RecognitionListener sara;
    private SpeechRecognizer listen;
    private Context mContext;
    private int insertion_point = -1;
    private boolean reset_listener = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_write);
        saraSays = getResources().getStringArray(R.array.sara_write);
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
            }
        });
        sharedpreferences = getSharedPreferences(USER_SESSION, Context.MODE_PRIVATE);
        username = sharedpreferences.getString(USER_NAME,null);
        userId = sharedpreferences.getString(USER_ID,null);
        mUser = new User(this, userId);
        mUser.loadFluencyModel();
        content = (FlowLayout) findViewById(R.id.composition);
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            listen = SpeechRecognizer.createSpeechRecognizer(this);
            sara = new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle bundle) {
                }

                @Override
                public void onBeginningOfSpeech() {
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
                }

                @Override
                public void onResults(Bundle bundle) {
                    reset_listener = false;
                    ArrayList<String> results = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    float[] confidence = bundle.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES);
                    if (results != null && results.size() > 0) {
                        addWord(results.get(0));
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
        FloatingActionButton sara_icon = (FloatingActionButton) findViewById(R.id.sara_write);
        FloatingActionButton record = (FloatingActionButton) findViewById(R.id.write_record);
        TextView backspace = (TextView) findViewById(R.id.write_backspace);
        sara_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!tts.isSpeaking()) {
                    tts.speak(saraSays[saraIndex], TextToSpeech.QUEUE_FLUSH,null);
                    saraIndex++;
                    if (saraIndex == saraSays.length) saraIndex = 0;
                }
            }
        });
        sara_icon.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                String composition = getComposition();
                if (!tts.isSpeaking() && composition != null) tts.speak(composition,TextToSpeech.QUEUE_FLUSH,null);
                return true;
            }
        });
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!tts.isSpeaking()) {
                    startSaraListener();
                }
            }
        });
        backspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (content.getChildCount() > 0) content.removeViewAt(content.getChildCount()-1);
            }
        });
        backspace.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                content.removeAllViews();
                return true;
            }
        });
    }
    private String getComposition() {
        String composition = "";
        for (int i=0; i < content.getChildCount(); i++) {
            TextView word = (TextView) content.getChildAt(i);
            composition = composition + word.getText() + " ";
        }
        return composition;
    }
    private void addWord(String phrase) {
        if (phrase!=null) {
            String[] spans = phrase.split("\\s");
            int iterations = 0;
            for (int i=0; i<spans.length; i++) {
                TextView text = (TextView) View.inflate(this, R.layout.reader_word_span,null);
                text.setText(spans[i]);
                Matcher m = User.wordEx.matcher(spans[i]);
                if (m.find() && !mUser.getWordFluency(spans[i])) text.setBackground(getDrawable(R.drawable.letter_key_back));
                text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String inspectWord = (String) ((TextView) view).getText();
                        Matcher m = User.wordEx.matcher(inspectWord);
                        if (!tts.isSpeaking() && m.find()) {
                            tts.speak(m.group(), TextToSpeech.QUEUE_FLUSH, null);
                            view.setBackground(getDrawable(R.drawable.letter_key_pressed));
                        }
                    }
                });
                text.setOnLongClickListener(new View.OnLongClickListener(){
                    @Override
                    public boolean onLongClick(View view) {
                        final String word = (String) ((TextView)view).getText();
                        final int location = ((ViewGroup)view.getParent()).indexOfChild(view);
                        Dialog action = new Dialog(mContext) {
                            public void onAttachedToWindow(){
                                TextView target = (TextView) findViewById(R.id.write_action_dialog_target);
                                TextView delete = (TextView) findViewById(R.id.write_action_dialog_delete);
                                TextView replace = (TextView) findViewById(R.id.write_action_dialog_replace);
                                TextView insert = (TextView) findViewById(R.id.write_action_dialog_insert);
                                final TextView dismiss = (TextView) findViewById(R.id.write_action_dialog_cancel);
                                target.setText(word);
                                delete.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        content.removeViewAt(location);
                                        dismiss();
                                    }
                                });
                                replace.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (!tts.isSpeaking()) {
                                            content.removeViewAt(location);
                                            insertion_point = location;
                                            startSaraListener();
                                            dismiss();
                                        }
                                    }
                                });
                                insert.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (!tts.isSpeaking()) {
                                            insertion_point = location;
                                            startSaraListener();
                                            dismiss();
                                        }
                                    }
                                });
                                dismiss.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dismiss();
                                    }
                                });
                            }
                        };
                        action.setContentView(R.layout.dialog_write_action);
                        action.show();
                        return true;
                    }
                });
                if (insertion_point == -1) content.addView(text);
                else {
                    content.addView(text,insertion_point);
                    insertion_point += 1;
                    iterations += 1;
                    if (iterations == spans.length) insertion_point = -1;
                }
            }
        }
    }
    private boolean startSaraListener() {
        if (sara == null) return false;
        int delay = 0;
        if (reset_listener) {
            listen.cancel();
            insertion_point = -1;
            delay = 500;
        }
        content.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent hearWords = new Intent();
                hearWords.setAction(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                hearWords.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,"en-US");
                hearWords.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"ai.sara.fluentlywithsaraai");
                if (android.os.Build.VERSION.SDK_INT >= 23) hearWords.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE,true);
                reset_listener = true;
                listen.startListening(hearWords);
            }
        }, delay);
        return true;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUser.close();
        tts.shutdown();
        listen.destroy();
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