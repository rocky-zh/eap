package eap.comps.datamapping.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * <p> Title: </p>
 * <p> Description: </p>
 * @作者 chiknin@gmail.com
 * @创建时间 
 * @版本 1.00
 * @修改记录
 * <pre>
 * 版本       修改人         修改时间         修改内容描述
 * ----------------------------------------
 * 
 * ----------------------------------------
 * </pre>
 */
public class ObjectUtil {
	private static final Logger logger = LoggerFactory.getLogger(ObjectUtil.class);
	
	@SuppressWarnings("unchecked")
	public static <T> T to(Object value, Class<T> clazz) {
		if (value == null) return null;
		
		if (clazz.isAssignableFrom(String.class)) {
			return (T) value.toString();
		} else if (clazz.isAssignableFrom(Integer.class) || clazz.isAssignableFrom(Integer.TYPE)) {
			return (T) new Integer(value.toString());
		} else if (clazz.isAssignableFrom(Double.class) || clazz.isAssignableFrom(Double.TYPE)) {
			return (T) new Double(value.toString());
		} else if (clazz.isAssignableFrom(Float.class) || clazz.isAssignableFrom(Float.TYPE)) {
			return (T) new Float(value.toString());
		} else if (clazz.isAssignableFrom(Boolean.class) || clazz.isAssignableFrom(Boolean.TYPE)) {
			return (T) new Boolean(value.toString());
		} else if (clazz.isAssignableFrom(Character.class) || clazz.isAssignableFrom(Character.TYPE)) {
			return (T) new Character(value.toString().charAt(0));
		} else {
			throw new IllegalArgumentException("unknow class type: " + clazz);
		}
	}
	
