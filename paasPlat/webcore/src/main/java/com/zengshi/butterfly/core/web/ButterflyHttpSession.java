/**
 * 
 */
package com.zengshi.butterfly.core.web;

import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

/**
 * 自定义session
 *
 */
public class ButterflyHttpSession implements HttpSession {

	private HttpSession session;

	
	public ButterflyHttpSession(HttpSession session) {
		super();
		this.session = session;
	}



	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#getAttribute(java.lang.String)
	 */
	@Override
	public Object getAttribute(String arg0) {
		// TODO Auto-generated method stub
		return session.getAttribute(arg0);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#getAttributeNames()
	 */
	@Override
	public Enumeration getAttributeNames() {
		// TODO Auto-generated method stub
		return session.getAttributeNames();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#getCreationTime()
	 */
	@Override
	public long getCreationTime() {
		// TODO Auto-generated method stub
		return session.getCreationTime();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#getId()
	 */
	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return session.getId();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#getLastAccessedTime()
	 */
	@Override
	public long getLastAccessedTime() {
		// TODO Auto-generated method stub
		return session.getLastAccessedTime();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#getMaxInactiveInterval()
	 */
	@Override
	public int getMaxInactiveInterval() {
		// TODO Auto-generated method stub
		return session.getMaxInactiveInterval();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#getServletContext()
	 */
	@Override
	public ServletContext getServletContext() {
		// TODO Auto-generated method stub
		return session.getServletContext();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#getSessionContext()
	 */
	@Override
	public HttpSessionContext getSessionContext() {
		// TODO Auto-generated method stub
		return session.getSessionContext();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#getValue(java.lang.String)
	 */
	@Override
	public Object getValue(String arg0) {
		// TODO Auto-generated method stub
		return session.getValue(arg0);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#getValueNames()
	 */
	@Override
	public String[] getValueNames() {
		// TODO Auto-generated method stub
		return session.getValueNames();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#invalidate()
	 */
	@Override
	public void invalidate() {
		// TODO Auto-generated method stub
		 session.invalidate();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#isNew()
	 */
	@Override
	public boolean isNew() {
		// TODO Auto-generated method stub
		return session.isNew();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#putValue(java.lang.String, java.lang.Object)
	 */
	@Override
	public void putValue(String arg0, Object arg1) {
		// TODO Auto-generated method stub
		 session.putValue(arg0,arg1);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#removeAttribute(java.lang.String)
	 */
	@Override
	public void removeAttribute(String arg0) {
		// TODO Auto-generated method stub
		 session.removeAttribute(arg0);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#removeValue(java.lang.String)
	 */
	@Override
	public void removeValue(String arg0) {
		// TODO Auto-generated method stub
		 session.removeValue(arg0);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#setAttribute(java.lang.String, java.lang.Object)
	 */
	@Override
	public void setAttribute(String arg0, Object arg1) {
		// TODO Auto-generated method stub
		 session.setAttribute(arg0,arg1);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#setMaxInactiveInterval(int)
	 */
	@Override
	public void setMaxInactiveInterval(int arg0) {
		// TODO Auto-generated method stub
		 session.setMaxInactiveInterval(arg0);
	}

}
