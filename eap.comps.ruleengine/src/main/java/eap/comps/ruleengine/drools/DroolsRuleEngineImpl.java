package eap.comps.ruleengine.drools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantLock;

import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.definition.KnowledgePackage;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.kie.api.runtime.rule.Match;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import eap.EapContext;
import eap.comps.ruleengine.IRuleEngine;
import eap.util.FileUtil;
import eap.util.ReflectUtil;
import eap.util.StringUtil;

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
public class DroolsRuleEngineImpl implements IRuleEngine {
	
	private ReentrantLock lock = new ReentrantLock();
	
	private Properties config;
	private KnowledgeBase kBase;
	
	public void init() {
//		Properties props = PropertiesUtil.filterForPrefix(config, "props.");
//		for (Object key : props.keySet()) {
//			System.setProperty(key.toString(), props.getProperty(key.toString()));
//		}
		System.setProperty("drools.dateformat", "yyyy-MM-dd HH:mm:ss");
		
		try {
			lock.lock();
			kBase = readKnowledgeBase(config);
		} catch (IOException e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		} finally {
			lock.unlock();
		}
	}
	
	public int execute(final String rulePackage, Object bom) {
		checkKBase();
		
		StatefulKnowledgeSession kSession = kBase.newStatefulKnowledgeSession();
		try {
			kSession.insert(bom);
//			return kSession.fireAllRules(new AgendaFilter() {
//				@Override
//				public boolean accept(Activation activation) {
//					if (activation.getRule().getPackageName().equals(rule)) {
//						return true;
//					}
//					return false;
//				}
//			});
			
			org.kie.internal.runtime.StatefulKnowledgeSession _kSession = (org.kie.internal.runtime.StatefulKnowledgeSession) ReflectUtil.getFieldValue(kSession, "delegate");
			return _kSession.fireAllRules(new org.kie.api.runtime.rule.AgendaFilter() {
				@Override
				public boolean accept(Match match) {
					if (match.getRule().getPackageName().equals(rulePackage)) {
						return true;
					}
					return false;
				}
			});
		} finally {
			kSession.dispose();
		}
	}
	
	public void removeRulePackage(String rulePackage) {
		checkKBase();
		kBase.removeKnowledgePackage(rulePackage);
	}
	public void addRulePackage(String resource) {
		addRulePackages(new String[] {resource});
	}
	public void addRulePackages(String[] resources) {
		checkKBase();
		if (resources == null || resources.length == 0) {
			return;
		}
		
		KnowledgeBuilder kBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		for (String resource : resources) {
			if (resource.toLowerCase().startsWith("http://") || resource.toLowerCase().startsWith("https://")) {
				kBuilder.add(ResourceFactory.newUrlResource(resource), ResourceType.PKG);
			} else { 
				String fileNameSuffix = FileUtil.getFileNameSuffix(resource);
				if (StringUtil.isNotBlank(fileNameSuffix)) {
					ResourceType resourceType = ResourceType.determineResourceType("." + fileNameSuffix);
					if (resourceType != null) {
						kBuilder.add(ResourceFactory.newClassPathResource(resource, EapContext.getEnv().getEncoding()), resourceType);
					}
				}
			}
		}
		checkError(kBuilder.getErrors());
		
		kBase.addKnowledgePackages(kBuilder.getKnowledgePackages());
	}
	public List<String> getRulePackages() {
		checkKBase();
		
		List<String> rulePackages = new ArrayList<String>();
		Collection<KnowledgePackage> kPackages = kBase.getKnowledgePackages();
		for (KnowledgePackage kPackage : kPackages) {
			rulePackages.add(kPackage.getName());
		}
		
		return rulePackages;
	}
	
	private KnowledgeBase readKnowledgeBase(Properties config) throws IOException {
		KnowledgeBuilder kBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		
		String[] localPaths = StringUtil.split(config.getProperty("localPath"), ",");
		if (localPaths != null && localPaths.length > 0) {
			for (String localPath : localPaths) {
				PathMatchingResourcePatternResolver resourceLoader = new PathMatchingResourcePatternResolver();
				org.springframework.core.io.Resource[] resources = null;
				try {
					resources = resourceLoader.getResources(localPath);
				} catch (IOException e) {
					throw new IllegalArgumentException(e.getMessage(), e);
				}
				
				if (resources != null && resources.length > 0) {
					for (org.springframework.core.io.Resource resource : resources) {
						String fileNameSuffix = FileUtil.getFileNameSuffix(resource.getFilename());
						if (StringUtil.isNotBlank(fileNameSuffix)) {
							ResourceType resourceType = ResourceType.determineResourceType("." + fileNameSuffix);
							if (resourceType != null) {
								kBuilder.add(ResourceFactory.newFileResource(resource.getFile()), resourceType);
							}
						}
					}
				}
			}
		}
		
		String[] remotePaths = StringUtil.split(config.getProperty("remotePath"), ",");
		if (remotePaths != null && remotePaths.length > 0) {
			for (String remotePath : remotePaths) {
				kBuilder.add(ResourceFactory.newUrlResource(remotePath), ResourceType.PKG);
			}
		}
		
		checkError(kBuilder.getErrors());
		
		KnowledgeBase kBase = kBuilder.newKnowledgeBase();
//		kBase.addKnowledgePackages(kBuilder.getKnowledgePackages());
		
		return kBase;
	}
	private void checkError(KnowledgeBuilderErrors errors) {
		if (errors != null && errors.size() > 0) {
			StringBuilder errorBuiler = new StringBuilder("Could not parse knowledge.");
			for (KnowledgeBuilderError error: errors) {
				errorBuiler.append("\r\n").append(error.getMessage());
			}
			throw new IllegalArgumentException(errorBuiler.toString());
		}
	}
	private void checkKBase() {
		if (kBase == null) {
			throw new IllegalStateException("kBase not initialized");
		}
	}
	public Properties getConfig() {
		return config;
	}
	public void setConfig(Properties config) {
		this.config = config;
	}

	public static void main(String[] args) {
		DroolsRuleEngineImpl re = new DroolsRuleEngineImpl();
		
		Properties conf = new Properties();
		conf.put("localPath", "classpath*:conf/rule/*");
		re.setConfig(conf);
		
		re.init();
		
		re.execute("p2", "1");
		
		re.removeRulePackage("p2");
		
		re.execute("p2", "1");
		
		re.addRulePackage("conf/rule/2.drl");
		
		re.execute("p2", "1");
	}
}