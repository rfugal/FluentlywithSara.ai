package ai.sara.fluentlywithsaraai;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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

import java.util.ArrayList;

import ai.sara.fluentlywithsaraai.data.User;
import ai.sara.fluentlywithsaraai.data.UserListContract;
import ai.sara.fluentlywithsaraai.data.UsersDb;

public class MainActivity extends AppCompatActivity {
    private ArrayList<User> Users = new ArrayList<>(0);

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        UsersDb db = new UsersDb(this);
        Cursor c = db.rawQuery("SELECT * FROM " + UserListContract.UserList.TABLE_NAME,
                new String[] {});
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
                        int gender = 0;
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
                        User user = new User(MainActivity.this,name,year,gender);
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
        Intent i = new Intent(this, userZachary.class);
        i.putExtra("USER_ID", user.getId());
        i.putExtra("USER_NAME", user.getUserName());
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
        LinearLayout userList = (LinearLayout) findViewById(R.id.user_list);
        userList.addView(newUser);
    }
}
