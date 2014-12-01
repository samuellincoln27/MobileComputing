package com.example.samuel.mobilecomputing;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class LibInfo extends Activity {

    String userNick;
    String libName;
    JSONObject floorCapacities;
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
        userNick = extras.getString("userNick");
        libName = extras.getString("libName");

        TextView lib = (TextView)findViewById(R.id.Lib);
        lib.setText(libName);
        String capacities = extras.getString("capacities");
        try {
            floorCapacities = new JSONObject(capacities);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String[] floorArray = new String[floorCapacities.length()];
        for(int ind = 0; ind < floorCapacities.length(); ind++) {
            String cap = null;
            int in = ind + 1;
            try {
                cap = floorCapacities.getString("floor" + in);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(cap.equals("NaN%"))
                cap = "0 %";
            floorArray[ind] = "Floor " + in +"                        "+ cap;
        }

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, floorArray);

        floorlist.setAdapter(arrayAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}