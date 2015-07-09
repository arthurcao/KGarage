package com.kooltech.droid.kgarage.utils;

/**
 * Created by NgocTien on 7/3/2015.
 */
public interface HttpRequestListener {
     void StartLoginWithFacebook(String access_token);
     void StartLoginWithAccount(String user, String passwork);
     void StartLogOut();
     void StartrequestResponse();
}
