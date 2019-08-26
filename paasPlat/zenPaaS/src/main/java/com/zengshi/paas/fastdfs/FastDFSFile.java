package com.zengshi.paas.fastdfs;

import java.io.Serializable;
import java.util.Map;

/**
 */
public class FastDFSFile implements Serializable {
    private static final long serialVersionUID = -3346739228683998173L;

    private String name;

    private byte[] content;

    private String ext;

    private Map<String,String> metaData;

    public FastDFSFile(){

    }

    public FastDFSFile(String name, byte[] content, String ext, Map<String, String> metaData) {
        this.name = name;
        this.content = content;
        this.ext = ext;
        this.metaData = metaData;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public Map<String, String> getMetaData() {
        return metaData;
    }

    public void setMetaData(Map<String, String> metaData) {
        this.metaData = metaData;
    }
}
