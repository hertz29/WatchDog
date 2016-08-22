package com.hfad.watchdog_client;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.StringTokenizer;


public class AdoptionPage extends AppCompatActivity {

    public ListView fReviewsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adoption_page);
        fReviewsListView =(ListView) findViewById(R.id.adoptionsList);
        BackgroundTaskGetAdoptions b = new BackgroundTaskGetAdoptions(this);
        b.execute("getAdoptions");
    }


    public class BackgroundTaskGetAdoptions extends AsyncTask<String,Void,String> {

        AlertDialog alertDialog;
        Context ctx;

        BackgroundTaskGetAdoptions(Context ctx){
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
            String postRequest_url = Constants.my_ip + "getAdoptions.php";
            String response = "";
            try {
                URL url = new URL(postRequest_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    response += line;
                }
                bufferedReader.close();
                inputStream.close();
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
                if(serverMsg.equals("adoptions")) {
                    Integer arraySize = Integer.parseInt(st.nextToken());
                    String str = st.nextToken();
                    String[] strReviews =new String[arraySize];
                    Post[] adoptions =  new Post[arraySize];
                    strReviews = initPosts(str,arraySize);
                    adoptions = createPosts(strReviews);
                    createCustomAdapter(adoptions);
                }

            }
        }
    }

    protected void createCustomAdapter(Post[] posts) {
        CustomAdapter ca = new CustomAdapter(this,posts,"adoptions");
        ListView postsList = (ListView) findViewById(R.id.adoptionsList);
        postsList.setAdapter(ca);
    }


    public String[] initPosts(String s, Integer arraySize) {
        StringTokenizer st = new StringTokenizer(s,"`$");
        String[] posts = new String[arraySize*4];

        for(int i = 0; st.hasMoreTokens()&&i<arraySize*4; i++){
            posts[i]= st.nextToken();
        }
        return posts;
    }


    private Post[] createPosts(String[] strPost) {
        Post[] retPost = new Post[(strPost.length/4)];
        String tempInstitution,tempDogType;
        int j = 0;
        for(int i = 0; i < strPost.length -3; i+=4){
            tempInstitution = strPost[i];
            tempDogType = strPost[i+1]+"`#"+strPost[i+2]+"`#"+strPost[i+3];
            retPost[j] = new Post(tempInstitution,tempDogType);
            j++;
        }
        return retPost;
    }

}