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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChangePassword extends Activity {

    String userNick;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //if (id == R.id.action_settings) {
        //    return true;
        //}
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
        Button changePwd = (Button)findViewById(R.id.confirm);

        changePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email_id = ((EditText)findViewById(R.id.emailid)).getText().toString().trim();
                String oldPassword = ((EditText)findViewById(R.id.oldPassword)).getText().toString().trim();
                String newPassword = ((EditText)findViewById(R.id.newPassword)).getText().toString().trim();
                String newPassword2 = ((EditText)findViewById(R.id.newPassword2)).getText().toString().trim();

                if(!validateEmail(email_id)) {
                    System.out.println("Invalid email ID!");
                    Toast.makeText(getApplicationContext(), "Invalid Email ID", Toast.LENGTH_LONG).show();
                    return;
                }

                if(oldPassword.length() == 0) {
                    String errorMsg = "Please enter your old Password.";
                    System.out.println(errorMsg);
                    Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    return;
                }

                if(!validatePassword(newPassword)) {
                    String errorMsg = "Password should contain at least a digit. It should be of length 6 to 20 characters.";
                    System.out.println(errorMsg);
                    Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    return;
                }

                if(!newPassword2.equals(newPassword)) {
                    String errorMsg = "Password fields don't match!";
                    System.out.println(errorMsg);
                    Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    return;
                }

                JSONObject json = new JSONObject();

                try {
                    json.put("username", userNick);
                    json.put("oldPassword", oldPassword);
                    json.put("newPassword", newPassword);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                  try{
                    String url = ServerAdd.SERVER_URL + "changePassword";
                    ExecuteChangePwdRequest ecpr = new ExecuteChangePwdRequest(json, url);
                    ecpr.execute();
                }
                catch(Exception e) {
                    Toast.makeText(getApplicationContext(), "Could not change your password!", Toast.LENGTH_LONG).show();
                    System.out.println("**** exception caught for changePassword...! + -> " + e.toString());
                }
            }
        });
    }

    public void processChangePwdResult(String changePwdSucess) {

        if(changePwdSucess.equals("true")) {
            System.out.println("Change Password Successful!");

            Toast.makeText(getApplicationContext(), "Password successfully changed!", Toast.LENGTH_LONG).show();

            Intent i = new Intent(getApplicationContext(),Nick.class);
            i.putExtra("userNick",userNick);
            startActivity(i);
        }
        else {
            String errorMsg = "Password could not be changed!";
            System.out.println("Change Password failed!");
            Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
        }
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

    class ExecuteChangePwdRequest extends AsyncTask<Void,Void ,Void> {

        String url;
        JSONObject inputJson;
        String changePwdSuccess;

        public ExecuteChangePwdRequest(JSONObject j,String urlStr) {
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


                HttpResponse response = (HttpResponse) httpClient.execute(httpPost);
                changePwdSuccess =  EntityUtils.toString(response.getEntity());

            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            System.out.println("********** received changePwd response ***********");
            processChangePwdResult(changePwdSuccess);
        }
    }
}