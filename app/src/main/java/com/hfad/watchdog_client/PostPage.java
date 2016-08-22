package com.hfad.watchdog_client;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
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

public class PostPage extends Activity {
    public UserClient fUserClient;
    public String fPostString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_page);
    }

    public void postToServer(View view){
        Intent intent = getIntent();
        fUserClient = (UserClient)intent.getSerializableExtra("UserClient");
        EditText ET_editText = (EditText) findViewById(R.id.PosrPageEditText);
        fPostString = ET_editText.getText().toString();
        BackgroundTaskPostPage bt = new BackgroundTaskPostPage(this);
        bt.execute(fUserClient.fUserName, fUserClient.fDogName, fPostString);
        finish();
    }

    public class BackgroundTaskPostPage extends AsyncTask<String,Void,String> {

        AlertDialog alertDialog;
        Context ctx;

        BackgroundTaskPostPage(Context ctx){
            this.ctx = ctx;

        }

        protected void onPreExecute(){
            alertDialog = new AlertDialog.Builder(ctx).create();
            alertDialog.setTitle("Login Information");
        }
        @Override
        protected String doInBackground(String... params) {
            return postRequest(params);
        }



        protected  String postRequest(String... params){
            String reg_url = Constants.my_ip+"postRequest.php";
            String username = params[0];
            String dogname = params[1];
            String post = params[2];
            try {
                URL url = new URL(reg_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(OS, "UTF-8");
                BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                String data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8") + "&" +
                        URLEncoder.encode("dogname", "UTF-8") + "=" + URLEncoder.encode(dogname, "UTF-8") + "&" +
                        URLEncoder.encode("post", "UTF-8") + "=" + URLEncoder.encode(post, "UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();

                InputStream IS = httpURLConnection.getInputStream();
                bufferedWriter.close();
                OS.close();
                IS.close();
                return "succeeded";
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
            if (result.equals("succeeded")){
                Toast.makeText(ctx, "Request posted!", Toast.LENGTH_LONG).show();
            }
            else{

                alertDialog.setMessage(result);
                alertDialog.show();
            }
        }
    }
}
