package _eap.comps.ruleengine;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.StopWatch;

import com.enci.ecp.bizprocess.product.rule.PremiumCalcBom;

import eap.comps.ruleengine.IRuleEngine;

public class Main2 {
	public static void main(String[] args) {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("AC-test.xml");
		IRuleEngine ruleEngine = ctx.getBean("ruleEngine", IRuleEngine.class);
		
		System.out.println(ruleEngine.getRulePackages());
		
//		ruleEngine.execute("p2", "1");
		
		PremiumCalcBom bom = new PremiumCalcBom();
		bom.setInsuredSex("F");
		bom.setInsPeriodUnit("Y");
		bom.setInsPeriod("10");
		bom.setPayMode("T");
		bom.setAmount(10000.0);
		bom.setInsuredBirthday("1990-01-01");
		
		System.out.println("start");
		
		StopWatch sw = new StopWatch();
		sw.start();
//		for (int j = 0; j < 10000; j++) {
		for (int i = 80; i < 90; i++) {
			bom.setInsuredBirthday("19"+i+"-01-01");
			int r = ruleEngine.execute("com.enci.ecp.bizprocess.product.rule.premiumcalc_00177000", bom);
//			System.out.println(bom.getTotalPremium());
		}
//		}
		sw.stop();
		System.out.println(sw.prettyPrint());
		
		
	}
}
