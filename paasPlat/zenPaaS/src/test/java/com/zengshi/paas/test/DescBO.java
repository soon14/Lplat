/** 
 * Date:2015-8-17上午10:07:58 
 * 
 */ 
package com.zengshi.paas.test;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * Description: <br>
 * Date:2015-8-17上午10:07:58  <br>
 * 
 * @version  
 * @since JDK 1.6 
 */
public class DescBO implements Serializable{
    
    /** 
     * @since JDK 1.6 
     */ 
    private static final long serialVersionUID = -5240706705656889528L;
    


    private Timestamp date;
    
    private Date utilDate;
    
    private String str;
    
    private int i;
    
    private long l;
    
    private BigDecimal bg;
    
    private String[] strs ;
    
    private List<String> lst;
    
    
    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public Date getUtilDate() {
        return utilDate;
    }

    public void setUtilDate(Date utilDate) {
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

    public DescBO copyMoreValues(DescBO descVO, SrcDTO srcVO) {
        descVO.setUtilDate(srcVO.getUtilDate());
        descVO.setStr(srcVO.getStr());
        descVO.setI(srcVO.getI());
        descVO.setL(srcVO.getL());
        descVO.setBg(srcVO.getBg());
        descVO.setStrs(srcVO.getStrs());
        descVO.setLst(srcVO.getLst());
        return descVO;
    }

}

