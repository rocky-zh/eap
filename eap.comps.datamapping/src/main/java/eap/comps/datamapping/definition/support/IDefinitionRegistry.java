package eap.comps.datamapping.definition.support;

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
public interface IDefinitionRegistry<T> {
	public void register(String name, T definition);
	public boolean containsName(String name);
	public void clear();
	public T remove(String name);
	public T get(String name);
}