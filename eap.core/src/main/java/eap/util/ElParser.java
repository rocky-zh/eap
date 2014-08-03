package eap.util;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

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
public class ElParser {
	private static ExpressionParser expressionParser = new SpelExpressionParser();
	
	private StandardEvaluationContext evalCtx;
	
	private ElParser(StandardEvaluationContext evalCtx) {
		this.evalCtx = (evalCtx != null ? evalCtx : new StandardEvaluationContext());
	}
	
	public static ElParser getInstance() {
		return new ElParser(new StandardEvaluationContext());
	}
	public static ElParser getInstance(StandardEvaluationContext evalCtx) {
		return new ElParser(evalCtx);
	}
	
	public ElParser setVar(String name, Object value) {
		if (value != null) {
			evalCtx.setVariable(name, value);
		}
		return this;
	}
	public ElParser setVars(Map<String, Object> vars) {
		for (Map.Entry<String, Object> entry : vars.entrySet()) {
			if (entry.getValue() != null) {
				evalCtx.setVariable(entry.getKey(), entry.getValue());
			}
		}
		
		return this;
	}
	public ElParser setRootVar(Object rootVar) {
		evalCtx.setRootObject(rootVar);
		return this;
	}
	
	public ElParser setFunc(String name, Method method) {
		evalCtx.registerFunction(name, method);
		return this;
	}
	
//	public ElParser setHttpRequset(HttpServletRequest request) {
//		Map<String, Object> requestScope = new HashMap<String, Object>();
//		Enumeration<String> attrNames = request.getAttributeNames();
//		while (attrNames.hasMoreElements()) {
//			String attrName = attrNames.nextElement();
//			requestScope.put(attrName, request.getAttribute(attrName));
//		}
//		this.setVar("requestScope", requestScope);
//		return this;
//	}
//	public ElParser setHttpSession(HttpServletRequest request) {
//		Map<String, Object> sessionScope = new HashMap<String, Object>();
//		Enumeration<String> attrNames = request.getSession(true).getAttributeNames();
//		while (attrNames.hasMoreElements()) {
//			String attrName = attrNames.nextElement();
//			sessionScope.put(attrName, request.getSession(true).getAttribute(attrName));
//		}
//		this.setVar("sessionScope", sessionScope);
//		return this;
//	}
//	public ElParser setHttpParams(HttpServletRequest request) {
//		this.setVar("paramScope", request.getParameterMap());
//		return this;
//	}
	
	public <T> T eval(String expr, Class<T> returnClazz) {
		if (StringUtils.isBlank(expr)) {
			return null;
		}
		
		return expressionParser.parseExpression(expr).getValue(evalCtx, returnClazz);
	}
	
	public static void main(String[] args) {
		ElParser el = ElParser.getInstance();
		el.setVar("a", "v-a").setVar("b", "v-b").setVar("n1", 1).setVar("n2", 2);
		
		String r1 = el.eval("' - x - ' + (#a + #b) + ' - x -'", String.class);
		System.out.println(r1);
		
		Integer r2 = el.eval("#n1 + #n2", Integer.class);
		System.out.println(r2);
		
		Map<String, Object> ctx1 = new HashMap<String, Object>();
		ctx1.put("x1", 1);
		ctx1.put("x2", 2);
		
		el.setVars(ctx1);
		String r3 = el.eval("#x1 + #x2", String.class);
		System.out.println(r3);
		
//		try {
//			el.setFunc("tod", DateUtil.class.getMethod("currDate", null));
//			String r4 = el.eval("#tod()", String.class);
//			System.out.println(r4);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
	}
}