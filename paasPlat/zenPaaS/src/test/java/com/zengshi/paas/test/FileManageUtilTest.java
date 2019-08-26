package com.zengshi.paas.test;

import com.zengshi.paas.utils.FileUtil;
import com.mongodb.gridfs.GridFSDBFile;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 */
public class FileManageUtilTest {
    @Test
    public void fileTest(){

        Map<String,String> conditions = new HashMap<>();
        conditions.put("objectId","55659592cc255189e6b7a12a");
        List<GridFSDBFile> files = FileUtil.queryWithCondition(conditions);
        if(files == null || files.isEmpty()){
            System.out.println("==啥也木有===");
        } else {
            System.out.println("============="+files.get(0).getFilename());
        }
    }
}
