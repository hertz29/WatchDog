package com.hfad.watchdog_client;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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

public class LoginPage extends Activity {

    public String fUsername;
    public String fPassword;
    public String fDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
    }

    public void onClickLogin(View view) {
        EditText ET_username = (EditText) findViewById(R.id.usernameInput);
        EditText ET_password = (EditText) findViewById(R.id.passwordInput);
        String username = ET_username.getText().toString();
        String password = ET_password.getText().toString();
        fUsername = username;
        fPassword = password;
        String method = "login";
        BackgroundTask1 bt = new BackgroundTask1(this);
        bt.execute(method, username, password);
        // finish();


    }

    public void onClickGoToSignUpActivity(View view) {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);


    }


    public class BackgroundTask1 extends AsyncTask<String,Void,String> {

        AlertDialog alertDialog;
        Context ctx;

        BackgroundTask1(Context ctx){
            this.ctx = ctx;

        }

        protected void onPreExecute(){
            alertDialog = new AlertDialog.Builder(ctx).create();
            alertDialog.setTitle("Login Information");
        }
        @Override
        protected String doInBackground(String... params) {
            String method = params[0];
            if (method.equals("login")) {
                return loginRequest(params);
            }
            return "connection faliure";
        }




        protected String loginRequest(String... params){

            String login_url = Constants.my_ip+"login.php";
            String login_user = params[1];
            String password_user = params[2];
            try {
                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));
                String data = URLEncoder.encode("login_user", "UTF-8") + "=" + URLEncoder.encode(login_user, "UTF-8") + "&" +
                        URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password_user, "UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                String response ="";
                String line ="";
                while ((line = bufferedReader.readLine())!=null){
                    response+=line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                fDetails = response;
                return response;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "Can not connect to the server";
        }



    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String result) {
        if(!result.equals("Error Login Failed..")){
            Intent intent = new Intent(ctx,MainNavigation.class);
            intent.putExtra("details",fDetails);
            startActivity(intent);
        }
        else {
            alertDialog.setMessage(result);
            alertDialog.show();
        }
    }
}
}


