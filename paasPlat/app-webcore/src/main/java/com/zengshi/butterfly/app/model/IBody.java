/**
 * 
 */
package com.zengshi.butterfly.app.model;

import org.apache.commons.beanutils.PropertyUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import java.beans.PropertyDescriptor;
import java.util.Map;

/**
 *
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class IBody {
    
    protected Map<String, Object> expand;

    public Map<String, Object> getExpand() {
        return expand;
    }

    public void setExpand(Map<String, Object> expand) {
        this.expand = expand;
    }

	@Override
	public String toString() 
	{
		String ret = "";
		
		try{
			
			StringBuffer string = new StringBuffer("");
			
			PropertyDescriptor[] props = PropertyUtils.getPropertyDescriptors(this.getClass());
			
			if(props == null || props.length==0)
			{
				return this.getClass().getName() + "[ ]";
			}
			
			for(PropertyDescriptor prop : props)
			{
				if(prop.getPropertyType() == java.lang.Class.class)
					continue;

				string.append(prop.getName() + "=" + PropertyUtils.getProperty(this, prop.getName()) + ", ");
			}
			
			ret = string.toString().substring(0, string.toString().lastIndexOf(","));
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return this.getClass().getName() + "[ "+ret+" ] ";
	}
	
}
