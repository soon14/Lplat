package com.zengshi.paas.test;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Assert;
import org.junit.Test;

import com.zengshi.paas.utils.ImageUtil;
import com.gif4j.GifDecoder;
import com.gif4j.GifEncoder;
import com.gif4j.GifTransformer;
import com.gif4j.TextPainter;
import com.gif4j.imageio.GifImageWriterSpi;
import com.lowagie.text.pdf.codec.GifImage;
public class GmClientTest {

//	@Test
	public void scaleImageTest(){
		String uri="/imageServer/image/bba04f1f3972a8b1/5968387e00325737f15f7a00_300x300!.gif";
		String imageName="5968387e00325737f15f7a00";
		int type=1;
		String imageSize="300x300";
		String imageType=".gif";
		boolean isExtent=true;
		try {
			String s=ImageUtil.getScaleImage(uri,imageName, imageSize,imageType,isExtent);
			Assert.assertNotNull(s);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Test
	public void scaleWatermarkImageTest(){
		String uri="/imageServer/image/7437d9dade4f1d071b4c3578b876df34/7f00325737f15f7a06_570x.gif?remark=200012017051002188810";
		String imageName="7437d9dade4f1d071b4c3578b876df34";
		int type=1;
		String imageSize="570x";
		String imageType=".gif";
		boolean isExtent=true;
		try {
			String s=ImageUtil.getScaleWatermarkImage(uri,imageName, imageSize,imageType,isExtent);
			Assert.assertNotNull(s);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

    /**
     * @param args
     */
    public static void main(String[] args) {
//    	//----------------------------------------取地址----------------------------
//    	System.out.println(ImageUtil.getImageUrl("596b185f90ee5db614094683", false));;
//    	
//    	//----------------------------------------取规格-----------------------------
//    	long begin = System.currentTimeMillis();
//    	String imageSize="100x";
//    	String width = imageSize.substring(0, imageSize.indexOf("x"));
//		String height = imageSize.substring(imageSize.indexOf("x")+1,imageSize.length());
//			System.out.println(width);
//			System.out.println(height);
//    	
//        // TODO Auto-generated method stub
//    	long begin = System.currentTimeMillis();
    	
    	try {
    		 File file=new File("D://596f051b45ce1f7b7ebfb9cf.gif");
    		 com.gif4j.GifImage gifImage=GifDecoder.decode(file); 
    		 int imageWideth=gifImage.getScreenWidth();
             int imageHeight=gifImage.getScreenHeight();
             getGifImage(file, file, imageWideth, imageHeight, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
////        getGifImage(new File("D://1234.gif"), new File("D://1235.gif"), 210, 140, true) ;
//        System.out.println("----gif4j----scaleImage---------end 耗时" + (System.currentTimeMillis() - begin));
//        System.out.println("success");
//    	
//    	//-----------------------------------------打水印------------------------------------------
//    	String path = "D://596ef6b30032c817dfa47f56.gif";
//
//        File dir = new File(path);
//	
//        try {
//			addTextWatermarkToGif(dir, dir);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//        System.out.println("----gif4j----scaleImage---------end 耗时" + (System.currentTimeMillis() - begin));
    	
    	//-----------------------------------------判断格式-----------------------------------------
    	String path = "D://596f051b45ce1f7b7ebfb9cf.jpg";
    	
		try {
			com.gif4j.GifImage gifImage2 = GifDecoder.decode(new File(path));
			System.out.println(gifImage2.getNumberOfFrames());
//			System.out.println(gifImage2.getNumberOfComments());
//			GifImage gifImage=new GifImage(path);
//			System.out.println(gifImage.getFrameCount());;
			 
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
    private static void addTextWatermarkToGif(File src, File dest)throws IOException {
    	//水印初始化、设置（字体、样式、大小、颜色）
//        TextPainter textPainter = new TextPainter(new Font("黑体", Font.ITALIC, 48));
//        textPainter.setOutlinePaint(Color.WHITE);
//        BufferedImage renderedWatermarkText = textPainter.renderString(watermarkText, true);
//        
        
        File file=new File("D://logo.png");
        BufferedImage bufferedImage = ImageIO.read(file);
        //图片对象
        com.gif4j.GifImage gf = GifDecoder.decode(src);
        //获取图片大小
        int iw = gf.getScreenWidth();
        int ih = gf.getScreenHeight();
        //获取水印大小
        int tw = bufferedImage.getWidth();
        int th = bufferedImage.getHeight();
        //水印位置
        Point p = new Point();
        p.x = iw - tw - 5;
        p.y = ih - th - 4;
        //加水印
        com.gif4j.Watermark watermark = new com.gif4j.Watermark(bufferedImage, p);
        gf = watermark.apply(GifDecoder.decode(src), true);
        //输出
        GifEncoder.encode(gf, dest);
    }
    /** 
        *GIF文件缩略图处理函数 
        *srcImg 源图  
        *destImg 缩略图 
        */  
        public static void getGifImage(File srcImg, File destImg, int formatWideth,  
                    int formatHeight, boolean smooth) {  
                try {  
                    com.gif4j.GifImage gifImage = GifDecoder.decode(srcImg); 
                    
                    
                    int imageWideth=gifImage.getScreenWidth();
                    int imageHeight=gifImage.getScreenHeight();
                    int changeToWideth = formatWideth;
                    int changeToHeight = formatHeight;
                    if (imageWideth > 0 && imageHeight > 0) {
                        // flag=true;

                        if (imageWideth / imageHeight >= formatWideth / formatHeight) {

                            if (imageWideth > formatWideth) {

                                changeToWideth = formatWideth;

                                changeToHeight = (imageHeight * formatWideth) / imageWideth;

                            } else {

                                changeToWideth = imageWideth;

                                changeToHeight = imageHeight;

                            }

                        } else {

                            if (imageHeight > formatHeight) {

                                changeToHeight = formatHeight;

                                changeToWideth = (imageWideth * formatHeight) / imageHeight;

                            } else {

                                changeToWideth = imageWideth;

                                changeToHeight = imageHeight;

                            }

                        }

                    }
              
                    com.gif4j.GifImage resizedGifImage2 = GifTransformer.resize(gifImage, changeToWideth, changeToHeight, smooth);  
                    GifEncoder.encode(resizedGifImage2, destImg);  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
          
            }  
}
