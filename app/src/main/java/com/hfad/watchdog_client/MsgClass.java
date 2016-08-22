package com.hfad.watchdog_client;

/**
 * Created by Hertz on 06/06/2016.
 */
public class MsgClass {
    public String fSender;
    public String fMsg;
    public String fDatenTime;

    public MsgClass(String sender,String msg,String dnt){
        fSender = sender;
        fMsg = msg;
        fDatenTime = dnt;
    }

}
