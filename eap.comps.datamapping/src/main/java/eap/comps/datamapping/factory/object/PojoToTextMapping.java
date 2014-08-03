package eap.comps.datamapping.factory.object;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import eap.comps.datamapping.definition.DataFieldDefinition;
import eap.comps.datamapping.definition.DataMappingDefinition;
import eap.comps.datamapping.definition.RendererDefinition;
import eap.comps.datamapping.definition.RepeatDefinition;
import eap.comps.datamapping.definition.TextFieldDefinition;
import eap.comps.datamapping.definition.TextFieldGroupDefinition;
import eap.comps.datamapping.definition.TextFieldSetDefinition;
import eap.comps.datamapping.definition.parser.ParserDefinition;
import eap.comps.datamapping.definition.parser.PojoToTextParserDefinition;
import eap.comps.datamapping.exception.ValidateFailExceptions;
import eap.comps.datamapping.util.ObjectUtil;

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
public class PojoToTextMapping extends AbstractDataMapping<Object, String> {
	
	protected String mappingImpl(String name, Object source, Map<String, Object> params, 
			DataMappingDefinition dmd, ParserDefinition pd) 
	{
		PojoToTextParserDefinition pttpd = (PojoToTextParserDefinition) pd;
		
		StringBuilder text = new StringBuilder();
		ValidateFailExceptions validateFailExceptions = new ValidateFailExceptions();
		if (source instanceof Collection<?>) { // OTHER Map, Array
			Collection<?> sourceCollection = (Collection<?>) source;
			for (Object o : sourceCollection) {
				String line = this.lineProcess(o, pttpd, validateFailExceptions);
				text.append(line);
			}
		} else {
			String line = this.lineProcess(source, pttpd, validateFailExceptions);
			text.append(line);
		}
		this.handle(text, source, pd, dmd.getHandlers(), validateFailExceptions);
		
		return text.toString();
	}
	
	private String lineProcess(Object data, PojoToTextParserDefinition pd, ValidateFailExceptions allValidateFailExceptions) {
		if (data == null) {
			return "";
		}
		
		RepeatDefinition<DataFieldDefinition> rd = pd.getRepeat();
		
		StringBuilder line = new StringBuilder();
		ValidateFailExceptions validateFailExceptions = new ValidateFailExceptions();
		for (DataFieldDefinition dfd : rd.getFields()) {
			this.fieldProcess(data, dfd, pd, line, validateFailExceptions);
		}
		line.append(rd.getNewLineChar());
		
		boolean validatePassed = this.validate(line, rd, rd.getValidator(), pd.getFailMode(), validateFailExceptions);
		if (validatePassed) {
			this.handle(line, data, rd, rd.getHandlers(), null);
		} else {
			this.handle(null, null, rd, rd.getHandlers(), validateFailExceptions);
		}
		allValidateFailExceptions.addException(validateFailExceptions);
		
		return line.toString();
	}
	
	private void fieldProcess(Object data, DataFieldDefinition dfd, PojoToTextParserDefinition pd, StringBuilder line, ValidateFailExceptions validateFailExceptions) {
		if (dfd instanceof TextFieldSetDefinition) {
			List<DataFieldDefinition> rdfds = dataMappingFactory.getDataFieldSet((TextFieldSetDefinition) dfd);
			for (DataFieldDefinition rdfd : rdfds) {
				this.fieldProcess(data, rdfd, pd, line, validateFailExceptions);
			}
		} else if (dfd instanceof TextFieldGroupDefinition) {
			TextFieldGroupDefinition tfgd = (TextFieldGroupDefinition) dfd;
			Object groupValue = ObjectUtil.getProperty(data, tfgd.getName(), Object.class);
			if (groupValue != null) { // TODO    index ...
				if (TextFieldDefinition.STARTMODE_CHARACTER.equalsIgnoreCase(tfgd.getStartMode())) {
					line.append(tfgd.getStartValue());
				}
				if (groupValue instanceof Collection<?>) {
					Collection<?> gvs = (Collection<?>) groupValue;
					for (Object gv : gvs) {
						for (TextFieldDefinition tfd : tfgd.getFields()) {
							this.fieldProcess(gv, tfd, pd, line, validateFailExceptions);
						}	
					}
				} else if (groupValue instanceof Object[]) {
					Object[] gvs = (Object[]) groupValue;
					for (Object gv : gvs) {
						for (TextFieldDefinition tfd : tfgd.getFields()) {
							this.fieldProcess(gv, tfd, pd, line, validateFailExceptions);
						}
					}
				} else {
					for (TextFieldDefinition tfd : tfgd.getFields()) {
						this.fieldProcess(groupValue, tfd, pd, line, validateFailExceptions);
					}
				}
				if (TextFieldDefinition.ENDMODE_CHARACTER.equalsIgnoreCase(tfgd.getEndMode())) {
					line.append(tfgd.getEndValue());
				}
			}
		} else if (dfd instanceof TextFieldDefinition) {
			TextFieldDefinition tfd = (TextFieldDefinition) dfd;
			
			Object value = null;
			if (StringUtils.isNotBlank(tfd.getName())) {
				value = ObjectUtil.getProperty(data, tfd.getName());
			} else if (ObjectUtil.isJavaType(data)) {
				value = data;
			} else {
				value = tfd.getValue();
			}
			if (value == null) {
				value = tfd.getValue();
			}
			
			value = this.render(value, tfd, tfd.getRenderers(), RendererDefinition.PHASE_BEFORE);
			
			boolean validatePassed = this.validate(value, tfd, tfd.getValidator(), pd.getFailMode(), validateFailExceptions);
			if (validatePassed) {
								
				Object result = this.render(value, tfd, tfd.getRenderers(), RendererDefinition.PHASE_AFTER); // TODO pos
				
				this.handle(result, data, tfd, tfd.getHandlers(), null);
				
				result = ObjectUtil.nullToBlankString(result);
				if (TextFieldDefinition.STARTMODE_CHARACTER.equalsIgnoreCase(tfd.getStartMode())) {
					line.append(tfd.getStartValue());
				}
				
				line.append(result);
				if (TextFieldDefinition.ENDMODE_CHARACTER.equalsIgnoreCase(tfd.getEndMode())) {
					line.append(tfd.getEndValue());
				}
			} else {
				this.handle(null, null, tfd, tfd.getHandlers(), validateFailExceptions);
			}
		}
	}
}