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

public class SendMsg extends Activity {
    public UserClient fUserClient;
    public UserClient fVisitClient;
    public String fMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_msg);
        Intent intent = getIntent();
        fUserClient = (UserClient)intent.getSerializableExtra("Reciever");
        fVisitClient = (UserClient)intent.getSerializableExtra("Sender");
    }

    public void sendToServer(View view){
        EditText ET_editMsg = (EditText) findViewById(R.id.SendMsgEditText);
        fMsg = ET_editMsg.getText().toString();
        BackgroundTaskSendMsg bt = new BackgroundTaskSendMsg(this);
        bt.execute(fUserClient.fUserName, fVisitClient.fUserName, fMsg);
        finish();
    }

    public class BackgroundTaskSendMsg extends AsyncTask<String,Void,String> {

        AlertDialog alertDialog;
        Context ctx;

        BackgroundTaskSendMsg(Context ctx){
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
            String reg_url = Constants.my_ip+"sendMsg.php";
            String reciever = params[0];
            String sender = params[1];
            String msg = params[2];
            try {
                URL url = new URL(reg_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(OS, "UTF-8");
                BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                String data = URLEncoder.encode("reciever", "UTF-8") + "=" + URLEncoder.encode(reciever, "UTF-8") + "&" +
                        URLEncoder.encode("sender", "UTF-8") + "=" + URLEncoder.encode(sender, "UTF-8") + "&" +
                        URLEncoder.encode("msg", "UTF-8") + "=" + URLEncoder.encode(msg, "UTF-8");
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
                Toast.makeText(ctx, "Message was sent!", Toast.LENGTH_LONG).show();
            }
            else{

                alertDialog.setMessage(result);
                alertDialog.show();
            }
        }
    }
}
