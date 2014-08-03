package eap.comps.datamapping.definition;

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
public class ClassMetadataDefinition extends Definition {
	
	public static final String SCOPE_SINGLETON = "singleton";
	public static final String SCOPE_PROTOTYPE = "prototype";
	
	public static final String OBJECTFACTORY_NEW = "new";
	public static final String OBJECTFACTORY_SPRING = "spring";
	
	private String className;
	private String objectFactory;
	private String scope = SCOPE_SINGLETON;

	private String ref;

	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}

	public String getObjectFactory() {
		return objectFactory;
	}
	public void setObjectFactory(String objectFactory) {
		this.objectFactory = objectFactory;
	}

	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getRef() {
		return ref;
	}
	public void setRef(String ref) {
		this.ref = ref;
	}
}