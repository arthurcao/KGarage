package com.kooltech.droid.kgarage;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.kooltech.droid.kgarage.MyGarage.ItemDoor;
import com.kooltech.droid.kgarage.MyGarage.MyWebsocketService;
import com.kooltech.droid.kgarage.MyGarage.SlidingTabLayout;
import com.kooltech.droid.kgarage.MyGarage.ViewPagerFragment;
import com.kooltech.droid.kgarage.fragment.AuthorizationsFragment;
import com.kooltech.droid.kgarage.fragment.LoginFragment;
import com.kooltech.droid.kgarage.fragment.RequestsFragment;
import com.kooltech.droid.kgarage.object.ItemAuthorizations;
import com.kooltech.droid.kgarage.object.ItemRequest;
import com.kooltech.droid.kgarage.utils.HttpPostToServer;
import com.kooltech.droid.kgarage.utils.HttpRequestData;
import com.kooltech.droid.kgarage.utils.HttpRequestListener;
import com.kooltech.droid.kgarage.utils.MYLOG;
import com.facebook.FacebookSdk;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

//http://kooltechs.com/wiki/doku.php/garage;protocol
public class MainActivity extends ActionBarActivity implements ActionBar.TabListener, HttpRequestListener {
    private CharSequence mTitle;
    public static FragmentManager fragmentManager = null;
    public static final HttpClient httpclient = new DefaultHttpClient();
    public static Context mContext;

    public static ViewPagerFragment mViewPagerFragment;

    public static ProgressDialog mProgressDialog = null;

    private ActionBar actionBar;
    // Tab titles

    private final int SET_AUTO_CLOSE = 1;


    private final int FRAGMENT_DOOR = 1;
    private final int FRAGMENT_REQUEST = 2;
    private final int FRAGMENT_AUTHORIZATIONS = 3;
    private final int FRAGMENT_MORE = 4;
    private int mCurrentFagment = 0;

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager = getSupportFragmentManager();
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(com.kooltech.droid.kgarage.R.layout.activity_main);
        mContext = this;
        if (mViewPagerFragment == null) {
            mViewPagerFragment = new ViewPagerFragment();
        }

        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.kooltech.droid.kgarage", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("KeyHash:", "NameNotFoundException: " + e.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("KeyHash:", "NoSuchAlgorithmException: " + e.toString());
        }


        RegistrationIntentService.startRegisterGCM(this);

        mTitle = "Login";
        restoreActionBar();
//        mTitle = getTitle();
        startService(new Intent(this, MyWebsocketService.class));

        actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(false);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
//        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;

        mSlidingTabLayout = (SlidingTabLayout) findViewById(com.kooltech.droid.kgarage.R.id.tabs);
        mSlidingTabLayout.setWidthScreen(width);
        ArrayList<String> mListTitle = new ArrayList<>();
        mListTitle.add("Doors");
        mListTitle.add("Requests");
        mListTitle.add("Authorizations");
        mListTitle.add("More...");
        mSlidingTabLayout.setListTabs(mListTitle);
        mSlidingTabLayout.setOnListener(new SlidingTabLayout.TabLayoutListener() {

            @Override
            public void onSelectedItem(int item) {
                updateFragment(item);
            }
        });
//        MySharedPreferences.saveLoginStatus(mContext, false);
        MySharedPreferences.saveAutoLogin(mContext,true);
        updateFragment(3);

        if (!haveNetworkConnection()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage("Network fail.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        }
    }

    //    private ArrayList<String> mListTitle;
    SlidingTabLayout mSlidingTabLayout;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SET_AUTO_CLOSE && resultCode == Activity.RESULT_OK) {
            if (mProgressDialog != null && mProgressDialog.isIndeterminate())
                mProgressDialog.dismiss();
            Log.e("test", "onActivityResult");
            mProgressDialog = ProgressDialog.show(mContext, null, "Waiting...", false, true);
            ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(0);
            HttpRequestData httpRequestData = new HttpRequestData(HttpRequestData.REQUEST_LOAD_DOOR, "http://kooltechs.com/garage/getDoor", nameValuePair);
            new HttpPostToServer(mContext).execute(httpRequestData);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        resumeReceiver();
        super.onResume();
    }

    @Override
    protected void onPause() {
        stopReceiver();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, MyWebsocketService.class));
