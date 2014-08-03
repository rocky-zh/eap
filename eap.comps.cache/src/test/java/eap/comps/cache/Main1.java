package eap.comps.cache;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import junit.framework.TestCase;

public class Main1 extends TestCase {
	public void test1 (){
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:AC.xml");
		System.out.println(ctx.getBeanDefinitionNames());
		ClazzA a = ctx.getBean(ClazzA.class);
		System.out.println(a.m1("3"));
		System.out.println(a.m2("3"));
		System.out.println(a.m1("3"));
	}
}
