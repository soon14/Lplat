package com.zengshi.paas.test;

import java.io.File;
import java.io.FileInputStream;
import java.nio.channels.FileLock;

/**
 */
public class FileTest {

    public static void main(String[] args) throws Exception{
//        File file=new File("E:\\logfile.txt");
//        boolean flag=file.renameTo(new File("E:\\1.txt"));
//        if(!flag){
//            file.delete();
//        }

//        FileInputStream fs=new FileInputStream(file);
//        while(true){
//            FileLock lock=fs.getChannel().tryLock(0,Long.MAX_VALUE,true);
//            if(lock!=null){
//                break;
//            }
//        }
        String[] muns={"a","b","c","d","e"};
        for(int i=0;i<100;i++){
            int idx=Double.valueOf(Math.floor(Math.random()*5)).intValue();
            System.out.println(muns[idx]+"  "+idx);
        }

    }
}
