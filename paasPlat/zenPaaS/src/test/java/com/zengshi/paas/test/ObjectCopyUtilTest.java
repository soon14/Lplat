/** 
 * Date:2015-8-17上午9:54:08 
 * 
 */ 
package com.zengshi.paas.test;

import com.zengshi.paas.utils.ObjectCopyUtil;
import net.sf.json.JSONObject;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Description: <br>
 * Date:2015-8-17上午9:54:08  <br>
 * 
 * @version  
 * @since JDK 1.6 
 */
public class ObjectCopyUtilTest {

    /**
     * Test method for {@link com.zengshi.paas.utils.ObjectCopyUtil#copyObjValue(java.lang.Object, java.lang.Object, java.lang.String, boolean)}.
     */
    @Test
    public void testCopyObjValue() {
        DateFormat format= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        
        SrcDTO dto = new SrcDTO();
        dto.setDate(Calendar.getInstance().getTime());
        dto.setUtilDate(new Timestamp(Calendar.getInstance().getTime().getTime()));
        dto.setBg(new BigDecimal(10));
        dto.setI(5);
        dto.setL(100L);
        List<String> lst = new ArrayList<String>();
        lst.add("zz");lst.add("KK");
        dto.setLst(lst);
        dto.setStr("ok");
        dto.setStrs(new String[]{"suc","err"});
        
        System.out.println("==DTO : " + JSONObject.fromObject(dto).toString());
        
        
        DescBO bo = new DescBO();
        ObjectCopyUtil.copyObjValue(dto, bo, "", true);
        System.out.println("==== dto--date : " + format.format(dto.getDate()));
        System.out.println("==== dto--utilDate : " + format.format(dto.getUtilDate()));
//        System.out.println("====  bo--date : : " + format.format(bo.getDate()));
        System.out.println("====  bo--utilDate : : " + format.format(bo.getUtilDate()));
        System.out.println("==bo : " + JSONObject.fromObject(bo).toString());
    }

    @Test
    public void testGenFromClass() {
        String res = ObjectCopyUtil.genClsCopyMoreValuesMethod(DescBO.class, SrcDTO.class);
        assertTrue(res.indexOf("DescBO") >= 0);
        System.out.println(res);
    }

    @Test
    public void testGenFromFile() {
        String destFileName = "D:\\workspace\\baseplat\\plat\\paasPlat\\zenPaaS\\src\\test\\java\\com\\zengshi\\paas\\test\\DescBO.java";
        String srcFileName = "D:\\workspace\\baseplat\\plat\\paasPlat\\zenPaaS\\src\\test\\java\\com\\zengshi\\paas\\test\\SrcDTO.java";
        String res = ObjectCopyUtil.genClsCopyMoreValuesMethod(destFileName, srcFileName);
        assertTrue(res.indexOf("DescBO") >= 0);
        System.out.println(res);
    }

}

