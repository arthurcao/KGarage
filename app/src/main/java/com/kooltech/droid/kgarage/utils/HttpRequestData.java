package com.kooltech.droid.kgarage.utils;

import android.content.Context;
import android.util.Log;

import com.kooltech.droid.kgarage.MySharedPreferences;
import com.kooltech.droid.kgarage.object.ItemAuthorizations;
import com.kooltech.droid.kgarage.object.ItemRequest;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dinh on 3/4/2015.
 */
public class HttpRequestData {

    public final static int REQUEST_LOAD_DOOR = 102;
    public final static int REQUEST_LOGIN = 103;
    public final static int REQUEST_OPEN_DOOR = 104;
    public final static int REQUEST_CLOSE_DOOR = 105;
    public final static int REQUEST_GET_REQUEST_LIST = 106;
    public final static int REQUEST_GEST_REQUEST = 107;
    public final static int REQUEST_SET_REQUEST_RESPONSE = 108;
    public final static int REQUEST_REQUEST_LIST_AUTHORIZATION = 109;
    public final static int REQUEST_DELETE_AUTHORIZATION = 110;
    public final static int REQUEST_DELETE_GEST = 111;
    public final static int REQUEST_CONTROL_GUEST = 112;
    public final static int REQUEST_LOGOUT = 113;
    public final static int REQUEST_AUTO_CLOSE = 114;
    public final static int REQUEST_CHECK_STATUS = 115;
    public final static int REQUEST_LOGIN_FACEBOOK = 116;
    public final static int REQUEST_GUEST_TURN_ON_OFF_NOTIFICATION = 117;

    public String url="";
    public int requestFunction=0;
    public List<NameValuePair> nameValuePair;

    public HttpRequestData(int requestFunction, String url, ArrayList<NameValuePair> nameValuePair){
        this.url = url;
        this.requestFunction = requestFunction;
        this.nameValuePair = nameValuePair;

    }

    public static void doorResponse(Context context, String control_id ,int status){
//        Sau khi đóng hoặc mở cổng bằng lệnh từ trên server. Gateway phản hồi lại server để loại bỏ lệnh vừa thực hiện
//
//        function: doorResponse
//         * question:
//              - (int)control_id
//                  + (int) control id number: id của lệnh đã thực hiện.
//              - (int)status
//                  + 1 - thực hiện lệnh thành công
//                  + 0 - lệnh không thực hiện được do không tìm thấy cửa
//                  + 2 - lệnh không thực hiện được lý do khác
//          * return:
//              - (int)status
//                  + 0 - (int) thành công, không có lỗi
//                  + 1 - (int) có lỗi phát sinh
        ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
        nameValuePair.add(new BasicNameValuePair("control_id",control_id));
        nameValuePair.add(new BasicNameValuePair("status",""+status));
        HttpRequestData httpRequestData = new HttpRequestData(REQUEST_LOGIN_FACEBOOK, "http://kooltechs.com/garage/doorResponse", nameValuePair);
        new HttpPostToServer(context).execute(httpRequestData);

    }


    public static void loginWithAccount(Context context, String user_email,String user_password, int user_phone_type, String user_phone_key){
//    function: login
//        question:
//           (str)user_email
//           (str)user_password
//           (int)user_phone_type
//          `       1 - Android
//                  2 - iOs
//           (str)user_phone_key
//        return
//           (int)status
//               0 - Dang nhap thanh cong
//               1 - co loi, tam thoi  chua tra ve loi
//            (obj)user
//               (str)user_email
//               (str)user_id
//               (str)user_firstname - chua co
//               (str)user_lastname - chua co

        ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(4);
        nameValuePair.add(new BasicNameValuePair("user_email",user_email));
        nameValuePair.add(new BasicNameValuePair("user_password",user_password));
        nameValuePair.add(new BasicNameValuePair("user_phone_type",""+user_phone_type));
        nameValuePair.add(new BasicNameValuePair("user_phone_key",user_phone_key));
        HttpRequestData httpRequestData = new HttpRequestData(REQUEST_LOGIN, "http://kooltechs.com/garage/login", nameValuePair);
        new HttpPostToServer(context).execute(httpRequestData);

    }

    public static void loginWithFacebook(Context context, String fb_access_token){
        //        function: loginfb
        //        question:
        //             - (str)access_token
        //        return
        //            - (int)status
        //                  0 - Đăng nhập thành công
        //                  1 - Có lỗi, tạm thời chưa trả về lỗi
        //            - (obj)user

        String user_phone_key = MySharedPreferences.loadRegisterID(context);
        Log.e("test1","fb_access_token: " + fb_access_token);
        Log.e("test1","user_phone_key: " + user_phone_key);
        ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
        nameValuePair.add(new BasicNameValuePair("access_token",fb_access_token));
        nameValuePair.add(new BasicNameValuePair("user_phone_type","1"));
        nameValuePair.add(new BasicNameValuePair("user_phone_key",user_phone_key));
        HttpRequestData httpRequestData = new HttpRequestData(REQUEST_LOGIN_FACEBOOK, "http://kooltechs.com/garage/loginfb", nameValuePair);
        new HttpPostToServer(context).execute(httpRequestData);


    }

    public static void logout(Context context){
    //        function: logout
    //        question:
    //        return:
    //          - status
    //              + 0 - Logout
    //              + 1 - Error
        ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(0);
        HttpRequestData httpRequestData = new HttpRequestData(REQUEST_LOGOUT, "http://kooltechs.com/garage/logout", nameValuePair);
        new HttpPostToServer(context).execute(httpRequestData);

    }




