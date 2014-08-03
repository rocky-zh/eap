import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import eap.comps.datamapping.DataMappingManager;
import eap.util.FileUtil;

public class DataMappingTest extends TestCase {
	
	public void test1() throws IOException {
		new ClassPathXmlApplicationContext("classpath*:META-INF/spring/eap_beans.xml");
		
		Map<String, Object> values = new HashMap<String, Object>();
//		values.put("outputStream", new FileOutputStream("e.pdf"));
		byte[] data = (byte[]) DataMappingManager.mapping("pojotopdf01", values, values);
		
		FileUtil.writeByteArrayToFile(new File("b.pdf"), data);
		
	}
}
