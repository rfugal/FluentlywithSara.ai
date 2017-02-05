package ai.sara.fluentlywithsaraai;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.regex.Pattern;

import ai.sara.fluentlywithsaraai.data.User;
import ai.sara.fluentlywithsaraai.data.UserListContract;
import ai.sara.fluentlywithsaraai.data.UsersDb;

public class MainActivity extends AppCompatActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "Kx5Jdpi3lU8LpNmSPi5NDrims";
    private static final String TWITTER_SECRET = "DXdaUkSiCs6oNHkN0TlGuBiFK5pZP2iBOreFNWHBN1M9qP2OHg";

    private ArrayList<User> Users = new ArrayList<>(0);
    private final String USER_SESSION = "CurrentUser";
    private final String USER_ID = "UserId";
    private final String USER_NAME = "UserName";
    private SharedPreferences sharedpreferences;
    private int saraIndex = 0;
    private String[] saraSays;
    private TextToSpeech tts;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        sharedpreferences = getSharedPreferences(USER_SESSION, Context.MODE_PRIVATE);
        setContentView(R.layout.activity_main);
        saraSays = getResources().getStringArray(R.array.sara_main);
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        UsersDb db = new UsersDb(this);
        Cursor c = db.rawQuery("SELECT * FROM " + UserListContract.UserList.TABLE_NAME,
                new String[] {});
        if (c.getCount() < 1) {
            User example = new User(this,getString(R.string.example_user_name),new GregorianCalendar(2012,12,12),UserListContract.UserList.GENDER_MALE,getString(R.string.example_known_words));
            listUser(example,getString(R.string.example_user_name));
        }
        if (c.moveToFirst()) {
            do {
                User user = new User(this, c.getString(c.getColumnIndex(UserListContract.UserList.COLUMN_USER_ID)));
                listUser(user, c.getString(c.getColumnIndex(UserListContract.UserList.COLUMN_USER_NAME)));
            } while (c.moveToNext());
        }
        c.close();
        db.close();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder addUserAlert = new AlertDialog.Builder(MainActivity.this);
                addUserAlert.setTitle("Add User");
                LinearLayout addUserPrompt = new LinearLayout(MainActivity.this);
                addUserPrompt.setOrientation(LinearLayout.VERTICAL);
                final EditText newUserName = new EditText(MainActivity.this);
                addUserPrompt.addView(newUserName);
                final Spinner newUserBirthyear = new Spinner(MainActivity.this);
                ArrayAdapter<CharSequence> adapterBirthyear = ArrayAdapter.createFromResource(MainActivity.this,
                        R.array.birth_years, android.R.layout.simple_spinner_item);
                adapterBirthyear.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                newUserBirthyear.setAdapter(adapterBirthyear);
                addUserPrompt.addView(newUserBirthyear);
                final Spinner newUserBirthmonth = new Spinner(MainActivity.this);
                ArrayAdapter<CharSequence> adapterBirthmonth = ArrayAdapter.createFromResource(MainActivity.this,
                        R.array.birth_months, android.R.layout.simple_spinner_item);
                adapterBirthmonth.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                newUserBirthmonth.setAdapter(adapterBirthmonth);
                addUserPrompt.addView(newUserBirthmonth);
                final Spinner newUserBirthday = new Spinner(MainActivity.this);
                ArrayAdapter<CharSequence> adapterDay = ArrayAdapter.createFromResource(MainActivity.this,
                        R.array.day_of_month, android.R.layout.simple_spinner_item);
                adapterDay.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                newUserBirthday.setAdapter(adapterDay);
                addUserPrompt.addView(newUserBirthday);
                final Spinner newUserGender = new Spinner(MainActivity.this);
                ArrayAdapter<CharSequence> adapterGender = ArrayAdapter.createFromResource(MainActivity.this,
                        R.array.gender, android.R.layout.simple_spinner_item);
                adapterGender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                newUserGender.setAdapter(adapterGender);
                addUserPrompt.addView(newUserGender);
                addUserAlert.setView(addUserPrompt);
                addUserAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = newUserName.getText().toString();
                        int year;
                        int month = newUserBirthmonth.getSelectedItemPosition();
                        int day = newUserBirthday.getSelectedItemPosition();
                        int gender = newUserGender.getSelectedItemPosition();
                        try {
                            year = Integer.getInteger(newUserBirthyear.getSelectedItem().toString());
                        } catch(Exception e) {
                            if (newUserBirthyear.getSelectedItem().toString() == "Year of Birthdate") {
                                year = 2016;
                            }
                            else {
                                year = 2003;
                            }
                        }
                        if (month == 0) {
                            month = 12;
                            day = 31;
                        }
                        if (day == 0) day = 1;
                        GregorianCalendar birthdate = new GregorianCalendar(year,month,day);
                        User user = new User(MainActivity.this,name,birthdate,gender);
                        listUser(user,name);
                    }
                });
                addUserAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                addUserAlert.show();
            }
        });
        Toast pressSara = Toast.makeText(this, getString(R.string.sara_main_toast), Toast.LENGTH_LONG);
        pressSara.show();
        FloatingActionButton sara = (FloatingActionButton) findViewById(R.id.sara_main);
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

    public void openUser(View view, User user) {
        Intent i = new Intent(this, UserHomeActivity.class);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(USER_ID,user.getId());
        editor.putString(USER_NAME,user.getUserName());
        editor.commit();
        startActivity(i);
    }
    public void listUser(User user, final String username){
        int index = Users.size();
        Users.add(index, user);
        TextView newUser = (TextView) View.inflate(MainActivity.this, R.layout.user_list,null);
        newUser.setText(username);
        newUser.setId(index);
        newUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openUser(view, Users.get(view.getId()));
            }
        });
        newUser.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {
                final AlertDialog.Builder deleteUserAlert = new AlertDialog.Builder(MainActivity.this);
                deleteUserAlert.setTitle("Delete User");
                final User dUser = Users.get(view.getId());
                TextView dConfirm = new TextView(MainActivity.this);
                dConfirm.setText("Are you sure you want to delete\n\t" + dUser.getUserName() + "\n\nTHIS CANNOT BE UNDONE!");
                dConfirm.setPadding(24,24,24,24);
                deleteUserAlert.setView(dConfirm);
                deleteUserAlert.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        view.setVisibility(View.GONE);
                        dUser.deleteUser();
                    }
                });
                deleteUserAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                deleteUserAlert.show();
                return true;
            }
        });
        LinearLayout userList = (LinearLayout) findViewById(R.id.user_list);
        userList.addView(newUser);
    }
    public void onDestroy() {
        super.onDestroy();
        tts.shutdown();
    }
}
