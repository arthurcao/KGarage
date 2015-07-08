package com.example.dinh.kgarage.MyGarage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.dinh.kgarage.utils.HttpPostToServer;
import com.example.dinh.kgarage.utils.HttpRequestData;
import com.example.dinh.kgarage.MainActivity;
import com.example.dinh.kgarage.R;
import com.example.dinh.kgarage.MyGarage.DoorView.DoorViewListener;
import com.example.dinh.kgarage.utils.MYLOG;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

public class DoorFragment extends Fragment {

	public static DoorFragment create(String id, int open, int battery){
		DoorFragment fr = new DoorFragment();
		Bundle args = new Bundle();
//     	args.putString("NAME", name);
     	args.putString("ID", id);
     	args.putInt("Open", open);
        args.putInt("Battery", battery);
     	fr.setArguments(args);
		return fr;
	}
	
	@Override
 	public void onCreate(Bundle savedInstanceState) {
     	super.onCreate(savedInstanceState);
     	if(getArguments() != null){
//     		mName = getArguments().getString("NAME") ;
     		mID = getArguments().getString("ID") ;
     		isOpen = getArguments().getInt("Open") ;
            mBattery = getArguments().getInt("Battery") ;
     	}


 	}
	String mID = "";
	String mName = "";
	int isOpen = 0;
    int mBattery;
	Context mContext;
	public DoorView mDoorView;
	TextView mTite;
    TextView mTVBattery;

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_door,container, false);
		mTite = (TextView) rootView.findViewById(R.id.fragment_door_title);
        mTVBattery = (TextView) rootView.findViewById(R.id.fragment_door_battery);
        mTVBattery.setText("Battery: " + mBattery + "%");


		mTite.setText("Door " + mID);
		mDoorView = (DoorView)rootView.findViewById(R.id.fragment_door_mydoor);
        Button control = (Button) rootView.findViewById(R.id.fragment_door_control);
        control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(mDoorView.isBusy){
//                    return;
//                }
                Intent i = new Intent(MainActivity.SHOW_DIALOG);
                mContext.sendBroadcast(i);
                Log.e("20150529","isOpen: " + isOpen);
                if(isOpen == 0){
//                    isOpen= 1;
//                    mDoorView.close();
                    ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
                    nameValuePair.add(new BasicNameValuePair("control_status", "1"));
                    nameValuePair.add(new BasicNameValuePair("control_door", mID));
                    HttpRequestData httpRequestData = new HttpRequestData(HttpRequestData.REQUEST_CLOSE_DOOR, "http://kooltechs.com/garage/doorControl", nameValuePair);
                    new HttpPostToServer(mContext).execute(httpRequestData);
                }else{
//                    isOpen= 0;
//                    mDoorView.open();
                    ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
                    nameValuePair.add(new BasicNameValuePair("control_status", "0"));
                    nameValuePair.add(new BasicNameValuePair("control_door", mID));
                    HttpRequestData httpRequestData = new HttpRequestData(HttpRequestData.REQUEST_OPEN_DOOR, "http://kooltechs.com/garage/doorControl", nameValuePair);
                    new HttpPostToServer(mContext).execute(httpRequestData);
                }
            }
        });

        if(isOpen == 1){
//            isOpen = 0;
            mDoorView.setOpen(false);
        }else{
//            isOpen = 1;
            mDoorView.setOpen(true);
        }
		mDoorView.setDoorViewListener(new DoorViewListener() {
			
			@Override
			public void DoorStarOpen() {

			}
			
			@Override
			public void DoorStarClose() {

			}
			
			@Override
			public void ActionFinished() {
                checkStatusDoor();
			}
		});
//		mDoorView = rootView;
		return rootView;
	}


	@Override
	public void onAttach(Activity activity) {
		mContext = activity;
		super.onAttach(activity);
	}

    public void updateStatus(int status){
        MYLOG.LOG("door update status: " + status);
        isOpen = status;
        if(isOpen == 1){
//            isOpen = 0;
            MYLOG.LOG("door close ");
            mDoorView.setOpen(false);
        }else{
//            isOpen = 1;
            MYLOG.LOG("door open ");
            mDoorView.setOpen(true);
        }
    }
	public void open(){
        MYLOG.getInstance().LOG("onReceive", "DoorFragment >>>> open");
		mDoorView.open();
        isOpen = 0;
	}
	
	public void close(){
        MYLOG.getInstance().LOG("onReceive", "DoorFragment >>>> close");
        isOpen = 1;
		mDoorView.close();
	}

    public  boolean checkIsOpen(){
        if(isOpen == 0){
            return true;
        }
        return false;
    }

    public void setBattery(int battery){
        mBattery = battery;
       mTVBattery.setText("Battery: " + mBattery + "%");
    }

    private void checkStatusDoor(){
        ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(0);
        HttpRequestData httpRequestData = new HttpRequestData(HttpRequestData.REQUEST_CHECK_STATUS, "http://kooltechs.com/garage/getDoor", nameValuePair);
        new HttpPostToServer(mContext).execute(httpRequestData);
    }

}
