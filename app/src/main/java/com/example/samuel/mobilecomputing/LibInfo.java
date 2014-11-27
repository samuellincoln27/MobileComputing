package com.example.samuel.mobilecomputing;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class LibInfo extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lib_info);
    }
    @Override
    protected void onResume(){
        super.onResume();
        ListView floorlist = (ListView) findViewById(R.id.floorlist);
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }
        String username = extras.getString("USERID");
        String floorCapacity = extras.getString("floorCapacity");

        String[]  friendArray = {"Floor1                                 20 %","Floor2                                 30%","Floor3                                 40%"};

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, friendArray);

        floorlist.setAdapter(arrayAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.lib_info, menu);
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
