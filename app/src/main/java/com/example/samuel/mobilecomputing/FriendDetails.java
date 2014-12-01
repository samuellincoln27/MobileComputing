package com.example.samuel.mobilecomputing;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class FriendDetails extends Activity {

    String library;
    String floor;
    String lastSeen;
    String userNick;
    String friendName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_details);


    }
    @Override
    protected void onResume(){
        super.onResume();
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }
        userNick = extras.getString("userNick");

        library = extras.getString("library");
        floor = extras.getString("floor");
        lastSeen = extras.getString("lastSeen");
        friendName = extras.getString("friendName");

        TextView friend = (TextView)findViewById(R.id.FriendName);
        TextView Library = (TextView)findViewById(R.id.Library);
        TextView Floor = (TextView)findViewById(R.id.Floor);
        TextView LastSeen = (TextView)findViewById(R.id.LastSeen);
        Date s = null;
        SimpleDateFormat sdf = null;
        try {
            s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(lastSeen);
           sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        friend.setText(friendName);
        Library.setText(library);
        Floor.setText(floor);
        LastSeen.setText(sdf.format(s));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.friend_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
