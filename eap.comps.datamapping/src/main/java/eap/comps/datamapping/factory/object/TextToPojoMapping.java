package eap.comps.datamapping.factory.object;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.util.Assert;

import eap.comps.datamapping.definition.DataFieldDefinition;
import eap.comps.datamapping.definition.DataMappingDefinition;
import eap.comps.datamapping.definition.Definition;
import eap.comps.datamapping.definition.HandlerDefinition;
import eap.comps.datamapping.definition.RendererDefinition;
import eap.comps.datamapping.definition.RepeatDefinition;
import eap.comps.datamapping.definition.TextFieldDefinition;
import eap.comps.datamapping.definition.TextFieldGroupDefinition;
import eap.comps.datamapping.definition.TextFieldSetDefinition;
import eap.comps.datamapping.definition.parser.ParserDefinition;
import eap.comps.datamapping.definition.parser.TextToPojoParserDefinition;
import eap.comps.datamapping.exception.GotoException;
import eap.comps.datamapping.exception.ValidateFailExceptions;
import eap.comps.datamapping.util.ObjectUtil;
import eap.util.CharsetUtil;

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
public class TextToPojoMapping extends AbstractDataMapping<String, Object> {
	
	public static final String LENGTH_BY_BYTE_CHARSET = "ISO8859-1";
	
	protected Object mappingImpl(String name, String source, Map<String, Object> params, 
			DataMappingDefinition dmd, ParserDefinition pd)
	{
		TextToPojoParserDefinition ttppd = (TextToPojoParserDefinition) pd;
		String lengthBy = ttppd.getLengthBy();
		String lengthByByteCharset = ttppd.getLengthByByteCharset();
		boolean isSingleResult = ttppd.getRepeat().isSingleResult();
		
		if (TextToPojoParserDefinition.LENGTH_BY_BYTE.equalsIgnoreCase(lengthBy)) {
			source = CharsetUtil.getString(source, lengthByByteCharset, LENGTH_BY_BYTE_CHARSET);
		}
		
		List<Object> textData = new ArrayList<Object>();
		ValidateFailExceptions allValidateFailExceptions = new ValidateFailExceptions();

		String newLineChar = ttppd.getRepeat().getNewLineChar();
		if (StringUtils.isNotEmpty(newLineChar)) {
			StringTokenizer textKen = new StringTokenizer(source, newLineChar);
			while (textKen.hasMoreTokens()) {
				String line = textKen.nextToken();
				this.lineProcess(line, ttppd, textData, allValidateFailExceptions);
				if (isSingleResult) {
					break;
				}
			}
		} else { // newLineChar is null or empty
			int lineLength = this.lineProcess(source, ttppd, textData, allValidateFailExceptions);
			if (!isSingleResult) {
				int textBeginIndex = lineLength;
				while (source.length() > textBeginIndex) {
					String line = source.substring(textBeginIndex);
					lineLength = this.lineProcess(line, ttppd, textData, allValidateFailExceptions);
					
					textBeginIndex += lineLength;
				}
			}
		}
		this.handle(textData, textData, dmd, dmd.getHandlers(), allValidateFailExceptions);
		
		if (isSingleResult) {
			return ObjectUtil.getFirstItem(textData);
		}
		
		return textData;
	}
	
