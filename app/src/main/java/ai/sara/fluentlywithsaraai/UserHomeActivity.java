package ai.sara.fluentlywithsaraai;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ai.sara.fluentlywithsaraai.data.User;

public class UserHomeActivity extends AppCompatActivity {
    private SharedPreferences sharedpreferences;
    private final String USER_SESSION = "CurrentUser";
    private final String USER_ID = "UserId";
    private final String USER_NAME = "UserName";
    private String username;
    private String userId;
    private Context context;
    private User mUser;
    private int saraIndex = 0;
    private String[] saraSays;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_user_home);
        saraSays = getResources().getStringArray(R.array.sara_home);
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
            }
        });
        TextView view = (TextView) findViewById(R.id.learner);
        sharedpreferences = getSharedPreferences(USER_SESSION,Context.MODE_PRIVATE);
        username = sharedpreferences.getString(USER_NAME,null);
        userId = sharedpreferences.getString(USER_ID,null);
        mUser = new User(this,userId);
        if (!mUser.allowTwitter()) (findViewById(R.id.tweet_access_button)).setVisibility(View.GONE);
        mUser.close();
        view.setText(username);
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
    }
    public void DeconstructionActivity(View view) {
        Intent i = new Intent(this, WordActivity.class);
        startActivity(i);
    }
    public void RandomReader(View view) {
        Intent i = new Intent(this,RandomReadingActivity.class);
        startActivity(i);
    }
    public void WordBuilder(View view) {
        Intent i = new Intent(this,WordBuilderActivity.class);
        startActivity(i);
    }
    public void Write(View view) {
        Intent i = new Intent(this,WriteActivity.class);
        startActivity(i);
    }
    public void Tweets(View view) {
        mUser = new User(context,userId);
        if (mUser.allowTwitter()) {
            Intent i = new Intent(this,TwitterActivity.class);
            mUser.close();
            startActivity(i);
        } else {
            Toast notAllowed = Toast.makeText(this, getString(R.string.twitter_not_allowed), Toast.LENGTH_LONG);
            notAllowed.show();
            mUser.close();
        }
    }
    public void Spanish(View view) {
        Intent i = new Intent(this,TranslateActivity.class);
        startActivity(i);
    }
    public void LearnerInfo(View view) {
        AlertDialog.Builder InfoDialog = new AlertDialog.Builder(context);
        InfoDialog.setTitle(username);
        mUser = new User(context,userId);
        mUser.loadFluencyModel();
        String LearnerInfo = "Score:  " + mUser.getScore() + "\nFluent Word Count:  " + mUser.getFluentCount();
        InfoDialog.setMessage(LearnerInfo);
        InfoDialog.setPositiveButton("Copy Fluent Words\nto Clipboard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ArrayList<String> fluentWords = mUser.getFluentWords();
                String clipFluentWords = "";
                for (String word : fluentWords) {
                    clipFluentWords = clipFluentWords + word + "\n";
                }
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("fluent words", clipFluentWords);
                clipboard.setPrimaryClip(clip);
                mUser.close();
            }
        });
        InfoDialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                mUser.close();
            }
        });
        InfoDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                mUser.close();
            }
        });
        InfoDialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        tts.shutdown();
    }

    @Override
    protected void onResume() {
        sharedpreferences = getSharedPreferences(USER_SESSION, Context.MODE_PRIVATE);
        username = sharedpreferences.getString(USER_NAME,null);
        userId = sharedpreferences.getString(USER_ID,null);
        super.onResume();
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
            }
        });

    }
    public void onDestroy() {
        super.onDestroy();
        tts.shutdown();
    }
}
