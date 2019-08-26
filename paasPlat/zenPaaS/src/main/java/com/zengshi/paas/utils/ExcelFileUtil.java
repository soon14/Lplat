/** 
 * Date:2015年8月22日下午3:27:18 
 * 
*/
package com.zengshi.paas.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.drools.core.RuntimeDroolsException;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/** 
 * Description: <br>
 * Date:2015年8月22日下午3:27:18  <br>
 * 
 * @since JDK 1.6 
 * @see       
 */
public class ExcelFileUtil {
    
    public final static String EXCEL_TYPE_XLS="xls";
    public final static String EXCEL_TYPE_XLSX="xlsx";
    
    
    protected static Workbook createWorkbook(List<List<Object>> datas,List<String> columnNames, String fileType){
        Workbook wb=null;
        if(EXCEL_TYPE_XLS.equalsIgnoreCase(fileType)){
            wb=new HSSFWorkbook();
        }else if(EXCEL_TYPE_XLSX.equalsIgnoreCase(fileType)){
            wb=new XSSFWorkbook();
        }else{
            wb=new XSSFWorkbook();
        }
      //生成一个表格
        Sheet sheet = wb.createSheet();
        // 设置表格默认列宽度为15个字节  
        sheet.setDefaultColumnWidth((short) 15);
        int ri = 0;
        if(!CollectionUtils.isEmpty(columnNames)){
            int cci=0;
            ri=1;
            Row row= sheet.createRow(0);
            for(String cname : columnNames){
                Cell cell=row.createCell(cci++, Cell.CELL_TYPE_STRING);
                cell.setCellValue(cname);
            }
        }
        
        for (Collection<?> data : datas) {
            Row row = sheet.createRow(ri++);
            int ci = 0;
            for (Object value : data) {
                if(null==value){
                    Cell cell=row.createCell(ci++,Cell.CELL_TYPE_BLANK);
                    cell.setCellValue("");
                }else if(int.class.isAssignableFrom(value.getClass()) || Integer.class.isAssignableFrom(value.getClass())
                        || short.class.isAssignableFrom(value.getClass()) || Short.class.isAssignableFrom(value.getClass())
                        || Long.class.isAssignableFrom(value.getClass()) || long.class.isAssignableFrom(value.getClass())
                        || float.class.isAssignableFrom(value.getClass()) || Float.class.isAssignableFrom(value.getClass())
                        || double.class.isAssignableFrom(value.getClass()) || Double.class.isAssignableFrom(value.getClass())){
                    Cell cell=row.createCell(ci++,Cell.CELL_TYPE_NUMERIC);
                    cell.setCellValue(new BigDecimal(String.valueOf(value)).doubleValue());
                }else{
                    Cell cell = row.createCell(ci++);
                    cell.setCellValue(String.valueOf(value));
                }

            }
        }
        return wb;
    }
    /**
     * 
     * exportExcel2Mongo:导出excel到mongodb. <br/> 
     * 
     * 
     * @param datas 数据集
     * @param columnNames 列名
     * @param fileName 文件名
     * @param fileType 文件类型:xls、xlsx
     * @return 文件记录ID
     * @throws Exception 
     * @since JDK 1.6
     */
    public static String exportExcel2Mongo(List<List<Object>> datas,List<String> columnNames,String fileName, String fileType){
//        OPCPackage pkg = OPCPackage.open(path);
        
        //声明一个工作薄
        Workbook wb=null;
        ByteArrayOutputStream fos=null;
        try {
            wb=createWorkbook(datas,columnNames,fileType);
            fos=new ByteArrayOutputStream();
            wb.write(fos);
            String fileId=FileUtil.saveFile(fos.toByteArray(), fileName, fileType);
            return fileId;
        }catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(),e);
        }finally {
            try {
                if(null!=fos){
                    fos.close();
                }
                if(null!=wb){
                    wb.close();
                }
                
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeDroolsException(e);
            }
            
        }
        
    }
    /**
     * 
     * exportExcel2File:导出excel文件. <br/> 
     * 
     * 
     * @param datas 数据集
     * @param columnNames 列名
     * @param path 文件路径
     * @param fileType 文件类型  xls、xlsx
     * @throws Exception 
     * @since JDK 1.6
     */
    public static void exportExcel2File(List<List<Object>> datas, List<String> columnNames,
            String path, String fileType) {

        Workbook wb = null;
        FileOutputStream fos = null;
        try {
            wb=createWorkbook(datas, columnNames, fileType);
            File file = new File(path);
            fos = new FileOutputStream(file);
            wb.write(fos);
        }catch(Exception e){
            throw new RuntimeException(e.getMessage(),e);
        }finally {
            try {
                if(null!=fos){
                    fos.close();
                }
                if(null!=wb){
                    wb.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(),e);
            }
        }

    }
    /**
     * 
     * exportExcelFromMongo:从mongodb导出excel文件. <br/> 
     * 
     * @param fileId 文件ID
     * @return 
     * @since JDK 1.6
     */
    public static byte[] exportExcelFromMongo(String fileId){
        
        byte[] bytes=FileUtil.readFile(fileId);
        
        return bytes;
    }
    /**
     * 
     * exportExcelFromMongo:从mongodb导出excel文件. <br/> 

     * 
     * @param fileId 文件ID
     * @param path 文件存储路径
     * @since JDK 1.6
     */
    public static void exportExcelFromMongo(String fileId,String path){
        byte[] bytes=FileUtil.readFile(fileId);
        FileOutputStream fops=null;
        try {
            fops=new FileOutputStream(new File(path));
            fops.write(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(),e);
        }finally{
            if(null!=fops){
                try {
                    fops.close();
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage(),e);
                }
            }
        }
    }
    /**
     * 
     * importExcel:导入excel文件. <br/> 
     * 
     * @param path excel文件路径
     * @param startRow 开始行
     * @param startCell 开始列
     * @return excel数据及mongodb文件存储id objct[0]:List<List<Object>> objct[1]:String
     * @since JDK 1.6
     */
    public static Object[] importExcel(String path,int startRow,int startCell){
        if(StringUtils.isEmpty(path)){
            return null;
        }
        String extend=path.substring(path.lastIndexOf(".")+1);
        if(!EXCEL_TYPE_XLS.equalsIgnoreCase(extend) && !EXCEL_TYPE_XLSX.equalsIgnoreCase(extend)){
            throw new RuntimeException("**************************File is not a excel file.");
        }
        File file=new File(path);
        String name=file.getName();
        FileInputStream fis=null;
        FileInputStream ofis=null;
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        try {
            fis=new FileInputStream(file);
            
            byte[] bytes=new byte[1024];
            int len;
            while((len=fis.read(bytes))!=-1){
                baos.write(bytes, 0, len);
            }
            String fileId=FileUtil.saveFile(baos.toByteArray(), name, extend);
            ofis=new FileInputStream(file);
            List<List<Object>> datas=readExcel(ofis,startRow,startCell,extend);
            return new Object[]{datas,fileId};
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(),e);
        }finally{
            try {
                if (null != fis) {
                    fis.close();
                }
                if (null != ofis) {
                    ofis.close();
                }
                if(null!=baos){
                    baos.close();
                }
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(),e);
            }
        }
    }

    /**
     * 导入Excel文件
     * @param ips 输入流
     * @param startRow 开始行
     * @param startCell 开始列
     * @param fileName 文件名
     * @param fileType 文件类型
     * @param isSave 是否保存原始文件
     * @return
     */
    public static Object[] importExcel(InputStream ips,int startRow,int startCell,String fileName,String fileType,boolean isSave){
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        ByteArrayInputStream bais=null;
        byte[] bytes=new byte[1024];
        int len;
        try {

            while((len=ips.read(bytes))!=-1){
                baos.write(bytes, 0, len);
            }
            bais=new ByteArrayInputStream(baos.toByteArray());

            List<List<Object>> datas=readExcel(bais, startRow, startCell, fileType);
            if(isSave){
                String fileId=FileUtil.saveFile(baos.toByteArray(), fileName, fileType);
                return new Object[]{datas,fileId};
            }else{
                return new Object[]{datas,null};
            }

        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(),e);
        }finally{
            if(null!=baos){
                try {
                    baos.close();
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage(),e);
                }
            }

        }
    }
    /**
     * 
     * importExcel:导入Excel文件并保存原始文件. <br/>
     * 
     * @param ips 文件输入流
     * @param startRow 开始行
     * @param startCell 开始类
     * @param fileName 文件名
     * @param fileType 文件类型 xls、xlsx
     * @return  excel数据及mongodb文件存储id objct[0]:List<List<Object>> objct[1]:String
     * @since JDK 1.6
     */
    public static Object[] importExcel(InputStream ips,int startRow,int startCell,String fileName,String fileType){

        return importExcel(ips,startRow,startCell,fileName,fileType,true);
    }
    /**
     * 
     * readExcel:读取excel文件数据. <br/> 
     * 
     * @param in Excel文件输入流
     * @param startRow 开始行
     * @param startCell 开始列
     * @param fileType 文件类型 xls、xlsx
     * @return 
     * @since JDK 1.6
     */
    public static List<List<Object>> readExcel(InputStream in,int startRow,int startCell,String fileType){
        Workbook wb=null;
//        OPCPackage pkg =null;
        try {
            
            if(EXCEL_TYPE_XLS.equalsIgnoreCase(fileType)){
                
                wb=new HSSFWorkbook(in);
            }else if(EXCEL_TYPE_XLSX.equalsIgnoreCase(fileType)){
                
//                pkg = OPCPackage.open(in);
                wb=new XSSFWorkbook(in);
            }
            
            int sheetSize=wb.getNumberOfSheets();
            List<List<Object>> rows=new ArrayList<List<Object>>();
            for(int i=0;i<sheetSize;i++){
                Sheet sheet=wb.getSheetAt(i);
                if(null==sheet){
                    continue;
                }
                int rownum=sheet.getLastRowNum()+1;
                startRow=startRow<1?1:startRow;
                if(startRow>rownum){
                    break;
                }
                boolean flag=false;
                for(int j=startRow-1;j<rownum;j++){
                    List<Object> cells=new ArrayList<Object>();
                    Row row=sheet.getRow(j);
                    if(null==row){
                        continue;
                    }
                    int cellnum=row.getLastCellNum();
                    startCell=startCell<1?1:startCell;
                    if(startCell>cellnum){
                        continue;
                    }
                    for(int k=startCell-1;k<cellnum;k++){
                        Cell cell=row.getCell(k);
                        if(null==cell){
                            cells.add(null);
                            continue;
                        }
                        Object value=null;
                        switch (cell.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            value=cell.getStringCellValue();
//                            cells.add(cell.getStringCellValue());
                            break;
                        case Cell.CELL_TYPE_BLANK:
                            value=cell.getStringCellValue();
//                            cells.add(cell.getStringCellValue());
                            break;
                        case Cell.CELL_TYPE_BOOLEAN:
                            value=cell.getBooleanCellValue();
//                            cells.add(cell.getBooleanCellValue());
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            value=cell.getNumericCellValue();
//                            cells.add(cell.getNumericCellValue());
                            break;
//                        case Cell.CELL_TYPE_FORMULA:
//                            cell.getDateCellValue()
                        default:
                            value=cell.getStringCellValue();
//                            cells.add(cell.getStringCellValue());
                            break;
                        }
                        if(null!=value && !"".equals(value.toString())){
                            flag=flag || true;
                        }
                        cells.add(value);
                    }
                    if(flag){
                        rows.add(cells);
                        flag=false;
                    }
                }
                
            }
            
            return rows;
        } catch (Exception e) {
            
            throw new RuntimeException(e.getMessage(),e);
        }finally{
            try {
//                if(null!=pkg){
//                    pkg.close();
//                }
                if(null!=wb){
                    wb.close();
                }
                
            } catch (IOException e) {
                
                throw new RuntimeException(e.getMessage(),e);
            }
            
        }
        
       
    }
    
    public static void main(String[] args) throws Exception {
        
        List<List<Object>> datas=new ArrayList<List<Object>>();
        
        List<String> titles=new ArrayList<String>();
        
        titles.add("序号");
        titles.add("名称");
        titles.add("日期");
        
        for(int i=0;i<20;i++){
            List<Object> data=new ArrayList<Object>();
            data.add(i);
            data.add("字符串"+i);
            data.add(new Date());
            datas.add(data);
        }
//        exportExcel2File(datas,titles,"D:\\test.xlsx","xlsx");
//        String fileId=exportExcel2Mongo(datas,titles,"D:\\test.xlsx","xlsx");
//        System.out.println(fileId);
        
//        importExcel2Mongo(new FileInputStream("D:\\test.xls"),1,1,"xls");
        

        List<List<Object>> list=readExcel(new FileInputStream("g:\\a.xls"),0,0,"xls");
        System.out.println(list.size());
    }
    
}

