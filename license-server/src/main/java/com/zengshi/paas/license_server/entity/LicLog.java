package com.zengshi.paas.license_server.entity;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "log")
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
@DynamicUpdate(true)
public class LicLog {
	private String log_id;
	private String from_ip;
	private Timestamp datetime;
	private int response_state;
	private String product_id;
	private String salt;
	private String msg;

	@Id
	@GeneratedValue(generator = "jpa-uuid")
	public String getLog_id() {
		return log_id;
	}

	public void setLog_id(String log_id) {
		this.log_id = log_id;
	}

	public String getFrom_ip() {
		return from_ip;
	}

	public void setFrom_ip(String from_ip) {
		this.from_ip = from_ip;
	}

	public Timestamp getDatetime() {
		return datetime;
	}

	public void setDatetime(Timestamp datetime) {
		this.datetime = datetime;
	}

	public int getResponse_state() {
		return response_state;
	}

	public void setResponse_state(int response_state) {
		this.response_state = response_state;
	}

	public String getProduct_id() {
		return product_id;
	}

	public void setProduct_id(String product_id) {
		this.product_id = product_id;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
