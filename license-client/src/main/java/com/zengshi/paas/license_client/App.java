package com.zengshi.paas.license_client;

/**
 * 增实产品License远程校验程序嵌入使用方法：
 * 1）将LicenseChecker类代码复制到增实产品中，修改PRODUCT_ID与license server数据库中的product_id对应，其他不用动。
 * 2）在该产品运行中（如在main方法中）增加如下两行代码：
 *     LicenseChecker lc = new LicenseChecker();
 *     lc.check();
 * @author mxd
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	LicenseChecker lc = new LicenseChecker();
    	lc.check();
    }
    

}
