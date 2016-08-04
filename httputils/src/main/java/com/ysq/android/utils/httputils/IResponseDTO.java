package com.ysq.android.utils.httputils;

public interface IResponseDTO {

    public boolean isSuccess();

    public String getErrorResult();

    public Object getSuccessResult();
}
