package com.kooltech.droid.kgarage.object;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Dinh on 3/3/2015.
 */
public class ItemAuthorizations implements Parcelable {

//    {"requests":[{
//          "request_notify":"1",
//          "request_door":"8",
//          "request_id":"134",
//          "request_endtime":"0",
//          "request_active":"1",
//          "request_guest":"5",
//          "door_status":"1",
//          "request_auth_notify":"1",
//          "user_email":"aq2",
//          "request_time":"2015-06-30 04:28:01"}],"status":0}

    public String request_notify;
    public String request_door;
    public String request_id;
    public String request_endtime;
    public String request_active;
    public String request_guest;
    public String door_status;
    public String request_auth_notify;
    public String user_email;
    public String request_time;
    public boolean isSetted;

   public ItemAuthorizations(){

   }


    public static final Parcelable.Creator<ItemAuthorizations> CREATOR      = new Parcelable.Creator<ItemAuthorizations>() {
        public ItemAuthorizations createFromParcel(Parcel in) {

            ItemAuthorizations item = new ItemAuthorizations();
            item.request_notify = in.readString();
            item.request_door = in.readString();
            item.request_id = in.readString();
            item.request_endtime = in.readString();
            item.request_active = in.readString();
            item.request_guest = in.readString();
            item.door_status = in.readString();
            item.request_auth_notify = in.readString();
            item.user_email = in.readString();
            item.request_time = in.readString();
            item.isSetted  = (in.readInt() == 0) ? false : true;
            return  item;
        }
        public ItemAuthorizations[] newArray(int size) {
            return new ItemAuthorizations[size];
        }

    };
    @Override
    public int describeContents() {

        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(request_notify);
        dest.writeString(request_door);
        dest.writeString(request_id);
        dest.writeString(request_endtime);
        dest.writeString(request_active);
        dest.writeString(request_guest);
        dest.writeString(door_status);
        dest.writeString(request_auth_notify);
        dest.writeString(user_email);
        dest.writeString(request_time);
        dest.writeInt(isSetted ? 1 : 0);
    }


}
