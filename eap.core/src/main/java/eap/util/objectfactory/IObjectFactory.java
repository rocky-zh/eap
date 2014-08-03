package eap.util.objectfactory;

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
public interface IObjectFactory {
	
	/**
	 * 根据 objectName 获取对象
	 * @param objectName 对象名称
	 * @return 对象
	 */
	public Object getObject(String objectName);
	
	/**
	 * 根据 objectName 获取对象
	 * @param objectName 对象名称
	 * @param requireType 对象类型
	 * @return 对象
	 */
	public <T> T getObject(String objectName, Class<T> requireType);
}
