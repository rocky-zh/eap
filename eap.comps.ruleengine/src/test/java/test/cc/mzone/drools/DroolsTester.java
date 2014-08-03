package test.cc.mzone.drools;
 
import java.util.Iterator;
 
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
 
/**
 * Drools规则引擎测试。
 * 
 * @author Eric
 *
 */
public class DroolsTester {
 
	public static class User {
		private int money; // 手中的钱
		private int kp; // 空瓶数
		private int totals; // 喝掉的瓶数
 
		public int getMoney() {
			return money;
		}
 
		public User setMoney(int money) {
			this.money = money;
			return this;
		}
 
		public int getKp() {
			return kp;
		}
 
		public void setKp(int kp) {
			this.kp = kp;
		}
 
		public int getTotals() {
			return totals;
		}
 
		public void setTotals(int totals) {
			this.totals = totals;
		}
 
	}
 
	public static void main(String[] args) throws Exception {
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newClassPathResource("a.drl"), ResourceType.DRL);
		if (kbuilder.hasErrors()) {
			System.out.println("规则错误：");
			Iterator<KnowledgeBuilderError> it = kbuilder.getErrors().iterator();
			while (it.hasNext()) System.out.println(it.next());
			return;
		}
		KnowledgeBase kb = KnowledgeBaseFactory.newKnowledgeBase();
		kb.addKnowledgePackages(kbuilder.getKnowledgePackages());
		StatefulKnowledgeSession s = kb.newStatefulKnowledgeSession();
		s.insert(new User().setMoney(50));
		s.fireAllRules();
		s.dispose();
	}
 
}