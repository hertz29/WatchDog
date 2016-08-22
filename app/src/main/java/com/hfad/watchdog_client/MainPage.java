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


public class MainPage extends Activity {


    public UserClient fUserClient = null;
    private ListView fListView;
    CustomAdapter fCustomAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        Intent intent = getIntent();
        fUserClient = (UserClient)intent.getSerializableExtra("UserClient");
        BackgroundTaskGetPosts bt = new BackgroundTaskGetPosts(this);
        fListView = (ListView) findViewById(R.id.mainPagePosts);
        bt.execute("getPosts");
    }
    public void GoToUserButtom(View view){
        Button B_username  = (Button) view;
        String postUsername = B_username.getText().toString();
        BackgroundTaskGetPosts bt = new BackgroundTaskGetPosts(this);
        bt.execute("getUser", postUsername);
    }

    public void onClickPostRequest(View view){
        //Button B_request  = (Button) findViewById(R.id.postRequestButton);
        Intent intent = new Intent(this, PostPage.class);
        intent.putExtra("UserClient", fUserClient);
        startActivity(intent);
    }

    public void onResume(){
        super.onResume();
        onCreate(null);
    }
    public class BackgroundTaskGetPosts extends AsyncTask<String,Void,String> {

        AlertDialog alertDialog;
        Context ctx;
        String response;

        BackgroundTaskGetPosts(Context ctx){
            this.ctx = ctx;

        }

        protected void onPreExecute(){
            alertDialog = new AlertDialog.Builder(ctx).create();
            alertDialog.setTitle("Login Information");
        }

        protected String doInBackground(String... params) {
            String method = params[0];

            if(method.equals("getPosts")){
                return getPosts();
            }
           //else if(method.equals("getUser")){
            else{
                String username = params[1];
                return getUser(params);
            }
           // return "error";
        }

        private String getUser(String... params) {
            String getUser_url = Constants.my_ip+"getUser.php";
            String login_user = params[1];
            try {
                URL url = new URL(getUser_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));
                String data = URLEncoder.encode("login_user", "UTF-8") + "=" + URLEncoder.encode(login_user, "UTF-8")+ "&" +
                        URLEncoder.encode("temp", "UTF-8") + "=" + URLEncoder.encode("temp", "UTF-8");
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

        protected String getPosts(){
            String postRequest_url = Constants.my_ip+"mainPageGetPosts.php";

            try {
                URL url = new URL(postRequest_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
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
            return "getPostError";
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
            else if(result.equals("getPostError")){
                alertDialog.setMessage("Loding Post error");
                alertDialog.show();
            }
            else{
                StringTokenizer st = new StringTokenizer(result,"@");
                String serverMsg = st.nextToken();

                if(serverMsg.equals("POST")) {
                    Integer arraySize = Integer.parseInt(st.nextToken());
                    String str = st.nextToken();
                    String[] strPost = new String[arraySize];
                    Post[] posts = new Post[arraySize];
                    strPost = initPosts(str,arraySize);
                    posts = createPosts(strPost);
                    createCustomAdapter(posts);
                }
                else if(serverMsg.equals("USER")){
                    String str = st.nextToken();
                    UserClient postUser = new UserClient(str);
                    Intent intent = new Intent(this.ctx,UserPage.class);
                    intent.putExtra("UserClient", postUser);
                    if(postUser.fUserName.equals(fUserClient.fUserName)){
                        intent.putExtra("Extra_Info" , true);

                    }
                    else{
                        intent.putExtra("Extra_Info" , false);
                        intent.putExtra("VisitUser", fUserClient);
                    }
                    startActivity(intent);

                }
                else{
                    alertDialog.setMessage("Fatal Error");
                    alertDialog.show();
                }
            }
        }
    }

    protected void createCustomAdapter(Post[] posts) {
        fCustomAdapter = new CustomAdapter(this,posts,"posts");
        fListView = (ListView) findViewById(R.id.mainPagePosts);
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
        String tempUsername,tempPost;
        for(int i = 0; i < strPost.length ; i++){
            StringTokenizer st = new StringTokenizer(strPost[i],"`#");
            tempUsername = st.nextToken();
            tempPost = st.nextToken();
            retPost[i] = new Post(tempUsername,tempPost);
        }
        return retPost;
    }
}
