/**
 * 
 */
package com.zengshi.butterfly.app.client;

import com.zengshi.butterfly.app.exception.MappException;
import com.zengshi.butterfly.app.model.IAppDatapackage;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;


/**
 */
public class XmlClientHandler<T extends IAppDatapackage> extends StringClientHandler<T> {

	public XmlClientHandler()
	{
		super();
	}
	
	@Override
	public T preProcess(String request) throws Exception 
	{	
		T t = null;
		try {
			JAXBContext context = JAXBContext.newInstance(entityClass);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			t = (T)unmarshaller.unmarshal(new StringReader(request));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return t;
	}

	@Override
	protected String buildResponse(T response) throws MappException 
	{
		String result = null;
		try 
		{
			JAXBContext context = JAXBContext.newInstance(response.getClass());
			Marshaller marshaller = context.createMarshaller();
	        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	        marshaller.setProperty(Marshaller.JAXB_ENCODING, "utf-8");
	        //去掉生成xml的默认报文头。
	        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
	        //转换所有的适配字符，包括xml实体字符&lt;和&gt;，xml实体字符在好多处理xml的框架中是处理不了的，除非序列化。
//	        marshaller.setProperty("com.sun.xml.internal.bind.marshaller.CharacterEscapeHandler",
//	                 new CharacterEscapeHandler() {
//	             @Override
//	             public void escape(char[] ch, int start,int length, boolean isAttVal,
//	                     Writer writer) throws IOException {
//	                 writer.write(ch, start, length);
//	             }
//	        });
	        StringWriter writer = new StringWriter();
	        //添加自己想要的xml报文头
	        writer.write("<?xml version=\'1.0\' encoding=\'utf-8\'?>\n");
	        marshaller.marshal(response, writer);
	        result = writer.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}
}
