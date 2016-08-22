package com.hfad.watchdog_client;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.nostra13.universalimageloader.utils.L;

import java.util.LinkedList;
import java.util.StringTokenizer;


public class CustomAdapter extends ArrayAdapter<Post> {
    private Boolean isPosts = true;
    private Boolean isBen = false;
    private Boolean isReview = false;
    private Boolean isAdoption = false;
    private Boolean isMsg = false;
    public View fCustomView = null;
    public CustomAdapter(Context context, Post[] posts, String msg) {
        super(context,R.layout.main_page_post_row,posts);
        if (msg.equals("reviews")){
            isReview = true;
            isPosts = false;
        }
        if (msg.equals("benefit")){
            isBen = true;
            isPosts = false;
        }

        if (msg.equals("adoptions")){
            isAdoption = true;
            isPosts = false;
        }
        if(msg.equals("messages")){
            isPosts = false;
            isMsg = true;
        }

    }



    private boolean isTooLarge (TextView text, String newText) {
        float textWidth = text.getPaint().measureText(newText);
        return (textWidth >= text.getMeasuredWidth ());
    }

    private void setStringInTextView(TextView text, String post){
        StringTokenizer st = new StringTokenizer(post," ");
        LinkedList<String> ll = new LinkedList<String>();
        LinkedList<String> insertionList = new LinkedList<String>();
        while(st.hasMoreTokens()){
            ll.add(st.nextToken());
        }
        String tempString = "";
        int line = 1;
        while(!ll.isEmpty()){
            if(isTooLarge(text,tempString+ll.peekFirst())){
                tempString = tempString+" "+ll.pollFirst();
            }
            else{
                insertionList.add(tempString);
                line++;
                tempString ="";
            }
        }
        while(!insertionList.isEmpty()){
            tempString = insertionList.getFirst()+"\n";
        }
        text.setText(tempString);
    }

    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater li = LayoutInflater.from(getContext());
        if(isPosts){
            fCustomView = li.inflate(R.layout.main_page_post_row, parent, false);
            Post singlePost = getItem(position);
            String post = singlePost.fPost;
            TextView TV_post = (TextView) fCustomView.findViewById(R.id.MainPagePostRowPost);
            Button b1 = (Button) fCustomView.findViewById(R.id.MainPagePostRowPostButtom);
            b1.setText(singlePost.fUsername);
           // setStringInTextView(TV_post,post);
            TV_post.setText(post);
        }
        else if(isBen){
            fCustomView = li.inflate(R.layout.get_ben_row, parent, false);
            Post singlePost = getItem(position);
            String post = singlePost.fPost;
            TextView TV_publisher = (TextView) fCustomView.findViewById(R.id.BenefitPublisher);
            TextView TV_content = (TextView) fCustomView.findViewById(R.id.BenefitContent);
            TV_publisher.setText(post);
            TV_content.setText(singlePost.fUsername);
        }
        else if(isReview){
            fCustomView = li.inflate(R.layout.user_page_review_row, parent, false);
            Review singlePost = (Review) getItem(position);
            String post = singlePost.fPost;
            TextView TV_post = (TextView) fCustomView.findViewById(R.id.UserPageReview);
            Button b1 = (Button) fCustomView.findViewById(R.id.UserPageRowButtom);
            TextView TV_rating = (TextView) fCustomView.findViewById(R.id.ratingView);
            TV_post.setText(post);
            b1.setText(singlePost.fUsername);
            TV_rating.setText(singlePost.fRating);

        }
        else if(isAdoption){
            fCustomView = li.inflate(R.layout.adoption_row, parent, false);
            Post singlePost = (Post) getItem(position);
            TextView TV_institution = (TextView) fCustomView.findViewById(R.id.AdoptionInstitution);
            TextView TV_dog_type = (TextView) fCustomView.findViewById(R.id.AdoptionDogType);
            TextView TV_dog_age = (TextView) fCustomView.findViewById(R.id.AdoptionDogAge);
            TextView TV_location = (TextView) fCustomView.findViewById(R.id.AdoptionInstitutionLocation);
            StringTokenizer st = new StringTokenizer(singlePost.fPost,"`#");
            TV_institution.setText(singlePost.fUsername);
            TV_dog_type.setText(st.nextToken());
            TV_dog_age.setText(st.nextToken());
            TV_location.setText(st.nextToken());

        }
        else{
            fCustomView = li.inflate(R.layout.msg_row, parent, false);
            Post singlePost = getItem(position);
            TextView TV_msg = (TextView) fCustomView.findViewById(R.id.MsgContent);
            TextView TV_date = (TextView) fCustomView.findViewById(R.id.MsgDate);
            Button b1 = (Button) fCustomView.findViewById(R.id.MsgButtom);
            b1.setText(singlePost.fUsername);
            StringTokenizer st = new StringTokenizer(singlePost.fPost,"`#");
            TV_msg.setText(st.nextToken());
            TV_date.setText(st.nextToken());
        }
        return fCustomView;
    }
}

