package eap.comps.codetable.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CodeType {
	public String type() default "";
	public String name();
	public String scope() default "system"; // system, application, session, request
	public String canModifyInd() default "2"; // 1-可以修改，2-不能修改  
	public String status() default "1"; // 1-有效
	public String java2dll() default "1"; // 1-创建, 2-更新
}