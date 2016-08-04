package com.ysq.android.utils.httputils;

/**
 * Created by ysq on 16/8/4.
 */
public interface RequestCallBack {
    public void onRequestSuccess(String urlAndRequestKey, Object response);

    public void onRequestFailed(String urlAndRequestKey, YResponseError error, Object response);
}
