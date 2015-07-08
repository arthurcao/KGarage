package com.example.dinh.kgarage.utils;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Message;
import android.util.Log;

import com.example.dinh.kgarage.MainActivity;
import com.example.dinh.kgarage.MyGarage.MyWebsocketService;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by Dinh on 3/3/2015.
 */
public class HttpPostToServer extends AsyncTask<HttpRequestData, String, Message> {

    Context mContext;

    public HttpPostToServer(Context c){
        mContext = c;
    }


    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
    }

    @Override
    protected Message doInBackground(HttpRequestData... params) {
        int request = 0;
        int command_error = 0;
        String message ="";
        Intent i = new Intent(MyWebsocketService.RECEIVE_DATA_POST_SERVER);
        request = params[0].requestFunction;

        Message msg = new Message();
        try {
            HttpPost method = new HttpPost(params[0].url);


            //Encoding POST data
            try {
                method.setEntity(new UrlEncodedFormEntity(params[0].nameValuePair));
            } catch (UnsupportedEncodingException e) {
                // log exception
                e.printStackTrace();
            }

            HttpResponse response = MainActivity.httpclient.execute(method);
            HttpEntity entity = response.getEntity();
            MYLOG.getInstance().LOG("test",">>>>>>>>>>entity " + entity);
            if (entity != null) {
                command_error = 0;
                msg.arg1=0;
                msg.arg2=params[0].requestFunction;
                String str = EntityUtils.toString(entity);

                MYLOG.getInstance().LOG("test",">>>>>>>>>>str " + str);
                message = str;
                msg.obj = str;
                if(request == HttpRequestData.REQUEST_OPEN_DOOR ||request == HttpRequestData.REQUEST_CLOSE_DOOR ){
                    List<NameValuePair> list = params[0].nameValuePair;
                    NameValuePair pair = list.get(1);
                    String id =  pair.getValue();
                    i.putExtra("DOORID",id);
                }

            } else {
                command_error = 1;
                msg.arg1=1;
                msg.obj = "No string.";
            }


        } catch (Exception e) {
            command_error = 1;
            msg.arg1=0;
            msg.obj = "Network problem";
        }
        MYLOG.getInstance().LOG("test","request: " + request);
        MYLOG.getInstance().LOG("test","command_error: " + command_error);
        MYLOG.getInstance().LOG("test","message: " + message);
        MYLOG.getInstance().LOG("test","mContext: " + mContext);
        MYLOG.getInstance().LOG("test","-----------------------------------------");
        if(mContext != null){
            MYLOG.getInstance().LOG("test","m mContext.sendBroadcast(i)" );
            i.putExtra("REQUEST", request);
            i.putExtra("COMMNAD_ERROR",command_error);
            i.putExtra("MESSAGE",message);
            mContext.sendBroadcast(i);
            Log.e("test", "m mContext.sendBroadcast(i)");
        }

        return msg;
    }

    @Override
    public void onPostExecute(Message result){
        super.onPostExecute(result);
//        MainActivity.mainHandler.sendMessage(result);
    }
}