	private int lineProcess(String line, TextToPojoParserDefinition pd, List<Object> textData, ValidateFailExceptions allValidateFailExceptions) {
		RepeatDefinition<DataFieldDefinition> rd = pd.getRepeat();
		
		Object lineData = ObjectUtil.instance(rd.getMappingClass());
		BeanWrapper lineBean = ObjectUtil.buildBeanWrapperImpl(lineData);
		
		LineIndex lineIndex = new LineIndex();
		boolean isArrayMappingClass = rd.getMappingClass().startsWith("[L");
		ValidateFailExceptions validateFailExceptions = new ValidateFailExceptions();
		try {
			for (DataFieldDefinition dfd : rd.getFields()) {
				this.fieldProcess(line, lineIndex, isArrayMappingClass, dfd, pd, lineBean, validateFailExceptions);
			}	
		} catch (GotoException ge) { // TODO TEMP DEEP 1
			String gotoIndex = ge.getGotoIndex();
			if (StringUtils.isNotBlank(gotoIndex)) {
				List<DataFieldDefinition> dfds = dataMappingFactory.getDataFieldSet(gotoIndex);
				for (DataFieldDefinition dfd : dfds) {
					this.fieldProcess(line, lineIndex, isArrayMappingClass, dfd, pd, lineBean, validateFailExceptions);
				}
			}
		}
		boolean validatePassed = this.validate(lineData, rd, rd.getValidator(), pd.getFailMode(), validateFailExceptions);
		if (validatePassed) {
			textData.add(lineData);
			this.handle(lineData, lineData, rd, rd.getHandlers(), lineIndex, null);
		} else {
			this.handle(null, null, rd, rd.getHandlers(), lineIndex, validateFailExceptions);
		}
		
		allValidateFailExceptions.addException(validateFailExceptions);
		
		return lineIndex.getLineBeginIndex();
	}
	
