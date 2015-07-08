package com.example.dinh.kgarage.MyGarage;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by NgocTien1 on 3/20/2015.
 */
public class ItemDoor implements Parcelable {

    public String door_id;
    public int door_status;
    public int door_battery;
    public String door_auto_begin;
    public String door_auto_end;
    public String door_auto_enable; //1: active, 0: not active
    public String door_auto_timer;

    public DoorFragment mFragment;

    public ItemDoor() {
        door_id = "";
        mFragment = null;
        door_battery = -1;

        door_id = "";
        door_status = 0;
        door_battery = 0;
        door_auto_begin = "";
        door_auto_end = "";
        door_auto_enable = "";
        door_auto_timer = "";
    }

    public static final Parcelable.Creator<ItemDoor> CREATOR      = new Parcelable.Creator<ItemDoor>() {
        public ItemDoor createFromParcel(Parcel in) {

            ItemDoor item = new ItemDoor();
            item.door_id = in.readString();
            item.door_status = in.readInt();
            item.door_battery = in.readInt();
            item.door_auto_begin = in.readString();
            item.door_auto_end = in.readString();
            item.door_auto_enable = in.readString();
            item.door_auto_timer = in.readString();
            return  item;
        }
        public ItemDoor[] newArray(int size) {
            return new ItemDoor[size];
        }

    };
    @Override
    public int describeContents() {

        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(door_id);
        dest.writeInt(door_status);
        dest.writeInt(door_battery);
        dest.writeString(door_auto_begin);
        dest.writeString(door_auto_end);
        dest.writeString(door_auto_enable);
        dest.writeString(door_auto_timer);


    }


}
