package com.kooltech.droid.kgarage.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kooltech.droid.kgarage.R;
import com.kooltech.droid.kgarage.object.ItemRequest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Dinh on 3/3/2015.
 */
public class RequestsAdapter extends ArrayAdapter<ItemRequest> {
    Activity context = null;
    ArrayList<ItemRequest> myArray = null;
    int layoutId;

    static class ViewHolder {
        public TextView mName;
        public TextView mDecription;

    }



    public RequestsAdapter(Activity context, int layoutId, ArrayList<ItemRequest> arr) {
        super(context, layoutId, arr);
        this.context = context;
        this.layoutId = layoutId;
        this.myArray = arr;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = convertView;
        if (rowView == null) {
            rowView = inflater.inflate(layoutId, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.mName = (TextView) rowView.findViewById(R.id.request_item_door_name);
            viewHolder.mDecription = (TextView) rowView.findViewById(R.id.request_item_description);
            rowView.setTag(viewHolder);
        }
        ViewHolder holder = (ViewHolder) rowView.getTag();
        ItemRequest item = myArray.get(position);
        holder.mName.setText("Door ID: " + item.request_door);
        String str = "";
        int color = 0xFF2196F3;
        if(item.request_active.equals("0")){
            //chua active
             str = "Request on" + convertRequestFromTime(item.request_time);
//            color = 0xFF2196F3;
            color = 0xFFCC6633;
        }else{
            //Da Active 0xFF9E9E9E
            int time = Integer.parseInt(item.request_endtime);
            if(time <= 0){
                str = "Forever";
            }else{
                str = "From " + convertRequestFromTime(item.request_time)  + " to " + detectEndTime(item.request_endtime, item.request_time);
            }
//            color = 0xFF9E9E9E;
            color = 0xFF0066CC;
        }
        holder.mDecription.setText(str);
        rowView.setBackgroundColor(color);

//        }
        return rowView;
    }



    public String convertRequestFromTime(String sstartTime){

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
