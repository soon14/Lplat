/**
 * 
 */
package com.zengshi.ecp.frame.matrix;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import net.coobird.thumbnailator.Thumbnails;

import com.google.zxing.common.BitMatrix;

/**
 * 
 */
public class MatrixImageTool {

	public static void main(String[] arg) {
		//createMatrixImgage("",null,"");
		
		try {
			createMatrixImage("http://www.google.com.hk",
					new Logo(100, 100, "/home/yangqx/work/share/workspace/zj/butterfly/butterfly-core/sources/ailk-logo.gif"),
					new Matrix(200,200,Color.WHITE));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static File createMatrixImage(String content,Logo loginfo,Matrix matrixInfo) throws Exception{
		Integer r=(int)(Math.random()*100);
		String fileSeq=Thread.currentThread().getId()+"_"+System.currentTimeMillis()+"_"+r;
;		String matrixfile=System.getProperty("java.io.tmpdir")+"/matrix-"+fileSeq+".jpg";
		String logFile=resizeImage(loginfo.getLogofile(), loginfo.getWidth(), loginfo.getHeight(),fileSeq);
		
		BitMatrix matrix = MatrixToImageWriterEx.createQRCode(content,
				matrixInfo.getWidth(), matrixInfo.getHeight());
		MatrixToLogoImageConfig logoConfig = new MatrixToLogoImageConfig(
				matrixInfo.getBorderColor(), 4);
		MatrixToImageWriterEx.writeToFile(matrix, matrixInfo.getExt(),
				matrixfile, logFile, logoConfig);
		
		new File(logFile).deleteOnExit();
		return new File(matrixfile);
	}
	
	public static String resizeImage(String image,int width,int lenght,String fileSeq) throws IOException {
		String fileName=System.getProperty("java.io.tmpdir")+"/logo-"+fileSeq+".jpg";
		Thumbnails.of(image).size(width, lenght).toFile(fileName);
		return fileName;
	}

	
	public static class Matrix {
		private int width;
		private int height;
		private Color borderColor;
		
		private String ext;
		public Matrix(int width, int height, Color borderColor) {
			this( width,  height,  borderColor, "jpg");
		}
		public Matrix(int width, int height, Color borderColor, String ext) {
			super();
			this.width = width;
			this.height = height;
			this.borderColor = borderColor;
			this.ext = ext;
		}
		public int getWidth() {
			return width;
		}
		public void setWidth(int width) {
			this.width = width;
		}
		public int getHeight() {
			return height;
		}
		public void setHeight(int height) {
			this.height = height;
		}
		public Color getBorderColor() {
			return borderColor;
		}
		public void setBorderColor(Color borderColor) {
			this.borderColor = borderColor;
		}
		public String getExt() {
			return ext;
		}
		public void setExt(String ext) {
			this.ext = ext;
		}
	}
	public static class Logo {
		private int width;
		private int height;
		private String logofile;
		
		
		public Logo(int width, int height, String logofile) {
			super();
			this.width = width;
			this.height = height;
			this.logofile = logofile;
		}
		
		public int getWidth() {
			return width;
		}
		public void setWidth(int width) {
			this.width = width;
		}
		public int getHeight() {
			return height;
		}
		public void setHeight(int height) {
			this.height = height;
		}
		public String getLogofile() {
			return logofile;
		}
		public void setLogofile(String logofile) {
			this.logofile = logofile;
		}
		
		
	}
}
