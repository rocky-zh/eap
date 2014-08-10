package eap.util;

import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.context.ApplicationContext;

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
@SuppressWarnings("unchecked")
public class BeanUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(BeanUtil.class);
	
	public static void forBeanPropertyAccess(Object target, ApplicationContext context) {
		BeanWrapper targetBean = PropertyAccessorFactory.forBeanPropertyAccess(target);
		
//		 Map<String, CustomEditorConfigurer>  BeanFactoryUtils.beansOfTypeIncludingAncestors(context, CustomEditorConfigurer.class, true, false);
		
	}
	
	public static Object copyProperties(Object source, Object target) {
		org.springframework.beans.BeanUtils.copyProperties(source, target);
		return target;
	}
	
	public static Object copyProperties(Map source, Object target) {
		BeanWrapper targetBean = PropertyAccessorFactory.forBeanPropertyAccess(target);
		targetBean.setPropertyValues(source);
		return target;
	}
	public static Map copyProperties(Map source, Map target) {
		if (source != null && target != null) {
			target.putAll(source);
		}
		
		return target;
	}
	
	public static Object getProperty(Object bean, String name) {
		if (bean == null) {
			return null;
		}
		
		try {
			return PropertyUtils.getNestedProperty(bean, name);
		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
		}
		
		return null;
	}
	public static String getPropertyAsString(Object bean, String name) {
		try {
			return BeanUtils.getProperty(bean, name);
		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
		}
		
		return null;
	}
	
	public static void setProperty(Object bean, String name, Object value) {
		try {
			BeanUtils.setProperty(bean, name, value);
		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
		}
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
	
	public static Map toMap(Object obj) {
		if (obj == null) {
			return null;
		}
		if (obj instanceof Map) {
			return (Map) obj;
		} 
		else {
			Map map = new HashMap();
			BeanWrapper bean = new BeanWrapperImpl(obj);
			PropertyDescriptor[] pds = bean.getPropertyDescriptors();
			for (PropertyDescriptor pd : pds) {
				String name = pd.getName();
				if ("class".equals(name)) {
					continue;
				}
				
				map.put(name, bean.getPropertyValue(name));
			}
			
			return map;
		}
	}
	
	public static <T> Map<String, T> toMap(List<T> list, String keyField) {
		if (list == null) {
			return null;
		}
		
		Map<String, T> map = new HashMap<String, T>(list.size());
		for (T item : list) {
			map.put(getPropertyAsString(item, keyField), item);
		}
		
		return map;
	}
	
	public static Object[][] toArray2(List list, String[] fields) {
		if (list == null || list.size() == 0 
			|| fields == null || fields.length == 0) {
			return null;
		}
		
		Object[][] result = new Object[list.size()][];
		for (int i = 0; i < list.size(); i++) {
			Object itemObj = list.get(i);
			result[i] = new Object[fields.length];
			for (int j = 0; j < fields.length; j++) {
				result[i][j] = getProperty(itemObj, fields[j]);
			}
		}
		
		return result;
	}
	public static List<Object[]> toArray(List list, String[] fields) {
		if (list == null || list.size() == 0 
			|| fields == null || fields.length == 0) {
			return null;
		}
		
		List<Object[]> result = new ArrayList<Object[]>(list.size());
		for (int i = 0; i < list.size(); i++) {
			Object itemObj = list.get(i);
			Object[] item = new Object[fields.length];
			for (int j = 0; j < fields.length; j++) {
				item[j] = getProperty(itemObj, fields[j]);
			}
			result.add(item);
		}
		
		return result;
	}
	
	public static List toTree(List nodeList) {
		return toTree(nodeList, "id", "parentId", "children", "leaf", true);
	}
	public static List toTreeByCode(List nodeList) {
		return toTree(nodeList, "code", "parentCode", "children", "leaf", true);
	}
	public static <T> List<T> toTree(List<T> nodeList, String nodeField, String parentNodeField, String childrenField, String leafField, boolean clone) {
		if (nodeList == null) {
			return null;
		}
		
		List<T> treeVOList = new ArrayList<T>();
		Map<String, List<Integer>> nodePaths = new HashMap<String, List<Integer>>() {
			@Override
			public List<Integer> get(Object key) {
				List<Integer> list = super.get(key);
				if (list == null) {
					list = new ArrayList<Integer>();
					super.put(key.toString(), list);
				}
				return list;
			}
		};
		
		for (T node : nodeList) {
			if (clone) {
				node = deepClone(node);
			}
			
			String nodeValue = getPropertyAsString(node, nodeField);
			String parentNodeValue = getPropertyAsString(node, parentNodeField);
			if (StringUtils.isBlank(parentNodeValue)) {
				if (StringUtils.isNotBlank(leafField)) {
					setProperty(node, leafField, true);
				}
				
				int idx = treeVOList.size();
				treeVOList.add(idx, node);
				nodePaths.get(nodeValue).add(idx);
			} else {
				List<Integer> parentMenuPath = nodePaths.get(parentNodeValue);
				if (parentMenuPath.isEmpty()) {
					continue;
				} else {
					T pNodeVO = treeVOList.get(parentMenuPath.get(0));
					List<T> pNodeVOList = (List<T>) getProperty(pNodeVO, childrenField);
					for (int i = 1; i < parentMenuPath.size(); i++) {
						pNodeVO = pNodeVOList.get(parentMenuPath.get(i));
						pNodeVOList = (List<T>) getProperty(pNodeVO, childrenField);
					}
					
					if (StringUtils.isNotBlank(leafField)) {
						setProperty(pNodeVO, leafField, false);
						setProperty(node, leafField, true);
					}
					
					int idx = pNodeVOList.size();
					pNodeVOList.add(idx, node);
					
					nodePaths.get(nodeValue).addAll(parentMenuPath);
					nodePaths.get(nodeValue).add(idx);
				}
			}
		}
		
		return treeVOList;
	}
	
	public static <T> T first(List<T> list) {
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		
		return null;
	}
	
	public static <T> T clone(T obj) {
		if (obj == null) {
			return null;
		}
		
		try {
			return (T) BeanUtils.cloneBean(obj);
		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
			return null;
		}
	}
	
	public static <T> T deepClone(T obj) {
		if (!(obj instanceof Serializable)) {
			throw new IllegalArgumentException("not implement Serializable interface");
		}
		
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = null;
			try {
				oos = new ObjectOutputStream(baos);
				oos.writeObject(obj);
			} finally {
				if (oos != null) {
					oos.close();
				}
			}
			
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream ois = null;
			try {
				ois = new ObjectInputStream(bais);
				return (T) ois.readObject();
			} finally {
				if (ois != null) {
					ois.close();
				}
			}
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
	}
	
	private static <T> T[] removeElement(T[] array, T removeEl, Object[] toArrayObj) {
		if (array == null) {return null;};
		if (array.length == 0) {return array;}
		
		List<T> list = new ArrayList<T>(array.length);
		for (T t : array) {
			if (!((t == null && removeEl == null) || (t != null && removeEl != null && t.equals(removeEl)))) {
				list.add(t);
			}
		}
		
		return (T[]) list.toArray(toArrayObj);
	}
	public static String[] removeElement(String[] array, String removeEl) {
		return removeElement(array, removeEl, new String[0]);
	}
	
	public static <V> Map<String,V> filterForPrefix(Map<String,V> srcMap, String prefix) {
		if (srcMap == null) return null;
		if (StringUtils.isBlank(prefix)) {
			return new HashMap<String, V>(srcMap);
		}
		
		Map<String,V> descMap = new HashMap<String,V>();
		for (Object keyObj : srcMap.keySet()) {
			String key = (String) keyObj;
			int prefixLen = prefix.length();
			if (key.startsWith(prefix)) {
				descMap.put(key.substring(prefixLen), srcMap.get(key));
			}
		}
		
		return descMap;
	}
	
	public static <T> Set<T> toSet(T[] array) {
		if (array == null) {
			return null;
		}
		
		Set<T> set = new LinkedHashSet<T>();
		for (T value : array) {
			set.add(value);
		}
		
		return set;
	}
	
	public static <T> List<T> toList(T[] array) {
		if (array == null) {
			return null;
		}
		
		List<T> list = new ArrayList<T>();
		for (T value : array) {
			list.add(value);
		}
		
		return list;
	}
}