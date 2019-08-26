package com.zengshi.paas.xml;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import com.thoughtworks.xstream.io.xml.XppDriver;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;

/**
 */
public class XMLResolver {
    private static final Logger logger=Logger.getLogger(XMLResolver.class);
    private static final String XML_HEAD="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    private XStream xstream;

    public XMLResolver(){
        xstream=new XStream(new XppDriver(new XmlFriendlyNameCoder("_-", "_")));
        xstream.autodetectAnnotations(true);
    }

    public XStream getXstream() {
        return xstream;
    }

    /**
     * 对象转成xml
     * @param source
     */
    public String toXml(Object source){
        return xstream.toXML(source);
    }

    /**
     * 对象转成xml流
     * @param source
     * @param ops 输出流
     */
    public void toXml(Object source, OutputStream ops){
        try {
            ops.write(XML_HEAD.getBytes());
        } catch (IOException e) {
            logger.error("写入xml声明语句失败。",e);
        }
        xstream.toXML(source,ops);
    }

    /**
     *对象转成xml文件
     * @param source 对象
     * @param file 文件
     */
    public void toXml(Object source,String file){
        OutputStream ops=null;
        try {
            ops=new FileOutputStream(file);
            toXml(source,ops);
            ops.flush();
        } catch (Exception e) {
            logger.error("文件生成错误。",e);
        }finally {
            if(null!=ops){
                try {
                    ops.close();
                } catch (IOException e) {
                    logger.error("文件close失败。",e);
                }
            }
        }
    }

    /**
     * xml文件转成对象
     * @param file
     * @return
     */
    public Object toObject(String file){
        File xmlFile=new File(file);
        return xstream.fromXML(xmlFile);
    }

    /**
     * xml输入流转成对象
     * @param ips
     * @return
     */
    public Object toObject(InputStream ips){

        return xstream.fromXML(ips);
    }

    /**
     * 添加类别名
     * @param alias 别名
     * @param clazz 类名
     */
    public void addAlias(String alias,Class clazz){
        xstream.alias(alias,clazz);
    }

    /**
     * 添加属性别名
     * @param field 属性名
     * @param alias 别名
     * @param clazz 类
     */
    public void addFieldAlias(String field,String alias,Class clazz){
        xstream.aliasField(alias,clazz,field);
    }

    /**
     * 去除数组层
     * @param field 属性名
     * @param clazz 类名
     */
    public void addImplicitArray(String field,Class clazz,Class itemClazz){
        if(itemClazz==null){
            xstream.addImplicitArray(clazz,field);
        }else{
            xstream.addImplicitArray(clazz,field,itemClazz);
        }
    }
    /**
     * 去除集合层
     * @param field 属性名
     * @param clazz 类名
     */
    public void addImplicitCollection(String field,Class clazz,Class itemClazz){
        if(itemClazz==null){
            xstream.addImplicitCollection(clazz,field);
        }else{
            xstream.addImplicitCollection(clazz,field,itemClazz);
        }
    }
    /**
     * 去除Map层
     * @param field 属性名
     * @param clazz 类名
     * @param itemClazz 值类名
     * @param keyFieldName key名
     */
    public void addImplicitMap(String field,Class clazz,Class itemClazz,String keyFieldName){
        xstream.addImplicitMap(clazz,field,itemClazz,keyFieldName);
    }
    /**
     *不处理的类
     * @param clazz 类名
     */
    public void addImmutableType(Class clazz){
        xstream.addImmutableType(clazz);
    }

    /**
     * 不处理的属性
     * @param field 属性名
     * @param clazz 类名
     */
    public void addOmitfield(String field,Class clazz){
        xstream.omitField(clazz,field);
    }

    /**
     * 添加转换器
     * @param clazz 类名
     * @param field 属性名
     * @param converter 转换器
     */
    public void addConverter(Class clazz,String field,Converter converter){
        xstream.registerLocalConverter(clazz,field,converter);
    }

    /**
     * 使用属性方式
     * @param clazz 类名
     * @param field 属性名
     */
    public void useAttributeFor(Class clazz,String field){
        xstream.useAttributeFor(clazz,field);
    }

    public static void main(String[] args){
        XMLResolver marshaller=new XMLResolver();
        marshaller.addAlias("person",Person.class);
//        marshaller.toXml(new Person("测试",Calendar.getInstance().getTime(),100),"E:\\person.xml");
        Person person=(Person) marshaller.toObject("E:\\person.xml");
        System.out.println(person.getName()+":"+person.getBirthday()+":"+person.getNumber());

//        String xml=marshaller.toXml(new Person("测试",Calendar.getInstance().getTime(),100));
//        System.out.println(xml);
    }
}
@XStreamAlias(value = "person",impl = Person.class)
class Person{
    @XStreamAsAttribute
    @XStreamAlias("NAME")
    private String name;
    private Date birthday;
    private int number;

    public Person(String name,Date birthday,int number){
        this.name=name;
        this.birthday=birthday;
        this.number=number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }
}
