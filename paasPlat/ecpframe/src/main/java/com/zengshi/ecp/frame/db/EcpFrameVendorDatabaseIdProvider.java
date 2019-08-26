/**
 * 
 */
package com.zengshi.ecp.frame.db;

import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.executor.BaseExecutor;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;

/**
 * 
 */
public class EcpFrameVendorDatabaseIdProvider extends VendorDatabaseIdProvider {

	private static final Log log = LogFactory.getLog(BaseExecutor.class);

	private String dataBaseProductName;
	
	private Properties properties;

	@Override
	public String getDatabaseId(DataSource dataSource) {
		if (dataBaseProductName == null || dataBaseProductName.length() == 0) {
			throw new NullPointerException("dataBaseProductName is not set  --by EcpFrameVendorDatabaseIdProvider");
		}
		try{
			return getDatabaseName(dataBaseProductName);
		} catch(Exception err){
			log.error("Could not get a databaseId from dataSource----by EcpFrameVendorDatabaseIdProvider", err);
		}
		return null;
	}

	

	public void setProperties(Properties p) {
		this.properties = p;
	}


	public void setDataBaseProductName(String dataBaseProductName) {
		this.dataBaseProductName = dataBaseProductName;
	}

	/**
	 * 如果有配置了properties，那么将正式配置的名称转义；
	 * @param productName
	 * @return
	 * @throws SQLException
	 */
	private String getDatabaseName(String productName) throws SQLException {

		if (this.properties != null) {
			for (Map.Entry<Object, Object> property : properties.entrySet()) {
				if (productName.contains((String) property.getKey())) {
					return (String) property.getValue();
				}
			}
			return null; // no match, return null
		}
		return productName;
	}

}