	public static Object instance(String className) {
		Assert.hasText(className, "'" + className + "' must not be empty");
		
		try {
			if (className.startsWith("[L")) {
				int idx = className.indexOf(":");
				if (idx > 0) {
					String componentType = className.substring(2, idx);
					int length = Integer.parseInt(className.substring(idx + 1));
					return Array.newInstance(ClassUtils.forName(componentType), length);
				} else {
					throw new IllegalArgumentException("can't instance class:" + className);
				}
			} else {
				return ClassUtils.forName(className).newInstance();
			}
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
	}
	
	public static BeanWrapper buildBeanWrapperImpl(Object o) {
		return new BeanWrapperImpl(o) {
			@SuppressWarnings("unchecked")
			public void setPropertyValue(String propertyName, Object value) throws BeansException {
				if (this.getWrappedInstance() instanceof Map) {
					((Map) this.getWrappedInstance()).put(propertyName, value);
				} else if (this.getWrappedInstance() instanceof Object[]) {
					((Object[]) this.getWrappedInstance())[Integer.parseInt(propertyName)] = value;
				} else if (this.getWrappedInstance() instanceof List) {
//					if (StringUtils.isBlank(propertyName)) {
						((List) this.getWrappedInstance()).add(value); // TODO add
//					} else {
//						((List) this.getWrappedInstance()).set(Integer.parseInt(propertyName), value);
//					}
				} else {
					super.setPropertyValue(propertyName, value);
				}
			}
			
			@Override
			public Object getPropertyValue(String propertyName) throws BeansException {
				if (this.getWrappedInstance() instanceof Map) {
					return ((Map) this.getWrappedInstance()).get(propertyName);
				} else if (this.getWrappedInstance() instanceof Object[]) {
					return ((Object[]) this.getWrappedInstance())[Integer.parseInt(propertyName)];
				} else if (this.getWrappedInstance() instanceof List) {
					return ((List) this.getWrappedInstance()).get(Integer.parseInt(propertyName)); // TODO add
				} else {
					return super.getPropertyValue(propertyName);
				}
			}
			
			public boolean isEmpty() {
				if (this.getWrappedInstance() instanceof Map) {
					return ((Map) this.getWrappedInstance()).isEmpty();
				} else if (this.getWrappedInstance() instanceof Object[]) {
					return ((Object[]) this.getWrappedInstance()).length == 0;
				} else if (this.getWrappedInstance() instanceof List) {
					return ((List) this.getWrappedInstance()).size() == 0;
				} else {
					PropertyDescriptor[] propDescs = super.getPropertyDescriptors();
					if (propDescs != null && propDescs.length > 0) {
						for (PropertyDescriptor propDesc : propDescs) {
							if (propDesc.getWriteMethod() == null) {
								continue;
							}
							
							String name = propDesc.getName();
							if ("class".equals(name)) {
								continue;
							}
							
							Object val = super.getPropertyValue(name);
							if (val != null && val.toString().length() != 0) {
								return false;
							}
						}
					}
					
					return true;
				}
			}
		};
	}
	
	public static void setProperty(Object o, Properties props) {
		for (Object keyObj : props.keySet()) {
			String key = (String) keyObj;
			try {
				BeanUtils.setProperty(o, key, props.getProperty(key));
			} catch (Exception e) {
				logger.debug(e.getMessage(), e);
			}
		}
	}
	
	public static void setProperty(Object bean, String name, Object value) {
		try {
			BeanUtils.setProperty(bean, name, value);
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
	}
	
	public static String getProperty(Object bean, String name) {
		try {
			return BeanUtils.getProperty(bean, name);
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getProperty(Object bean, String name, Class<T> clazz) {
		try {
			return (T) PropertyUtils.getNestedProperty(bean, name);
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
	}

	public static Object invokeMethod(Object obj, Object[] args, String methodName) {
		Class<?> objClass = obj.getClass();
		Class<?>[] argsClasses = null;
		if (args != null && args.length > 0) {
			argsClasses = new Class[args.length];
			for (int i = 0; i < args.length; i++) {
				argsClasses[i] = args[i].getClass();
			}
		}
		
		try {
			if (argsClasses == null || argsClasses.length == 0) {
				Method objMethod = objClass.getMethod(methodName); 
				objMethod.setAccessible(true);
				return objMethod.invoke(obj);
			} else {
				Method objMethod = objClass.getMethod(methodName, argsClasses); 
				objMethod.setAccessible(true);
				return objMethod.invoke(obj, args);
			}
		} catch (InvocationTargetException e) {
			Throwable t = e.getTargetException();
			throw new IllegalArgumentException(t.getMessage(), t);
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
	}
	
	public static final Class[] JAVA_TYPES = new Class[] {
		Byte.class, Byte.TYPE,
		Character.class, Character.TYPE,
		Short.class, Short.TYPE,
		Integer.class, Integer.TYPE,
		Float.class, Float.TYPE,
		Double.class, Double.TYPE,
		Long.class, Long.TYPE,
		String.class,
		BigDecimal.class
	}; 
	public static boolean isJavaType(Object o) {
		if (o == null) return false;
		return isJavaType(o.getClass());
	}
	public static boolean isJavaType(Class clazz) {
		for (Class ct : JAVA_TYPES) {
			if (clazz.isAssignableFrom(ct)) {
				return true;
			}
		}
		
		return false;
	}
	
	public static String identityToString(Object obj) {
		if (obj == null) {
			return "";
		}
		
		return obj.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(obj));
	}
	
	public static final Object nullToBlankString(Object o) {
		if (o == null) {
			return "";
		}
		
		return o;
	}
	
	public static <T> T getFirstItem(List<T> list) {
		if (list == null || list.size() == 0) {
			return null;
		}
		
		return list.get(0);
	}
	
	public static <T> T clone(T bean) {
		try {
			return (T) BeanUtils.cloneBean(bean);
		} catch (Exception e) {
		}
		
		return null;
	}
	
	/**
	 * <code>
	 * 10.00 -> 10
	 * 10.001 -> 10.001
	 * 10.00100 -> 10.001
	 * </code>
	 * @param number
	 * @return
	 */
	public static String removeEndsWithAsString(Double number) {
		if (number == null) {
			return null;
		}
		
		String numberStr = new BigDecimal(number).toPlainString();
		if (numberStr.indexOf(".") == -1) {
		    return numberStr;
		}
		int idx = numberStr.length() - 1;
		boolean found = false;
		while (idx >= 0) {
			char c = numberStr.charAt(idx);
			if ('0' != c || '.' == c) {
				if ('.' == c) idx--;
				found = true;
				break;
			}
			
			idx--;
		}
		
		if (found) {
			return numberStr.substring(0, idx + 1);
		}
		
		return numberStr;
	}
	
	public static Map<String, String> splitPropString(String propString) {
		if (StringUtils.isBlank(propString)) {
			return Collections.EMPTY_MAP;
		}
		
		Map<String,String> propMap = new HashMap<String, String>();
		String[] propPairGroup = propString.split(";");
		for (String propPair : propPairGroup) {
			if (StringUtils.isNotBlank(propPair)) {
				String[] propPairArr = propPair.split(":");
				if (propPairArr != null) {
					if (propPairArr.length == 2) {
						propMap.put(propPairArr[0].trim().toLowerCase(), propPairArr[1]);
					} else if (propPairArr.length == 1) {
						propMap.put(propPairArr[0].trim().toLowerCase(), "");
					}
				}
			}
		}
		
		return propMap;
	}
}