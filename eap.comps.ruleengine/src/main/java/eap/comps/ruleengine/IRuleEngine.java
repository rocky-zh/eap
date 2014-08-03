package eap.comps.ruleengine;

import java.util.List;

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
public interface IRuleEngine {
//	public void setConfig(Properties config);
	public void init();
	
	public int execute(String rulePackage, Object bom);
	
	public void removeRulePackage(String rulePackage);
	public void addRulePackage(String resource);
	public void addRulePackages(String[] resources);
	public List<String> getRulePackages();
}