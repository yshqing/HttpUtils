package com.ysq.android.utils.httputils;

import com.alibaba.fastjson.JSON;
import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ysq on 16/8/4.
 */
public class CustomRequest extends StringRequest {
    /**
     * Default charset for JSON request.
     */
    protected static final String PROTOCOL_CHARSET = "utf-8";
    /**
     * Content type for request.
     */
    public static final String PROTOCOL_CONTENT_TYPE =
            String.format("application/json; charset=%s", PROTOCOL_CHARSET);
    private final Map<String, String> headers;
    private final Map<String, String> params;
    private final String requestKey;
    private final String requestBody;
    private final String bodyContentType;
    private final int timeoutMillisecond;

    public CustomRequest(HttpUtils.RequestBuilder builder) {
        super(builder.getMethod(), builder.getUrl(), builder.getSuccessListener(), builder.getErrorListener());
        headers = builder.getHeaders();
        params = builder.getParams();
        requestKey = builder.getRequestKey();
        requestBody = builder.getRequestBody();
        bodyContentType = builder.getBodyContentType();
        timeoutMillisecond = builder.getTimeoutMillisecond();
    }

    @Override
    public Map<String, String> getParams() throws AuthFailureError {
        return params != null ? params : super.getParams();
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        try {
            return requestBody == null ? null : requestBody.getBytes(PROTOCOL_CHARSET);
        } catch (UnsupportedEncodingException uee) {
            return null;
        }
    }

    @Override
    public String getBodyContentType() {
        return bodyContentType != null ? bodyContentType : super.getBodyContentType();
    }

    public String getRequestKey() {
        return requestKey;
    }

    @Override
    public RetryPolicy getRetryPolicy() {
        return super.getRetryPolicy();
    }

    public int getTimeoutMillisecond() {
        return timeoutMillisecond;
    }


}
