package com.hfad.watchdog_client;

import java.io.Serializable;
import java.util.StringTokenizer;

/**
 * Created by Hertz on 20/12/2015.
 */
public class UserClient implements Serializable{

    public String fUserName;
    public String fEmail;
    public String fDogName;
    public String fFirstName;
    public String fLastName;

    public UserClient(String s){
        StringTokenizer st = new StringTokenizer(s,"`#");
        String[] tokens = new String[5];
        int i  = 0;
        while(st.hasMoreTokens()){
            if(i == 0) fUserName = st.nextToken();
            if(i == 1) fEmail = st.nextToken();
            if(i == 2 ) fDogName = st.nextToken();
            if(i == 3 ) fFirstName = st.nextToken();
            if(i == 4 ) fLastName = st.nextToken();
            i++;
        }

    }

}
