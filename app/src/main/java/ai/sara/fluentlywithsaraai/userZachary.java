package ai.sara.fluentlywithsaraai;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class userZachary extends AppCompatActivity {
    private String username;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_zachary);
        TextView view = (TextView) findViewById(R.id.learner);
        username = getIntent().getStringExtra("USER_NAME");
        userId = getIntent().getStringExtra("USER_ID");
        view.setText(username);
    }
    public void FlipActivity(View view) {
        Intent i = new Intent(this, WordActivity.class);
        i.putExtra("USER_ID",userId);
        startActivity(i);
    }
}
