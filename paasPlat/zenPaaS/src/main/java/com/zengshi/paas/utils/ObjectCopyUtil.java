package com.zengshi.paas.utils;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 对象复制工具类
 * 
 *
 */
public class ObjectCopyUtil {
	
	public static final Logger log = Logger.getLogger(ObjectCopyUtil.class);

	public static final String CP_MORE_METHOD_NAME = "copyMoreValues";

	/**
	 * 根据destJavaFile和srcJavaFile源文件，生成destJavaFile源文件中从srcJavaFile复制值的方法
	 *
	 * @param destJavaFile 要生成复制值的方法
	 * @param srcJavaFile  要从这个源文件复制值
	 * @return 生成的复制值的方法的字符串
	 */
	public static String genClsCopyMoreValuesMethod(String destJavaFile, String srcJavaFile) {
		String resTmpFileName = destJavaFile.substring(0, destJavaFile.lastIndexOf(File.separatorChar) + 1)
				+ CP_MORE_METHOD_NAME + "_tmp.txt";

		StringBuilder resBuf = new StringBuilder();
		try (BufferedReader br4dest =
					 new BufferedReader(new FileReader(destJavaFile));
			 BufferedReader br4src =
					 new BufferedReader(new FileReader(srcJavaFile));
			 BufferedWriter bw4tmp = new BufferedWriter(new FileWriter(resTmpFileName, false))
		) {
			List<String> destLines = new ArrayList<>();
			List<String> srcLines = new ArrayList<>();
			String line = "";
			while ((line = br4dest.readLine()) != null) {
				destLines.add(line);
			}
			while ((line = br4src.readLine()) != null) {
				srcLines.add(line);
			}
			StringBuilder strBuf = new StringBuilder();
			int x = destJavaFile.lastIndexOf(File.separatorChar) + 1;
			int y = destJavaFile.lastIndexOf(".");
			String destClazzName = destJavaFile.substring(x, y);
			x = srcJavaFile.lastIndexOf(File.separatorChar) + 1;
			y = srcJavaFile.lastIndexOf(".");
			String srcClazzName = srcJavaFile.substring(x, y);
			strBuf.append("public ").append(destClazzName).append(" ")
					.append(CP_MORE_METHOD_NAME).append("(").append(destClazzName)
					.append(" descVO,").append(srcClazzName).append(" srcVO) {");
			resBuf.append(strBuf).append("\r\n");
			bw4tmp.write(strBuf.toString());
			bw4tmp.newLine();
			for (String destLine : destLines) {
				if (null != destLine && destLine.indexOf("set") >= 0) {
					String srcMethodName = "";

					String destFieldName = destLine.substring(destLine.indexOf("set") + 3, destLine.indexOf("("));
					String destMethodName = "set" + destFieldName;
					if (destLine.toLowerCase().indexOf("boolean") < 0) {
						for (String srcLine : srcLines) {
							if (srcLine.indexOf("get" + destFieldName) >= 0) {
								srcMethodName = "get" + destFieldName;
							}
						}
					} else {
						for (String srcLine : srcLines) {
							if (srcLine.indexOf("is" + destFieldName) >= 0) {
								srcMethodName = "is" + destFieldName;
							}
						}
					}
					if (StringUtil.isNotBlank(srcMethodName)) {
						bw4tmp.write('\t' + "descVO." + destMethodName + "(srcVO." + srcMethodName + "());");
						bw4tmp.newLine();
						resBuf.append('\t' + "descVO." + destMethodName + "(srcVO." + srcMethodName + "());").append("\r\n");
					}
				}
			}
			bw4tmp.write('\t' + "return descVO;");
			bw4tmp.newLine();
			bw4tmp.write("}");
			bw4tmp.flush();
			resBuf.append('\t' + "return descVO;").append("\r\n");
			resBuf.append("}");
			return resBuf.toString();
		} catch (IOException e) {
			log.error("打开文件读异常:", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * 根据destClazz和srcClazz类，生成destClazz中从srcClazz复制值的方法
	 *
	 * @param destClazz 要生成复制值的方法
	 * @param srcClazz  要从这个源文件复制值
	 * @return 生成的复制值的方法的字符串
	 */
	public static String genClsCopyMoreValuesMethod(Class destClazz, Class srcClazz) {

		Method[] destClazzMethods = destClazz.getMethods();
		StringBuilder strBuf = new StringBuilder();
		StringBuilder resBuf = new StringBuilder();
		String destClazzName = destClazz.getName().substring(destClazz.getName().lastIndexOf(".") + 1);
		String srcClazzName = srcClazz.getName().substring(srcClazz.getName().lastIndexOf(".") + 1);
		URL url = destClazz.getClassLoader().getResource("");
		String urlFileName = url.getFile() + CP_MORE_METHOD_NAME + "_tmp.txt";
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(urlFileName, false))) {

			strBuf.append("public ").append(destClazzName).append(" ")
					.append(CP_MORE_METHOD_NAME).append("(").append(destClazzName)
					.append(" descVO,").append(srcClazzName).append(" srcVO) {");
			resBuf.append(strBuf).append("\r\n");
			bw.write(strBuf.toString());
			bw.newLine();
			for (Method destClazzMethod : destClazzMethods) {
				String destClazzMethodName = destClazzMethod.getName();
				if (destClazzMethodName.indexOf("set") >= 0) {
					Class<?>[] paramTypes = destClazzMethod.getParameterTypes();
					String srcClazzMethodName = "";
					if (paramTypes[0].getName().toLowerCase().indexOf("boolean") < 0) {
						srcClazzMethodName = "get" + destClazzMethodName.substring(3);
						try {
							Method srcClazzMethod = srcClazz.getMethod(srcClazzMethodName, paramTypes);
						} catch (NoSuchMethodException e) {
//					log.info("方法名称:"+srcClazzMethodName+"在类:"+srcClazz.getName()+"不存在。",e);
//					throw new RuntimeException(e);
						}

					} else {
						srcClazzMethodName = "is" + destClazzMethodName.substring(3);
						try {
							Method srcClazzMethod = srcClazz.getMethod(srcClazzMethodName, paramTypes);
						} catch (NoSuchMethodException e) {
//					log.error("方法名称:"+srcClazzMethodName+"在类:"+srcClazz.getName()+"不存在。",e);
//					throw new RuntimeException(e);
						}
					}
					if (StringUtil.isNotBlank(srcClazzMethodName)) {
						bw.write('\t' + "descVO." + destClazzMethodName + "(srcVO." + srcClazzMethodName + "());");
						bw.newLine();
						resBuf.append('\t' + "descVO." + destClazzMethodName + "(srcVO." + srcClazzMethodName + "());").append("\r\n");
					}
				}
			}
			resBuf.append('\t' + "return descVO;").append("\r\n");
			resBuf.append("}");
			bw.write('\t' + "return descVO;");
			bw.newLine();
			bw.write("}");
			bw.flush();
			return resBuf.toString();
		} catch (IOException ioe) {
			log.error("打开文件读异常:", ioe);
			throw new RuntimeException(ioe);
		}

	}

	/**
	 * 复制整个对象的属性值到另外一个对象的对应的同名和同类型的属性，但名为sguid的属性不拷贝
	 * 
	 * @param vo
	 *            <Object> 源值对象
	 * @param target
	 *            <Object> 目标对象
	 * @param not_copy
	 *            <String> 用于指定不拷贝值的属性，可传多个属性名，之间用逗号隔开
	 * @param isCopyNull
	 *            <boolean> 是否拷贝NULL值
	 * @return void
	 */
	public static void copyObjValue(Object vo, Object target, String not_copy,
			boolean isCopyNull) {
	    ///如果原值为空，那么直接放回；
	    if(vo == null){
	        log.info("对象复制，入参为空");
			return;
		}
		//modified by zhangys3 2016/05/25 如果目标对象有复制方法，则调用目标对象的复制方法来提高复制的效率
		//call this method and return;
		try {
			Method method = target.getClass().getMethod(CP_MORE_METHOD_NAME, target.getClass(), vo.getClass());
			method.invoke(target, target, vo);
			return;
		} catch (NoSuchMethodException e) {
//			log.warn("调用"+target.getClass().getName()+"的方法copyMoreValues异常",e);
		} catch (SecurityException | IllegalAccessException | InvocationTargetException e) {
			log.warn("调用" + target.getClass().getName() + "的方法copyMoreValues异常",e);
		}
		@SuppressWarnings("rawtypes")
		Class cls = vo.getClass();
		if(StringUtil.isEmpty(not_copy)){
		    not_copy = "serialVersionUID";
		} else {
		    not_copy = not_copy + ",serialVersionUID";
		}
		
		while (!cls.getName().equals("java.lang.Object")) {
			copyObjectValue(vo, target, cls, not_copy, isCopyNull);
			cls = cls.getSuperclass();
		}
	}

	@SuppressWarnings("rawtypes")
	private static void copyObjectValue(Object vo, Object target, Class cls,
			String not_copy, boolean isCopyNull) {
		int flag = 0;
		if (StringUtils.isNotBlank(not_copy)) {
			not_copy = "," + not_copy + ",";// 前后加逗号是为了后面能够准确的判断所包含的属性名称
		}

		try {
			String sname = "";
			Field[] fields = cls.getDeclaredFields();

			for (int i = 0; i < fields.length; i++) {
				sname = fields[i].getName();

				// 如果属性名存在于not_copy指的属性名范围中，则不拷贝访属性值
				if ((StringUtils.isNotBlank(not_copy) && not_copy.indexOf(","
						+ sname + ",") != -1))
					continue;
				///检查vo 是否有该属性的  PropertyDescriptor ;如果没有，或者读取方法为空，那么则继续；
				PropertyDescriptor descriptor = PropertyUtils.getPropertyDescriptor(vo, sname);
				if(descriptor == null || descriptor.getReadMethod() == null){
				    continue;
				}
				//BeanUtils.
				if ((fields[i].getType().toString().startsWith("class") || fields[i].getType().toString().startsWith("interface"))
						&& !fields[i].getType().getName().equals("java.lang.String")) { // 对象类型字段值拷贝
					try {
					    Object obj= BeanUtils.getProperty(vo, sname);
						if (isCopyNull == false && obj == null) {
							continue;
						}else if(null!=obj){
						    BeanUtils.setProperty(target, sname, MethodUtils
                                    .invokeMethod(vo, "get"
                                            + sname.substring(0, 1)
                                                    .toUpperCase()
                                            + sname.substring(1), null));
						}
					} catch (Exception ne) {
					    log.error(ne.getMessage(),ne);
						flag = 1;
						continue;
					}
				} else { // 基本类型字段值拷贝
					try {
						if (isCopyNull == false
								&& BeanUtils.getProperty(vo, sname) == null) {
							continue;
						} else {
							BeanUtils.setProperty(target, sname,
									BeanUtils.getProperty(vo, sname));
						}
					} catch (Exception ne) {
					    log.error(ne.getMessage(),ne);
						flag = 1;
						continue;
					}
				}
			}
		} catch (Exception e) {
			flag = 1;
			log.error(e.getMessage(),e);
		}

		if (flag == 1) {
			flag = 0;

		}
	}

}
