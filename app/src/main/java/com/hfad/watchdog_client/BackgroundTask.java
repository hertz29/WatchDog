package com.hfad.watchdog_client;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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


public class BackgroundTask extends AsyncTask<String,Void,String> {

    AlertDialog alertDialog;
    Context ctx;

    BackgroundTask(Context ctx){
        this.ctx = ctx;

    }

    protected void onPreExecute(){
        alertDialog = new AlertDialog.Builder(ctx).create();
        alertDialog.setTitle("Login Information");
    }
    @Override
    protected String doInBackground(String... params) {
        String method = params[0];
        if (method.equals("register")){
            return registerRequest(params);
        }
        else if (method.equals("login")) {
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
                    URLEncoder.encode("password_user", "UTF-8") + "=" + URLEncoder.encode(password_user, "UTF-8");
            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
            String respone ="";
            String line ="";
            while ((line = bufferedReader.readLine())!=null){
                respone+=line;
            }
            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();
            return respone;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Can not connect to the server";
    }


    /*
    * handle case of user registertion request
    * status: working with the Emulator
    *         doesn't working from a regular device
    * */
    protected  String registerRequest(String... params){
        String reg_url = "http://"+Constants.my_ip+"/watchdog/register.php";
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
            IS.close();
            return "succsses";
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
        if (result.equals("succsses")){
            Toast.makeText(ctx,result,Toast.LENGTH_LONG).show();
          //  Intent intent = new Intent(getApplicationContenxt())
        }
        else{

            alertDialog.setMessage(result);
            alertDialog.show();
        }
    }
}

