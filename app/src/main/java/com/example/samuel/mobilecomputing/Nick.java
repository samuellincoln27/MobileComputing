package com.example.samuel.mobilecomputing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

public class Nick extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nick);
    }
    @Override
    protected void onResume(){
        super.onResume();
        Button capacity = (Button)findViewById(R.id.capacity_view);

        capacity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JSONObject json = new JSONObject();
                Bundle extras = getIntent().getExtras();
                if (extras == null) {
                    return;
                }
                String username = extras.getString("USERID");

                try {
                    json.put("username", username);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String concatLibNames = "";

                try{
                    String libNames =(String) libCapacityReq(json);
                }
                catch(Exception e) {
                    System.out.println("****exception caught for login...! + -> " + e.toString());
                }
                // TODO Auto-generated method stub
                Intent i = new Intent(getApplicationContext(),LibraryChoice.class);
                i.putExtra("USERID", username);
                i.putExtra("LibNames",concatLibNames);
                startActivity(i);
            }
        });

        Button friendFinder = (Button)findViewById(R.id.findfriend);

        friendFinder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle extras = getIntent().getExtras();
                if (extras == null) {
                    return;
                }
                String username = extras.getString("USERID");

                // TODO Auto-generated method stub
                JSONObject json = new JSONObject();

                try {
                    json.put("username", username);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

               String friendList = "";

                try{
                    friendList =(String) findFriendReq(json);
                }
                catch(Exception e) {
                    System.out.println("****exception caught for libraries...! + -> " + e.toString());
                }

                // TODO Auto-generated method stub
                Intent i = new Intent(getApplicationContext(),findFriend.class);
                i.putExtra("Friends",friendList);
                startActivity(i);
            }
        });

        Button changePassword = (Button)findViewById(R.id.changePassword);
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),ChangePassword.class);
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

    public String findFriendReq(JSONObject json) throws Exception {
        //instantiates httpclient to make request
        DefaultHttpClient httpclient = new DefaultHttpClient();

        //String url = "http://localhost:8190/register";
        String url = "http://10.0.2.2:8190/getFriendList";
        //url with the post data
        HttpPost httpPost = new HttpPost(url);

        //passes the results to a string builder/entity
        StringEntity se = new StringEntity(json.toString());

        //sets the post request as the resulting string
        httpPost.setEntity(se);
        //sets a request header so the page receiving the request
        //will know what to do with it
        httpPost.setHeader("Accept", "text/plain");
        httpPost.setHeader("Content-type", "application/json");

        //Handles what is returned from the page
        ResponseHandler responseHandler = new BasicResponseHandler();
        System.out.println("********** before executing request ***********");

        JSONObject obj = (JSONObject) httpclient.execute(httpPost, responseHandler);

        System.out.println("********** received response ***********");
        String friendList = obj.getString("friendList");
        return friendList;

    }

    public String libCapacityReq(JSONObject json) throws Exception {
        //instantiates httpclient to make request
        DefaultHttpClient httpclient = new DefaultHttpClient();

        //String url = "http://localhost:8190/register";
        String url = "http://10.0.2.2:8190/libCapacity";
        //url with the post data
        HttpPost httpPost = new HttpPost(url);

        //passes the results to a string builder/entity
        StringEntity se = new StringEntity(json.toString());

        //sets the post request as the resulting string
        httpPost.setEntity(se);
        //sets a request header so the page receiving the request
        //will know what to do with it
        httpPost.setHeader("Accept", "text/plain");
        httpPost.setHeader("Content-type", "application/json");

        //Handles what is returned from the page
        ResponseHandler responseHandler = new BasicResponseHandler();
        System.out.println("********** before executing request ***********");

        JSONObject obj = (JSONObject) httpclient.execute(httpPost, responseHandler);

        System.out.println("********** received response ***********");
        String libCapacity = obj.getString("libCapacity");
        return libCapacity;

    }
}
