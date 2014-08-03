package eap.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class PagingDataList {
	
	private static final Logger logger = LoggerFactory.getLogger(PagingDataList.class);
	
	/** 总记录数 */
	private long totalCount = 0;
	/** 数据 */
	private List items;
	
	private Paginator paginator;
	
	private Map<String, Object> metaData;
	
	public PagingDataList() {
		super();
	}
	
	public PagingDataList(List items) {
		this.totalCount = items.size();
		this.items = items;
	}
	
	public PagingDataList(long totalCount, List items) {
		this.totalCount = totalCount;
		this.items = items;
	}
	
	public PagingDataList(long totalCount, List items, Paginator paginator) {
		this.totalCount = totalCount;
		this.items = items;
		this.paginator = paginator;
	}
	
	public Map<String, Object> addMetaData(String name, Object value) {
		if (metaData == null) {
			metaData = new LinkedHashMap<String, Object>();
		}
		metaData.put(name, value);
		return metaData;
	}
	public Map<String, Object> setFields(String[] fields) {
		return addMetaData("fields", fields);
	}
	public Map<String, Object> setFields(String[] fields, boolean itemsAsArray) {
		if (itemsAsArray) {
			if (items != null && items.size() > 0 && fields != null && fields.length > 0) {
				List<Object[]> result = new ArrayList<Object[]>(items.size());
				for (int i = 0; i < items.size(); i++) {
					Object itemObj = items.get(i);
					Object[] item = new Object[fields.length];
					for (int j = 0; j < fields.length; j++) {
						try {
							item[j] = PropertyUtils.getNestedProperty(itemObj, fields[j]);
						} catch (Exception e) {
							logger.debug(e.getMessage(), e);
						}
						
					}
					result.add(item);
				}
				items = result;
			}
		}
		
		return this.setFields(fields);
	}
	public Map<String, Object> setIdProperty(String idProperty) {
		return addMetaData("idProperty", idProperty);
	}

	public long getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}
	public List getItems() {
		return items != null ? items : new ArrayList();
	}
	public void setItems(List items) {
		this.items = items;
	}
	public Paginator getPaginator() {
		return paginator;
	}
	public void setPaginator(Paginator paginator) {
		this.paginator = paginator;
	}
	
	public Map<String, Object> getMetaData() {
		return metaData;
	}

	public void setMetaData(Map<String, Object> metaData) {
		this.metaData = metaData;
	}

//	public boolean isEmpty(){
//		return items == null || items.isEmpty();
//	}
}