package com.example.samuel.mobilecomputing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

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

    public boolean strIsValid(String str) {
        if(str == null || str.trim().length() == 0)
            return false;

        return true;
    }
    @Override
    protected void onResume(){
        super.onResume();
        Button register = (Button)findViewById(R.id.button2);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emailID = ((EditText)findViewById(R.id.emailid)).getText().toString().trim();
                String nickname = ((EditText)findViewById(R.id.nick)).getText().toString().trim();
                String password = ((EditText)findViewById(R.id.password)).getText().toString().trim();
                String password2 = ((EditText)findViewById(R.id.password2)).getText().toString().trim();

                //Checks for validation
                if(nickname.length() == 0) {
                    System.out.println("Please enter a nickname!");
                    Toast.makeText(getApplicationContext(), "Please enter a nickname!", Toast.LENGTH_LONG).show();
                    return;
                }

                if(!validateEmail(emailID)) {
                    System.out.println("invalid email ID!");
                    Toast.makeText(getApplicationContext(), "Invalid Email ID", Toast.LENGTH_LONG).show();
                    return;
                }

                if(!validatePassword(password)) {
                    String errorMsg = "Password should contain a number, an uppercase letter, a lowercase letter. It should be of length 6 to 20 characters.";
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
                    json.put("username", emailID);
                    json.put("password", password);
                    json.put("nickname", nickname);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Boolean registerSuccess;
                try{
                    registerSuccess = registerRequest(json);
                    System.out.println("register successfully called");

                }
                catch(Exception e) {
                    registerSuccess = false;
                    System.out.println("****exception caught for register...! + -> " + e.toString());
                }
                // TODO Auto-generated method stub
                if(registerSuccess) {
                    System.out.println("Registration Successful!");
                    Toast.makeText(getApplicationContext(), "Registration Successful!", Toast.LENGTH_LONG).show();
                }
                else {
                    System.out.println("Registration failed!");
                    Toast.makeText(getApplicationContext(), "Registration failed!", Toast.LENGTH_LONG).show();
                }
                // Changed nick to login by niharika below
                Intent i = new Intent(getApplicationContext(),Login.class);
                startActivity(i);
            }
        });
    }

    public Boolean registerRequest (JSONObject json) throws Exception {
        //instantiates httpclient to make request
        DefaultHttpClient httpclient = new DefaultHttpClient();

        //String url = "http://localhost:8190/register";
        String url = "http://10.0.2.2:8190/register";
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
        Boolean success = (Boolean) httpclient.execute(httpPost, responseHandler);
        System.out.println("********** received response ***********");

        return success;
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

        String PWD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})";

        pattern = Pattern.compile(PWD_PATTERN);
        matcher = pattern.matcher(pwd);
        return matcher.matches();
    }
}
