package com.example.samuel.mobilecomputing;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
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
        Button register = (Button)findViewById(R.id.button2);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email_id = ((EditText)findViewById(R.id.emailid)).getText().toString().trim();
                String username = ((EditText)findViewById(R.id.nick)).getText().toString().trim();
                String password = ((EditText)findViewById(R.id.password)).getText().toString().trim();
                String password2 = ((EditText)findViewById(R.id.password2)).getText().toString().trim();


                WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                WifiInfo info = manager.getConnectionInfo();
                String mac_address = info.getMacAddress();

                //Checks for validation
               if(username.length() == 0) {
                    System.out.println("Please enter a nickname!");
                    Toast.makeText(getApplicationContext(), "Please enter a nickname!", Toast.LENGTH_LONG).show();
                    return;
                }

                if(!validateEmail(email_id)) {
                    System.out.println("Invalid email ID!");
                    Toast.makeText(getApplicationContext(), "Invalid Email ID", Toast.LENGTH_LONG).show();
                    return;
                }

                if(password.length() == 0) {
                    String errorMsg = "Please enter a Password.";
                    System.out.println(errorMsg);
                    Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    return;
                }

                if(!validatePassword(password)) {
                    String errorMsg = "Password should contain at least a digit. It should be of length 6 to 20 characters.";
                    System.out.println(errorMsg);
                    Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    return;
                }

                if(!password.equals(password2)) {
                    String errorMsg = "Password fields don't match!";
                    System.out.println(errorMsg);
                    Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    return;
                }

                JSONObject json = new JSONObject();

                try {
                    json.put("username", username);
                    json.put("password", password);
                    json.put("email_id", email_id);
                    json.put("mac_address", mac_address);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try{
                    String url = ServerAdd.SERVER_URL + "register";
                    //String url = "http://192.168.0.23:8190/register";
                    ExecuteRegisterRequest err = new ExecuteRegisterRequest(json, url);
                    err.execute();
                }
                catch(Exception e) {
                    Toast.makeText(getApplicationContext(), "Registration Failed!", Toast.LENGTH_LONG).show();
                    System.out.println("**** exception caught for register...! + -> " + e.toString());
                }
            }
        });
    }

    public void processResult(String registerSuccess) {
        if(registerSuccess.equals("true")) {
            System.out.println("Registration Successful!");
            Toast.makeText(getApplicationContext(), "Registration Successful!", Toast.LENGTH_LONG).show();
        }
        else {
            System.out.println("Registration failed!");
            Toast.makeText(getApplicationContext(), "Registration failed!", Toast.LENGTH_LONG).show();
        }

        Intent i = new Intent(getApplicationContext(),Login.class);
        startActivity(i);
    }

    public boolean validateEmail(String email) {

        Pattern pattern;
        Matcher matcher;

        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public boolean validatePassword(String pwd) {

        Pattern pattern;
        Matcher matcher;

        String PWD_PATTERN = "((?=.*\\d)(?=.*[a-z]).{6,20})";
        pattern = Pattern.compile(PWD_PATTERN);
        matcher = pattern.matcher(pwd);
        return matcher.matches();
    }


    class ExecuteRegisterRequest extends AsyncTask<Void,Void ,Void> {

        public JSONObject inputJson;
        public String url;
        public String registerSuccess;

        public ExecuteRegisterRequest(JSONObject j, String urlStr) {
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

                System.out.println("********** before executing register request ***********");

                HttpResponse response = (HttpResponse) httpClient.execute(httpPost);
                registerSuccess =  EntityUtils.toString(response.getEntity());

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
            processResult(registerSuccess);
        }
    }
}