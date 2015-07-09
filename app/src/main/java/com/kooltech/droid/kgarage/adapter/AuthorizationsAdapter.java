package com.kooltech.droid.kgarage.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kooltech.droid.kgarage.R;
import com.kooltech.droid.kgarage.object.ItemAuthorizations;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Dinh on 3/3/2015.
 */
public class AuthorizationsAdapter extends ArrayAdapter<ItemAuthorizations> {
    Activity context = null;
    ArrayList<ItemAuthorizations> myArray = null;
    int layoutId;

    public AuthorizationsAdapter(Activity context, int layoutId, ArrayList<ItemAuthorizations> arr) {
        super(context, layoutId, arr);
        this.context = context;
        this.layoutId = layoutId;
        this.myArray = arr;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        convertView = inflater.inflate(layoutId, null);
        if (myArray.size() > 0 && position >= 0) {
            final TextView door_id = (TextView)convertView.findViewById(R.id.door_id);
            final TextView user_email = (TextView)convertView.findViewById(R.id.user_name);



            final ItemAuthorizations request_list = myArray.get(position);
            user_email.setText(request_list.user_email);
            if(request_list.request_active.equals("1")) {
                int time = Integer.parseInt(request_list.request_endtime);
               if(time <= 0){
                   door_id.setText("Forever");
               }else{
                   String str = "From " + detectStartTime(request_list.request_time);
                   str += " to " + detectEndTime(request_list.request_endtime, request_list.request_time);
                   door_id.setText(str);
               }

            }else{
                door_id.setText("Door ID: " + request_list.request_door);
            }
            convertView.setBackgroundColor(request_list.request_active.equals("0")?0xFFFFEB3B:0xFF4CAF50);
//            detectStartTime(request_list.request_time);
        }
        return convertView;
    }

    public String detectStartTime(String sstartTime){

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

            c.add(Calendar.HOUR_OF_DAY, (+offsetHrs));
            c.add(Calendar.MINUTE, (+offsetMins));

            int gmtYear = c.get(Calendar.YEAR);
            int gmtMonth = c.get(Calendar.MONTH);
            int gmtDay = c.get(Calendar.DAY_OF_MONTH);
            int gmtHour = c.get(Calendar.HOUR_OF_DAY);
            int gmtMinute = c.get(Calendar.MINUTE);
            String str = "";
            str = "" + gmtYear + "-";
            str += "" + gmtMonth + "-";
            str += "" + gmtDay + " ";
            str += "" + gmtHour + ":";
            str += "" + gmtMinute;

            return str;

        }
        catch (Exception ex ){
            System.out.println(ex);
        }
        return "";

    }


    public String detectEndTime(String dura,String sstartTime){
        int duration = Integer.parseInt(dura);
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

            int gmtYear = c.get(Calendar.YEAR);
            int gmtMonth = c.get(Calendar.MONTH);
            int gmtDay = c.get(Calendar.DAY_OF_MONTH);
            int gmtHour = c.get(Calendar.HOUR_OF_DAY);
            int gmtMinute = c.get(Calendar.MINUTE);
            String str = "";
            str = "" + gmtYear + "-";
            str += "" + gmtMonth + "-";
            str += "" + gmtDay + " ";
            str += "" + gmtHour + ":";
            str += "" + gmtMinute;

            return str;

        }
        catch (Exception ex ){
            System.out.println(ex);
        }
        return "";

    }
}
