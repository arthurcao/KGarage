package com.kooltech.droid.kgarage.utils;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

/**
 * Created by NgocTien on 5/30/2015.
 */
public class MYLOG {
    // <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
    private static final MYLOG INSTANCE = new MYLOG();
    private BufferedWriter mBufferedWriter = null;
    private String nameApp = "MyGarage";

    public static void LOG(String msg){
        Log.e("20150530",msg);
    }
    public void close(){
        if(mBufferedWriter != null){
            try {
                mBufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mBufferedWriter = null;
    }

    public void open(){

        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
//        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/GarageDoor";
        String folder = nameApp;
        String p =path + "/" + folder ;
//        String p =path  ;

        Calendar c = Calendar.getInstance();
        int year =  c.get(Calendar.YEAR);
        int month =  c.get(Calendar.MONTH);
        int day =  c.get(Calendar.DAY_OF_MONTH);
        String name = year + "" + convertNumberToString(month) + "" + convertNumberToString(day);


        File f1 = new File(p);
        if(!f1.isDirectory()){
            f1.mkdirs();
        }
        try {

            mBufferedWriter = new BufferedWriter(new FileWriter(p + "/LOG_" + name, true));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            mBufferedWriter = null;
        } catch (IOException e) {
            mBufferedWriter = null;
        }
    }

    private MYLOG(){    }


    public static MYLOG getInstance(){
        return INSTANCE;
    }

    public void LOG(String tag, String message ){
        Log.e(tag, message);
    }

    public void saveLog (String tag, String message ){
        if (mBufferedWriter == null) {
            open();
        }

        if (mBufferedWriter != null) {
            String msg = "";
            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            int hour = c.get(Calendar.HOUR);
            int minute = c.get(Calendar.MINUTE);
            int second = c.get(Calendar.SECOND);

            msg += year + "/" + convertNumberToString(month) + "/" + convertNumberToString(day) + ":";
            msg += convertNumberToString(hour) + ":" + convertNumberToString(minute) + ":" + convertNumberToString(second) + ":";
            msg += tag + ">>>>";
            msg += message;
            writeLog(msg);
        }
        close();

    }

    private String convertNumberToString(int number){
        if(number < 9){
            return "0" + number;
        }
        return "" + number;
    }
    private void writeLog(String msg){
        if(mBufferedWriter != null) {
            try {

                mBufferedWriter.write(msg);
                mBufferedWriter.newLine();
            } catch (IOException e) {

                e.printStackTrace();
            }

        }
    }
}
