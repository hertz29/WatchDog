package com.hfad.watchdog_client;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.StringTokenizer;

public class Benefits extends Activity {

    public ListView fReviewsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_benefits);
        fReviewsListView =(ListView) findViewById(R.id.benefitsList);
        BackgroundTaskGetBen b = new BackgroundTaskGetBen(this);
        b.execute("getBen");

    }


    public class BackgroundTaskGetBen extends AsyncTask<String,Void,String> {

        AlertDialog alertDialog;
        Context ctx;

        BackgroundTaskGetBen(Context ctx){
            this.ctx = ctx;

        }

        protected void onPreExecute(){
            alertDialog = new AlertDialog.Builder(ctx).create();
            alertDialog.setTitle("Login Information");
        }

        protected String doInBackground(String... params) {
            String method = params[0];
            return getBen();
        }



        protected String getBen() {
            String postRequest_url = Constants.my_ip + "getBen.php";
            String response = "";
            try {
                URL url = new URL(postRequest_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                /*BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));
                bufferedWriter.close();*/
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    response += line;
                }
                bufferedReader.close();
                inputStream.close();
              //  httpURLConnection.disconnect();
                return response;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return response;
        }


            @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            if(result.equals("getBenError")){
                alertDialog.setMessage("Loding Post error");
                alertDialog.show();
            }
            else{
                StringTokenizer st = new StringTokenizer(result,"@");
                String serverMsg = st.nextToken();
                if(serverMsg.equals("benefit")) {
                    Integer arraySize = Integer.parseInt(st.nextToken());
                    String str = st.nextToken();
                    String[] strReviews =new String[arraySize];
                    Post[] reviews =  new Review[arraySize];
                    strReviews = initPosts(str,arraySize);
                    reviews = createPosts(strReviews);
                    createCustomAdapter(reviews);
                }

            }
        }
    }

    protected void createCustomAdapter(Post[] posts) {
        CustomAdapter ca = new CustomAdapter(this,posts,"benefit");
        ListView postsList = (ListView) findViewById(R.id.benefitsList);
        postsList.setAdapter(ca);
    }


    public String[] initPosts(String s, Integer arraySize) {
        StringTokenizer st = new StringTokenizer(s,"`$");
        String[] posts = new String[arraySize*2];

        for(int i = 0; st.hasMoreTokens()&&i<arraySize*2; i++){
            posts[i]= st.nextToken();
        }
        return posts;
    }


    private Post[] createPosts(String[] strPost) {
        int num = (strPost.length)/2;
        Post[] retPost = new Post[num];
        String tempUsername,tempPost;
        int j = 0;
        for(int i = 0; i < strPost.length-1 ; i+=2){
            tempUsername = strPost[i];
            tempPost = strPost[i+1];
            retPost[j] = new Post(tempUsername,tempPost);
            j++;
        }
        return retPost;
    }

}
