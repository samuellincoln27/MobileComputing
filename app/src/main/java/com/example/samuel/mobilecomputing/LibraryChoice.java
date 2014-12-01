package com.example.samuel.mobilecomputing;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;

public class LibraryChoice extends Activity {

    String userNick;
    String libName;
    String capacities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_choice);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }
    @Override
    protected void onResume(){
        super.onResume();

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }
        userNick = extras.getString("userNick");
        String[] libraryList = extras.getStringArray("libList");

        ListView Liblist = (ListView) findViewById(R.id.LibList);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, libraryList);

        Liblist.setAdapter(arrayAdapter);

        Liblist.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                libName = (String) adapterView.getAdapter().getItem(i);

                try{

                    String url = ServerAdd.SERVER_URL + "viewCapacity/" + libName;
                    //String url = "http://192.168.0.23:8190/viewCapacity/" + libName;
                    ExecuteFloorRequest efr = new ExecuteFloorRequest(url);
                    efr.execute();
                }
                catch(Exception e) {
                    Toast.makeText(getApplicationContext(), "Cannot load capacity List!", Toast.LENGTH_LONG).show();
                    System.out.println("**** exception caught for viewCapacity "+ libName+" ...! + -> " + e.toString());
                }
            }
        });
    }


    public void processFloorResult() {

            Intent i = new Intent(getApplicationContext(),LibInfo.class);
            i.putExtra("userNick", userNick);
            i.putExtra("libName", libName);
            i.putExtra("capacities", capacities);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

       class ExecuteFloorRequest extends AsyncTask<Void,Void ,Void> {

        public String url;

        public ExecuteFloorRequest(String urlStr) {
            url = urlStr;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {

                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet();
                httpGet.setURI(new URI(url));

                System.out.println("********** before executing floorCapacity request ***********");

                HttpResponse response = (HttpResponse) httpClient.execute(httpGet);
                capacities =  EntityUtils.toString(response.getEntity());
                System.out.println("Response received as : " + capacities);
                System.out.println("********** printing result ***********");

            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            System.out.println("********** received viewCapacity response ***********");
            processFloorResult();
        }
    }
}