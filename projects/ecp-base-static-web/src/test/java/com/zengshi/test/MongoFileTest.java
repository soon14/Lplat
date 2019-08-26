package com.zengshi.test;

import com.zengshi.paas.utils.FileUtil;

public class MongoFileTest {
    
    public static void main(String[] args){
        
        String s = "<div><span>hello world</span></div>";
        String fileId = FileUtil.saveFile(s.getBytes(), "文件名", "html");
        System.out.println("FileId : "+fileId);
        byte[] bytes = FileUtil.readFile(fileId);
        System.out.println("html : "+new String(bytes));
    }
}

