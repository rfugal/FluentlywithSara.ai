package ai.sara.fluentlywithsaraai;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class WordActivity extends AppCompatActivity {
    User zachary;
    String word = "First Word";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        zachary = new User ("Zachary", this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word);
        TextView randWord = (TextView) findViewById(R.id.randomWord);
        word = zachary.getRandWord();
        randWord.setText(word);
    }
    public void nextWord(View view) {
        TextView randWord = (TextView) findViewById(R.id.randomWord);
        word = zachary.recognizedWord(word);
        if (word == "error") {
            word = zachary.getRandWord();
            randWord.setText(word + " recovered error");
        }
        else randWord.setText(word);
    }
}
