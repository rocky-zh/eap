package eap.util;

import java.beans.PropertyDescriptor;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

/**
 * <p> Title: </p>
 * <p> Description: </p>
 * @作者 chiknin@gmail.com
 * @创建时间 
 * @版本 1.00
 * @修改记录
 * <pre>
 * 版本	   修改人		 修改时间		 修改内容描述
 * ----------------------------------------
 * 
 * ----------------------------------------
 * </pre>
 */
public class MockUtil {

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static <T> T randomFill(T obj) {
		if (obj == null) {
			return null;
		}

		BeanWrapper bean = new BeanWrapperImpl(obj);
		PropertyDescriptor[] pds = bean.getPropertyDescriptors();
		for (PropertyDescriptor pd : pds) {
			String name = pd.getName();
			if (!"class".equals(name)) {
				Class type = bean.getPropertyType(name);
				Object value = null;
				if (type.isAssignableFrom(String.class)) {
					value = RandomUtil.randomAlphanumeric(5);
				} 
				else if ((type.isAssignableFrom(Integer.class)) || (type.isAssignableFrom(Integer.TYPE))) {
					value = RandomUtil.randomNumeric(5);
				} 
				else if ((type.isAssignableFrom(Double.class)) || (type.isAssignableFrom(Double.TYPE))) {
					value = RandomUtil.nextItem(new Double[] {
							Double.valueOf(1.1D), Double.valueOf(2.2D),
							Double.valueOf(3.3D), Double.valueOf(4.4D),
							Double.valueOf(5.5D) });
				} 
				else if ((type.isAssignableFrom(Float.class)) || (type.isAssignableFrom(Float.TYPE))) {
					value = RandomUtil.nextItem(new Float[] {
							Float.valueOf(1.1F), Float.valueOf(2.2F),
							Float.valueOf(3.3F), Float.valueOf(4.4F),
							Float.valueOf(5.5F) });
				} 
				else if ((type.isAssignableFrom(Boolean.class)) || (type.isAssignableFrom(Boolean.TYPE))) {
					value = RandomUtil.nextItem(new Boolean[] {Boolean.valueOf(true), Boolean.valueOf(false) });
				} 
				else if ((type.isAssignableFrom(Character.class)) || (type.isAssignableFrom(Character.TYPE))) {
					value = Character.valueOf(RandomUtil.randomAlphabetic(1).charAt(0));
				}
				bean.setPropertyValue(name, value);
			}
		}
		
		return obj;
	}
}
