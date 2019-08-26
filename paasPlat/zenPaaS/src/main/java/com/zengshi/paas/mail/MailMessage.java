package com.zengshi.paas.mail;

import java.io.Serializable;
import java.util.List;

import net.sf.json.JSONSerializer;

public class MailMessage implements Serializable {
	private static final long serialVersionUID = 3381536013474647396L;
	private String from;
	private List<String> toList;
	private String title;
	private String content;

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public List<String> getToList() {
		return toList;
	}

	public void setToList(List<String> toList) {
		this.toList = toList;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String toJsonString() {
		return JSONSerializer.toJSON(this).toString();
	}

}
