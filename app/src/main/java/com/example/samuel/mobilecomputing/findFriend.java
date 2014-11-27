package com.example.samuel.mobilecomputing;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class findFriend extends Activity {
    final Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend);
    }

    @Override
    protected void onResume(){
        super.onResume();
        ListView friendlist = (ListView) findViewById(R.id.friendList);

      /*  Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }*/
       // String friendList = extras.getString("Friends");

        String[]  friendArray = {"Naruto","Ryuk","Natsu","Light","Sam","Lipee","Mohan","Anurag","Mohit"};
       // String[]  friendArray = friendList.split("|");
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, friendArray);

        friendlist.setAdapter(arrayAdapter);
        Button addfriend = (Button)findViewById(R.id.addfriend);
        addfriend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),AddFriend.class);
                startActivity(i);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.add_friend, menu);
        return false;
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