    public static void requestResponse(Context context, ItemAuthorizations item){
//        function: requestResponse
//        question
//          - request_id
//          - request_active:
//              + 0 - Don't accept, remove request
//              + 1 - Accept request
//          - request_endtime: thời gian yêu cầu cấp quyền, tổng thời gian tính bằng phút
//          - request_time:  format 2015-03-10 05:32:38
//          - request_auth_notify
//          - request_notify
//        return
//          -  status
//              + 0 - No error
//              + 1 - Have some error
//              + 2 - Don't find request
//              + 3 - Khong accept duoc do khong phai master
//              + 4 - Khong xoa record duoc do khong phai master hoac guest
        ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(10);
        nameValuePair.add(new BasicNameValuePair("request_notify", item.request_notify));
        nameValuePair.add(new BasicNameValuePair("request_door", item.request_door));
        nameValuePair.add(new BasicNameValuePair("request_id", item.request_id));
        nameValuePair.add(new BasicNameValuePair("request_endtime", item.request_endtime));
        nameValuePair.add(new BasicNameValuePair("request_active", item.request_active));
        nameValuePair.add(new BasicNameValuePair("request_guest", item.request_guest));
        nameValuePair.add(new BasicNameValuePair("door_status", item.door_status));
        nameValuePair.add(new BasicNameValuePair("request_auth_notify", item.request_auth_notify));
        nameValuePair.add(new BasicNameValuePair("user_email", item.user_email));
        nameValuePair.add(new BasicNameValuePair("request_time", item.request_time));
        HttpRequestData httpRequestData = new HttpRequestData(REQUEST_SET_REQUEST_RESPONSE, "http://kooltechs.com/garage/requestResponse", nameValuePair);
        new HttpPostToServer(context).execute(httpRequestData);

    }

    public static void requestResponseDelete(Context context, ItemRequest item){
//        function: requestResponse
//        question
//          - request_id
//          - request_active:
//              + 0 - Don't accept, remove request
//              + 1 - Accept request
//          - request_endtime: thời gian yêu cầu cấp quyền, tổng thời gian tính bằng phút
//          - request_time:  format 2015-03-10 05:32:38
//          - request_auth_notify
//          - request_notify
//        return
//          -  status
//              + 0 - No error
//              + 1 - Have some error
//              + 2 - Don't find request
//              + 3 - Khong accept duoc do khong phai master
//              + 4 - Khong xoa record duoc do khong phai master hoac guest
        ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(10);
        nameValuePair.add(new BasicNameValuePair("request_notify", item.request_notify));
        nameValuePair.add(new BasicNameValuePair("request_door", item.request_door));
        nameValuePair.add(new BasicNameValuePair("request_id", item.request_id));
        nameValuePair.add(new BasicNameValuePair("request_endtime", item.request_endtime));
        nameValuePair.add(new BasicNameValuePair("request_active", item.request_active));
        nameValuePair.add(new BasicNameValuePair("request_guest", item.request_guest));
        nameValuePair.add(new BasicNameValuePair("door_status", item.door_status));
        nameValuePair.add(new BasicNameValuePair("request_auth_notify", item.request_auth_notify));
        nameValuePair.add(new BasicNameValuePair("user_email", item.user_email));
        nameValuePair.add(new BasicNameValuePair("request_time", item.request_time));
        HttpRequestData httpRequestData = new HttpRequestData(REQUEST_DELETE_GEST, "http://kooltechs.com/garage/requestResponse", nameValuePair);
        new HttpPostToServer(context).execute(httpRequestData);

    }


    public static void requestTurnOnOffNotificationGues(Context context,String request_notify, String request_id){
//       function: guestConfig
//        question:
//          request_notify
//          request_id
//        return:
//           1: no login
//          2: do not access

        Log.e("test","requestTurnOnOffNotificationGues request_notify: " + request_notify);
        Log.e("test","requestTurnOnOffNotificationGues request_id: " + request_id);
        ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
        nameValuePair.add(new BasicNameValuePair("request_notify", request_notify));
        nameValuePair.add(new BasicNameValuePair("request_id", request_id));
        HttpRequestData httpRequestData = new HttpRequestData(REQUEST_GUEST_TURN_ON_OFF_NOTIFICATION, "http://kooltechs.com/garage/guestconfig", nameValuePair);
        new HttpPostToServer(context).execute(httpRequestData);

    }

    public static void ControlDoor(Context context,String control_status, String control_door){

//        function: doorControl
//        question:
//          control_status
//              0 - open
//              1 - close
//          control_door
//              id of door what you want to control
//        return
//          status
//              0 - no error
//              1 - login error
//              2 - hasn't any door
//              3 - don't authorities
//              4 - hết giờ
//              5 - door busy
//
        ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(10);
        nameValuePair.add(new BasicNameValuePair("control_status", control_status));
        nameValuePair.add(new BasicNameValuePair("control_door", control_door));
        HttpRequestData httpRequestData = new HttpRequestData(REQUEST_CONTROL_GUEST, "http://kooltechs.com/garage/doorControl", nameValuePair);
        new HttpPostToServer(context).execute(httpRequestData);
    }


    public static void getRequestList(Context context){
        ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
        nameValuePair.add(new BasicNameValuePair("masterList", "0"));
        HttpRequestData httpRequestData = new HttpRequestData(HttpRequestData.REQUEST_GET_REQUEST_LIST, "http://kooltechs.com/garage/requestsList", nameValuePair);
        new HttpPostToServer(context).execute(httpRequestData);
    }
}
