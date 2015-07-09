package com.kooltech.droid.kgarage.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.kooltech.droid.kgarage.utils.HttpPostToServer;
import com.kooltech.droid.kgarage.utils.HttpRequestData;
import com.kooltech.droid.kgarage.R;
import com.kooltech.droid.kgarage.SetTimeAuthorizationsActivity;
import com.kooltech.droid.kgarage.adapter.AuthorizationsAdapter;
import com.kooltech.droid.kgarage.object.ItemAuthorizations;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

/**
 * Created by Dinh on 3/2/2015.
 */
public class AuthorizationsFragment extends Fragment {
    private Context mContext;
    public static ListView RList=null;
    public static ArrayList<ItemAuthorizations> arrItemAuthorizations = new ArrayList<ItemAuthorizations>();
    public static AuthorizationsAdapter adapter = null;
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static AuthorizationsFragment newInstance(int sectionNumber) {
        AuthorizationsFragment fragment = new AuthorizationsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public AuthorizationsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
        nameValuePair.add(new BasicNameValuePair("masterList", "1"));
        HttpRequestData httpRequestData = new HttpRequestData(HttpRequestData.REQUEST_REQUEST_LIST_AUTHORIZATION, "http://kooltechs.com/garage/requestsList", nameValuePair);
        new HttpPostToServer(mContext).execute(httpRequestData);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.request_main, container, false);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
//        ((MainActivity) activity).onSectionAttached(        getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        RList = (ListView) getActivity().findViewById(R.id.listView);
        arrItemAuthorizations = new ArrayList<ItemAuthorizations>();
        adapter = new AuthorizationsAdapter(getActivity(), R.layout.request_item_layout, arrItemAuthorizations);
        RList.setAdapter(adapter);
//        RList.setLongClickable(true);
        RList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
               final int pos = position;
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("Do you want delete this item?");
                builder.setCancelable(true);
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
                        nameValuePair.add(new BasicNameValuePair("request_id", arrItemAuthorizations.get(pos).request_id));
                        nameValuePair.add(new BasicNameValuePair("request_active", "0"));
                        HttpRequestData httpRequestData = new HttpRequestData(HttpRequestData.REQUEST_DELETE_AUTHORIZATION, "http://kooltechs.com/garage/requestResponse", nameValuePair);
                        new HttpPostToServer(mContext).execute(httpRequestData);
                    }
                });
                builder.setNegativeButton("Cancel", null);
                AlertDialog dialog = builder.create();
                dialog.show();
                return false;
            }
        });

        RList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Log.d("Click>>>>>", arrRequestList.get(position).user_email);
                ItemAuthorizations item = arrItemAuthorizations.get(position);
//                if(item.request_active.equals("0")) {
//                    Intent i = new Intent(mContext, SetTimeAuthorizationsActivity.class);
//                    i.putExtra("REQUESTID", item.request_id);
//                    startActivityForResult(i, 110);
//                }

                Intent i = new Intent(mContext, SetTimeAuthorizationsActivity.class);
                i.putExtra("REQUESTID", item.request_id);
                i.putExtra("ITEM", item);
                startActivityForResult(i, 110);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 110 && resultCode == Activity.RESULT_OK){
            ItemAuthorizations item = data.getParcelableExtra("ITEM");
            item.request_active ="1";
            HttpRequestData.requestResponse(mContext,item);
        }
    }
}
