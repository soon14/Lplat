package com.zengshi.paas.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zengshi.paas.captcha.ICaptchaManager;

public class CaptchaUtil {
	private static ICaptchaManager captchaSv = null;

	private CaptchaUtil() {

	}

	private static ICaptchaManager getIntance() {
		if (null != captchaSv)
			return captchaSv;
		else {
			captchaSv = (ICaptchaManager) PaasContextHolder.getContext().getBean(
					"captchaSv");
			return captchaSv;
		}
	}

	public static void genCaptcha(HttpServletRequest request,HttpServletResponse response) {
		getIntance().genCaptcha(request, response);
	}

	public static void main(String[] args) {
		getIntance();
	}
}