	private void fieldProcess(String line, LineIndex lineIndex, boolean isArrayMappingClass,
			DataFieldDefinition dfd, TextToPojoParserDefinition pd,
			BeanWrapper lineBean, ValidateFailExceptions validateFailExceptions) 
	{
		if (dfd instanceof TextFieldSetDefinition) {
			List<DataFieldDefinition> rdfds = dataMappingFactory.getDataFieldSet((TextFieldSetDefinition) dfd);
			for (DataFieldDefinition rdfd : rdfds) {
				this.fieldProcess(line, lineIndex, isArrayMappingClass, rdfd, pd, lineBean, validateFailExceptions);
			}
		} else if (dfd instanceof TextFieldGroupDefinition) { // TODO temp
			TextFieldGroupDefinition tfgd = (TextFieldGroupDefinition) dfd;
			int lineColumnBak = lineIndex.getColumn();
			
			Object groupData = ObjectUtil.instance(tfgd.getMappingClass());
			BeanWrapper groupBean = ObjectUtil.buildBeanWrapperImpl(groupData);
			boolean isArrayGroupMappingClass = tfgd.getMappingClass().startsWith("[L");
			boolean groupUseIndexName = isArrayGroupMappingClass || (groupBean.getWrappedInstance() instanceof List);
			Assert.isTrue(groupUseIndexName, tfgd.getName() + " FieldGroup MappingClass type must is [Ljava.lang.Object or java.util.List");
			Integer count = Integer.valueOf(lineBean.getPropertyValue(tfgd.getCountRef()).toString());
			for (int i = 0; i < count; i++) {
				lineIndex.setColumn(0);
				
				Object itemData = ObjectUtil.instance(tfgd.getItemMappingClass());
				BeanWrapper itemBean = ObjectUtil.buildBeanWrapperImpl(itemData);
				boolean isArrayItemMappingClass = tfgd.getItemMappingClass().startsWith("[L");
				for (DataFieldDefinition gdfd : tfgd.getFields()) {
					this.fieldProcess(line, lineIndex, isArrayItemMappingClass, gdfd, pd, itemBean, validateFailExceptions);
				}
				
				groupBean.setPropertyValue(String.valueOf(i), itemBean.getWrappedInstance());
			}
			
			boolean useIndexName = isArrayMappingClass || StringUtils.isBlank(tfgd.getName());
			String name = (useIndexName ? String.valueOf(lineColumnBak) : tfgd.getName());
			lineBean.setPropertyValue(name, groupBean.getWrappedInstance());
			
			lineIndex.setColumn(lineColumnBak);
			lineIndex.incrementColumn(); // -> FieldGroup
		} else if (dfd instanceof TextFieldDefinition) {
			TextFieldDefinition tfd = (TextFieldDefinition) dfd;
			int lineBeginOffset = 0;
			
			int beginIndex = 0;
			if (TextFieldDefinition.STARTMODE_CHARACTER.equalsIgnoreCase(tfd.getStartMode())) {
				beginIndex = line.indexOf(tfd.getStartValue()); // TODO -1
				beginIndex += tfd.getStartValue().length();
			} else if (TextFieldDefinition.STARTMODE_ABSOLUTE.equalsIgnoreCase(tfd.getStartMode())) {
				beginIndex = Integer.parseInt(tfd.getStartValue());
			} else { // TextFieldDefinition.STARTMODE_RELATIVE
				beginIndex = lineIndex.getLineBeginIndex() + Integer.parseInt(tfd.getStartValue());
			}
			
			int endIndex = beginIndex;
			if (TextFieldDefinition.ENDMODE_CHARACTER.equalsIgnoreCase(tfd.getEndMode())) {
				endIndex = line.indexOf(tfd.getEndValue(), beginIndex); // TODO -1
				lineBeginOffset = (endIndex + tfd.getEndValue().length()) ;
			} else { // TextFieldDefinition.ENDMODE_LENGTH
				endIndex = beginIndex + Integer.parseInt(tfd.getEndValue());
				lineBeginOffset = endIndex;
			}
			// next line index
			lineIndex.setLineBeginIndex(lineBeginOffset);
			lineIndex.incrementColumn();
			
			String value = line.substring(beginIndex, endIndex);
			if (StringUtils.isBlank(value)) {
				value = tfd.getValue();
			} else {
				if (TextToPojoParserDefinition.LENGTH_BY_BYTE.equalsIgnoreCase(pd.getLengthBy())) {
					value = CharsetUtil.getString(value, LENGTH_BY_BYTE_CHARSET, pd.getLengthByByteCharset());
				}
				if (StringUtils.isBlank(value)) {
					value = tfd.getValue();
				} else {
					if (tfd.isTrim()) {
						value = value.trim();
					}
				}
			}
			
			value = (String) this.render(value, tfd, tfd.getRenderers(), RendererDefinition.PHASE_BEFORE);
			
			boolean validatePassed = this.validate(value, tfd, tfd.getValidator(), pd.getFailMode(), validateFailExceptions);
			Object result = null;
			if (validatePassed) {
				result = this.render(value, tfd, tfd.getRenderers(), RendererDefinition.PHASE_AFTER);
				
				boolean useIndexName = isArrayMappingClass || StringUtils.isBlank(tfd.getName());
				String name = (useIndexName ? String.valueOf(lineIndex.getColumn() - 1) : tfd.getName());
				lineBean.setPropertyValue(name, result);
				
				this.handle(result, lineBean.getWrappedInstance(), tfd, tfd.getHandlers(), lineIndex, null);
			} else {
				this.handle(null, null, tfd, tfd.getHandlers(), lineIndex, validateFailExceptions);
			}
		}
	}
	
	private void handle(Object data, Object dataHost, Definition definition, List<HandlerDefinition> hds, LineIndex lineIndex, ValidateFailExceptions validateFailExceptions) {
		Map<String, Object> attributes = new HashMap<String, Object>();
		attributes.put("lineIndex", lineIndex);
		super.handle(data, dataHost, definition, hds, attributes, validateFailExceptions);
	}
	
	public static class LineIndex {
		private int lineBeginIndex = 0;
		private int column = 0;
		
		public LineIndex() {
		}

		public LineIndex(int lineBeginIndex, int column) {
			this.lineBeginIndex = lineBeginIndex;
			this.column = column;
		}
		
		public void incrementColumn() {
			this.column++;
		}

		public int getLineBeginIndex() {
			return lineBeginIndex;
		}

		public void setLineBeginIndex(int lineBeginIndex) {
			this.lineBeginIndex = lineBeginIndex;
		}

		public int getColumn() {
			return column;
		}

		public void setColumn(int column) {
			this.column = column;
		}
	}
}