package com.hfad.watchdog_client;


import android.*;
import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.BubbleIconFactory;

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
import java.util.ArrayList;
import java.util.StringTokenizer;

public class MainNavigation extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    static int MARKERS_RADIUS = 2;
    static int TIMEINTERVAL = 2;
    public static String fUsernameDetails;
    public UserClient fUserClient = null;
    private DrawerLayout mDrawer;
    Button btnShowLocation;
    GPSTracker gps;
    private GoogleMap googleMap;
    private double fLat = -1;
    private double fLng = -1;
    private Button makeAcall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_navigation);
        // init userClient details
        String data = getIntent().getExtras().getString("details", fUsernameDetails);
        fUsernameDetails = data;
        fUserClient = new UserClient(fUsernameDetails);
        //init Drawer settings (include toolbar)
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("WatchDog");
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        //init NavigationView
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        setUpDrowerContent(navigationView);
        //init my location button
        btnShowLocation = (Button) findViewById(R.id.btnShowLocation);
        btnShowLocation.setText("Share Location");
        // show location button click event
        btnShowLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // create class object
                gps = new GPSTracker(MainNavigation.this);
                // check if GPS enabled
                if (fLng != -1 && fLng != -1) {
                    final LatLng myPos = new LatLng(fLat, fLng);
                    BackgroundTaskaddMarker bt = new BackgroundTaskaddMarker(MainNavigation.this);
                    bt.execute(fUserClient.fUserName, String.valueOf(fLat), String.valueOf(fLng));
                    googleMap.addMarker(new MarkerOptions().position(myPos).draggable(false));
                } else {
                    gps.showSettingsAlert();
                }
            }
        });
        //init google map
        try {
            if (googleMap == null) {
                SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                googleMap = fm.getMap();
               /*googleMap = ((MapFragment) getFragmentManager().
                        findFragmentById(R.id.map)).getMap();*/
            }
            googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        } catch (Exception e) {
            e.printStackTrace();
        }
        getUserLocation();
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(fLat, fLng), 18));
        BackgroundTaskGetMarker bt = new BackgroundTaskGetMarker(MainNavigation.this);
        bt.execute(String.valueOf(fLat), String.valueOf(fLng));

        makeAcall = (Button) findViewById(R.id.makeAcall);
        makeAcall.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:0545645856"));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for Activity#requestPermissions for more details.
                        return;
                    }
                }
                startActivity(callIntent);

            }
        });
    }

    private void getUserLocation() {
        gps = new GPSTracker(MainNavigation.this);
        // check if GPS enabled
        if (gps.canGetLocation()) {
            fLat = gps.getLatitude();
            fLng = gps.getLongitude();
        } else {
            gps.showSettingsAlert();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camara) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {


        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void setUpDrowerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the planet to show based on
        // position
        Intent intent;
        switch (menuItem.getItemId()) {
            case R.id.nav_requests:
                intent = new Intent(this, MainPage.class);
                intent.putExtra("UserClient", fUserClient);
                startActivity(intent);
                break;
            case R.id.nav_adoption:
                intent = new Intent(this, AdoptionPage.class);
                startActivity(intent);
                break;
            case R.id.nav_benefits:
                intent = new Intent(this, Benefits.class);
                startActivity(intent);
                break;
            case R.id.nav_postRequest:
                intent = new Intent(this, PostPage.class);
                intent.putExtra("UserClient", fUserClient);
                startActivity(intent);
                break;
            case R.id.nav_profile:
                intent = new Intent(this, UserPage.class);
                intent.putExtra("UserClient", fUserClient);
                intent.putExtra("Extra_Info", true);
                startActivity(intent);
                break;
            case R.id.nav_msg:
                intent = new Intent(this, Msg.class);
                intent.putExtra("UserClient", fUserClient);
                startActivity(intent);
                break;

            default:

        }


        // Highlight the selected item, update the title, and close the drawer
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());

        mDrawer.closeDrawers();
    }


    public class BackgroundTaskaddMarker extends AsyncTask<String, Void, String> {

        AlertDialog alertDialog;
        Context ctx;

        BackgroundTaskaddMarker(Context ctx) {
            this.ctx = ctx;

        }

        protected void onPreExecute() {
            alertDialog = new AlertDialog.Builder(ctx).create();
            alertDialog.setTitle("Login Information");
        }

        @Override

        protected String doInBackground(String... params) {
            String login_url = Constants.my_ip + "addMarker.php";
            String username = params[0];
            String lat = params[1];
            String lng = params[2];
            try {
                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(OS, "UTF-8");
                BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                String data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8") + "&" +
                        URLEncoder.encode("lat", "UTF-8") + "=" + URLEncoder.encode(lat, "UTF-8") + "&" +
                        URLEncoder.encode("lng", "UTF-8") + "=" + URLEncoder.encode(lng, "UTF-8");
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
            return "good";
        }


        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
        }

    }

    public Double getDistanceFromLatLonInKm(String lat1, String lon1, String lat2, String lon2) {
        int R = 6371; // Radius of the earth in km
        Double dLat = deg2rad(Double.parseDouble(lat2) - Double.parseDouble(lat1));  // deg2rad below
        Double dLon = deg2rad(Double.parseDouble(lon2) - Double.parseDouble(lon1));
        Double a =
                Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                        Math.cos(deg2rad(Double.parseDouble(lat1)))
                                * Math.cos(deg2rad(Double.parseDouble(lat2))) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        Double d = R * c; // Distance in km
        return d;
    }

    public Double deg2rad(double deg) {
        return deg * (Math.PI / 180);
    }

    public class BackgroundTaskGetMarker extends AsyncTask<String, Void, String> {

        AlertDialog alertDialog;
        Context ctx;

        BackgroundTaskGetMarker(Context ctx) {
            this.ctx = ctx;

        }

        protected void onPreExecute() {
            alertDialog = new AlertDialog.Builder(ctx).create();
            alertDialog.setTitle("Login Information");
        }

        @Override

        protected String doInBackground(String... params) {
            String login_url = Constants.my_ip + "getMarker.php";
            String username = fUserClient.fUserName;
            String lat = params[0];
            String lng = params[1];
            try {
                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(OS, "UTF-8");
                BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                String data =
                        URLEncoder.encode("lat", "UTF-8") + "=" + URLEncoder.encode(lat, "UTF-8") + "&" +
                                URLEncoder.encode("lng", "UTF-8") + "=" + URLEncoder.encode(lng, "UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String response = "";
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    response += line;
                }

                StringTokenizer markTokenazier = new StringTokenizer(response, "`#");
                String ret = "";
                boolean flag = true;
                while (markTokenazier.hasMoreTokens() && flag) {
                    String mark = markTokenazier.nextToken();
                    StringTokenizer st = new StringTokenizer(mark, "&");
                    String lat1 = st.nextToken();
                    String lng1 = st.nextToken();
                    String username1 = st.nextToken();
                    String currDate = st.nextToken();
                    String markDate = st.nextToken();
                    Double distance = getDistanceFromLatLonInKm(lat, lng, lat1, lng1);
                    if (dateQuanta(currDate, markDate)) {
                        if (distance <= 2) ret += lat1 + "-" + lng1 + "-" + username1 + "@";
                    } else {
                        flag = false;
                    }
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return ret;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "good";
        }


        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            // (--- lan-lng-username)*

            StringTokenizer major = new StringTokenizer(result, "@");
            while (major.hasMoreTokens()) {
                StringTokenizer mainor = new StringTokenizer(major.nextToken(), "-");
                String newLan = mainor.nextToken();
                String newLng = mainor.nextToken();
                String username = mainor.nextToken();
                //BubbleIconFactory mBubbleFactory = new BubbleIconFactory();
                LatLng tempPos = new LatLng(Double.parseDouble(newLan), Double.parseDouble(newLng));
                googleMap.addMarker(new MarkerOptions().position(tempPos).draggable(false));

            }
        }

    }

    private boolean dateQuanta(String curr, String mark) {
        StringTokenizer currSpaceTokenizer = new StringTokenizer(curr, " ");
        StringTokenizer markSpaceTokenizer = new StringTokenizer(mark, " ");
        String currDate = currSpaceTokenizer.nextToken();
        String currtime = currSpaceTokenizer.nextToken();
        String markDate = markSpaceTokenizer.nextToken();
        String marktime = markSpaceTokenizer.nextToken();
        StringTokenizer currTimeTokenizer = new StringTokenizer(currtime, ":");
        StringTokenizer markTimeTokenizer = new StringTokenizer(marktime, ":");
        StringTokenizer currDateTokenizer = new StringTokenizer(currDate, "-");
        StringTokenizer markDateTokenizer = new StringTokenizer(markDate, "-");
        Integer[] currArr = new Integer[6];
        Integer[] markArr = new Integer[6];
        int i = 0;
        while (i < 6) {
            if (i < 3) {
                currArr[i] = Integer.parseInt(currDateTokenizer.nextToken());
                markArr[i] = Integer.parseInt(markDateTokenizer.nextToken());
            } else {
                currArr[i] = Integer.parseInt(currTimeTokenizer.nextToken());
                markArr[i] = Integer.parseInt(markTimeTokenizer.nextToken());
            }
            i++;
        }
        return compareByHours(currArr, markArr);
    }

    private boolean compareByHours(Integer[] currArr, Integer[] markArr) {
        int markSum = markArr[3] * 60 * 60 + markArr[4] * 60 + markArr[5];
        int currSum = currArr[3] * 60 * 60 + currArr[4] * 60 + currArr[5];
        //same day
        if (timeLimitationSameDate(currArr, markArr))
            return currSum - markSum < TIMEINTERVAL * 60 * 60;
        else if (timeLimitationDiffDate(currArr, markArr)) {
            currSum += 24 * 60 * 60;
            return currSum - markSum < TIMEINTERVAL * 60 * 60;
        }
        return false;
    }

    private boolean timeLimitationSameDate(Integer[] markArr, Integer[] currArr) {
        return (currArr[0].intValue() == markArr[0].intValue() && currArr[1].intValue() == markArr[1].intValue() && currArr[2].intValue() == markArr[2].intValue());

    }

    private boolean timeLimitationDiffDate(Integer[] markArr, Integer[] currArr) {
        return (currArr[0] - markArr[0] == 1 && currArr[1] == 1 && markArr[1] == 12 && currArr[2] == 1 && markArr[2] == 31)
                || (currArr[0].equals(markArr[0]) && currArr[1] - markArr[1] == 1 && currArr[2] == 1 && MonthCompare(markArr))
                || (currArr[0].equals(markArr[0]) && currArr[1].equals(markArr[1]) && currArr[2] - markArr[2] == 1);
    }

    private boolean MonthCompare(Integer[] markArr) {
        if (markArr[1] == 2) {
            return markArr[2] == 28;
        }
        if (markArr[1] == 4 || markArr[1] == 6 || markArr[1] == 9 || markArr[1] == 11) {
            return markArr[2] == 30;
        }
        return markArr[2] == 31;
    }


}

