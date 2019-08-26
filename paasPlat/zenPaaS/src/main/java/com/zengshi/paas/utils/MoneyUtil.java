package com.zengshi.paas.utils;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;


/**
 * 金额转化类
 * Description: <br>
 * Date: 2014-10-25 <br>
 * 
 * @update liwb3
 */
public class MoneyUtil {
    
    
    /**
     * 按照设置的精度把元转为分
     * @param yuanStr
     * @param scale
     * @return
     */
    public static long convertYuanToCent(String yuanStr,int scale){
        try {
            return (new BigDecimal(yuanStr).setScale(scale).multiply(new BigDecimal(100))).longValue();
        } catch (Exception e) {
           throw new RuntimeException( "把String转化为数字失败");
        }
    }
    
    /**
     * 把元转为分
     * @param yuanStr
     * @return
     */
    public static long convertYuanToCent(String yuanStr){
        try {
            return (new BigDecimal(yuanStr).multiply(new BigDecimal(100))).longValue();
        } catch (Exception e) {
           throw new RuntimeException( "把String转化为数字失败");
        }
    }
    
    /**
     * 把分转为元
     * @param yuanStr
     * @return
     */
    public static String convertCentToYuan(long cent){
        try {
            String result = (new BigDecimal(cent).divide(new BigDecimal(100))).toString();
            return result;
        } catch (Exception e) {
           throw new RuntimeException( "转化失败");
        } 
    }
    
    /**
     * 把分转为元
     * @param yuanStr
     * @return
     */
    public static String convertCentToYuan(String centStr){
        try {
            String result = (new BigDecimal(centStr).divide(new BigDecimal(100))).toString();
            return result;
        } catch (Exception e) {
           throw new RuntimeException( "把String转化为数字失败");
        }
    }
    
    
    /**
     * 生成BigDecimal，只支持int,long,String,double
     * @param value
     * @return
     */
    public static BigDecimal buildBigDecimal(Object value){
        BigDecimal result=null;
        try {
            if (value==null) {
                return new BigDecimal(0);
            }
            
            if (value instanceof String) {
                String valueStr=(String)value;
                if (StringUtils.isBlank(valueStr)) {
                    valueStr="0";
                }
                result=new BigDecimal(valueStr);
            }else if (value instanceof Double) {
                result=new BigDecimal((double)value);
            }else if (value instanceof Integer) {
                result=new BigDecimal((int)value);
            }else if(value instanceof Long){
                result=new BigDecimal((long)value);
            }else{
                throw new RuntimeException("生成BigDecimal失败，只支持int,long,String,double");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        
        return result;
    }
    
    /**
     * 按照传入的精度，四色五入获取参数value的double值
     * @param value 只支持int,long,String,double类型
     * @param newScale 精度,保留newScale位小数
     * @return
     */
    public static String convertDoubleToString(Object value,int newScale){
        BigDecimal bigDecimal=buildBigDecimal(value).setScale(newScale,BigDecimal.ROUND_HALF_UP);
        return bigDecimal.toString();
    }
    
    /**
     * 将分转化成元，并以字符串返回 。
     * 当cents值很大时，会丢失精度…… 如2222222222222222222L
     */
//    @Deprecated  
//    public static String getMoneyStr(long cents){
//       // 当cents值很大时，会丢失精度……
//        return new java.text.DecimalFormat("#.00").format(cents/100.00);
//    }
    
    public static void main(String[] args) {
     //   System.out.println(getMoneyStr(2222222222222222222L));
        
//        System.out.println(convertCentToYuan(2222222222222222222L));
//        System.out.println(convertYuanToCent("12345678912345678.22"));
        
        System.out.println(buildBigDecimal(37.94));
        System.out.println(buildBigDecimal(37.94).doubleValue());
        System.out.println(buildBigDecimal("37.945").doubleValue());
        System.out.println(convertDoubleToString(.945, 2));
        
        
        System.out.println(buildBigDecimal(2.80000000).doubleValue()==buildBigDecimal("2.80000000000000").doubleValue());
    }
}
