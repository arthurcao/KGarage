package com.kooltech.droid.kgarage.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kooltech.droid.kgarage.utils.HttpRequestData;
import com.kooltech.droid.kgarage.MySharedPreferences;
import com.kooltech.droid.kgarage.R;
import com.kooltech.droid.kgarage.utils.HttpRequestListener;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

/**
 * Created by Dinh on 3/2/2015.
 */
public class LoginFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    public Button btnLogIn;
    public EditText etEmail, etPassword;
    Context mContext;

    public TextView txtUser;
    private Button btnLogOut;
    private LinearLayout mLayoutLogin;
    private LoginButton mLoginButton;
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;

    public static LoginFragment newInstance(int sectionNumber) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public LoginFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        FacebookSdk.sdkInitialize(getApplicationContext());
        View rootView = inflater.inflate(R.layout.login_main, container, false);
        mLayoutLogin = (LinearLayout) rootView.findViewById(R.id.login_main_layout_login);
        btnLogOut = (Button) rootView.findViewById(R.id.login_main_button_logout);
        txtUser = (TextView) rootView.findViewById(R.id.login_main_user);

        callbackManager = CallbackManager.Factory.create();
        mLoginButton = (LoginButton) rootView.findViewById(R.id.login_main_loginfacebook);
        mLoginButton.setReadPermissions("user_friends");
        // If using in a fragment
        mLoginButton.setFragment(this);
        // Other app specific specialization
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                // App code
                if (currentAccessToken == null) {
                    //write your code here what to do when user logout
                    mLayoutLogin.setVisibility(View.VISIBLE);
                    mLoginButton.setVisibility(View.VISIBLE);
                    btnLogOut.setVisibility(View.INVISIBLE);
                    txtUser.setVisibility(View.INVISIBLE);
                    HttpRequestData.logout(mContext);

                }
            }
        };

        // Callback registration
        mLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                String token = loginResult.getAccessToken().getToken();
                loginFacebook(token);

                mLayoutLogin.setVisibility(View.INVISIBLE);
                mLoginButton.setVisibility(View.VISIBLE);
                btnLogOut.setVisibility(View.INVISIBLE);
                txtUser.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancel() {
                // App code

            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });


//        boolean login = MySharedPreferences.loadLoginStatus(mContext);
//        if (login) {
//            //auto login
//            mLayoutLogin.setVisibility(View.GONE);
//
//            boolean autoLogin = MySharedPreferences.loadAutoLogin(mContext);
//            int type = MySharedPreferences.loadLoginType(mContext);
//
//            if (type == 1) {
//                btnLogOut.setVisibility(View.VISIBLE);
//                mLoginButton.setVisibility(View.INVISIBLE);
//            }else{
//                btnLogOut.setVisibility(View.INVISIBLE);
//                mLoginButton.setVisibility(View.VISIBLE);
//            }
//
//
//            if(autoLogin) {
//                if (type == 1) {
//                    //login account
//                    String user_email = MySharedPreferences.loadUser(mContext);
//                    String user_password = MySharedPreferences.loadPassword(mContext);
//                    String user_phone_key = MySharedPreferences.loadRegisterID(mContext);
//                    loginAccount(user_email, user_password, user_phone_key);
//
//                } else {
//                    //login facebook
//                    if (checkFacebookLogin()) {
//                        AccessToken accessToken = AccessToken.getCurrentAccessToken();
//                        String token = accessToken.getToken();
//                        loginFacebook(token);
//                    }
//
//                }
//            }
//        } else {
//
//            mLayoutLogin.setVisibility(View.VISIBLE);
//            mLoginButton.setVisibility(View.VISIBLE);
//            btnLogOut.setVisibility(View.INVISIBLE);
//
//        }

        if (checkFacebookLogin()) {

            mLayoutLogin.setVisibility(View.GONE);
            btnLogOut.setVisibility(View.GONE);
            txtUser.setVisibility(View.GONE);
            mLoginButton.setVisibility(View.VISIBLE);




            MySharedPreferences.saveLoginStatus(mContext, true);
            boolean autoLogin = MySharedPreferences.loadAutoLogin(mContext);
//            autoLogin = true;
            if(autoLogin){
                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                loginFacebook(accessToken.getToken());
            }

        }else{
            boolean login = MySharedPreferences.loadLoginStatus(mContext);
            if (login) {
                mLayoutLogin.setVisibility(View.GONE);
                btnLogOut.setVisibility(View.VISIBLE);
                txtUser.setVisibility(View.VISIBLE);
                String user = MySharedPreferences.loadUser(mContext);
                txtUser.setText("User: " + user);

                mLoginButton.setVisibility(View.GONE);
                boolean autoLogin = MySharedPreferences.loadAutoLogin(mContext);
                if(autoLogin){
                    String user_email = MySharedPreferences.loadUser(mContext);
                    String user_password = MySharedPreferences.loadPassword(mContext);
                    String user_phone_key = MySharedPreferences.loadRegisterID(mContext);
                    loginAccount(user_email, user_password, user_phone_key);
                }
            }else{

                mLayoutLogin.setVisibility(View.VISIBLE);
                btnLogOut.setVisibility(View.GONE);
                txtUser.setVisibility(View.GONE);
                mLoginButton.setVisibility(View.VISIBLE);
            }
        }
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
        try {
            mListener = (HttpRequestListener) activity;
        } catch (ClassCastException e) {
            mListener = null;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        btnLogIn = (Button) getActivity().findViewById(R.id.button);
        etEmail = (EditText) getActivity().findViewById(R.id.etEmail);
        etPassword = (EditText) getActivity().findViewById(R.id.etPassword);
        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String user_email = etEmail.getText().toString();
                String user_password = etPassword.getText().toString();

                String user_phone_key = MySharedPreferences.loadRegisterID(mContext);
                MySharedPreferences.savePassword(mContext,user_password);
                MySharedPreferences.saveUser(mContext, user_email);
                loginAccount(user_email, user_password, user_phone_key);

            }
        });

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLogout();
                HttpRequestData.logout(mContext);

            }
        });
    }

    public boolean checkFacebookLogin() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken == null) {
            return false;
        } else {
            return true;
        }
    }

    private void loginFacebook(String token){

        HttpRequestData.loginWithFacebook(mContext, token);
        loginStartLoginFacebook(token);
        MySharedPreferences.saveLoginType(mContext, 2);
    }

    private void loginAccount(String user_email,String user_password,String user_phone_key){
        int user_phone_type = 1;
        HttpRequestData.loginWithAccount(mContext, user_email, user_password, user_phone_type, user_phone_key);
        MySharedPreferences.saveLoginType(mContext, 1);
        startLogin(user_email, user_password);
    }

    private void startLogin(String user, String password) {
        if (mListener != null) {
            mListener.StartLoginWithAccount(user, password);
        }
    }

    private void startLogout() {
        if (mListener != null) {
            mListener.StartLogOut();
        }
    }

    private void loginStartLoginFacebook(String accesstoken) {
        if (mListener != null) {
            mListener.StartLoginWithFacebook(accesstoken);
        }
    }


    HttpRequestListener mListener;
}
