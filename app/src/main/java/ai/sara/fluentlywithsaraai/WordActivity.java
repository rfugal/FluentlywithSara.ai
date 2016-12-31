package ai.sara.fluentlywithsaraai;

import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewDebug;
import android.widget.TextView;

public class WordActivity extends AppCompatActivity {
    User zachary;
    String word = "First Word";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        zachary = new User (
                "Zachary",
                this.getResources().getStringArray(R.array.frequent_words),
                this.getResources().getIntArray(R.array.initial_weights));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word);
        TextView randWord = (TextView) findViewById(R.id.randomWord);
        word = zachary.getRandWord();
        randWord.setText(word);
    }
    public void nextWord(View view) {
        TextView randWord = (TextView) findViewById(R.id.randomWord);
        TextView score = (TextView) findViewById(R.id.score);
        word = zachary.recognizedWord(word);
        String newscore = Integer.toString(zachary.getScore());
        randWord.setText(word);
        score.setText("Score: " + newscore);
    }
}
