package com.zengshi.butterfly.core.afs;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.zengshi.butterfly.core.util.StreamHelper;

public class SimpleFTPClient extends AbstractAFile {

	private FTPConnectInfo conn;
	private static final Log logger=LogFactory.getLog(SimpleFTPClient.class);

	public SimpleFTPClient(FTPConnectInfo conn) {
		super();
		this.conn =conn;
	}

	@Override
	public InputStream read(String fileName) throws Exception {
		URL resourceURL=null;
		URLConnection con=null;
		try {
			resourceURL=this.conn.getResourceURL(fileName);
			con = resourceURL.openConnection();
			return con.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}
	@Override
	public long save(String fileName,InputStream is) throws Exception {
		if(conn == null ) {
			throw new AFSException("FTPConnectInfo 未初始化");
		}
		URL resourceURL=null;
		URLConnection con;
		OutputStream outStream=null;
		long fileSize = 0;
		try {
			resourceURL=this.conn.getResourceURL(fileName);
			logger.debug("begin save file "+resourceURL);
			con = resourceURL.openConnection();
			outStream=con.getOutputStream();
			
			byte[] b = new byte[1024];
			long size = is.read(b);
			while(size != -1){
				outStream.write(b);
				fileSize += size;
				size = is.read(b);
				outStream.flush();
			}
			logger.debug("save file success "+resourceURL);
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(outStream != null) outStream.close();
		}
		
		return fileSize;
	}
	
	public static void main(String[] arg) {
		FTPConnectInfo connectInfo=new FTPConnectInfo("10.10.19.4","panght","panght");
		SimpleFTPClient client =new SimpleFTPClient(connectInfo);
		String filepath="/home/yangqx/work/share/workspace/UltraNote/ailk-core/src/test/java/Base64.java";
		try {
			client.save("/yangqx/2.txt", new FileInputStream(filepath) );
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("now read....");
		
		try {
			InputStream is=client.read("/yangqx/2.txt");
			System.out.println(StreamHelper.inputstreamToString(is));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	


}
