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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;


public class Login extends Activity {

    String userNick;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void onResume(){
        super.onResume();
        Button login = (Button)findViewById(R.id.login);
        Button register = (Button)findViewById(R.id.register);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = ((EditText)findViewById(R.id.newPassword2)).getText().toString().trim();
                String password = ((EditText)findViewById(R.id.password)).getText().toString().trim();
                userNick = username;

                if(username.length() == 0) {
                    System.out.println("Please enter a valid username!");
                    Toast.makeText(getApplicationContext(), "Please enter a valid username!", Toast.LENGTH_LONG).show();
                    return;
                }

                if(password.length() == 0) {
                    System.out.println("Please enter password!");
                    Toast.makeText(getApplicationContext(), "Please enter password!", Toast.LENGTH_LONG).show();
                    return;
                }
                // TODO Auto-generated method stub
                JSONObject json = new JSONObject();

                try {
                    json.put("username", username);
                    json.put("password", password);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try{
                    //String url = "http://192.168.0.23:8190/login";
                    String url = ServerAdd.SERVER_URL + "login";
                    ExecuteLoginRequest elr = new ExecuteLoginRequest(json, url);
                    elr.execute();
                }
                catch(Exception e) {
                    Toast.makeText(getApplicationContext(), "Login Failed!", Toast.LENGTH_LONG).show();
                    System.out.println("**** exception caught for Login...! + -> " + e.toString());
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent i = new Intent(getApplicationContext(),Register.class);
                startActivity(i);
            }
        });

    }

    public void processResult(String loginSuccess) {
       // loginSuccess = "true";
        if(loginSuccess.equals("true")) {
            System.out.println("Login Successful!");

            Toast.makeText(getApplicationContext(), "Login Successful!", Toast.LENGTH_LONG).show();

            Intent i = new Intent(getApplicationContext(),Nick.class);
            i.putExtra("userNick",userNick);
            startActivity(i);
        }
        else {
            System.out.println("Login failed!");
            Toast.makeText(getApplicationContext(), "Login failed!", Toast.LENGTH_LONG).show();
        }
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

    class ExecuteLoginRequest extends AsyncTask<Void,Void ,Void> {

        public JSONObject inputJson;
        public String url;
        public String loginSuccess;

        public ExecuteLoginRequest(JSONObject j, String urlStr) {
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

            System.out.println("********** before executing login request ***********");

            HttpResponse js = (HttpResponse) httpClient.execute(httpPost);
            loginSuccess =  EntityUtils.toString(js.getEntity());

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
            processResult(loginSuccess);
        }
    }
}