package com.zengshi.paas.captcha;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ICaptchaManager {
    public void genCaptcha(HttpServletRequest request,HttpServletResponse response);
}
