package com.zengshi.ecp.frame.bean;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.util.StringUtils;

import java.util.Map;

/**bean定义
 */
public class BeanDefine {
    private String scope= BeanDefinition.SCOPE_SINGLETON;
    /**
     * bean 名称
     */
    private String beanName;
    /**
     * 接口名称
     */
    private Class<?> interfaceClass;
    /**
     * bean 类名
     */
    private Class<?> beanClass;
    /**
     * bean属性
     */
    private Map<String,Object> attrMap;
    /**
     * bean引用属性
     */
    private Map<String,String> beanRefMap;
    /**
     * 初始化方法
     */
    private String initMethodName;
    /**
     * 销毁方法
     */
    private String destroyMethodName;
    /**
     * 依赖bean
     */
    private String dependsOn;
    /**
     * @see #AUTOWIRE_NO 0
     * @see #AUTOWIRE_BY_NAME 1
     * @see #AUTOWIRE_BY_TYPE 2
     * @see #AUTOWIRE_CONSTRUCTOR 3
     * @see #AUTOWIRE_AUTODETECT 4
     */
    private int autowireMode= AbstractBeanDefinition.AUTOWIRE_NO;
    /**
     * @see #DEPENDENCY_CHECK_NONE 0
     * @see #DEPENDENCY_CHECK_OBJECTS 1
     * @see #DEPENDENCY_CHECK_SIMPLE 2
     * @see #DEPENDENCY_CHECK_ALL 3
     */
    private int dependencyCheck=AbstractBeanDefinition.DEPENDENCY_CHECK_NONE;

    private boolean lazy;

    private String parentName;

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getBeanName() {
        if(!StringUtils.hasLength(beanName) && null!=interfaceClass){
            return StringUtils.uncapitalize(interfaceClass.getSimpleName());

        }
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public Map<String, Object> getAttrMap() {
        return attrMap;
    }

    public void setAttrMap(Map<String, Object> attrMap) {
        this.attrMap = attrMap;
    }

    public Map<String, String> getBeanRefMap() {
        return beanRefMap;
    }

    public void setBeanRefMap(Map<String, String> beanRefMap) {
        this.beanRefMap = beanRefMap;
    }

    public Class<?> getInterfaceClass() {
        return interfaceClass;
    }

    public void setInterfaceClass(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    public String getInitMethodName() {
        return initMethodName;
    }

    public void setInitMethodName(String initMethodName) {
        this.initMethodName = initMethodName;
    }

    public String getDestroyMethodName() {
        return destroyMethodName;
    }

    public void setDestroyMethodName(String destroyMethodName) {
        this.destroyMethodName = destroyMethodName;
    }

    public String getDependsOn() {
        return dependsOn;
    }

    public void setDependsOn(String dependsOn) {
        this.dependsOn = dependsOn;
    }

    public int getAutowireMode() {

        return autowireMode;
    }

    public void setAutowireMode(int autowireMode) {
        this.autowireMode = autowireMode;
    }

    public int getDependencyCheck() {
        return dependencyCheck;
    }

    public void setDependencyCheck(int dependencyCheck) {
        this.dependencyCheck = dependencyCheck;
    }

    public boolean isLazy() {
        return lazy;
    }

    public void setLazy(boolean lazy) {
        this.lazy = lazy;
    }

    public boolean beanNameIsNull(){

        return !StringUtils.hasLength(beanName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BeanDefine that = (BeanDefine) o;

        return new EqualsBuilder().append(beanName,that.getBeanName())
                .append(interfaceClass.getName(),that.getInterfaceClass().getName())
                .isEquals();



    }

    @Override
    public int hashCode() {

        return new HashCodeBuilder().append(beanName).append(interfaceClass.getName()).toHashCode();
    }
}
