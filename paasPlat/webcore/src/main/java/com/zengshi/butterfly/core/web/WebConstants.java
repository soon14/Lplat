package com.zengshi.butterfly.core.web;

/**
 * web常量定义
 *
 */
public interface WebConstants {
	
	public static final String URL_HOME_PAGE="url.homePage";
	public static final String URL_LOGIN_PAGE="url.loginPage";
	public static final String URL_NOT_PERMIT="url.notPermit";
	public static final String URL_NOT_LOGIN="url.notLogin";
	public static final String URL_SESSION_EXPRIED="url.sessionExpired";
	public static final String URL_LOGOUT_PAGE="url.logoutPage";
	
	public static final String URL_MALL_PAGE = "url.mallPage";
	
	public static final String URL_MANAGE_PAGE = "url.namagePage";
	
	public static final String VIEW_VELOCITY_SUFFIX="view.velocity.suffix";
	
	
	// 样式和静态资源
	/** 资源路径 **/
	public static final String THEME_RESOURCE_PATH="project.theme.path";
	
	/** 样式domain配置 **/
	public static final String THEME_DOMAIN_KEY="project.theme.domain";
	
	/** 是否使用静态资源缓存，只缓存路径信息**/
	public static final String THEME_REOURCE_CACHE="project.theme.useCache";
	
	/** 静态资源module配置 **/
	public static final String THEME_MODULE_KEY="project.theme.module";
	/** 静态资源支持的theme配置配置 **/
	public static final String THEME_SUPPORT_THEMES="project.theme.support";
	
	/**	 **/
	public static final String THEME_REQUEST_PARAM_KEY="project.theme.request.paramkey";
	
	/** theme在request中的key值配置 **/
	public static final String THEME_REQUEST_ATTR_KEY="project.theme.request.key";
	/** theme在request中的cookie值配置 **/
	public static final String THEME_COOKIE_KEY="project.theme.cookie.key";
	/** theme在session中的key值配置 **/
	public static final String THEME_SESSION_KEY="project.theme.session.key";
	/** theme的默认值配置 **/
	public static final String THEME_DEFAULT="project.theme.default";
	
	
	/**本系统的站点信息**/
	public static final String SITE_KEY = "site.key";
}
