package com.ysq.android.utils.httputils;

import android.app.Application;

import com.alibaba.fastjson.JSON;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ysq on 16/8/4.
 */
public class HttpUtils {
    private static HttpUtils mSelf;
    private RequestQueue mQueue;

    private HttpUtils() {

    }

    public synchronized static HttpUtils getInstance(Application context) {
        if (mSelf == null) {
            mSelf = new HttpUtils();
            mSelf.mQueue = Volley.newRequestQueue(context);
        }
        return mSelf;
    }

    public void request(RequestBuilder builder) {
        if (builder.cancelSame) {
            cancelAll(builder.getUrl(), builder.getRequestKey());
        }
        CustomRequest customRequest = builder.build();
        customRequest.setRetryPolicy(new DefaultRetryPolicy(customRequest.getTimeoutMillisecond(),
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        customRequest.setTag(customRequest.getUrl() + customRequest.getRequestKey());
        mSelf.mQueue.add(customRequest);
    }

    /**
     * 取消所有指定的请求
     * 调用{@link #cancelAll(String, String)}
     *
     * @param url 请求地址
     */
    public void cancelAll(String url) {
        cancelAll(null, url);
    }

    /**
     * 取消所有指定的请求
     *
     * @param requestKey 请求关键字
     * @param url        请求地址
     */
    public void cancelAll(final String requestKey, final String url) {
        mSelf.mQueue.cancelAll(new RequestQueue.RequestFilter() {

            @Override
            public boolean apply(Request<?> request) {
                return request.getTag().toString().equals(url + requestKey);
            }
        });
    }

    public static class RequestBuilder {
        private static final int DEFAULT_TIMEOUT_MILLISECOND = 20 * 1000;
        private int method = Request.Method.GET;
        private String url;
        private Map<String, String> headers;
        private Map<String, String> params;
        private RequestCallBack callBack;
        private boolean cancelSame;
        private String requestKey;
        private String requestBody;
        private Class<? extends IResponseDTO> clazz;
        private int timeoutMillisecond = DEFAULT_TIMEOUT_MILLISECOND;
        private Response.Listener successListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (callBack == null) {
                    return;
                }
                IResponseDTO ret = null;
                try {
                    ret = JSON.parseObject(response, clazz == null ? IResponseDTO.class : clazz);
                } catch (Exception e) {
                    YResponseError error = new YResponseError(YResponseError.Type.DECODEERROR);
                    callBack.onRequestFailed(url + requestKey, error, response);
                    e.printStackTrace();
                    return;
                }
                if (!ret.isSuccess()) {
                    YResponseError error = new YResponseError(YResponseError.Type.OPERATEERROR);
                    callBack.onRequestFailed(url + requestKey, error, ret.getErrorResult());
                } else {
                    callBack.onRequestSuccess(url + requestKey, clazz == null ? ret : ret.getSuccessResult());
                }
            }
        };
        private Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                YResponseError yResponseError = new YResponseError(YResponseError.Type.UNKNOWERROR);
                if (error instanceof TimeoutError) {
                    yResponseError = new YResponseError(YResponseError.Type.TIMEOUT);
                } else if (error instanceof ServerError) {
                    yResponseError = new YResponseError(YResponseError.Type.SERVERERROR);
                } else if (error instanceof NoConnectionError) {
                    yResponseError = new YResponseError(YResponseError.Type.NOCONNECTIONERROR);
                } else {
                    error.printStackTrace();
                }
                if (callBack != null) {
                    callBack.onRequestFailed(url + requestKey, yResponseError, null);
                }
            }
        };

        private RequestBuilder() {
        }

        /**
         * @return post请求的RequestBuilder
         */
        public static RequestBuilder post() {
            return new RequestBuilder().method(Request.Method.POST);
        }

        /**
         * @return get请求的RequestBuilder
         */
        public static RequestBuilder Get() {
            return new RequestBuilder().method(Request.Method.GET);
        }

        /**
         * @return put请求的RequestBuilder
         */
        public static RequestBuilder put() {
            return new RequestBuilder().method(Request.Method.PUT);
        }

        private RequestBuilder method(int method) {
            this.method = method;
            return this;
        }

        public RequestBuilder url(String url) {
            this.url = url;
            return this;
        }

        public RequestBuilder addHeader(String key, String value) {
            if (headers == null) {
                headers = new HashMap<>();
            }
            headers.put(key, value);
            return this;
        }

        public RequestBuilder headers(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public RequestBuilder params(Map<String, String> params) {
            this.params = params;
            return this;
        }

        public RequestBuilder addParams(String key, String value) {
            if (params == null) {
                params = new HashMap<>();
            }
            params.put(key, value);
            return this;
        }

        public RequestBuilder cancelSame(boolean cancelSame) {
            this.cancelSame = cancelSame;
            return this;
        }

        public RequestBuilder requestKey(String requestKey) {
            this.requestKey = requestKey;
            return this;
        }

        public RequestBuilder requestBody(String requestBody) {
            this.requestBody = requestBody;
            return this;
        }

        public RequestBuilder callBack(RequestCallBack callBack) {
            this.callBack = callBack;
            return this;
        }

        public RequestBuilder clazz(Class<? extends IResponseDTO> clazz) {
            this.clazz = clazz;
            return this;
        }

        public RequestBuilder timeoutMillisecond(int timeoutMillisecond) {
            this.timeoutMillisecond = timeoutMillisecond;
            return this;
        }

        public CustomRequest build() {
            return new CustomRequest(this);
        }

        public int getMethod() {
            return method;
        }

        public String getUrl() {
            return url;
        }

        public Map<String, String> getHeaders() {
            return headers;
        }

        public Map<String, String> getParams() {
            return params;
        }

        public String getRequestKey() {
            return requestKey;
        }

        public String getRequestBody() {
            return requestBody;
        }

        public int getTimeoutMillisecond() {
            return timeoutMillisecond;
        }

        public Response.Listener getSuccessListener() {
            return successListener;
        }

        public Response.ErrorListener getErrorListener() {
            return errorListener;
        }
    }
}
