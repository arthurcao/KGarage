package com.example.dinh.kgarage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.dinh.kgarage.MyGarage.ItemDoor;
import com.example.dinh.kgarage.MyGarage.MyWebsocketService;
import com.example.dinh.kgarage.segment.SegmentedGroup;
import com.example.dinh.kgarage.utils.HttpPostToServer;
import com.example.dinh.kgarage.utils.HttpRequestData;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class DoorAutoCloseSetupActivity extends ActionBarActivity {

    TimePicker mTimePickerFrom;
    TimePicker mTimePickerTo;
    NumberPicker mDuarationPicker;

    Switch mSwitchEnable;
    Switch mSwitchTime;
    TextView mDurationText;
    int currentH, currentMinute;
    int fromH, fromMinute;
    int toH, toMinute;
    int mDuration = 1;
    String door_id = "0";
    boolean isEnable;
    Context mContext;
    ItemDoor mItemDoor;
    ItemDoor mItemDoor1;
    boolean isCustom;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_door_auto_close_setup);


        mSwitchEnable = (Switch) findViewById(R.id.auto_close_activity_enable);
        mSwitchTime = (Switch) findViewById(R.id.auto_close_time_window);

        mDurationText = (TextView) findViewById(R.id.auto_close_activity_duration_text);
        mSwitchEnable = (Switch) findViewById(R.id.auto_close_activity_enable);
        mTimePickerFrom = (TimePicker) findViewById(R.id.auto_close_activity_from_timePicker);
        mTimePickerTo = (TimePicker) findViewById(R.id.auto_close_activity_to_timePicker);
        mDuarationPicker = (NumberPicker) findViewById(R.id.auto_close_activity_durationspicker);
        mDuarationPicker.setMaxValue(100);
        mContext = this;
        isEnable = false;
        mItemDoor1 = new ItemDoor();
        door_id = getIntent().getStringExtra("DOORID");
        mItemDoor = getIntent().getParcelableExtra("DOOR");

        Log.e("20150525","load mItemDoor.door_id: " + mItemDoor.door_id);
        Log.e("20150525","load mItemDoor.door_auto_begin: " + mItemDoor.door_auto_begin);
        Log.e("20150525","load mItemDoor.door_auto_end: " + mItemDoor.door_auto_end);
        Log.e("20150525", "load mItemDoor.door_auto_enable: " + mItemDoor.door_auto_enable);
        Log.e("20150525", "load mItemDoor.door_auto_timer: " + mItemDoor.door_auto_timer);
        Log.e("20150525","------------------------");
