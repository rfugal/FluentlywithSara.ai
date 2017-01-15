package ai.sara.fluentlywithsaraai;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import ai.sara.fluentlywithsaraai.data.User;

public class WordActivity extends AppCompatActivity {
    User zachary;
    String word = "First Word";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        zachary = new User (this, "Zachary");
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
