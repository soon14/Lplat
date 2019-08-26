/**
 * 
 */
package com.zengshi.butterfly.app.client;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.zengshi.butterfly.app.exception.MappException;
import com.zengshi.butterfly.app.exception.MappExceptionCode;
import com.zengshi.butterfly.app.model.IAppDatapackage;

/**
 *
 */
@Service("jsonHandler")
public class JsonClientHandler<T extends IAppDatapackage> extends StringClientHandler<T>{

	Logger logger = LoggerFactory.getLogger(JsonClientHandler.class);

	@Override
	protected String buildResponse(T response) throws MappException 
	{
		String json = null;

		ObjectMapper mapper = new ObjectMapper();
		//数字加引号
        mapper.configure(JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS, true);
        mapper.configure(JsonGenerator.Feature.QUOTE_NON_NUMERIC_NUMBERS, true);
		// null替换为""
		mapper.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {
			@Override
			public void serialize(Object arg0, JsonGenerator arg1, SerializerProvider arg2) throws IOException, JsonProcessingException {
				arg1.writeString("");
			}
		});
		try {

			json=mapper.writeValueAsString(response);

		} catch (Exception e) {
			e.printStackTrace();
			throw new MappException(MappExceptionCode.UNRECOGNIZED_RESPONSE,e);
		}

		logger.debug("Json response ："+json);

		return json;

	}

	@Override
	protected T preProcess(String request) throws MappException {

		logger.debug("Json request："+request);

		if(StringUtils.isBlank(request))
			return null;

		try {
			ObjectMapper mapper = new ObjectMapper();
			return (T)mapper.readValue(request, IAppDatapackage.class);
		} catch (Exception e) {
			throw new MappException(MappExceptionCode.UNRECOGNIZED_REQUEST, e);
		} 
	}

}
