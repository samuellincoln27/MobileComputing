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


public class Login extends Activity {

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

                String emailID = ((EditText)findViewById(R.id.user)).getText().toString().trim();
                String password = ((EditText)findViewById(R.id.password)).getText().toString().trim();

                if(!validateEmail(emailID)) {
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
                    json.put("username", emailID);
                    json.put("password", password);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Boolean success = false;
                try{
                    success = (Boolean)doLogin(json);
                }
                catch(Exception e) {
                    System.out.println("****exception caught for login...! + -> " + e.toString());
                }
                // TODO Auto-generated method stub
                if(success)
                    System.out.println("Login Successful!");
                else
                    System.out.println("Login Failed!");

                Intent i = new Intent(getApplicationContext(),Nick.class);
                i.putExtra("USERID",emailID);
                startActivity(i);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.login, menu);
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

    public boolean validateEmail(String email) {

        if(email.length() == 0)
            return false;

        Pattern pattern;
        Matcher matcher;

        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public Boolean doLogin (JSONObject json) throws Exception {
        //instantiates httpclient to make request
        DefaultHttpClient httpclient = new DefaultHttpClient();

        //String url = "http://localhost:8190/register";
        String url = "http://10.0.2.2:8190/login";
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
        Toast.makeText(getApplicationContext(), "here received response : ", Toast.LENGTH_LONG).show();
        System.out.println("********** received response ***********");
        System.out.println(responseHandler.toString());
        Toast.makeText(getApplicationContext(),"here response printed : " , Toast.LENGTH_LONG).show();
        System.out.println("**********  response printed ***********");
        return success;
    }

}
