package com.microsoft.onlineid.internal.exception;

import com.microsoft.onlineid.exception.AuthenticationException;
import com.microsoft.onlineid.internal.ApiRequest;

public class PromptNeededException extends AuthenticationException {
    private static final long serialVersionUID = 1;
    private ApiRequest _request;

    public PromptNeededException(ApiRequest apiRequest) {
        this._request = apiRequest;
    }

    public ApiRequest getRequest() {
        return this._request;
    }
}
