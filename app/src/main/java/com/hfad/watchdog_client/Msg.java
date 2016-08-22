package com.hfad.watchdog_client;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
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
import java.util.StringTokenizer;

public class Msg extends Activity {
    public UserClient fUserClient = null;
    private ListView fListView;
    CustomAdapter fCustomAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg);
        Intent intent = getIntent();
        fUserClient = (UserClient)intent.getSerializableExtra("UserClient");
        BackgroundTaskGetMsg bt = new BackgroundTaskGetMsg(this);
        fListView = (ListView) findViewById(R.id.mainPagePosts);
        bt.execute("getMsg",fUserClient.fUserName);
    }

    public void GoToUserButtom1(View view){
        Button B_username  = (Button) view;
        String postUsername = B_username.getText().toString();
        BackgroundTaskGetMsg bt = new BackgroundTaskGetMsg(this);
        bt.execute("getUser", postUsername);
    }
    public class BackgroundTaskGetMsg extends AsyncTask<String,Void,String> {

        AlertDialog alertDialog;
        Context ctx;
        String response;

        BackgroundTaskGetMsg(Context ctx){
            this.ctx = ctx;

        }

        protected void onPreExecute(){
            alertDialog = new AlertDialog.Builder(ctx).create();
            alertDialog.setTitle("Login Information");
        }

        protected String doInBackground(String... params) {
            String method = params[0];
            if(method.equals("getMsg")) {
                String username = params[1];
                return getMsg(username);
            }
            else{
                String username = params[1];
                return getUser(username);
            }
        }

        private String getMsg(String username) {
            String getUser_url = Constants.my_ip+"getMessages.php";

            try {
                URL url = new URL(getUser_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));
                String data = URLEncoder.encode("reciever", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                response ="";
                String line ="";
                while ((line = bufferedReader.readLine())!=null){
                    response+=line;
                }
                bufferedReader.close();
                inputStream.close();
                //httpURLConnection.disconnect();
                return response;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "Error, GetUser Failed..";
        }

        private String getUser(String username) {
            String getUser_url = Constants.my_ip+"getUser.php";
            String login_user = username;
            try {
                URL url = new URL(getUser_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));
                String data = URLEncoder.encode("login_user", "UTF-8") + "=" + URLEncoder.encode(login_user, "UTF-8");
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
                return response;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "Error, GetUser Failed..";
        }
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            if(result.equals("Error, GetUser Failed..")){
                alertDialog.setMessage("System Error");
                alertDialog.show();
            }
            else if(result.equals("No rows found")){
                Toast.makeText(ctx, "You have no messages", Toast.LENGTH_LONG).show();
                finish();
            }
            else{
                StringTokenizer st = new StringTokenizer(result,"@");
                String serverMsg = st.nextToken();

                if(serverMsg.equals("MSG")) {
                    Integer arraySize = Integer.parseInt(st.nextToken());
                    if(arraySize>0) {
                        String str = st.nextToken();
                        String[] strPost = new String[arraySize];
                        Post[] posts = new Post[arraySize];
                        strPost = initPosts(str, arraySize);
                        posts = createPosts(strPost);
                        createCustomAdapter(posts);
                    }
                }
                else if(serverMsg.equals("USER")) {
                    String str = st.nextToken();
                    UserClient postUser = new UserClient(str);
                    Intent intent = new Intent(this.ctx, UserPage.class);
                    intent.putExtra("UserClient", postUser);
                    intent.putExtra("Extra_Info", false);
                    startActivity(intent);
                }
                else{
                    alertDialog.setMessage("Fatal Error");
                    alertDialog.show();
                }
            }
        }
    }

    protected void createCustomAdapter(Post[] messages) {
        fCustomAdapter = new CustomAdapter(this,messages,"messages");
        fListView = (ListView) findViewById(R.id.MsgListView);
        fListView.setAdapter(fCustomAdapter);
    }


    public String[] initPosts(String s, Integer arraySize){
        StringTokenizer st = new StringTokenizer(s,";");
        String[] posts = new String[arraySize];
        for(int i = 0; st.hasMoreTokens()&&i<arraySize; i++){
            posts[i]= st.nextToken();
        }
        return posts;
    }


    private Post[] createPosts(String[] strPost) {
        Post[] retPost = new Post[strPost.length];
        String tempSender,tempMsg,tempTimenDate;
        for(int i = 0; i < strPost.length ; i++){
            StringTokenizer st = new StringTokenizer(strPost[i],"`#");
            tempSender = st.nextToken();
            tempMsg = st.nextToken()+"`#"+st.nextToken();
            retPost[i] = new Post(tempSender,tempMsg);
        }
        return retPost;
    }

}
