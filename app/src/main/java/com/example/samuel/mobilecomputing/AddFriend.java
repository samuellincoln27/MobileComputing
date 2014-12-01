package com.example.samuel.mobilecomputing;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;

public class AddFriend extends Activity {

    String userNick;
    String message;
    JSONArray friendList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
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

    @Override
    protected void onResume(){
        super.onResume();
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }
        userNick = extras.getString("userNick");

        Button cancel = (Button) findViewById(R.id.cancelAddFriend);
        cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        Button addFriend = (Button)findViewById(R.id.addfriend);
        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String friendName = ((EditText)findViewById(R.id.newPassword2)).getText().toString().trim();

                //Checks for validation
                if(friendName.length() == 0) {
                    System.out.println("Please enter your friend's nickname!");
                    Toast.makeText(getApplicationContext(), "Please enter your friend's nickname!", Toast.LENGTH_LONG).show();
                    return;
                }

                JSONObject json = new JSONObject();

                try {
                    json.put("friendname", friendName);
                    json.put("username", userNick);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try{
                    String url = ServerAdd.SERVER_URL + "addfriend";
                    ExecuteAddFriendRequest eafr = new ExecuteAddFriendRequest(json, url);
                    eafr.execute();
                }
                catch(Exception e) {
                    Toast.makeText(getApplicationContext(), "Could not add you friend!", Toast.LENGTH_LONG).show();
                    System.out.println("**** exception caught for Add Friend...! + -> " + e.toString());
                }
            }
        });
    }

    public void processResult(String addFriendSuccess) {
        if(addFriendSuccess.equals("true")) {
            System.out.println("addFriend Successful!");
            Toast.makeText(getApplicationContext(), "Friend successfully added!", Toast.LENGTH_LONG).show();
            reloadFriendPage();
        }
        else {
            System.out.println("addFriend failed!");
            Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
        }
    }

    public void reloadFriendPage() {
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
    class ExecuteAddFriendRequest extends AsyncTask<Void,Void ,Void> {

        public JSONObject inputJson;
        public String url;
        public String addFriendSuccess;


        public ExecuteAddFriendRequest(JSONObject j, String urlStr) {
            inputJson = j;
            url = urlStr;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try{

                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);
                StringEntity se = null;

                try {
                    se = new StringEntity(inputJson.toString());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                httpPost.setEntity(se);
                httpPost.setHeader("Accept", "text/plain");
                httpPost.setHeader("Content-type", "application/json");

                System.out.println("********** before executing add friend request ***********");

                HttpResponse js = (HttpResponse) httpClient.execute(httpPost);
                String responseStr=  EntityUtils.toString(js.getEntity());
                JSONObject resultJson = new JSONObject(responseStr);
                message = resultJson.getString("message");
                addFriendSuccess = resultJson.getString("boolean");

            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            System.out.println("********** received response ***********");
            processResult(addFriendSuccess);
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