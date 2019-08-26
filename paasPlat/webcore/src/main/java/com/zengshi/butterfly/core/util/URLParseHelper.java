package com.zengshi.butterfly.core.util;

import java.util.HashMap;
import java.util.Map;

public class URLParseHelper {
	/**
	 * ������url�����·��������ҳ��
	 * 
	 * @param strURL
	 *            url��ַ
	 * @return url·��
	 */
	public static String UrlPage(String strURL) {
		String strPage = null;
		String[] arrSplit = null;

		strURL = strURL.trim().toLowerCase();

		arrSplit = strURL.split("[?]");
		if (strURL.length() > 0) {
			if (arrSplit.length > 1) {
				if (arrSplit[0] != null) {
					strPage = arrSplit[0];
				}
			}
		}

		return strPage;
	}

	/**
	 * ȥ��url�е�·����������������
	 * 
	 * @param strURL
	 *            url��ַ
	 * @return url��������
	 */
	private static String TruncateUrlPage(String strURL) {
		String strAllParam = null;
		String[] arrSplit = null;

		strURL = strURL.trim();

		arrSplit = strURL.split("[?]");
		if (strURL.length() > 1) {
			if (arrSplit.length > 1) {
				if (arrSplit[1] != null) {
					strAllParam = arrSplit[1];
				}
			}
		}

		return strAllParam;
	}

	/**
	 * ������url�����еļ�ֵ�� �� "index.jsp?Action=del&id=123"��������Action:del,id:123����map��
	 * 
	 * @param URL
	 *            url��ַ
	 * @return url��������
	 */
	public static Map<String, String> URLRequest(String URL) {
		Map<String, String> mapRequest = new HashMap<String, String>();

		String[] arrSplit = null;

		String strUrlParam = TruncateUrlPage(URL);
		if (strUrlParam == null) {
			return mapRequest;
		}
		// ÿ����ֵΪһ��
		arrSplit = strUrlParam.split("[&]");
		for (String strSplit : arrSplit) {
			String[] arrSplitEqual = null;
			arrSplitEqual = strSplit.split("[=]");

			// ��������ֵ
			if (arrSplitEqual.length > 1) {
				// ��ȷ����
				mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);

			} else {
				if (arrSplitEqual[0] != "") {
					// ֻ�в���û��ֵ��������
					mapRequest.put(arrSplitEqual[0], "");
				}
			}
		}
		return mapRequest;
	}
	
	
	 /**���ڲ���CRequest��
     * @param args
     */
    public static void main(String[] args) {
    // ����url
        String str = "chnl_order?service=page/Order_Center&listener=queryMyOrder&cond_ORDER_STATUS=SUBMIT&cond_ORDER_TYPE=%";     
        
        //urlҳ��·��
        System.out.println(URLParseHelper.UrlPage(str));
        
        //url�����ֵ��
        String strRequestKeyAndValues="";        
        Map<String, String> mapRequest = URLParseHelper.URLRequest(str);
        
        for(String strRequestKey: mapRequest.keySet()) {

            String strRequestValue=mapRequest.get(strRequestKey);
            strRequestKeyAndValues+="key:"+strRequestKey+",Value:"+strRequestValue+";";            
           
        } 
        System.out.println(strRequestKeyAndValues);
        
        //��ȡ��Ч��ʱ�����null
        System.out.println(mapRequest.get("page"));
    }
    

}
