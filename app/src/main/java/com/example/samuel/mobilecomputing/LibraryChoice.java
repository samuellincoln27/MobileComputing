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

public class LibraryChoice extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_choice);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.add_friend, menu);
        return false;
    }
    @Override
    protected void onResume(){
        super.onResume();
        Button marston = (Button)findViewById(R.id.marston);

        marston.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle extras = getIntent().getExtras();
                if (extras == null) {
                    return;
                }
                String username = extras.getString("USERID");

                // TODO Auto-generated method stub
                JSONObject json = new JSONObject();
                String libName = "Marston";
                try {
                    json.put("username", username);
                    json.put("libName", libName);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String floorCapacity = "";

                try{
                    floorCapacity =(String) getFloorCapacity(json);
                }
                catch(Exception e) {
                    System.out.println("****exception caught for libraries...! + -> " + e.toString());
                }

                // TODO Auto-generated method stub
                Intent i = new Intent(getApplicationContext(),LibInfo.class);
                i.putExtra("USERID", username);
                i.putExtra("floorCapacity", floorCapacity);
                startActivity(i);
            }
        });
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

    public String getFloorCapacity(JSONObject json) throws Exception {
        //instantiates httpclient to make request
        DefaultHttpClient httpclient = new DefaultHttpClient();

        //String url = "http://localhost:8190/register";
        String url = "http://10.0.2.2:8190/getFloorCapacity";
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
        String floorCapacity = obj.getString("floorCapacity");
        return floorCapacity;

    }
}


