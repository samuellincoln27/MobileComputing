package com.example.samuel.mobilecomputing;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.TextView;


import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

public class Nick extends Activity {

    JSONArray libraryList;
    JSONArray friendList;
    String userNick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nick);
    }
    @Override
    protected void onResume(){
        super.onResume();
        Button capacity = (Button)findViewById(R.id.capacity_view);
        Button friendFinder = (Button)findViewById(R.id.findfriend);
        Button changePassword = (Button)findViewById(R.id.changePassword);
        TextView nick = (TextView)(findViewById(R.id.nick));

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }
        userNick = extras.getString("userNick");
        nick.setText("Hello "+userNick+" ,");

        capacity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            try{

                String url = ServerAdd.SERVER_URL + "viewCapacity";
                //String url = "http://192.168.0.23:8190/viewCapacity";
                ExecuteCapacityRequest ecr = new ExecuteCapacityRequest(url);
                ecr.execute();
            }
            catch(Exception e) {
                Toast.makeText(getApplicationContext(), "Cannot load Library List!", Toast.LENGTH_LONG).show();
                System.out.println("**** exception caught for viewCapacity...! + -> " + e.toString());
            }
            }
        });

        friendFinder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{
                    String url = ServerAdd.SERVER_URL + "fetch/" + userNick;
                    ExecuteFindFriendRequest effr = new ExecuteFindFriendRequest (url);
                    effr.execute();
                }
                catch(Exception e) {
                    String errorMsg = "Friend List could not be loaded!";
                    Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    System.out.println("**** exception caught for findFriend...! + -> " + e.toString());
                }
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(),ChangePassword.class);
                i.putExtra("userNick", userNick);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
                finish();
            }
        });
    }

    public void processCapacityResult() {

        if(libraryList == null){
            System.out.println("Cannot load Library List!");
            Toast.makeText(getApplicationContext(), "Cannot load Library List!", Toast.LENGTH_LONG).show();
            return;
        }
        else {
            Intent i = new Intent(getApplicationContext(),LibraryChoice.class);
            i.putExtra("userNick", userNick);

            String[] libListString = new String[libraryList.length()];
            for(int ind = 0; ind < libraryList.length(); ind++) {
                try {
                    libListString[ind] = (String)libraryList.get(ind);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Bundle b = new Bundle();
            b.putStringArray("libList", libListString);

            i.putExtras(b);
            i.putExtra("bundle",  libListString);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

        }
    }

    public void processFindFriendResult() {

        if(friendList == null){
            String errorMsg = "Friend List could not be loaded!";
            System.out.println(errorMsg);
            Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
            return;
        }
        else {
            Intent i = new Intent(getApplicationContext(),findFriend.class);
            i.putExtra("userNick", userNick);

            String[] friendListString = new String[friendList.length()];
            for(int ind = 0; ind < friendList.length(); ind++) {
                try {
                    friendListString[ind] = (String)friendList.get(ind);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Bundle b = new Bundle();
            b.putStringArray("friendList", friendListString);

            i.putExtras(b);
            i.putExtra("bundle",  friendListString);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.nick, menu);
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

    class ExecuteCapacityRequest extends AsyncTask<Void,Void ,Void> {

        public String url;

        public ExecuteCapacityRequest(String urlStr) {
            url = urlStr;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try{

                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet();
                httpGet.setURI(new URI(url));

                System.out.println("********** before executing viewCapacity request ***********");

                HttpResponse response = (HttpResponse) httpClient.execute(httpGet);

                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                StringBuilder builder = new StringBuilder();
                for (String line = null; (line = reader.readLine()) != null;) {
                    builder.append(line).append("\n");
                }
                JSONTokener tokener = new JSONTokener(builder.toString());
                libraryList = new JSONArray(tokener);
                System.out.print(libraryList.toString());

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
            processCapacityResult();
        }
    }

    class ExecuteFindFriendRequest extends AsyncTask<Void,Void ,Void> {

        public String url;

        public ExecuteFindFriendRequest(String urlStr) {
            url = urlStr;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try{

                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet();
                httpGet.setURI(new URI(url));

                System.out.println("********** before executing findFriend request ***********");

                HttpResponse response = (HttpResponse) httpClient.execute(httpGet);

                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                StringBuilder builder = new StringBuilder();
                for (String line = null; (line = reader.readLine()) != null;) {
                    builder.append(line).append("\n");
                }
                JSONTokener tokener = new JSONTokener(builder.toString());
                friendList = new JSONArray(tokener);
                System.out.print(friendList.toString());

            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            System.out.println("********** received findFriend response ***********");
            processFindFriendResult();
        }


    }
}