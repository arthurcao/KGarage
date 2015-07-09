package com.kooltech.droid.kgarage;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.kooltech.droid.kgarage.object.ItemAuthorizations;
import com.kooltech.droid.kgarage.utils.DatePickerFragment;
import com.kooltech.droid.kgarage.utils.TimePickerFragment;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class SetTimeAuthorizationsActivity extends ActionBarActivity {

    String mUser;
    public String request_id = "";

    CheckBox mForever;
    CheckBox mAuthNotify;
    CheckBox mNotify;

    DatePicker mDatePickerFrom;
    DatePicker mDatePickerTo;
    TimePicker mTimePickerFrom;
    TimePicker mTimePickerTo;

    ItemAuthorizations mItemAuthorizations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.kooltech.droid.kgarage.R.layout.activity_set_time);
        mDatePickerFrom = (DatePicker) findViewById(com.kooltech.droid.kgarage.R.id.set_time_from_datePicker);
        mTimePickerFrom = (TimePicker) findViewById(com.kooltech.droid.kgarage.R.id.set_time_from_timePicker);
        mDatePickerTo = (DatePicker) findViewById(com.kooltech.droid.kgarage.R.id.set_time_to_datePicker);
        mTimePickerTo = (TimePicker) findViewById(com.kooltech.droid.kgarage.R.id.set_time_to_timePicker);

        mAuthNotify = (CheckBox) findViewById(com.kooltech.droid.kgarage.R.id.set_time_auth_notify);
        mNotify = (CheckBox) findViewById(com.kooltech.droid.kgarage.R.id.set_time_notify);
        mForever = (CheckBox) findViewById(com.kooltech.droid.kgarage.R.id.set_time_forever);

        request_id = getIntent().getStringExtra("REQUESTID");
        mItemAuthorizations = getIntent().getParcelableExtra("ITEM");
        detectItem();
        setListener();


        mTimePickerFrom.setCurrentHour(fromH);
        mTimePickerFrom.setCurrentMinute(fromMinute);

        mTimePickerTo.setCurrentHour(toH);
        mTimePickerTo.setCurrentMinute(toMinute);

        mUser = MySharedPreferences.loadUser(this);

        if(mItemAuthorizations.request_endtime.equals("0")){
            mForever.setChecked(true);
        }

        if(mItemAuthorizations.request_auth_notify.equals("1")){
            mAuthNotify.setChecked(true);
        }
        if(mItemAuthorizations.request_notify.equals("1")){
            mNotify.setChecked(true);
        }


        setResult(RESULT_CANCELED, null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(com.kooltech.droid.kgarage.R.menu.menu_set_time, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == com.kooltech.droid.kgarage.R.id.set_time_submit) {
            updateItem();
            Intent data = new Intent();
            data.putExtra("ITEM",mItemAuthorizations);
            setResult(RESULT_OK, data);
            super.finish();

            return true;
        }else if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setListener(){
        //TODO setListener
        mDatePickerFrom.init(fromY, fromMonth, fromD, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                fromY = year;
                fromMonth = monthOfYear;
                fromD = dayOfMonth;
                mDatePickerTo.updateDate(fromY, fromMonth, fromD);
                updateTime();
            }

        });

        mTimePickerFrom.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {

                fromH = hourOfDay;
                fromMinute = minute;
                updateTime();

            }
        });

        mDatePickerTo.init(toY, toMonth, toD, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                if (year < fromY) {
                    mDatePickerTo.updateDate(fromY, fromMonth, fromD);

                } else if (year == fromY) {
                    if (monthOfYear < fromMonth) {
                        mDatePickerTo.updateDate(fromY, fromMonth, fromD);
                        return;
                    } else if (monthOfYear == fromMonth) {
                        if (dayOfMonth < fromD) {
                            mDatePickerTo.updateDate(fromY, fromMonth, fromD);
                            return;
                        }
                    }
                }
                toY = year;
                toMonth = monthOfYear;
                toD = dayOfMonth;

            }
        });

        mTimePickerTo.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                int year = mDatePickerTo.getYear();
                int month = mDatePickerTo.getMonth();
                int day = mDatePickerTo.getDayOfMonth();
                if (year == fromY && month == fromMonth && day == fromD) {

                    if (hourOfDay < fromH) {

                        toH = fromH;
                        toMinute = fromMinute + 1;
                        toH += toMinute / 60;
                        fromMinute = fromMinute % 60;

                        mTimePickerTo.setCurrentHour(toH);
                        mTimePickerTo.setCurrentMinute(toMinute);
                        return;
                    } else if (hourOfDay == fromH) {
                        if (minute <= fromMinute) {

                            toH = fromH;
                            toMinute = fromMinute + 1;
                            toH += toMinute / 60;
                            fromMinute = fromMinute % 60;
                            mTimePickerTo.setCurrentMinute(toMinute);
                            return;
                        }
                    }
                }

                toH = hourOfDay;
                toMinute = minute;
            }
        });

        mForever.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                mDatePickerFrom.setEnabled(true);
                mDatePickerTo.setEnabled(true);
                mTimePickerFrom.setEnabled(true);
                mTimePickerTo.setEnabled(true);

                if (isChecked) {
                    mDatePickerFrom.setEnabled(false);
                    mDatePickerTo.setEnabled(false);
                    mTimePickerFrom.setEnabled(false);
                    mTimePickerTo.setEnabled(false);
                }
            }
        });
    }

    int currentY, currentD, currentMonth, currentH, currentMinute;
    int fromY, fromD, fromMonth, fromH, fromMinute;
    int toY, toD, toMonth, toH, toMinute;

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
    public Calendar convertGMTToTime(Calendar calendar){
        Calendar c = Calendar.getInstance();

        TimeZone z = c.getTimeZone();
        int offset = z.getRawOffset();
        if(z.inDaylightTime(new Date())){
            offset = offset + z.getDSTSavings();
        }
        int offsetHrs = offset / 1000 / 60 / 60;
        int offsetMins = offset / 1000 / 60 % 60;
        calendar.add(Calendar.HOUR_OF_DAY, (offsetHrs));
        calendar.add(Calendar.MINUTE, (offsetMins));

        return calendar;
    }

    private void detectItem(){

        //2015-06-30 04:28:01
        String time = mItemAuthorizations.request_time;
        time = time.trim();
        if(checkFormatDate(time)){
            String[] temp = time.split(" ");
            String[] temp1 = temp[0].split("-");
            fromY = Integer.parseInt(temp1[0]);
            fromMonth = Integer.parseInt(temp1[1]);
            fromD = Integer.parseInt(temp1[2]);

            String[] temp2 = temp[1].split(":");
            fromH = Integer.parseInt(temp2[0]);
            fromMinute = Integer.parseInt(temp2[1]);

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, fromH);
            calendar.set(Calendar.MINUTE,fromMinute);
            calendar.set(Calendar.YEAR, fromY);
            calendar.set(Calendar.MONTH, fromMonth);
            calendar.set(Calendar.DAY_OF_MONTH, fromD);

            Calendar calendar1 = convertGMTToTime(calendar);
            fromY = calendar1.get(Calendar.YEAR);
            fromMonth = calendar1.get(Calendar.MONTH);
            fromD = calendar1.get(Calendar.DAY_OF_MONTH);
            fromH = calendar1.get(Calendar.HOUR_OF_DAY);
            fromMinute = calendar1.get(Calendar.MINUTE);

        }else{
            Calendar c = Calendar.getInstance();
            currentY = c.get(Calendar.YEAR);
            currentMonth = c.get(Calendar.MONTH);
            currentD = c.get(Calendar.DAY_OF_MONTH);
            currentH = c.get(Calendar.HOUR_OF_DAY);
            currentMinute = c.get(Calendar.MINUTE);
        }


        int request_endtime = Integer.parseInt(mItemAuthorizations.request_endtime);
        if(request_endtime > 0){
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, fromH);
            calendar.set(Calendar.MINUTE,fromMinute);
            calendar.set(Calendar.YEAR, fromY);
            calendar.set(Calendar.MONTH, fromMonth);
            calendar.set(Calendar.DAY_OF_MONTH, fromD);
            int second = (int)calendar.getTimeInMillis()/1000;
            second +=  request_endtime;

            calendar.setTimeInMillis(second * 1000);
            fromY = calendar.get(Calendar.YEAR);
            fromMonth = calendar.get(Calendar.MONTH);
            fromD = calendar.get(Calendar.DAY_OF_MONTH);
            fromH = calendar.get(Calendar.HOUR_OF_DAY);
            fromMinute = calendar.get(Calendar.MINUTE);
        }
        updateTime();
    }



    private boolean checkFormatDate(String str){
        Log.e("test",";length str:" +str.length());
        if(str.length() == 19){
            return true;
        }else {
            return false;
        }
    }

    private void updateItem(){
        //TODO update
        Calendar fromTime = Calendar.getInstance();
        fromTime.set(fromY,fromMonth,fromD, fromH, fromMinute);

        Calendar toTime = Calendar.getInstance();
        toTime.set(toY, toMonth, toD,toH, toMinute);

        int second = (int) (toTime.getTimeInMillis()/1000 - fromTime.getTimeInMillis()/1000);
        int minute = second/60;

        Calendar c = Calendar.getInstance();

        TimeZone z = c.getTimeZone();
        int offset = z.getRawOffset();
        if(z.inDaylightTime(new Date())){
            offset = offset + z.getDSTSavings();
        }
        int offsetHrs = offset / 1000 / 60 / 60;
        int offsetMins = offset / 1000 / 60 % 60;

        fromTime.add(Calendar.HOUR_OF_DAY, (-offsetHrs));
        fromTime.add(Calendar.MINUTE, (-offsetMins));


        int gmtYear = fromTime.get(Calendar.YEAR);
        int gmtMonth = fromTime.get(Calendar.MONTH);
        int gmtDay = fromTime.get(Calendar.DAY_OF_MONTH);
        int gmtHour = fromTime.get(Calendar.HOUR_OF_DAY);
        int gmtMinute = fromTime.get(Calendar.MINUTE);

        // update request endtime
        boolean forever = mForever.isChecked();
        if(forever){
            mItemAuthorizations.request_endtime = "0";
        }else{
            mItemAuthorizations.request_endtime = "" + minute;
        }

        // update request_time "2015-06-30 04:28:01"


        String str = "" + gmtYear + "-";
        str +=format2Number(gmtMonth);
        str +="-";
        str +=format2Number(gmtDay);
        str +=" ";

        str +=format2Number(gmtHour);
        str +=":";
        str +=format2Number(gmtMinute);
        str +=":";
        str += "00";
        mItemAuthorizations.request_time  = str;
        //update Auth notify
        boolean authnotify = mAuthNotify.isChecked();
        if(authnotify){
            mItemAuthorizations.request_auth_notify = "1";
        }else{
            mItemAuthorizations.request_auth_notify = "0";
        }

        // update notify
        boolean notify = mNotify.isChecked();
        if(notify){
            mItemAuthorizations.request_notify = "1";
        }else{
            mItemAuthorizations.request_notify = "0";
        }
    }

    private String format2Number(int n){
        if(n < 10){
            return "0" + n;
        }else{
            return "" + n;
        }
    }
    public void setDateFrom(View v){
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setListenen(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                fromY = year;
                fromMonth = monthOfYear;
                fromD = dayOfMonth;

            }
        });
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void setTimeFrom(View v){
        TimePickerFragment newFragment = new TimePickerFragment();
        newFragment.setListenen(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                fromH = hourOfDay;
                fromMinute = minute;
            }
        });
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void updateTime(){

        Calendar fromTime = Calendar.getInstance();
        fromTime.set(fromY,fromMonth,fromD, fromH, fromMinute);

        Calendar toTime = Calendar.getInstance();
        toTime.set(toY, toMonth, toD,toH, toMinute);

        if(fromTime.getTimeInMillis() >= toTime.getTimeInMillis()){
            toY = fromY;
            toMonth = fromMonth;
            toD = fromD;
            toH = fromH;
            toMinute = fromMinute + 1;
            mDatePickerTo.updateDate(toY,toMonth,toD);
            mTimePickerTo.setCurrentHour(toH);
            mTimePickerTo.setCurrentMinute(toMinute);
        }
    }


    //TODO #################### RECEIVER #############
    private void resumeReceiver() {

        if(mGattUpdateReceiver == null){
            mGattUpdateReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (Intent.ACTION_TIME_CHANGED.equals(action) || Intent.ACTION_DATE_CHANGED.equals(action)|| Intent.ACTION_TIME_TICK.equals(action)) {
                        updateTime();
                    }
                }
            } ;
            registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        }
    }

    private void stopReceiver() {
        if(mGattUpdateReceiver != null){
            unregisterReceiver(mGattUpdateReceiver);
            mGattUpdateReceiver = null;
        }
    }
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        intentFilter.addAction(Intent.ACTION_DATE_CHANGED);
        return intentFilter;
    }

    private BroadcastReceiver mGattUpdateReceiver = null;



}
