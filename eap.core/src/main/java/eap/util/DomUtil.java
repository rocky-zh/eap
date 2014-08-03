package eap.util;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;

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
 * @see org.apache.cxf.helpers.XMLUtils
 */
public class DomUtil {
	private static final Map<ClassLoader, DocumentBuilderFactory> DOCUMENT_BUILDER_FACTORIES = Collections.synchronizedMap(new WeakHashMap<ClassLoader, DocumentBuilderFactory>());
	
	private static DocumentBuilderFactory getDocumentBuilderFactory() {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if (loader == null) {
			loader = DomUtil.class.getClassLoader();
		}
		if (loader == null) {
			return DocumentBuilderFactory.newInstance();
		}
		DocumentBuilderFactory factory = DOCUMENT_BUILDER_FACTORIES.get(loader);
		if (factory == null) {
			factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DOCUMENT_BUILDER_FACTORIES.put(loader, factory);
		}
		return factory;
	}
	
	public static DocumentBuilder getParser() {
		try {
			return getDocumentBuilderFactory().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}
	
	public static Document newDocument() {
		return getParser().newDocument();
	}
}