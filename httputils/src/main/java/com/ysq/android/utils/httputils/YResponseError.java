/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ysq.android.utils.httputils;

/**
 * Exception style class encapsulating Volley errors
 */
@SuppressWarnings("serial")
public class YResponseError extends Exception {

    private int mCode;

    public enum Type {
        OPERATEERROR(-6, "操作失败"),
        NOCONNECTIONERROR(-5, "无法访问网络"),
        UNKNOWERROR(-4, "未知错误，请联系管理员"),
        DECODEERROR(-3, "数据解析失败，请联系管理员"),
        SERVERERROR(-2, "服务器错误，请联系管理员"),
        TIMEOUT(-1, "请求超时");

        private int mCode;
        private String mMessage;

        private Type(int code, String message) {
            mCode = code;
            mMessage = message;
        }

    }

    public YResponseError(Type type) {
        this(type.mCode, type.mMessage);
    }

    public YResponseError(int code, String exceptionMessage) {
        super(exceptionMessage);
        mCode = code;
    }

    public int getmCode() {
        return mCode;
    }

}
