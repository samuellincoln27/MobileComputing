package com.example.samuel.mobilecomputing;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import java.io.IOException;
import java.net.URI;

public class findFriend extends Activity {

    String userNick;
    String friendName;
    String location;
    String floor;
    String timestamp;
    String library;

    final Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend);
    }

    @Override
    protected void onResume(){
        super.onResume();

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }
        userNick = extras.getString("userNick");
        String[] friendsList = extras.getStringArray("friendList");
        ListView friendList = (ListView) findViewById(R.id.friendList);

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, friendsList);
        friendList.setAdapter(arrayAdapter);
        friendList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                friendName = (String) adapterView.getAdapter().getItem(i);

                try{
                    String url = ServerAdd.SERVER_URL + "fetch/mac/" + friendName;
                    ExecuteFriendLocRequest eflr = new ExecuteFriendLocRequest(url);
                    eflr.execute();
                }
                catch(Exception e) {
                    Toast.makeText(getApplicationContext(), "Cannot load capacity List!", Toast.LENGTH_LONG).show();
                    System.out.println("**** exception caught for viewCapacity "+ friendName +" ...! + -> " + e.toString());
                }
            }
        });


        Button addFriend = (Button)findViewById(R.id.addfriend);
        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AddFriend.class);
                i.putExtra("userNick", userNick);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

            }
        });
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

    public void processLocationResult() {

        if(location.equals("not found")) {
            Toast.makeText(getApplicationContext(), "Your friend  "+ friendName +" could not be found!", Toast.LENGTH_LONG).show();
            System.out.println("**** Friend " + friendName + " could not be found " + " ...! + -> ");
            return;
        }

        Intent i = new Intent(getApplicationContext(), FriendDetails.class);
        i.putExtra("userNick", userNick);
        i.putExtra("friendName",friendName);
        i.putExtra("library", library);
        i.putExtra("floor", floor);
        i.putExtra("lastSeen", timestamp);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

    }

    class ExecuteFriendLocRequest extends AsyncTask<Void,Void ,Void> {

        public String url;
        public String friendMac;
        public String locString;

        public ExecuteFriendLocRequest(String urlStr) {
            url = urlStr;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {

                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet();
                httpGet.setURI(new URI(url));

                System.out.println("********** before executing friend mac request ***********");

                HttpResponse response = (HttpResponse) httpClient.execute(httpGet);
                String mac = EntityUtils.toString(response.getEntity());
                JSONObject jsonResult = new JSONObject(mac);
                friendMac = jsonResult.getString("mac");
                System.out.println("***************MAC ADDRESS recevied : " + friendMac);
                //friendMac = "00:26:5E:07:2D:33";
                String locURL = ServerAdd.SERVER_URL + "fetch/location/" + friendMac;
                httpGet.setURI(new URI(locURL));

                System.out.println("********** before executing friend Location request ***********");

                HttpResponse locResponse = (HttpResponse) httpClient.execute(httpGet);
                locString = EntityUtils.toString(locResponse.getEntity());

                JSONObject json = new JSONObject(locString);
                location = json.getString("location");
                System.out.println("********** location received :" + location);
                if(location.equals("not found")) {
                    return null;
                }
                floor = json.getString("floor");
                library = json.getString("lib_name");
                timestamp = json.getString("saved_on");

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
            processLocationResult();
        }
    }
}