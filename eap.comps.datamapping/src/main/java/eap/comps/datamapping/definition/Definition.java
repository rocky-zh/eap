package eap.comps.datamapping.definition;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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
public class Definition {
	
	protected String name;
	protected String description;
	
	private List<PropertyDefinition> properties = new ArrayList<PropertyDefinition>();
	
	public void mergeProps(Properties srcProps, boolean overwrite) {
		
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public void addProperty(PropertyDefinition property) {
		this.properties.add(property);
	}
	public List<PropertyDefinition> getProperties() {
		return properties;
	}
	public void setProperties(List<PropertyDefinition> properties) {
		this.properties = properties;
	}
//	private static final Properties EMPTY_PROPERTIES = new Properties();
	public Properties getProps() {
		if (properties == null) {
//			return EMPTY_PROPERTIES;
			return new Properties();
		}
		
		Properties props = new Properties();
		for (PropertyDefinition property : properties) {
			props.put(property.getName(), property.getValue());
		}
		
		return props;
	}
}