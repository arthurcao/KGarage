package com.kooltech.droid.kgarage.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.kooltech.droid.kgarage.utils.HttpPostToServer;
import com.kooltech.droid.kgarage.utils.HttpRequestData;
import com.kooltech.droid.kgarage.R;
import com.kooltech.droid.kgarage.adapter.RequestsAdapter;
import com.kooltech.droid.kgarage.object.ItemRequest;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Dinh on 3/2/2015.
 */
public class RequestsFragment extends Fragment {
    public static ListView GList=null;

    public static ArrayList<ItemRequest> arrItemRequest = new ArrayList<ItemRequest>();
    public static RequestsAdapter adapter = null;
    private static final String ARG_SECTION_NUMBER = "section_number";

    private  AlertDialog mAlertDialog;

    Context mContext;
    public static RequestsFragment newInstance(int sectionNumber) {
        RequestsFragment fragment = new RequestsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public RequestsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
        nameValuePair.add(new BasicNameValuePair("masterList", "0"));
        HttpRequestData httpRequestData = new HttpRequestData(HttpRequestData.REQUEST_GET_REQUEST_LIST, "http://kooltechs.com/garage/requestsList", nameValuePair);
        new HttpPostToServer(mContext).execute(httpRequestData);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.guest_main, container, false);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
//        ((MainActivity) activity).onSectionAttached(    getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        GList = (ListView) getActivity().findViewById(R.id.listView);
        ImageButton imageButton = (ImageButton) getActivity().findViewById(R.id.imageButton);

        arrItemRequest = new ArrayList<ItemRequest>();
        adapter = new RequestsAdapter(getActivity(), R.layout.guest_item_layout, arrItemRequest);
        GList.setAdapter(adapter);
        GList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final int pos = position;
                ItemRequest item = arrItemRequest.get(pos);
                Log.e("test","item: " + item.toString());
                Log.e("test", "item.request_notify: " + item.request_notify);
                Log.e("test", "item.request_notify: " + item.request_notify);
                Log.e("test","item.request_active: " + item.request_active);

                String str = "Open";
                if (item.door_status.equals("1")) {
                    str = "Open";
                } else {
                    str = "Close";
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("What do you want?");
                builder.setCancelable(true);
                builder.setPositiveButton("Cancel", null);

                LinearLayout mll = new LinearLayout(mContext);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                mll.setLayoutParams(lp);
                mll.setOrientation(LinearLayout.VERTICAL);
                Button btn1 = new Button(mContext);
                Button btn2 = new Button(mContext);
                Button btn3 = new Button(mContext);

                btn1.setText("Delete");
                btn1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ItemRequest item = arrItemRequest.get(pos);
                        HttpRequestData.requestResponseDelete(mContext, item);
                        mAlertDialog.dismiss();
                    }
                });
                boolean forever = Integer.parseInt(item.request_endtime) == 0 ? true : false;
                btn2.setEnabled(false);
                btn2.setText(str);
                if (item.request_active.equals("1") && (forever || (!isExpiredTime(item.request_endtime, item.request_time)))) {
                    btn2.setEnabled(true);
                    btn2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ItemRequest item = arrItemRequest.get(pos);
                            String control_status = "0";
                            if (item.door_status.equals("0")) {
                                control_status = "1";
                            } else {
                                control_status = "0";
                            }

                            HttpRequestData.ControlDoor(mContext, control_status,item.request_door);
                            mAlertDialog.dismiss();
                        }
                    });
                }
//                /request_notify
                if(item.request_notify.equals("1")){
                    btn3.setText("Turn Off Notification");
                }else{
                    btn3.setText("Turn On Notification");
                }

                if(item.request_auth_notify.equals("0")){
                    btn3.setEnabled(false);
                }

                btn3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ItemRequest item = arrItemRequest.get(pos);
                        String request_notify = "0";
                        Log.e("test","request_notify: " + request_notify);
                        if (item.request_notify.equals("1")) {
                            item.request_notify = "0";
                        } else {
                            item.request_notify = "1";
                        }
                        Log.e("test","request_notify: " + request_notify);
                        HttpRequestData.requestTurnOnOffNotificationGues(mContext, item.request_notify,item.request_id);
                        mAlertDialog.dismiss();
                    }
                });

                mll.addView(btn1);
                mll.addView(btn2);
                mll.addView(btn3);
                builder.setView(mll);

                mAlertDialog = builder.create();
                mAlertDialog.show();
            }
        });
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // custom dialog
                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.guest_dialog);
                dialog.setTitle("Add Friend's door");

                final EditText door_id = (EditText) dialog.findViewById(R.id.door_id);
                Button dlBtnOk = (Button) dialog.findViewById(R.id.dlBtnOk);
                Button dlBtnCancel = (Button) dialog.findViewById(R.id.dlBtnCancel);

                dlBtnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
                        nameValuePair.add(new BasicNameValuePair("request_door", door_id.getText().toString()));
                        HttpRequestData httpRequestData = new HttpRequestData(HttpRequestData.REQUEST_GEST_REQUEST, "http://kooltechs.com/garage/guestRequest", nameValuePair);
                        new HttpPostToServer(mContext).execute(httpRequestData);
                        dialog.dismiss();
                    }
                });

                dlBtnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_main_add:
                // Do Fragment menu item stuff here
                addItem();
                return true;
            default:
                break;
        }

        return false;
    }

    private boolean isExpiredTime(String sduration, String sstartTime){



        int duration = Integer.parseInt(sduration);
        String[] temp = sstartTime.split(" ");
        String date = temp[0];
        String time = temp[1];
        DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try
        {

            Date d2 = df2.parse(sstartTime);
            Calendar c = Calendar.getInstance();
            c.setTime(d2);

            Calendar c1 = Calendar.getInstance();
            TimeZone z = c1.getTimeZone();
            int offset = z.getRawOffset();
            if(z.inDaylightTime(new Date())){
                offset = offset + z.getDSTSavings();
            }
            int offsetHrs = offset / 1000 / 60 / 60;
            int offsetMins = offset / 1000 / 60 % 60;
            offsetHrs += duration/60;
            offsetMins += duration%60;

            c.add(Calendar.HOUR_OF_DAY, (+offsetHrs));
            c.add(Calendar.MINUTE, (+offsetMins));

            Calendar currentTime = Calendar.getInstance();
            long current = currentTime.getTimeInMillis();
            long expire = c.getTimeInMillis();
            if( current < expire){
                return false;
            }else{
                return true;
            }




        }
        catch (Exception ex ){
            System.out.println(ex);
        }
        return true;
    }

    public void addItem(){
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.guest_dialog);
        dialog.setTitle("Add Friend's door");

        final EditText door_id = (EditText) dialog.findViewById(R.id.door_id);
        Button dlBtnOk=(Button)dialog.findViewById(R.id.dlBtnOk);
        Button dlBtnCancel=(Button)dialog.findViewById(R.id.dlBtnCancel);

        dlBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
                nameValuePair.add(new BasicNameValuePair("request_door", door_id.getText().toString()));
                HttpRequestData httpRequestData = new HttpRequestData(HttpRequestData.REQUEST_GEST_REQUEST, "http://kooltechs.com/garage/guestRequest", nameValuePair);
                new HttpPostToServer(mContext).execute(httpRequestData);
                dialog.dismiss();
            }
        });

        dlBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


}