//        load mItemDoor.door_auto_begin: 00:00:00
//        06-10 10:23:28.521  29924-29924/com.example.dinh.kgarage E/20150525? load mItemDoor.door_auto_end: 23:59:5
        isCustom = true;
        if(mItemDoor.door_auto_begin.equals("00:00:00") && mItemDoor.door_auto_end.equals("23:59:59")){
            isCustom = false;
        }
        mSwitchTime.setChecked(isCustom);

        if(mItemDoor != null){
            updateDoor(1);

            Calendar c = Calendar.getInstance();
            TimeZone z = c.getTimeZone();
            int offset = z.getRawOffset();
            if(z.inDaylightTime(new Date())){
                offset = offset + z.getDSTSavings();
            }
            int offsetHrs = offset / 1000 / 60 / 60;
            int offsetMins = offset / 1000 / 60 % 60;

            //start tiem:
            String[] temp = mItemDoor.door_auto_begin.split(":");
            if(temp.length == 3){
                //TODO

                int h = Integer.parseInt(temp[0]);
                int m = Integer.parseInt(temp[1]);
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, h);
                calendar.set(Calendar.MINUTE, m);
                calendar.add(Calendar.HOUR_OF_DAY, (offsetHrs));
                calendar.add(Calendar.MINUTE, (offsetMins));
                int fromHour = calendar.get(Calendar.HOUR_OF_DAY);
                int fromMinute = calendar.get(Calendar.MINUTE);
                mTimePickerFrom.setCurrentHour(fromHour);
                mTimePickerFrom.setCurrentMinute(fromMinute);
            }
            //end time
            temp = mItemDoor.door_auto_end.split(":");
            if(temp.length == 3){
                //TODO
                int h = Integer.parseInt(temp[0]);
                int m = Integer.parseInt(temp[1]);
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, h);
                calendar.set(Calendar.MINUTE, m);
                calendar.add(Calendar.HOUR_OF_DAY, (offsetHrs));
                calendar.add(Calendar.MINUTE, (offsetMins));
                int fromHour = calendar.get(Calendar.HOUR_OF_DAY);
                int fromMinute = calendar.get(Calendar.MINUTE);
                mTimePickerTo.setCurrentHour(fromHour);
                mTimePickerTo.setCurrentMinute(fromMinute);
            }
        }

        mSwitchEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    mItemDoor1.door_auto_enable = "1";
                    mDuarationPicker.setEnabled(true);
                    mSwitchTime.setEnabled(true);

                    if(isCustom){
                        mTimePickerFrom.setEnabled(true);
                        mTimePickerTo.setEnabled(true);
                    }else{
                        mTimePickerFrom.setEnabled(false);
                        mTimePickerTo.setEnabled(false);
                    }

                }else{
                    mItemDoor1.door_auto_enable = "0";
                    mDuarationPicker.setEnabled(false);
                    mSwitchTime.setEnabled(false);
                    mTimePickerFrom.setEnabled(false);
                    mTimePickerTo.setEnabled(false);
                }



            }
        });
        mSwitchTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isCustom = isChecked;
                if (isCustom) {
                    mTimePickerFrom.setEnabled(true);
                    mTimePickerTo.setEnabled(true);
                } else {
                    mTimePickerFrom.setEnabled(false);
                    mTimePickerTo.setEnabled(false);

                    mTimePickerFrom.setCurrentHour(0);
                    mTimePickerFrom.setCurrentMinute(0);
                    mTimePickerTo.setCurrentHour(23);
                    mTimePickerTo.setCurrentMinute(59);


                }
            }
        });

        if(mItemDoor.door_auto_enable.equals("1")){
            mSwitchEnable.setChecked(true);
            mDuarationPicker.setEnabled(true);
            mSwitchTime.setEnabled(true);

            if(isCustom){
                mTimePickerFrom.setEnabled(true);
                mTimePickerTo.setEnabled(true);
            }else{
                mTimePickerFrom.setEnabled(false);
                mTimePickerTo.setEnabled(false);
            }

//            mTimePickerFrom.setEnabled(true);
//            mTimePickerTo.setEnabled(true);


        }else{
            mSwitchEnable.setChecked(false);
            mSwitchTime.setEnabled(false);
            mTimePickerFrom.setEnabled(false);
            mTimePickerTo.setEnabled(false);
            mDuarationPicker.setEnabled(false);
        }

        getCurrentTime();

        mTimePickerFrom.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {

                fromH = hourOfDay;
                fromMinute = minute;
                getLimitDuration();
            }
        });



        mTimePickerTo.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {

                toH = hourOfDay;
                toMinute = minute;
                getLimitDuration();
            }
        });

        getCurrentTime();
        fromH = currentH;
        fromMinute = currentMinute;
        toH = currentH;
        toMinute = currentMinute;


        getLimitDuration();
        if(mItemDoor != null){
            if( mItemDoor.door_auto_timer != null){
                int time = Integer.parseInt(mItemDoor.door_auto_timer);
                if(time < mDuarationPicker.getMaxValue()){
                    mDuarationPicker.setValue(time);
                }
            }
        }


        setResult(RESULT_OK);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Auto-close Configuration");

        if(!isCustom){
            fromH = 0;
            fromMinute = 0;
            toH = 23;
            toMinute = 59;

            mTimePickerFrom.setCurrentHour(0);
            mTimePickerFrom.setCurrentMinute(0);
            mTimePickerTo.setCurrentHour(23);
            mTimePickerTo.setCurrentMinute(59);
        }

    }



    private void setEnableSegment(SegmentedGroup segmentedGroup, boolean value){
        for (int i = 0; i < segmentedGroup.getChildCount(); i++) {
            segmentedGroup.getChildAt(i).setEnabled(value);
        }
    }

    private void updateDoor(int type){
        if(type == 1){
            mItemDoor1.door_id = mItemDoor.door_id;
            mItemDoor1.door_auto_begin = mItemDoor.door_auto_begin;
            mItemDoor1.door_auto_end = mItemDoor.door_auto_end;
            mItemDoor1.door_auto_enable = mItemDoor.door_auto_enable;
            mItemDoor1.door_auto_timer = mItemDoor.door_auto_timer;
        }else{
            mItemDoor.door_id = mItemDoor1.door_id;
            mItemDoor.door_auto_begin = mItemDoor1.door_auto_begin;
            mItemDoor.door_auto_end = mItemDoor1.door_auto_end;
            mItemDoor.door_auto_enable = mItemDoor1.door_auto_enable;
            mItemDoor.door_auto_timer = mItemDoor1.door_auto_timer;
        }

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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_auto_cloase_add:
                sendHtp("0");
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public String convertTwoNumber(int number){
        String str = "";
        if(number > 9){
            str += number;
        }else{
            str +="0" + number;
        }
        return str;
    }


    public Calendar convertTimeToGMT(Calendar calendar){
        Calendar c = Calendar.getInstance();

        TimeZone z = c.getTimeZone();
        int offset = z.getRawOffset();
        if(z.inDaylightTime(new Date())){
            offset = offset + z.getDSTSavings();
        }
        int offsetHrs = offset / 1000 / 60 / 60;
        int offsetMins = offset / 1000 / 60 % 60;
        calendar.add(Calendar.HOUR_OF_DAY, (-offsetHrs));
        calendar.add(Calendar.MINUTE, (-offsetMins));

        return calendar;
    }

    public void getCurrentTime(){
        Calendar c = Calendar.getInstance();
        currentH = c.get(Calendar.HOUR_OF_DAY);
        currentMinute = c.get(Calendar.MINUTE);

    }

    public void save(View v){
        sendHtp("1");
    }



    int mMaxDuration = 0;
    public void getLimitDuration(){
        Calendar fromTime = Calendar.getInstance();
        fromTime.set(Calendar.HOUR_OF_DAY, fromH);
        fromTime.set(Calendar.MINUTE, fromMinute);

        Calendar toTime = Calendar.getInstance();
        toTime.set(Calendar.HOUR_OF_DAY, toH);
        toTime.set(Calendar.MINUTE, toMinute);

        fromTime = convertTimeToGMT(fromTime);
        toTime = convertTimeToGMT(toTime);


        int fromHour = fromTime.get(Calendar.HOUR_OF_DAY);
        int fromMinute = fromTime.get(Calendar.MINUTE);

        int toHour = toTime.get(Calendar.HOUR_OF_DAY);
        int toMinute = toTime.get(Calendar.MINUTE);


        int fTime = fromHour*60 + fromMinute;
        int tTime = toHour* 60 + toMinute;

        if(fTime < tTime){
            mMaxDuration = tTime - fTime;
        }else{
            mMaxDuration = 24*60 - fTime +  tTime;
        }
        mDuarationPicker.setMaxValue(mMaxDuration);
    }


    private void sendHtp(String enable){
        String door_auto_begin  ="";
        String door_auto_end= "";
        String door_auto_enable = mItemDoor1.door_auto_enable;
        int door_auto_timer = 1;
        if(!isCustom){
            door_auto_begin = "00:00:00";
            door_auto_end = "23:59:59";
        }else{
//            int fromH, fromMinute;
//            int toH, toMinute;
            fromH = mTimePickerFrom.getCurrentHour();
            fromMinute = mTimePickerFrom.getCurrentMinute();
            toH = mTimePickerTo.getCurrentHour();
            toMinute = mTimePickerTo.getCurrentMinute();

            Calendar fromTime = Calendar.getInstance();
            fromTime.set(Calendar.HOUR_OF_DAY, fromH);
            fromTime.set(Calendar.MINUTE, fromMinute);

            Calendar toTime = Calendar.getInstance();
            toTime.set(Calendar.HOUR_OF_DAY, toH);
            toTime.set(Calendar.MINUTE, toMinute);

            fromTime = convertTimeToGMT(fromTime);
            toTime = convertTimeToGMT(toTime);


            int fromHour = fromTime.get(Calendar.HOUR_OF_DAY);
            int fromMinute = fromTime.get(Calendar.MINUTE);
            Log.e("20150525","sendHtp fromHour: " + fromHour);
            Log.e("20150525","sendHtp fromMinute: " + fromMinute);

            door_auto_begin = "" + convertTwoNumber(fromHour) + ":" + convertTwoNumber(fromMinute) + ":00";

            int toHour = toTime.get(Calendar.HOUR_OF_DAY);
            int toMinute = toTime.get(Calendar.MINUTE);

            door_auto_end = "" + convertTwoNumber(toHour) + ":" + convertTwoNumber(toMinute) + ":00";
        }
        door_auto_timer = mDuarationPicker.getValue();

        mItemDoor1.door_auto_begin = door_auto_begin;
        mItemDoor1.door_auto_end = door_auto_end;
        mItemDoor1.door_auto_enable = door_auto_enable;
        mItemDoor1.door_auto_timer = String.valueOf(door_auto_timer);

        Log.e("20150525","sendHtp door_id: " + door_id);
        Log.e("20150525","sendHtp door_auto_begin: " + door_auto_begin);
        Log.e("20150525","sendHtp door_auto_end: " + door_auto_end);
        Log.e("20150525","sendHtp door_auto_enable: " + door_auto_enable);
        Log.e("20150525","sendHtp door_auto_timer: " + door_auto_timer);
        Log.e("20150525","------------------------");

        ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(3);
        nameValuePair.add(new BasicNameValuePair("door_id", door_id));
        nameValuePair.add(new BasicNameValuePair("door_auto_begin", door_auto_begin));
        nameValuePair.add(new BasicNameValuePair("door_auto_end", door_auto_end));
        nameValuePair.add(new BasicNameValuePair("door_auto_enable", door_auto_enable));
        nameValuePair.add(new BasicNameValuePair("door_auto_timer", String.valueOf(door_auto_timer)));

        HttpRequestData httpRequestData = new HttpRequestData(HttpRequestData.REQUEST_AUTO_CLOSE, "http://kooltechs.com/garage/door_auto_config", nameValuePair);
        new HttpPostToServer(this).execute(httpRequestData);
    }

    //TODO #################### RECEIVER #############
    private void resumeReceiver() {

        if(mGattUpdateReceiver == null){
            mGattUpdateReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (MyWebsocketService.RECEIVE_DATA_POST_SERVER.equals(action)) {
                        int request = intent.getIntExtra("REQUEST", 1);
                        int command_error = intent.getIntExtra("COMMNAD_ERROR", 1);
                        String message = intent.getStringExtra("MESSAGE");
                        if (command_error == 0) {
                            //command ok
                            Object json = null;
                            try {

                                json = new JSONTokener(message).nextValue();
                                if (json instanceof JSONObject) {
                                    JSONObject reader = (JSONObject) json;
                                    //TODO
                                    if (request == HttpRequestData.REQUEST_AUTO_CLOSE) {
                                        receiveAutoConfig(reader);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }
            } ;
            registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        }
    }
    private void receiveAutoConfig(JSONObject reader) throws JSONException {
        int status = reader.getInt("status");
        Log.e("20150525", "receiveAutoConfig save ..... status: " + status);
        String error = "";
        if(status == 0){
            error = "No error";
            isEnable = true;
            updateDoor(0);

        }else if(status == 1){
            error = "Error";
            isEnable = false;
            updateDoor(1);
        }
        invalidateOptionsMenu();
    }
    private void stopReceiver() {
        if(mGattUpdateReceiver != null){
            unregisterReceiver(mGattUpdateReceiver);
            mGattUpdateReceiver = null;
        }
    }
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MyWebsocketService.RECEIVE_DATA_POST_SERVER);
        return intentFilter;
    }

    private BroadcastReceiver mGattUpdateReceiver = null;
}
