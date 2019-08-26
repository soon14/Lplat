package com.zengshi.paas.license_client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

/**
 * 启动后1天开始检查，检查的周期为28天（4周）一次。
 * 注意：如果license非法，直接退出系统。
 * @author mxd
 *
 */
public class LicenseChecker implements Runnable {
//	private final static Logger logger = LoggerFactory.getLogger(LicenseChecker.class);

	public static int PRODUCT_ID = 1;
	private static String INVALID_STATE = "2";
	public static String LIC_SERVER_URL = "http://47.93.18.168:8678/1?salt=1234567890";

	@Override
	public void run() {
//		logger.debug("LicenseChecker started.");
		String state = sendGet();
		if (INVALID_STATE.equalsIgnoreCase(state)) {
			System.out.println("Invalid license! Please contact your vendor!");
			System.exit(1);
		}
//		logger.debug("LicenseChecker finished.");

	}
	
    public void check() {
        ScheduledExecutorService p = Executors.newSingleThreadScheduledExecutor();
        Runnable command = new LicenseChecker();
        long initialDelay = 1;
        long delay = 28;
        TimeUnit unit = /*TimeUnit.SECONDS;/*/ TimeUnit.DAYS;
        p.scheduleWithFixedDelay(command, initialDelay, delay, unit);
    	
    }

	/**
	 * @Description:使用HttpURLConnection发送get请求
	 */
	public String sendGet() {
		StringBuffer resultBuffer = new StringBuffer();
		BufferedReader br = null;
		HttpURLConnection con = null;
		try {
			URL url = new URL(LIC_SERVER_URL);
			con = (HttpURLConnection) url.openConnection();
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			con.connect();
			br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
			String temp;
			while (br != null && (temp = br.readLine()) != null) {
				resultBuffer.append(temp);
			}
		} catch (Exception e) {
			//keep silent even there is exception. 
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					br = null;
					throw new RuntimeException(e);
				} finally {
					if (con != null) {
						con.disconnect();
						con = null;
					}
				}
			}
		}
		return resultBuffer.toString();
	}

}
