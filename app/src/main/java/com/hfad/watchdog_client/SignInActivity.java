package com.hfad.watchdog_client;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;


public class SignInActivity extends AppCompatActivity {
//define some variabls for the database
    String username,password,firstname,lastname,email, dogname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
    }

    public void onClickSignIn(View view){

        EditText ET_username = (EditText) findViewById(R.id.editUsername);
        EditText ET_password = (EditText) findViewById(R.id.editPassword);
        EditText ET_email = (EditText) findViewById(R.id.editEmail);
        EditText ET_firstname = (EditText) findViewById(R.id.editFirstName);
        EditText ET_lastname = (EditText) findViewById(R.id.editLastName);
        EditText ET_dogname = (EditText) findViewById(R.id.editDogName);
        username = ET_username.getText().toString();
        password = ET_password.getText().toString();
        email    = ET_email.getText().toString();
        firstname = ET_firstname.getText().toString();
        lastname = ET_lastname.getText().toString();
        dogname = ET_dogname.getText().toString();
        String method = "register";
        BackgroundTaskSignUp bt = new BackgroundTaskSignUp(this);
        bt.execute(method, username, password, firstname, lastname, email, dogname);
        finish();
    }


    public class BackgroundTaskSignUp extends AsyncTask<String,Void,String> {

        AlertDialog alertDialog;
        Context ctx;

        BackgroundTaskSignUp(Context ctx){
            this.ctx = ctx;

        }

        protected void onPreExecute(){
            alertDialog = new AlertDialog.Builder(ctx).create();
            alertDialog.setTitle("Login Information");
        }
        @Override
        protected String doInBackground(String... params) {
                return registerRequest(params);}


        protected  String registerRequest(String... params){
            String reg_url = Constants.my_ip+"register.php";
            String username = params[1];
            String password = params[2];
            String firstname = params[3];
            String lastname = params[4];
            String email = params[5];
            String dogname = params[6];
            try {
                URL url = new URL(reg_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(OS, "UTF-8");
                BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                String data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8") + "&" +
                        URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8") + "&" +
                        URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8")+ "&" +
                        URLEncoder.encode("firstname", "UTF-8") + "=" + URLEncoder.encode(firstname, "UTF-8") + "&" +
                        URLEncoder.encode("lastname", "UTF-8") + "=" + URLEncoder.encode(lastname, "UTF-8")+"&"+
                        URLEncoder.encode("dogname", "UTF-8") + "=" + URLEncoder.encode(dogname, "UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();
                InputStream IS = httpURLConnection.getInputStream();
                httpURLConnection.disconnect();
                IS.close();
                return "Welcome!";

            } catch (MalformedURLException e) {
                return "faliue1";
            } catch (IOException e) {
                return "faliure2";
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("Welcome!")){
                Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();
            }
            else{
                alertDialog.setMessage(result);
                alertDialog.show();
            }
        }
    }
}