//        MySharedPreferences.saveLoginStatus(this, false);
        super.onDestroy();
    }


    private int mTabs = -1;

    private Fragment getCurrentFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        int count = fragmentManager.getBackStackEntryCount();


        String fragmentTag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
        if (fragmentTag != null) {
            Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(fragmentTag);
            return currentFragment;
        }
        return null;
    }

    public void updateFragment(int position) {

        mSlidingTabLayout.selectTab(position);
//        if(mTabs == position){
//            return;
//        }

        mTabs = position;


        switch (position) {
            case 0:
                mCurrentFagment = FRAGMENT_DOOR;
                mTitle = "My doors";
                if (mViewPagerFragment == null) {
                    mViewPagerFragment = new ViewPagerFragment();
                }
                fragmentManager.beginTransaction().replace(com.kooltech.droid.kgarage.R.id.container, mViewPagerFragment)
                        .commit();

                break;
            case 1:
                mCurrentFagment = FRAGMENT_REQUEST;
                mTitle = "My requests";
                fragmentManager.beginTransaction()
                        .replace(com.kooltech.droid.kgarage.R.id.container, RequestsFragment.newInstance(position + 1))
                        .commit();
                break;
            case 2:
                mCurrentFagment = FRAGMENT_AUTHORIZATIONS;
                mTitle = "My authorizations";
                fragmentManager.beginTransaction()
                        .replace(com.kooltech.droid.kgarage.R.id.container, AuthorizationsFragment.newInstance(position + 1))
                        .commit();
                break;
            case 3:
                mCurrentFagment = FRAGMENT_MORE;
                mTitle = "More...";
                fragmentManager.beginTransaction()
                        .replace(com.kooltech.droid.kgarage.R.id.container, LoginFragment.newInstance(position + 1))
                        .commit();
//               boolean login =  MySharedPreferences.loadLoginStatus(mContext);
//                if(login){
//                    fragmentManager.beginTransaction()
//                            .replace(R.id.container, LogoutFragment.newInstance())
//                            .commit();
//                }else {
//                    fragmentManager.beginTransaction()
//                            .replace(R.id.container, LoginFragment.newInstance(position + 1))
//                            .commit();
//                }
                break;
            default: {
                mTitle = "Login";
                if (mViewPagerFragment == null) {
                    mViewPagerFragment = new ViewPagerFragment();
                }
                fragmentManager.beginTransaction()
                        .replace(com.kooltech.droid.kgarage.R.id.container, mViewPagerFragment)
                        .commit();
            }

        }
        invalidateOptionsMenu();
        restoreActionBar();
    }

