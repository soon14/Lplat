/** 
 * Date:2015年10月26日上午10:14:22 
 * 
*/
package com.zengshi.paas.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

/** 
 * Description: <br>
 * Date:2015年10月26日上午10:14:22  <br>
 * 
 * @since JDK 1.6 
 * @see       
 */
public class JasperReportUtil {
    
    private static final Logger logger=Logger.getLogger(JasperReportUtil.class);
    /**
     * 
     * 
     * generatePDF:jasperReport生成pdf文件. <br/> 
     * 
     * @param jasperFile jasper模板文件
     * @param dataSource 数据源
     * @param parameters 参数
     * @param outFile 输出文件
     * @since JDK 1.6
     */
    public static void generatePDF(String jasperFile,JRDataSource dataSource,Map<String,Object> parameters,String outFile){
        if(StringUtils.isBlank(jasperFile)){
            throw new RuntimeException("jasperFile is need.");
        }
        File file=new File(jasperFile);
        try {
            JasperReport jasperRpt = (JasperReport) JRLoader.loadObject(file);
            JasperPrint jasperPrint=JasperFillManager.fillReport(jasperRpt,parameters,dataSource);
            JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(outFile));
        } catch (JRException | FileNotFoundException e) {
            throw new RuntimeException("generate file error.", e);
        }
        
    }
    /**
     * 
     * generatePDF:jasperReport生成pdf文件流. <br/> 
     * 
     * @param jasperFile jasper模板文件
     * @param dataSource 数据源
     * @param parameters 参数
     * @return 
     * @since JDK 1.6
     */
    public static InputStream generatePDF(String jasperFile,JRDataSource dataSource,Map<String,Object> parameters){
        if(StringUtils.isBlank(jasperFile)){
            throw new RuntimeException("jasperFile is need.");
        }
        File file=new File(jasperFile);
        try {
            ByteArrayOutputStream bytes=new ByteArrayOutputStream();
            JasperReport jasperRpt = (JasperReport) JRLoader.loadObject(file);
            JasperPrint jasperPrint=JasperFillManager.fillReport(jasperRpt,parameters,dataSource);
            JasperExportManager.exportReportToPdfStream(jasperPrint, bytes);
            ByteArrayInputStream bips=new ByteArrayInputStream(bytes.toByteArray());
            return bips;
        } catch (JRException e) {
            throw new RuntimeException("generate file error.", e);
        }
    }
    /**
     * 
     * generatePDF:jasperReport生成pdf文件流. <br/> 
     * 
     * @param jasperFile jasper模板文件ID
     * @param dataSource 数据源
     * @param parameters 参数
     * @return 
     * @since JDK 1.6
     */
    public static InputStream generatePDFByFileId(String jasperFileId,JRDataSource dataSource,Map<String,Object> parameters){
        byte[] japerFile=FileUtil.readFile(jasperFileId);
        if(null==japerFile){
            logger.info("===========================  fileId["+jasperFileId+"] not exists.");
            return null;
        }
        try {
            ByteArrayOutputStream bytes=new ByteArrayOutputStream();
            JasperReport jasperRpt = (JasperReport) JRLoader.loadObject(new ByteArrayInputStream(japerFile));
            JasperPrint jasperPrint=JasperFillManager.fillReport(jasperRpt,parameters,dataSource);
            JasperExportManager.exportReportToPdfStream(jasperPrint, bytes);
            ByteArrayInputStream bips=new ByteArrayInputStream(bytes.toByteArray());
            return bips;
        } catch (JRException e) {
            throw new RuntimeException("generate file error", e);
        }
    }
    /**
     * 
     * generatePDF:jasperReport生成pdf文件流. <br/> 
     * 
     * @param jasperFile jasper模板文件名
     * @param dataSource 数据源
     * @param parameters 参数
     * @return 
     * @since JDK 1.6
     */
    public static InputStream generatePDFByFileName(String jasperFileName,JRDataSource dataSource,Map<String,Object> parameters){
        byte[] japerFile=FileUtil.readFileByName(jasperFileName);
        if(null==japerFile){
            logger.info("===========================  fileId["+jasperFileName+"] not exists.");
            return null;
        }
        try {
            ByteArrayOutputStream bytes=new ByteArrayOutputStream();
            JasperReport jasperRpt = (JasperReport) JRLoader.loadObject(new ByteArrayInputStream(japerFile));
            JasperPrint jasperPrint=JasperFillManager.fillReport(jasperRpt,parameters,dataSource);
            JasperExportManager.exportReportToPdfStream(jasperPrint, bytes);
            ByteArrayInputStream bips=new ByteArrayInputStream(bytes.toByteArray());
            return bips;
        } catch (JRException e) {
            throw new RuntimeException("generate file error", e);
        }
    }
}

