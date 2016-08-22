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
import android.widget.TextView;

import org.w3c.dom.Text;

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
import java.util.List;
import java.util.StringTokenizer;
public class UserPage extends Activity {

    public UserClient fUserClient;
    public UserClient fVisitUser = null;
    public boolean fIsUserClientOwnPage;
    private ListView fReviewsListView;
    public TextView fRatingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);
        Intent intent = getIntent();
        fUserClient = (UserClient)intent.getSerializableExtra("UserClient");
        fIsUserClientOwnPage = getIntent().getExtras().getBoolean("Extra_Info");
        if (!fIsUserClientOwnPage) {
            fVisitUser = (UserClient)intent.getSerializableExtra("VisitUser");
        }
        else {fVisitUser = fUserClient; }
        onCreateHelper();

        BackgroundTaskGetReviews bt = new BackgroundTaskGetReviews(this);
        bt.execute("getReviews",fUserClient.fUserName);
        fReviewsListView =(ListView) findViewById(R.id.userPageReviewList);

    }
    /*
    onCreateHelper init the TextViews according to fUserClient
    the method set the UserPageButton according to the visited premissions
     */
    private void onCreateHelper(){
        TextView TV_displayUsername = (TextView) findViewById(R.id.UserPageUsername);
        TextView TV_displayDogName = (TextView) findViewById(R.id.UserPageDogName);
        TextView TV_displayEmail = (TextView) findViewById(R.id.UserPageEmail);
        Button sendMsg = (Button) findViewById(R.id.UserPageSendMsg);
        //maybe problematic
        if(fIsUserClientOwnPage) sendMsg.invalidate();

        Button serviceButtom = (Button) findViewById(R.id.UserPageEditReview);
        TV_displayUsername.setText(fUserClient.fUserName);
        StringBuilder buildDogName = new StringBuilder();
        buildDogName.append(" dog name: ").append(fUserClient.fDogName);
        TV_displayDogName.setText(buildDogName);
        StringBuilder buildEmail = new StringBuilder();
        buildEmail.append(" Email: ").append(fUserClient.fEmail);
        TV_displayEmail.setText(buildEmail);
        setButtonDefinitions(serviceButtom);
    }

    protected void onResume(){
        super.onResume();
        this.onCreate(null);
    }

    private void setButtonDefinitions(Button serviceButtom) {
        if (fIsUserClientOwnPage) {
            serviceButtom.setText("Edit Profile");
        }else{
            serviceButtom.setText("Post Review");
        }
    }

    public void buttomMethod(View view){
        if(fIsUserClientOwnPage){
            Intent intent = new Intent(this,UpdateUserProfile.class);
            intent.putExtra("UserClient", fUserClient);
            startActivity(intent);
        }
        else{
            Intent intent = new Intent(this,ReviewPage.class);
            intent.putExtra("Reviewer", fVisitUser);
            intent.putExtra("Reviewee", fUserClient);
            startActivity(intent);
        }
    }


    public void GoToUserButtom(View view){
        Button B_username  = (Button) view;
        String postUsername = B_username.getText().toString();
        BackgroundTaskGetReviews bt = new BackgroundTaskGetReviews(this);
        bt.execute("getUser", postUsername);
    }

    public void sendMsg(View view){
        Intent intent = new Intent(this,SendMsg.class);
        intent.putExtra("Sender", fVisitUser);
        intent.putExtra("Reciever", fUserClient);
        startActivity(intent);
    }

    public class BackgroundTaskGetReviews extends AsyncTask<String,Void,String> {

        AlertDialog alertDialog;
        Context ctx;

        BackgroundTaskGetReviews(Context ctx){
            this.ctx = ctx;

        }

        protected void onPreExecute(){
            alertDialog = new AlertDialog.Builder(ctx).create();
            alertDialog.setTitle("Login Information");
        }

        protected String doInBackground(String... params) {
            String method = params[0];

            if(method.equals("getReviews")){
                String reviewee = params[1];
                return getReviews(reviewee);
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

        protected String getReviews(String reviewee){
            String postRequest_url = Constants.my_ip+"userPageGetReviews.php";
            try {
                URL url = new URL(postRequest_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));
                String data = URLEncoder.encode("reviewee", "UTF-8") + "=" + URLEncoder.encode(reviewee, "UTF-8");
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
                if(serverMsg.equals("Reviews")) {
                    Integer arraySize = Integer.parseInt(st.nextToken());
                    String str = st.nextToken();
                    String[] strReviews =new String[arraySize];
                    Review[] reviews =  new Review[arraySize];
                    strReviews = initPosts(str,arraySize);
                    reviews = createPosts(strReviews);
                    createCustomAdapter(reviews);
                }
                else if(serverMsg.equals("USER")){
                    String str = st.nextToken();
                    UserClient postUser = new UserClient(str);
                    Intent intent = new Intent(this.ctx,UserPage.class);
                    intent.putExtra("UserClient", postUser);
                    if(postUser.fUserName.equals(fVisitUser.fUserName)){
                        intent.putExtra("Extra_Info" , true);
                    }
                    else{
                        intent.putExtra("Extra_Info" , false);
                        intent.putExtra("VisitUser", fVisitUser);
                    }

                    startActivity(intent);
                }
                else{
                    //alertDialog.setMessage("Fatal Error");
                    //alertDialog.show();
                }
            }
        }
    }

    protected void createCustomAdapter(Post[] posts) {
        CustomAdapter ca = new CustomAdapter(this,posts,"reviews");
        ListView postsList = (ListView) findViewById(R.id.userPageReviewList);
        postsList.setAdapter(ca);
    }


    public String[] initPosts(String s, Integer arraySize) {
        StringTokenizer st = new StringTokenizer(s,";");
        String[] posts = new String[arraySize];

        for(int i = 0; st.hasMoreTokens()&&i<arraySize; i++){
            posts[i]= st.nextToken();
        }
        return posts;
    }


    private Review[] createPosts(String[] strPost) {
        Review[] retPost = new Review[strPost.length];
        String tempUsername,tempPost,tempRating;
        Integer ratingCounter = 0;

        for(int i = 0; i < strPost.length ; i++){
            StringTokenizer st = new StringTokenizer(strPost[i],"`#");
            tempUsername = st.nextToken();
            tempPost = st.nextToken();
            tempRating = st.nextToken();
            ratingCounter+=Integer.parseInt(tempRating);
            retPost[i] = new Review(tempUsername,tempPost,tempRating);

        }
        Double avgRating = Double.valueOf(ratingCounter/strPost.length);
        fRatingButton = (TextView)findViewById(R.id.ratingAvgView);
        fRatingButton.setText(avgRating.toString());
        return retPost;
    }


}
