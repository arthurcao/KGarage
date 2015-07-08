package com.example.dinh.kgarage.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.dinh.kgarage.utils.HttpPostToServer;
import com.example.dinh.kgarage.utils.HttpRequestData;
import com.example.dinh.kgarage.MySharedPreferences;
import com.example.dinh.kgarage.R;

import org.apache.http.NameValuePair;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link LogoutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LogoutFragment extends Fragment {




    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment LogoutFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LogoutFragment newInstance() {
        LogoutFragment fragment = new LogoutFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    public LogoutFragment() {
        // Required empty public constructor
    }


    Button mBtnLogout;
    TextView mTVUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {

       View rootView = inflater.inflate(R.layout.logout_main,container,false);
        mBtnLogout = (Button) rootView.findViewById(R.id.logout_logout);
        mTVUser = (TextView) rootView.findViewById(R.id.logout_email);
        TextView textView = new TextView(getActivity());
        textView.setText(R.string.hello_blank_fragment);
        mBtnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(0);
                HttpRequestData httpRequestData = new HttpRequestData(HttpRequestData.REQUEST_LOGOUT, "http://kooltechs.com/garage/logout", nameValuePair);
                new HttpPostToServer(mContext).execute(httpRequestData);
            }
        });
        String user = MySharedPreferences.loadUser(mContext);
        mTVUser.setText(user);
        return rootView;
    }

    Context mContext;

    @Override
    public void onAttach(Activity activity) {
        mContext = activity;
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }



}
