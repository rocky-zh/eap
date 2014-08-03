package _eap.comps.ruleengine;

import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.Activation;
import org.drools.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.Match;

import eap.util.ReflectUtil;

//http://www.cnblogs.com/ibook360/archive/2012/07/30/2615706.html
public class Main1 {
	public static void main(String[] args) {
		KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		builder.add(ResourceFactory.newClassPathResource("1.drl"), ResourceType.DRL);
		builder.add(ResourceFactory.newClassPathResource("2.drl"), ResourceType.DRL);
		KnowledgeBuilderErrors errors = builder.getErrors();
		if (errors.size() > 0) {
			StringBuilder errorBuiler = new StringBuilder("Could not parse knowledge.");
			for (KnowledgeBuilderError error: errors) {
				errorBuiler.append("\r\n");
				errorBuiler.append(error.getMessage());
			}
			throw new IllegalArgumentException(errorBuiler.toString());
		}
		
		KnowledgeBase base = builder.newKnowledgeBase();
//		base.addKnowledgePackages(builder.getKnowledgePackages());
		StatefulKnowledgeSession session = base.newStatefulKnowledgeSession();
//		StatelessKnowledgeSession session = base.newStatelessKnowledgeSession();
		KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newFileLogger(session, "test");
		
//		session.execute("1");
		
		session.insert(null);
//		session.fireAllRules();
		
		org.kie.internal.runtime.StatefulKnowledgeSession _session = (org.kie.internal.runtime.StatefulKnowledgeSession) ReflectUtil.getFieldValue(session, "delegate");
		_session.fireAllRules(new org.kie.api.runtime.rule.AgendaFilter() {
			@Override
			public boolean accept(Match match) {
				System.out.println(match);
				
				return true;
			}
		});
		
//		session.fireAllRules(new AgendaFilter() {
//			public boolean accept(Activation activation) {
//				System.out.println(activation.getRule().getName());
//				return false;
//			}
//		});
		
		session.dispose();
		logger.close();
	}
}
