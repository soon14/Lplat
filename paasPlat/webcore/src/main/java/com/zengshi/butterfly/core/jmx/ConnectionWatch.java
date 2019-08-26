/**
 * 
 */
package com.zengshi.butterfly.core.jmx;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.zengshi.butterfly.core.reflect.DatasourceReflectUtil;
import com.zengshi.butterfly.core.spring.BeanFactory;
import com.zengshi.butterfly.core.util.DateUtils;

/**
 *
 */
public class ConnectionWatch implements ConnectionWatchMBean {


	private String dsName;
	private List<String> buffer=new ArrayList<String>();
	
	private boolean isThreadRunning=false;
	
	private Properties properties;
	
	Thread recordThread=null;
	public ConnectionWatch(String dsName,Properties properties) {
		super();
		this.dsName=dsName;
		this.properties=properties;
	}
	
	private Thread createThread() {
		
		final int timeInterval=Integer.parseInt(properties.getProperty("TIME_INTERVAL", "500"));
		final String file=properties.getProperty("FILE_PATH_CONNECTION_MONITOR");
		final int bufferline=Integer.parseInt(properties.getProperty("BUFFER_LINE","40"));
		
		return new Thread() {
			FileOutputStream fos=null;
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				try {
					if(fos == null) {
						 fos=new FileOutputStream(file);
						 fos.write(DateUtils.getTime("yyyy-MM-dd HH:mm:ss:SSS").getBytes());
						 fos.write("\r\n".getBytes());
						 fos.flush();
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
				StringBuffer logBuffer=new StringBuffer();
				int count=0;
				while(isThreadRunning) {
					try {
						Thread.sleep(timeInterval);
						logBuffer.append(ConnectionWatch.this.activeConnection()+",");
						count++;
						if(count >= bufferline ) {
							fos.write(logBuffer.toString().getBytes());
							fos.write("\r\n".getBytes());
							fos.flush();
							logBuffer.setLength(0);
							count=0;
						}
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			}
			
		};
	}

	@Override
	public int activeConnection() {
		try {
			return DatasourceReflectUtil.getActiveNumber(BeanFactory.getObject(this.dsName));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public int idleConnection() {
		return DatasourceReflectUtil.getIdleNumber(BeanFactory.getObject(this.dsName));
	}

	@Override
	public void appendToFile(boolean start) {
		// TODO Auto-generated method stub
		if(properties == null) return;
		
		if(start) {
			this.recordThread=this.createThread();
			isThreadRunning=true;
			recordThread.start();
			
		}else {
			if(this.recordThread != null) {
				isThreadRunning=false;
			}
		}
	}

	
	
	
}
