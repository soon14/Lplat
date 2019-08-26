/** 
 * Date:2015-8-17上午10:07:19 
 * 
 */ 
package com.zengshi.paas.test;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * Description: <br>
 * Date:2015-8-17上午10:07:19  <br>
 * 
 * @version  
 * @since JDK 1.6 
 */
public class SrcDTO {
    
    private static final String MODULE = DescBO.class.getName();
    
    private Date date;
    
    private Timestamp utilDate;
    
    private String str;
    
    private int i;
    
    private long l;
    
    private BigDecimal bg;
    
    private String[] strs ;
    
    private List<String> lst;
    
    
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Timestamp getUtilDate() {
        return utilDate;
    }

    public void setUtilDate(Timestamp utilDate) {
        this.utilDate = utilDate;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public long getL() {
        return l;
    }

    public void setL(long l) {
        this.l = l;
    }

    public BigDecimal getBg() {
        return bg;
    }

    public void setBg(BigDecimal bg) {
        this.bg = bg;
    }

    public String[] getStrs() {
        return strs;
    }

    public void setStrs(String[] strs) {
        this.strs = strs;
    }

    public List<String> getLst() {
        return lst;
    }

    public void setLst(List<String> lst) {
        this.lst = lst;
    }
    
}

