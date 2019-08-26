package com.zengshi.butterfly.core.web.security;

public class AuthorizationResult {

    private String message;
    private ResultEnum result = ResultEnum.OK;
    private String redirectPage;

    public void setRedirectPage(String redirectPage) {
        this.redirectPage = redirectPage;
    }

    public ResultEnum getResult() {
        return result;
    }

    public void setResult(ResultEnum result) {
        this.result = result;
    }

    public String getRedirectPage() {
        return redirectPage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isOk() {
        return ResultEnum.OK.equals(result);
    }

    static public enum ResultEnum {
    	OK, NOT_LOGIN, NOT_PERMIT, EXPIRED, METHOD_NEED_OAUTH;
    }
}