//

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(com.kooltech.droid.kgarage.R.menu.menu_main, menu);

        if (mCurrentFagment == FRAGMENT_DOOR) {
            menu.findItem(com.kooltech.droid.kgarage.R.id.menu_main_set).setVisible(true);
            menu.findItem(com.kooltech.droid.kgarage.R.id.menu_main_add).setVisible(false);
        } else if (mCurrentFagment == FRAGMENT_REQUEST) {
            menu.findItem(com.kooltech.droid.kgarage.R.id.menu_main_set).setVisible(false);
            menu.findItem(com.kooltech.droid.kgarage.R.id.menu_main_add).setVisible(true);
        } else {
            menu.findItem(com.kooltech.droid.kgarage.R.id.menu_main_set).setVisible(false);
            menu.findItem(com.kooltech.droid.kgarage.R.id.menu_main_add).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == com.kooltech.droid.kgarage.R.id.menu_main_set) {
            if (mViewPagerFragment != null) {

                ItemDoor door = mViewPagerFragment.getCurrentDoor();
                if (door != null) {
                    Intent i = new Intent(mContext, DoorAutoCloseSetupActivity.class);
                    i.putExtra("DOORID", door.door_id);
                    i.putExtra("DOOR", door);
                    startActivityForResult(i, SET_AUTO_CLOSE);
                }

            }
            return true;
        } else if (id == com.kooltech.droid.kgarage.R.id.menu_main_add) {
            return false;
        }

        return super.onOptionsItemSelected(item);
    }




    //-------------------------
    private void resumeReceiver() {

        if (mGattUpdateReceiver == null) {
            mGattUpdateReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (SHOW_DIALOG.equals(action)) {
                        Log.e("test", "resumeReceiver");
                        if (mProgressDialog != null && mProgressDialog.isIndeterminate())
                            mProgressDialog.dismiss();
                        mProgressDialog = ProgressDialog.show(context, null, "Waiting...", false, true);
                    } else if (MyWebsocketService.RECEIVE_COMMAND_1.equals(action)) {
                        MYLOG.getInstance().LOG("onReceive", "RECEIVE_COMMAND_1 ");
                        int door_id = intent.getIntExtra("door_id", -1);
                        int door_status = intent.getIntExtra("door_status", -1);
                        if (door_id > 0) {
                            MYLOG.getInstance().LOG("onReceive", "mViewPagerFragment: " + mViewPagerFragment);
                            if (mViewPagerFragment != null) {
                                mViewPagerFragment.updateDoorStatus(String.valueOf(door_id), door_status);
//                               mViewPagerFragment.updateBattery(String.valueOf(door_id),door_battery);
                            }
                        }

                    } else if (MyWebsocketService.RECEIVE_COMMAND_2.equals(action)) {
                        int control_id = intent.getIntExtra("control_id", -1);
                        int door_id = intent.getIntExtra("door_id", -1);
                        int control_status = intent.getIntExtra("control_status", -1);

                        MYLOG.getInstance().LOG("onReceive", "mViewPagerFragment: " + mViewPagerFragment);
                        mViewPagerFragment.updateDoorStatus(String.valueOf(door_id), control_status);
                        if (mProgressDialog != null && mProgressDialog.isIndeterminate())
                            mProgressDialog.dismiss();

                    } else if (MyWebsocketService.RECEIVE_COMMAND_4.equals(action)) {
                        int door_id = intent.getIntExtra("door_id", -1);
                        int door_battery = intent.getIntExtra("door_battery", -1);

                        if (door_id > 0) {//
                            MYLOG.getInstance().LOG("onReceive", "mViewPagerFragment: " + mViewPagerFragment);
                            if (mViewPagerFragment != null) {
                                mViewPagerFragment.updateBattery(String.valueOf(door_id), door_battery);
                            }
                        }

                        if (mProgressDialog != null && mProgressDialog.isIndeterminate())
                            mProgressDialog.dismiss();

                    } else if (MyWebsocketService.RECEIVE_DATA_POST_SERVER.equals(action)) {
                        int request = intent.getIntExtra("REQUEST", 1);
                        int command_error = intent.getIntExtra("COMMNAD_ERROR", 1);
                        String message = intent.getStringExtra("MESSAGE");
                        Log.e("test", "resumeReceiver message: " + message);
                        Log.e("test", "resumeReceiver request: " + request);
                        Log.e("test", "resumeReceiver command_error: " + command_error);
                        if (command_error == 0) {
                            //command ok
                            Object json = null;
                            try {

                                json = new JSONTokener(message).nextValue();
                                if (json instanceof JSONObject) {
                                    JSONObject reader = (JSONObject) json;
                                    //TODO FUNCTION BROADCAST
                                    MYLOG.LOG("request: " + request);

                                    MYLOG.getInstance().LOG("test", "resumeReceiver request: " + request);
                                    if (request == HttpRequestData.REQUEST_LOGIN) {
                                        if (mProgressDialog != null && mProgressDialog.isIndeterminate()){
                                            mProgressDialog.dismiss();
                                        }
                                        MYLOG.getInstance().LOG("onReceive", "REQUEST_LOGIN ");
                                        receiveLogin(reader);
                                    } else if (request == HttpRequestData.REQUEST_LOGIN_FACEBOOK) {
                                        Log.e("test", "REQUEST_LOGIN_FACEBOOK message: " + message);
                                        Log.e("test", "REQUEST_LOGIN_FACEBOOK request: " + request);

                                        if (mProgressDialog != null && mProgressDialog.isIndeterminate()){
                                            mProgressDialog.dismiss();
                                        }
                                        MYLOG.getInstance().LOG("onReceive", "REQUEST_LOGIN ");
                                        receiveLogin(reader);
                                    } else if (request == HttpRequestData.REQUEST_LOAD_DOOR) {
                                        Log.e("test", "REQUEST_LOAD_DOOR message: " + message);
                                        Log.e("test", "REQUEST_LOAD_DOOR request: " + request);
                                        MYLOG.getInstance().LOG("onReceive", "REQUEST_LOAD_DOOR ");
                                        if (mProgressDialog != null && mProgressDialog.isIndeterminate())
                                            mProgressDialog.dismiss();
                                        receiveGetDoors(reader);
                                    } else if (request == HttpRequestData.REQUEST_OPEN_DOOR) {
                                        MYLOG.getInstance().LOG("onReceive", "REQUEST_OPEN_DOOR ");
                                        String id = intent.getStringExtra("DOORID");
                                        receiveOpenDoor(id, reader);
                                    } else if (request == HttpRequestData.REQUEST_CLOSE_DOOR) {
                                        MYLOG.getInstance().LOG("onReceive", "REQUEST_CLOSE_DOOR ");
                                        String id = intent.getStringExtra("DOORID");
                                        receiveCloseDoor(id, reader);
                                    } else if (request == HttpRequestData.REQUEST_GET_REQUEST_LIST) {
                                        MYLOG.getInstance().LOG("onReceive", "REQUEST_GET_REQUEST_LIST ");
                                        receiveGetListRequest(reader);
                                    } else if (request == HttpRequestData.REQUEST_GEST_REQUEST) {
                                        MYLOG.getInstance().LOG("onReceive", "REQUEST_GEST_REQUEST ");
                                        receiveGestRequest(reader);
                                    } else if (request == HttpRequestData.REQUEST_SET_REQUEST_RESPONSE) {
                                        MYLOG.getInstance().LOG("onReceive", "REQUEST_SET_REQUEST_RESPONSE ");
                                        if (mProgressDialog != null && mProgressDialog.isIndeterminate()){
                                            mProgressDialog.dismiss();
                                        }
                                        receiveSetRequestResponse(reader);
                                    } else if (request == HttpRequestData.REQUEST_REQUEST_LIST_AUTHORIZATION) {
                                        MYLOG.getInstance().LOG("onReceive", "REQUEST_REQUEST_LIST_AUTHORIZATION ");
                                        receiveGetListAuthorization(reader);
                                    } else if (request == HttpRequestData.REQUEST_DELETE_AUTHORIZATION) {
                                        MYLOG.getInstance().LOG("onReceive", "REQUEST_DELETE_AUTHORIZATION ");
                                        receiveDeleteAuthorization(reader);
                                    } else if (request == HttpRequestData.REQUEST_DELETE_GEST) {
                                        MYLOG.getInstance().LOG("onReceive", "REQUEST_DELETE_GEST ");
                                        receiveDeleteGuest(reader);
                                    } else if (request == HttpRequestData.REQUEST_CONTROL_GUEST) {
                                        MYLOG.getInstance().LOG("onReceive", "REQUEST_CONTROL_GUEST ");
                                        receiveControlGuest(reader);
                                    } else if (request == HttpRequestData.REQUEST_LOGOUT) {
                                        if (mProgressDialog != null && mProgressDialog.isIndeterminate()){
                                            mProgressDialog.dismiss();
                                        }
                                        MYLOG.getInstance().LOG("onReceive", "REQUEST_LOGOUT ");
                                        receiveLogout(reader);
                                    } else if (request == HttpRequestData.REQUEST_AUTO_CLOSE) {
                                        MYLOG.getInstance().LOG("onReceive", "REQUEST_AUTO_CLOSE ");
                                        receiveAutoConfig(reader);
                                    } else if (request == HttpRequestData.REQUEST_CHECK_STATUS) {
                                        MYLOG.getInstance().LOG("onReceive", "REQUEST_CHECK_STATUS ");
//                                        receiveCheckDoorStatus(reader);
                                    }else if (request == HttpRequestData.REQUEST_GUEST_TURN_ON_OFF_NOTIFICATION) {
                                        MYLOG.getInstance().LOG("onReceive", "REQUEST_GUEST_TURN_ON_OFF_NOTIFICATION ");
                                        Log.e("test", "REQUEST_GUEST_TURN_ON_OFF_NOTIFICATION " );
                                        receiveGuestTurnOnOffNotification(reader);
                                    }


                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {
                            if (mProgressDialog != null && mProgressDialog.isIndeterminate())
                                mProgressDialog.dismiss();
                            //network error
                            dialogNetworkError("Network error!");

                        }

                    }
                }
            };
            registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        }
    }

    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //++++++++++++++++++++++++++++FUNCTION DETECTION++++++++++++++++++++++++++++++++++++
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    public void dialogNetworkError(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(message);
        builder.setCancelable(true);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                System.exit(1);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void doShowDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(message);
        builder.setCancelable(true);
        builder.setPositiveButton("OK", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // TODO  FUNCTION RECEIVE
    private void receiveLogin(JSONObject reader) throws JSONException {
        int status = reader.getInt("status");
        if (status == 0) {
            JSONObject user = reader.getJSONObject("user");
//            int user_id = user.getInt("user_id");
            String user_email = user.getString("user_email");
            MySharedPreferences.saveUser(mContext, user_email);
            MySharedPreferences.saveLoginStatus(mContext, true);
            MySharedPreferences.saveAutoLogin(mContext,false);
            updateFragment(0);

        } else {
            dialogNetworkError("Login Fail!");
            updateFragment(3);
        }
    }



    private void receiveGetDoors(JSONObject reader) throws JSONException {
        int status = reader.getInt("status");
        MYLOG.getInstance().LOG("onReceive", "receiveGetDoors status: " + status);
        if (status == 0) {
            //error: No error
            //message:{"status":0,"doors":[{"door_id":"8","door_status":"1","door_battery":"34"},{"door_id":"9","door_status":"0","door_battery":"19"}]}
            JSONArray doors = reader.getJSONArray("doors");
            ArrayList<ItemDoor> list = new ArrayList<>();
            for (int i = 0; i < doors.length(); i++) {
                JSONObject door = new JSONObject(doors.get(i).toString());
                Log.e("20150525", "receiveGetDoors door: " + door);

                MYLOG.getInstance().LOG("onReceive", "receiveGetDoors door: " + door);
                ItemDoor item = new ItemDoor();
                item.door_id = door.getString("door_id");
                item.door_status = door.getInt("door_status");
                item.door_battery = door.getInt("door_battery");
                item.door_auto_begin = door.getString("door_auto_begin");
                item.door_auto_end = door.getString("door_auto_end");
                item.door_auto_enable = door.getString("door_auto_enable");
                item.door_auto_timer = door.getString("door_auto_timer");
                MYLOG.getInstance().LOG("onReceive", "receiveGetDoors door: " + door);
//
                list.add(item);
            }
//            updateFragment(0);
            MYLOG.getInstance().LOG("onReceive", "mViewPagerFragment: " + mViewPagerFragment);
            if (mViewPagerFragment != null) {
                mViewPagerFragment.updateListDoor(list);
            }

        } else {
//            MySharedPreferences.saveLoginStatus(mContext, false);
            //error: Have some error
            dialogNetworkError("Have some error");
            updateFragment(3);
        }
        MYLOG.getInstance().LOG("onReceive", "--------------------------------");
    }


    private void receiveOpenDoor(String id, JSONObject reader) throws JSONException {
        if (mProgressDialog != null && mProgressDialog.isIndeterminate()) mProgressDialog.dismiss();
        int status = reader.getInt("status");
        String error = "";
        MYLOG.getInstance().LOG("onReceive", "status: " + status);
        switch (status) {
            case 0: {
                error = "no error";
//                Fragment fr = getCurrentFragment();
//                if(fr != null) {
//                    if (fr instanceof ViewPagerFragment) {
//                        ViewPagerFragment viewPagerFragment = (ViewPagerFragment) fr;
//                        viewPagerFragment.open(id);
//                    }
//                }
                MYLOG.getInstance().LOG("onReceive", "mViewPagerFragment: " + mViewPagerFragment);
                if (mViewPagerFragment != null) {
                    mViewPagerFragment.open(id);
                } else {

                }
//                if(mProgressDialog!=null &&mProgressDialog.isIndeterminate())mProgressDialog.dismiss();
                break;
            }
            case 1: {
                error = "Error: Login error";
                doShowDialog(error);
//                dialogNetworkError("Login error!");
                updateFragment(3);
                break;
            }
            case 2: {
                error = "Error: hasn't any door";
                doShowDialog(error);
                break;
            }
            case 3: {
                error = "Error: don't authorities";
                doShowDialog(error);
                break;
            }
            case 4: {
                error = "Error: Expire time";
                doShowDialog(error);
                break;
            }
            case 5: {
                error = "Error: door busy";
                doShowDialog(error);
                break;
            }
        }

        MYLOG.getInstance().LOG("onReceive", "error:" + error);
        MYLOG.getInstance().LOG("onReceive", "------------------------------");

    }

    private void receiveCloseDoor(String id, JSONObject reader) throws JSONException {
        if (mProgressDialog != null && mProgressDialog.isIndeterminate()) mProgressDialog.dismiss();
        int status = reader.getInt("status");
        String error = "";
        MYLOG.getInstance().LOG("onReceive", "status: " + status);
        switch (status) {
            case 0: {
                error = "no error";
                MYLOG.getInstance().LOG("onReceive", "mViewPagerFragment: " + mViewPagerFragment);
                if (mViewPagerFragment != null) {
                    mViewPagerFragment.close(id);
                }

                break;
            }
            case 1: {
                error = "Error: login error";
                doShowDialog(error);
//                dialogNetworkError("Login error!");
                updateFragment(3);
                break;
            }
            case 2: {
                error = "Error: hasn't any door";
                doShowDialog(error);
                break;
            }
            case 3: {
                error = "Error: don't authorities";
                doShowDialog(error);
                break;
            }
            case 4: {
                error = "Error: Expire time";
                doShowDialog(error);
                break;
            }
            case 5: {
                error = "Error: door busy";
                doShowDialog(error);
                break;
            }
        }
        MYLOG.getInstance().LOG("onReceive", "error:" + error);
        MYLOG.getInstance().LOG("onReceive", "------------------------------");


    }


    private void receiveGetListRequest(JSONObject reader) throws JSONException {

//
        int status = reader.getInt("status");
        if (status == 0) {
            try {
                JSONArray guests = reader.getJSONArray("requests");
                RequestsFragment.arrItemRequest.clear();
                for (int i = 0; i < guests.length(); i++) {
                    JSONObject guest = new JSONObject(guests.get(i).toString());
                    ItemRequest item = new ItemRequest();
//message: {"status":0,"requests":[{"request_id":"115","request_door":"12","request_guest":"4","request_active":"0","request_endtime":"0","request_time":"2015-06-13 04:18:28","door_status":"1","user_email":"aq1"}]}

                    item.request_id = guest.getString("request_id");
                    item.request_door = guest.getString("request_door");
                    item.request_guest = guest.getString("request_guest");
                    item.request_active = guest.getString("request_active");
                    item.request_endtime = guest.getString("request_endtime");
                    item.request_time = guest.getString("request_time");
                    item.user_email = guest.getString("user_email");
                    item.door_status = guest.getString("door_status");
                    item.request_notify = guest.getString("request_notify");
                    item.request_auth_notify = guest.getString("request_auth_notify");

                    RequestsFragment.arrItemRequest.add(item);
                }
                RequestsFragment.adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            String error = "Error: not login";
            doShowDialog(error);
//            dialogNetworkError("Not Login!");
            updateFragment(3);
        }
    }

    private void receiveGestRequest(JSONObject reader) throws JSONException {
        int status = reader.getInt("status");
        String error = "";
        switch (status) {
            case 0: {
//                ArrayList<NameValuePair> nameValuePair5 = new ArrayList<NameValuePair>(1);
//                nameValuePair5.add(new BasicNameValuePair("masterList", "0"));
//                HttpRequestData httpRequestData5 = new HttpRequestData(4, "http://kooltechs.com/garage/requestsList", nameValuePair5);
//                new HttpPostToServer(mContext).execute(httpRequestData5);
                ArrayList<NameValuePair> nameValuePair = new ArrayList<>(1);
                nameValuePair.add(new BasicNameValuePair("masterList", "0"));
//        HttpRequestData httpRequestData = new HttpRequestData(4, "http://kooltechs.com/garage/requestsList", nameValuePair);
                HttpRequestData httpRequestData = new HttpRequestData(HttpRequestData.REQUEST_GET_REQUEST_LIST, "http://kooltechs.com/garage/requestsList", nameValuePair);
                new HttpPostToServer(mContext).execute(httpRequestData);

                break;
            }
            case 1: {
                error = "Error: Login error";
                doShowDialog(error);
//                dialogNetworkError("Login error!");
                updateFragment(3);
                break;
            }
            case 2: {
                error = "Error: Not find door";
                doShowDialog(error);
                break;
            }
            case 3: {
                error = "Error: Had a request";
                doShowDialog(error);
                break;
            }
            case 4: {
                error = "Error: The door belong your manager";
                doShowDialog(error);
                break;
            }
        }


    }

    private void receiveSetRequestResponse(JSONObject reader) throws JSONException {
        int status = reader.getInt("status");
        String error = "";
        if (status == 0) {
            error = "No error";
            ArrayList<NameValuePair> nameValuePair = new ArrayList<>(1);
            nameValuePair.add(new BasicNameValuePair("masterList", "1"));
            HttpRequestData httpRequestData = new HttpRequestData(HttpRequestData.REQUEST_REQUEST_LIST_AUTHORIZATION, "http://kooltechs.com/garage/requestsList", nameValuePair);
            new HttpPostToServer(mContext).execute(httpRequestData);
        } else if (status == 1) {
            error = "Error: Have some error";
            doShowDialog(error);
        } else if (status == 2) {
            error = "Error: Don't find request";
            doShowDialog(error);
        } else if (status == 3) {
            error = "Eror: don't accept because you is not master";
            doShowDialog(error);
        } else if (status == 4) {
            error = "Error: Can't delete record because not master or guest";
            doShowDialog(error);
        }
    }

    private void receiveGetListAuthorization(JSONObject reader) throws JSONException {
        int status = reader.getInt("status");
        if (status == 0) {
            try {
                JSONArray requests = reader.getJSONArray("requests");
                AuthorizationsFragment.arrItemAuthorizations.clear();
                Log.e("test1", "reader: " + reader);
                for (int i = 0; i < requests.length(); i++) {
                    JSONObject request = new JSONObject(requests.get(i).toString());
                    ItemAuthorizations item = new ItemAuthorizations();
                    item.request_id = request.getString("request_id");
                    item.request_door = request.getString("request_door");
                    item.user_email = request.getString("user_email");
                    item.request_active = request.getString("request_active");
                    item.request_endtime = request.getString("request_endtime");
                    item.request_time = request.getString("request_time");
                    item.door_status = request.getString("door_status");
                    item.request_notify = request.getString("request_notify");
                    item.request_auth_notify = request.getString("request_auth_notify");
                    item.request_guest = request.getString("request_guest");

                    AuthorizationsFragment.arrItemAuthorizations.add(item);
                }
                AuthorizationsFragment.adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            String error = "Error: login error";
            doShowDialog(error);
            updateFragment(3);
        }
    }

    private void receiveDeleteAuthorization(JSONObject reader) throws JSONException {
        int status = reader.getInt("status");
        String error = "";
        if (status == 0) {
            error = "No error";
            ArrayList<NameValuePair> nameValuePair = new ArrayList<>(1);
            nameValuePair.add(new BasicNameValuePair("masterList", "1"));
            HttpRequestData httpRequestData = new HttpRequestData(HttpRequestData.REQUEST_REQUEST_LIST_AUTHORIZATION, "http://kooltechs.com/garage/requestsList", nameValuePair);
            new HttpPostToServer(mContext).execute(httpRequestData);
        } else if (status == 1) {
            error = "Error: Have some error";
            doShowDialog(error);
        } else if (status == 2) {
            error = "Error: Don't find request";
            doShowDialog(error);
        } else if (status == 3) {
            error = "Eror: don't accept because you is not master";
            doShowDialog(error);
        } else if (status == 4) {
            error = "Error: Can't delete record because not master or guest";
            doShowDialog(error);
        }
    }

    private void receiveDeleteGuest(JSONObject reader) throws JSONException {
        int status = reader.getInt("status");
        String error = "";
        if (status == 0) {
            error = "No error";
            ArrayList<NameValuePair> nameValuePair = new ArrayList<>(1);
            nameValuePair.add(new BasicNameValuePair("masterList", "0"));
            HttpRequestData httpRequestData = new HttpRequestData(HttpRequestData.REQUEST_GET_REQUEST_LIST, "http://kooltechs.com/garage/requestsList", nameValuePair);
            new HttpPostToServer(mContext).execute(httpRequestData);

        } else if (status == 1) {
            error = "Error: Have some error";
            doShowDialog(error);
        } else if (status == 2) {
            error = "Error: Don't find request";
            doShowDialog(error);
        } else if (status == 3) {
            error = "Eror: don't accept because you is not master";
            doShowDialog(error);
        } else if (status == 4) {
            error = "Error: Can't delete record because not master or guest";
            doShowDialog(error);
        }
    }

    private void receiveControlGuest(JSONObject reader) throws JSONException {
        int status = reader.getInt("status");
        String error = "";
        if (status == 0) {
            error = "No error";
            ArrayList<NameValuePair> nameValuePair = new ArrayList<>(1);
            nameValuePair.add(new BasicNameValuePair("masterList", "0"));
            HttpRequestData httpRequestData = new HttpRequestData(HttpRequestData.REQUEST_GET_REQUEST_LIST, "http://kooltechs.com/garage/requestsList", nameValuePair);
            new HttpPostToServer(mContext).execute(httpRequestData);

        } else if (status == 1) {
            error = "Error: login error";
            doShowDialog(error);
//            dialogNetworkError("Login error!");
            updateFragment(3);
        } else if (status == 2) {
            error = "Error: hasn't any door";
            doShowDialog(error);
        } else if (status == 3) {
            error = "Error: don't authorities";
            doShowDialog(error);
        } else if (status == 4) {
            error = "Error: Expire time";
            doShowDialog(error);
        } else if (status == 5) {
            error = "Error: door busy";
            doShowDialog(error);
        }
    }

    private void receiveLogout(JSONObject reader) throws JSONException {
        int status = reader.getInt("status");
        String error = "";
        if (status == 0) {
            error = "No error";
            MySharedPreferences.saveLoginStatus(mContext, false);
            updateFragment(3);
        } else if (status == 1) {
            error = "Error: Logout error";
            doShowDialog(error);

        }
    }

    private void receiveAutoConfig(JSONObject reader) throws JSONException {
        int status = reader.getInt("status");
        String error = "";
        if (status == 0) {
            error = "No error";

        } else if (status == 1) {
            error = "Error:  Have some error";
            doShowDialog(error);
        }
    }

    private void receiveCheckDoorStatus(JSONObject reader) throws JSONException {
        int status = reader.getInt("status");
        if (status == 0) {
            //error: No error
            //message:{"status":0,"doors":[{"door_id":"8","door_status":"1","door_battery":"34"},{"door_id":"9","door_status":"0","door_battery":"19"}]}
            JSONArray doors = reader.getJSONArray("doors");
            ArrayList<ItemDoor> list = new ArrayList<>();
            for (int i = 0; i < doors.length(); i++) {
                JSONObject door = new JSONObject(doors.get(i).toString());
                ItemDoor item = new ItemDoor();
                item.door_id = door.getString("door_id");
                item.door_status = door.getInt("door_status");
                item.door_battery = door.getInt("door_battery");
                item.door_auto_begin = door.getString("door_auto_begin");
                item.door_auto_end = door.getString("door_auto_end");
                item.door_auto_enable = door.getString("door_auto_enable");
                item.door_auto_timer = door.getString("door_auto_timer");
//
                list.add(item);
            }
//            updateFragment(0);
            MYLOG.getInstance().LOG("onReceive", "mViewPagerFragment: " + mViewPagerFragment);
            if (mViewPagerFragment != null) {
                mViewPagerFragment.updateStatus(list);
            }

        } else {
//            MySharedPreferences.saveLoginStatus(mContext, false);
            //error: Have some error
            dialogNetworkError("Have some error");
            updateFragment(3);
        }
    }

//    REQUEST_GUEST_TURN_ON_OFF_NOTIFICATION
    private void receiveGuestTurnOnOffNotification(JSONObject reader) throws JSONException {
        int status = reader.getInt("status");
        Log.e("test", "receiveGuestTurnOnOffNotification  status: " + status );
        String error = "";
        if(status == 0){

            HttpRequestData.getRequestList(mContext);
        }else if (status == 1) {
            error = "Error: Have some error";
            doShowDialog(error);
        } else {
            error = "Error: Don't access";
            doShowDialog(error);
        }
    }
    //===========================================================================
    //===========================================================================
    private void stopReceiver() {
        if (mGattUpdateReceiver != null) {
            unregisterReceiver(mGattUpdateReceiver);
            mGattUpdateReceiver = null;
        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MyWebsocketService.RECEIVE_COMMAND_1);
        intentFilter.addAction(MyWebsocketService.RECEIVE_COMMAND_2);
        intentFilter.addAction(MyWebsocketService.RECEIVE_COMMAND_4);
        intentFilter.addAction(MyWebsocketService.RECEIVE_DATA_POST_SERVER);

//        MyWebsocketService.RECEIVE_DATA_POST_SERVER
        intentFilter.addAction(SHOW_DIALOG);

        return intentFilter;
    }

    private BroadcastReceiver mGattUpdateReceiver = null;

//    private String[] tabs = { "My doors", "My Request", "My Authorizations", "More..." };


    public final static String SHOW_DIALOG = "SHOW_DIALOG";
//    public final static int FRAGMENT_MY_DOORS = 1;
//    public final static int FRAGMENT_MY_REQUEST = 2;
//    public final static int FRAGMENT_MY_AUTHORIZATIONS = 3;
//    public final static int FRAGMENT_MY_LOG1N = 4;
//    public final static int FRAGMENT_MY_LOGOUT = 5;
    //============================================================================
    //========================== tab selection ==================================
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    //====================TODO HttpRequestListener ====================
    @Override
    public void StartLoginWithFacebook(String access_token) {
        if (mProgressDialog != null && mProgressDialog.isIndeterminate()) mProgressDialog.dismiss();
        mProgressDialog = ProgressDialog.show(mContext, null, null, false, true);

    }


    @Override
    public void StartLoginWithAccount(String user, String passwork) {
        if (mProgressDialog != null && mProgressDialog.isIndeterminate()) mProgressDialog.dismiss();
        mProgressDialog = ProgressDialog.show(mContext, null, null, false, true);

    }

    @Override
    public void StartLogOut() {
        if (mProgressDialog != null && mProgressDialog.isIndeterminate()) mProgressDialog.dismiss();
        mProgressDialog = ProgressDialog.show(mContext, null, null, false, true);
    }

    @Override
    public void StartrequestResponse() {
        if (mProgressDialog != null && mProgressDialog.isIndeterminate()) mProgressDialog.dismiss();
        mProgressDialog = ProgressDialog.show(mContext, null, null, false, true);
    }


}
