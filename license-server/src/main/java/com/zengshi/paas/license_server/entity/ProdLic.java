package com.zengshi.paas.license_server.entity;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "product_licenses")
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
@DynamicUpdate(true)
public class ProdLic {
	private String product_id;
	private int state;
	private String product_name;
	private String customer;
	private String customer_contact;
	private Timestamp install_date;
	private String install_server_ip;
	private Timestamp expire_date;
	private String license_code;
	private String service_url;
	private String callback_url;
	private String callback_port;
	private String callback_params;
	private Timestamp last_check_datetime;

	@Id
	@GeneratedValue(generator = "jpa-uuid")
	public String getProduct_id() {
		return product_id;
	}

	public void setProduct_id(String product_id) {
		this.product_id = product_id;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getProduct_name() {
		return product_name;
	}

	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public String getCustomer_contact() {
		return customer_contact;
	}

	public void setCustomer_contact(String customer_contact) {
		this.customer_contact = customer_contact;
	}

	public Timestamp getInstall_date() {
		return install_date;
	}

	public void setInstall_date(Timestamp install_date) {
		this.install_date = install_date;
	}

	public String getInstall_server_ip() {
		return install_server_ip;
	}

	public void setInstall_server_ip(String install_server_ip) {
		this.install_server_ip = install_server_ip;
	}

	public Timestamp getExpire_date() {
		return expire_date;
	}

	public void setExpire_date(Timestamp expire_date) {
		this.expire_date = expire_date;
	}

	public String getLicense_code() {
		return license_code;
	}

	public void setLicense_code(String license_code) {
		this.license_code = license_code;
	}

	public String getService_url() {
		return service_url;
	}

	public void setService_url(String service_url) {
		this.service_url = service_url;
	}

	public String getCallback_url() {
		return callback_url;
	}

	public void setCallback_url(String callback_url) {
		this.callback_url = callback_url;
	}

	public String getCallback_port() {
		return callback_port;
	}

	public void setCallback_port(String callback_port) {
		this.callback_port = callback_port;
	}

	public String getCallback_params() {
		return callback_params;
	}

	public void setCallback_params(String callback_params) {
		this.callback_params = callback_params;
	}

	public Timestamp getLast_check_datetime() {
		return last_check_datetime;
	}

	public void setLast_check_datetime(Timestamp last_check_datetime) {
		this.last_check_datetime = last_check_datetime;
	}
}
