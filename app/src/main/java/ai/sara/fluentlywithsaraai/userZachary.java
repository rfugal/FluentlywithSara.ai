package ai.sara.fluentlywithsaraai;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class userZachary extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_zachary);
    }
    public void FlipActivity(View view) {
        Intent i = new Intent(this, WordActivity.class);
        startActivity(i);
    }
}
