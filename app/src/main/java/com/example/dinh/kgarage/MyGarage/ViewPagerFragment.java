package com.example.dinh.kgarage.MyGarage;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dinh.kgarage.utils.HttpPostToServer;
import com.example.dinh.kgarage.utils.HttpRequestData;
import com.example.dinh.kgarage.R;
import com.example.dinh.kgarage.utils.MYLOG;

import org.apache.http.NameValuePair;

import java.util.ArrayList;

public class ViewPagerFragment extends Fragment {

    public  ViewPagerAdapter mAdapter;
    public  ViewPager mPager;
    private  Context mContext;
    private Activity mActivity;
    private int mCurrentItem = -1;


    public ItemDoor getCurrentDoor(){
        int  p = mPager.getCurrentItem();
        ItemDoor item = null;
        if(p < mListDoorFragment.size() && p >= 0){

            item = mListDoorFragment.get(p);
        }
        return item;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.view_pager_fragment, container, false);
        Log.e("20150522", "onCreateView");
        if(mListDoorFragment == null){
            mListDoorFragment = new ArrayList<>();
        }
        mListDoorFragment.clear();
        mAdapter = new ViewPagerAdapter(getChildFragmentManager(),mListDoorFragment);
        mPager = (ViewPager) rootView.findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrentItem = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {


            }
        });

        resumeReceiver();
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);



    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopReceiver();
    }


    @Override
    public void onAttach(Activity activity) {
        mContext = activity;
        mActivity = activity;
        super.onAttach(activity);
    }

    @Override
    public void onResume() {
        loadDoor();
        super.onResume();
    }

    public void loadDoor(){
        ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(0);
        HttpRequestData httpRequestData = new HttpRequestData(HttpRequestData.REQUEST_LOAD_DOOR, "http://kooltechs.com/garage/getDoor", nameValuePair);
        new HttpPostToServer(mContext).execute(httpRequestData);
    }

    public void updateStatus(ArrayList<ItemDoor> list){

        for(ItemDoor item1 :list){
            for(ItemDoor item2 :mListDoorFragment){
                if(item1.door_id.equals(item2.door_id)){
                    item2.door_status = item1.door_status;
                    if(item2.mFragment != null){
                        item2.mFragment.updateStatus(item2.door_status);
                    }
                    break;
                }
            }
        }
    }


    public  void updateListDoor(ArrayList<ItemDoor> list){

        clearDoor();

        ArrayList<String> listID = new ArrayList<>();

        for(int i = 0; i < list.size(); i++){
            ItemDoor item = new ItemDoor();
            item.door_id = list.get(i).door_id;
            item.door_status = list.get(i).door_status;
            item.door_battery = list.get(i).door_battery;
            item.door_auto_begin = list.get(i).door_auto_begin;
            item.door_auto_end = list.get(i).door_auto_end;
            item.door_auto_enable = list.get(i).door_auto_enable;
            item.door_auto_timer = list.get(i).door_auto_timer;

            mListDoorFragment.add(item);
            listID.add(list.get(i).door_id);

        }
        mAdapter.notifyDataSetChanged();

        if(mContext != null){
            Intent i = new Intent(MyWebsocketService.SEND_COMMAND_3);
            i.putStringArrayListExtra("LISTID",listID);
            mContext.sendBroadcast(i);
        }

    }

    public void clearDoor(){
        if(mListDoorFragment != null){
            mListDoorFragment.clear();
            if(mAdapter != null){
//                mActivity
                mAdapter.notifyDataSetChanged();
            }
        }

    }
    public void addDoor(ItemDoor item){
        if(mListDoorFragment != null){
            mListDoorFragment.add(item);
            mAdapter.notifyDataSetChanged();
        }
    }
   private  ArrayList<ItemDoor> mListDoorFragment;

    public  class ViewPagerAdapter extends FragmentPagerAdapter {
    	 ArrayList<ItemDoor> mListDoor;
        int mNotifyChanged;
        public ViewPagerAdapter(FragmentManager fm, ArrayList<ItemDoor> list) {
            super(fm);
            mNotifyChanged = 0;
            mListDoor = list;
        }


        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int num) {
            ItemDoor item = mListDoor.get(num);
            mListDoor.get(num).mFragment =  DoorFragment.create(item.door_id, item.door_status, item.door_battery);

        	return mListDoor.get(num).mFragment;
           
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ItemDoor item = mListDoor.get(position);
            if(mListDoor.get(position).mFragment == null){
                Fragment fragment = (Fragment) super.instantiateItem(container, position);
                mListDoor.get(position).mFragment = (DoorFragment)  fragment;
                return  mListDoor.get(position).mFragment;
            }

            return  super.instantiateItem(container, position);
        }



        @Override
        public int getCount() {
            return mListDoor.size();
        }
    }

    public void close(String id){
        MYLOG.getInstance().LOG("onReceive", "close  id:" + id);
        if(mListDoorFragment != null){
            for(ItemDoor item:mListDoorFragment){

                if(item.mFragment != null){
                    //TODO tien
                    if(item.door_id.equals(id)){
                        item.mFragment.close();
                    }
                }
            }
        }
    }

    public void open(String id){
        MYLOG.getInstance().LOG("open", "open  id:" + id);
        MYLOG.getInstance().LOG("open", "mListDoorFragment:" + mListDoorFragment);
        if(mListDoorFragment != null){
            for(ItemDoor item:mListDoorFragment){
                MYLOG.getInstance().LOG("open", "item.mFragment:" + item.mFragment);
                if(item.mFragment != null){
                    //TODO tien
                    if(item.door_id.equals(id)){
                        item.mFragment.open();
                    }
                }
            }
        }
    }

    public void updateDoorStatus(String id, int status){
        Log.e("20150526","command 1 updateDoorStatus door id: " +  id + " == status: " + status);
        if(mListDoorFragment != null){
            for(ItemDoor item:mListDoorFragment){
                if(item.mFragment != null){
                    //TODO tien
                    if(item.door_id.equals(id)){

                        if(status == 0){
                            //open
                            item.mFragment.open();
//                            if(!item.mFragment.checkIsOpen()){
//                                item.mFragment.open();
//                            }
                        }else{
                            //to do close
                            item.mFragment.close();
//                            if(item.mFragment.checkIsOpen()){
//                                item.mFragment.close();
//                            }
                        }
                    }
                }
            }
        }
    }

    public void updateBattery(String id, int battery){
        if(mListDoorFragment != null){
            for(ItemDoor item:mListDoorFragment){
                if(item.mFragment != null){
                    //TODO tien
                    if(item.door_id.equals(id)){
                        item.mFragment.setBattery(battery);
                        break;
                    }
                }
            }
        }
    }

    public final static String UPDATE_UI = "UPDATE_UI";
    private void resumeReceiver() {
        if(mGattUpdateReceiver == null){
            mGattUpdateReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (UPDATE_UI.equals(action)) {
                        if(mAdapter != null){
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }
            } ;
//            Log.e("www"," mContext.registerReceiver");
            mContext.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        }
    }

    private void stopReceiver() {
        if(mGattUpdateReceiver != null){
//            Log.e("www"," mContext.unregisterReceiver");
            mContext.unregisterReceiver(mGattUpdateReceiver);
            mGattUpdateReceiver = null;
        }
    }
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UPDATE_UI);

        return intentFilter;
    }
    private  BroadcastReceiver mGattUpdateReceiver = null;

}
