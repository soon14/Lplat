package com.zengshi.paas.config;

import com.zengshi.paas.ConfigurationCenter;
import com.zengshi.paas.ConfigurationWatcher;
import com.zengshi.paas.PaasException;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 */
public abstract class ConfigManager implements ConfigurationWatcher{
    private final static Logger logger=Logger.getLogger(ConfigManager.class);

    private ConfigurationCenter cc = null;
    /**
     * zk节点路径
     */
    private String confPath;
    /**
     * 多zk节点路径
     */
    private List<String> confPaths;
    /**
     * 监控变化的属性
     */
    private List<String> changeProps;


    private Map<String,Object> map;
    /**
     * 初始化
     */
    private boolean first;

    private boolean change;

    public void setChange(boolean change) {
        this.change = change;
    }

    public void setConfPath(String confPath) {
        this.confPath = confPath;
    }

    public void setConfPaths(List<String> confPaths) {
        this.confPaths = confPaths;
    }

    public void setCc(ConfigurationCenter cc) {
        this.cc = cc;
    }

    public void setChangeProps(List<String> changeProps) {
        this.changeProps = changeProps;
    }

    public void init() {
        try {
            if(null==confPaths && null==confPath){
                return;
            }
            first=true;
            map=new HashMap<String,Object>();
            if(confPaths==null && StringUtils.hasText(confPath)){
                confPaths=new ArrayList<String>();
                confPaths.add(confPath);
            }
            int size=confPaths.size();
            int i=0;
            for(String path : confPaths){
                if(++i==size){
                    first=false;
                }
                process(cc.getConfAndWatch(path, this));
            }

        } catch (PaasException e) {
            logger.error("ConfigManager 初始化失败。",e);
        }
    }

    @Override
    public void process(String conf) {

        JSONObject json = JSONObject.fromObject(conf);
        if(!CollectionUtils.isEmpty(map)){
            if(null!=changeProps){
                for(String prop : changeProps){
                    String preValue=map.get(prop)==null?"":map.get(prop).toString();
                    String curValue=json.get(prop)==null?"":json.get(prop).toString();
                    if(!preValue.equals(curValue)){
                        change=true;
                    }
                }
            }
        }else{
            change=true;
        }

        Set<Map.Entry> entrySet = json.entrySet();
        for(Map.Entry entry : entrySet){
            map.put((String)entry.getKey(),entry.getValue());
        }

        if(change && !first){
            doHandle(map);
        }
    }

    public abstract void doHandle(Map<String,Object> map);
}
