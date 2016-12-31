package ai.sara.fluentlywithsaraai;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ArrayList<User> Users = new ArrayList<>(0);
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder addUser = new AlertDialog.Builder(MainActivity.this);
                addUser.setTitle("Add User");
                LinearLayout newUserPrompt = (LinearLayout) View.inflate(MainActivity.this, R.layout.add_user, null);
                addUser.setView(newUserPrompt);
                addUser.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = "Addison";
                        Users.add(0, new User(
                                name,
                                MainActivity.this.getResources().getStringArray(R.array.frequent_words),
                                MainActivity.this.getResources().getIntArray(R.array.initial_weights)));
                        TextView newUser = (TextView) View.inflate(MainActivity.this, R.layout.user_list, null);
                        newUser.setText(name);
                        LinearLayout userList = (LinearLayout) findViewById(R.id.user_list);
                        userList.addView(newUser);
                    }
                });
                addUser.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                addUser.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void userZachary(View view) {
        Intent i = new Intent(this, userZachary.class);
        startActivity(i);
    }
    public void userAva(View view) {
        Intent i = new Intent(this, userAva.class);
        startActivity(i);
    }
    public void userLyla(View view) {
        Intent i = new Intent(this, userLyla.class);
        startActivity(i);
    }

}
