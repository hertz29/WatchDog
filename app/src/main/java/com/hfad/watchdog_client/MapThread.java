package com.hfad.watchdog_client;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

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

/**
 * Created by Hertz on 07/05/2016.
 */
public class MapThread implements Runnable {

    public LatLng fMarkerPos;
    public String fUsername;

    public MapThread() {

    }

    public void setUsername(String username){
        fUsername = username;
    }

    public void setLatLng(LatLng pos){
        fMarkerPos = pos;
    }

    @Override
    public void run() {
        String login_url = Constants.my_ip+"addMarker.php";
        try {
            URL url = new URL(login_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            OutputStream OS = httpURLConnection.getOutputStream();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(OS, "UTF-8");
            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
            String data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(fUsername, "UTF-8") + "&" +
                    URLEncoder.encode("lat", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(fMarkerPos.latitude), "UTF-8")  + "&" +
                    URLEncoder.encode("lng", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(fMarkerPos.longitude), "UTF-8");
            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();
            OS.close();
            InputStream IS = httpURLConnection.getInputStream();
            httpURLConnection.disconnect();
            IS.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}


