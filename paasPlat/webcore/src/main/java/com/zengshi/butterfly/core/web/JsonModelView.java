package com.zengshi.butterfly.core.web;

import java.util.Map;

import org.springframework.web.servlet.ModelAndView;

//TODO 要修改content-type
public class JsonModelView extends ModelAndView {

	private static  String JSON_VIEW_NAME="jsonView";
	
	public JsonModelView() {
		super(JSON_VIEW_NAME);
		// TODO Auto-generated constructor stub
	}
	/*public JsonModelView(JsonObject json) {
		super(JSON_VIEW_NAME,"jsonString", json.toJsonString());
		// TODO Auto-generated constructor stub
	} */

	public JsonModelView(Map<String, ?> model) {
		super(JSON_VIEW_NAME, model);
		// TODO Auto-generated constructor stub
	}

	public JsonModelView( String modelName, Object modelObject) {
		super(JSON_VIEW_NAME, modelName, modelObject);
		// TODO Auto-generated constructor stub
	}

	/*private JsonModelView(View view, Map<String, ?> model) {
		super(JSON_VIEW_NAME, model);
		// TODO Auto-generated constructor stub
	}

	private JsonModelView(View view, String modelName, Object modelObject) {
		super(JSON_VIEW_NAME, modelName, modelObject);
		// TODO Auto-generated constructor stub
	}

	private JsonModelView(View view) {
		super(JSON_VIEW_NAME);
		// TODO Auto-generated constructor stub
	}*/

}
