package com.zengshi.paas.test;

import com.zengshi.paas.ConfigurationCenter;
import com.zengshi.paas.utils.PaasContextHolder;
import org.junit.Test;

/**
 */
public class ConfigurationCenterTest {

    @Test
    public void setDataTest(){
        String path="/test/demo/cfg";
        ConfigurationCenter cc = PaasContextHolder.getContext().getBean(ConfigurationCenter.class);
        cc.setData(path,"ss");

    }
}
