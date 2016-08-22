package com.hfad.watchdog_client;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class ReviewPage extends Activity {

    public UserClient fReviewer;
    public UserClient fReviewee;
    public String fReviewRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_page);
        Intent intent = getIntent();
        fReviewer = (UserClient) intent.getSerializableExtra("Reviewer");
        fReviewee = (UserClient) intent.getSerializableExtra("Reviewee");
        fReviewRating = "0";

        final RadioButton buttonOne = (RadioButton) findViewById(R.id.ratingOne);
        final RadioButton buttonTwo = (RadioButton) findViewById(R.id.ratingTwo);
        final RadioButton buttonThree = (RadioButton) findViewById(R.id.ratingThree);
        final RadioButton buttonFour = (RadioButton) findViewById(R.id.ratingFour);
        final RadioButton buttonFive = (RadioButton) findViewById(R.id.ratingFive);
        final RadioGroup radioGroup = (RadioGroup) findViewById(R.id.myRadioGroup);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.ratingOne) {
                    fReviewRating = "1";
                } else if(checkedId == R.id.ratingTwo) {
                    fReviewRating = "2";
                } else if(checkedId == R.id.ratingThree) {
                    fReviewRating = "3";
                } else if(checkedId == R.id.ratingFour) {
                    fReviewRating = "4";
                } else if(checkedId == R.id.ratingFive){
                    fReviewRating = "5";
                } else fReviewRating = "0";
            }
        });
    }


    public void addReview(View view){
        EditText ET_postContant = (EditText) findViewById(R.id.reviewContant);
        String postContant = ET_postContant.getText().toString();
        BackgroundTaskAddReview bt = new BackgroundTaskAddReview(this);
        bt.execute(postContant, fReviewer.fUserName, fReviewee.fUserName, fReviewRating);
        finish();
    }



    public class BackgroundTaskAddReview extends AsyncTask<String,Void,String> {

        AlertDialog alertDialog;
        Context ctx;

        BackgroundTaskAddReview(Context ctx){
            this.ctx = ctx;

        }

        protected void onPreExecute(){
            alertDialog = new AlertDialog.Builder(ctx).create();
            alertDialog.setTitle("Login Information");
        }
        @Override
        protected String doInBackground(String... params) {
            return addReview(params);
        }



        protected  String addReview(String... params){
            String reg_url = Constants.my_ip+"addReview.php";
            String reviewMsg = params[0];
            String reviewer  = params[1];
            String reviewee  = params[2];
            String reviewRating = params[3];
            try {
                URL url = new URL(reg_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(OS, "UTF-8");
                BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                String data = URLEncoder.encode("reviewer", "UTF-8") + "=" + URLEncoder.encode(reviewer, "UTF-8") + "&" +
                        URLEncoder.encode("reviewee", "UTF-8") + "=" + URLEncoder.encode(reviewee, "UTF-8") + "&" +
                        URLEncoder.encode("reviewMsg", "UTF-8") + "=" + URLEncoder.encode(reviewMsg, "UTF-8")+ "&" +
                        URLEncoder.encode("reviewRating", "UTF-8") + "=" + URLEncoder.encode(reviewRating, "UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();
                InputStream IS = httpURLConnection.getInputStream();
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
                Toast.makeText(ctx, "Review posted!", Toast.LENGTH_LONG).show();
            }
            else{

                alertDialog.setMessage(result);
                alertDialog.show();
            }
        }
    }
}
