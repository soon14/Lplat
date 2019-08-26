/** 
 * Date:2015年10月22日上午11:44:25 
 * 
*/
package com.zengshi.ecp.base.util;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/** 
 * Description: <br>
 * Date:2015年10月22日上午11:44:25  <br>
 * 
 * @since JDK 1.6 
 * @see       
 */
public class PdfPrintUtil {

    
    public static void print(InputStream fileIps,HttpServletResponse response,boolean isView){
        
        print(fileIps,response,isView,String.valueOf(System.currentTimeMillis()));
    }
    
    /**
     * 
     * print:pdf文件打印. <br/> 
     * 
     * @param fileIps pdf文件流
     * @param response http响应
     * @param isView 打印时是否显示打印机选择
     * @param filename 输出文件名
     * @since JDK 1.6
     */
    public static void print(InputStream fileIps,HttpServletResponse response,boolean isView,String filename){
        try {
            PdfReader reader=new PdfReader(fileIps);
            ServletOutputStream ops=response.getOutputStream();
            
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            PdfStamper stamp = new PdfStamper(reader, bos);
            stamp.setViewerPreferences(PdfWriter.HideMenubar | PdfWriter.HideToolbar| PdfWriter.HideWindowUI);
            StringBuffer script = new StringBuffer(64);
            if(isView){
                script.append("this.print({bUI: true,bSilent: true,bShrinkToFit: false});").append("\r\nthis.closeDoc();");
            }else{
                script.append("this.print({bUI: false,bSilent: true,bShrinkToFit: false});").append("\r\nthis.closeDoc();");
            }
            stamp.addJavaScript(script.toString());
            stamp.close();
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "inline;filename="+filename);
            response.setHeader("Expires", "0");
            response.setContentLength(bos.size());
            bos.close();
            response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0"); 
            response.setHeader("Pragma", "public");
            ops.write(bos.toByteArray());
            ops.flush();
        } catch (IOException | DocumentException e) {
            
            throw new RuntimeException("print pdf error!!", e);
        }
    }
    
    public static void print(String pdfFile,HttpServletResponse response,boolean isView){
        
        try {
            FileInputStream fis=new FileInputStream(pdfFile);
            String filename=pdfFile.substring(pdfFile.lastIndexOf("/")+1).substring(pdfFile.lastIndexOf("\\")+1);
            print(fis,response,isView,filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
    }
    
    public static void printView(InputStream fileIps,HttpServletResponse response,String filename){
        try {
            PdfReader reader=new PdfReader(fileIps);
            ServletOutputStream ops=response.getOutputStream();
            
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            PdfStamper stamp = new PdfStamper(reader, bos);
            stamp.setViewerPreferences(PdfWriter.HideMenubar | PdfWriter.HideToolbar| PdfWriter.HideWindowUI);
            // 加密密码
            String password = "ZENGSHI" + Math.random() * 1000;
            //不允许打印
            stamp.setEncryption(null, password.getBytes(), 0, false);
            stamp.setViewerPreferences(PdfWriter.PageLayoutSinglePage | PdfWriter.PageModeFullScreen);
            stamp.close();
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "inline;filename="+filename);
            response.setHeader("Expires", "0");
            response.setContentLength(bos.size());
            bos.close();
            response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0"); 
            response.setHeader("Pragma", "public");
            ops.write(bos.toByteArray());
            ops.flush();
        } catch (IOException | DocumentException e) {
            
            throw new RuntimeException("pdf view error!!", e);
        }
    }
}

