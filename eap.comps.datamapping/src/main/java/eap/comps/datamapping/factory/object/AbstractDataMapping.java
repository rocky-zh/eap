package eap.comps.datamapping.factory.object;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import eap.comps.datamapping.api.IDataMapping;
import eap.comps.datamapping.api.IHandler;
import eap.comps.datamapping.api.IRenderer;
import eap.comps.datamapping.api.IValidator;
import eap.comps.datamapping.definition.DataMappingDefinition;
import eap.comps.datamapping.definition.Definition;
import eap.comps.datamapping.definition.HandlerDefinition;
import eap.comps.datamapping.definition.RendererDefinition;
import eap.comps.datamapping.definition.ValidatorDefinition;
import eap.comps.datamapping.definition.parser.ParserDefinition;
import eap.comps.datamapping.exception.ValidateFailException;
import eap.comps.datamapping.exception.ValidateFailExceptions;
import eap.comps.datamapping.factory.IDataMappingFactory;

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
public abstract class AbstractDataMapping<S, D> implements IDataMapping<S, D> {
	
	protected IDataMappingFactory dataMappingFactory;
	
//	public D mapping(String name, S source) {
//		return mapping(name, source, null);
//	}
	
	public D mapping(String name, S source, Map<String, Object> params) {
		if (source == null) {
			return null;
		}
		
		if (params == null) {
		    params = Collections.EMPTY_MAP;
		}
		
		DataMappingDefinition dmd = dataMappingFactory.getDataMappingDefinition(name);
		return mappingImpl(name, source, params, dmd, dmd.getParser());
	}
	
	protected abstract D mappingImpl(String dtName, S source, Map<String, Object> params, DataMappingDefinition dmd, ParserDefinition pd);
	
	protected Object getObject(String objectFactory, String className) {
		return dataMappingFactory.getObjectFactory().getObject(objectFactory + " " + className);
	}
	
	protected boolean validate(Object data, Definition definition, ValidatorDefinition vd, String failMode, ValidateFailExceptions validateFailExceptions) {
		IValidator validator = dataMappingFactory.getValidator(vd);
		if (validator != null) {
			boolean validatePassed = validator.validate(data, definition, vd);
			if (!validatePassed) {
				if (ParserDefinition.FAILMODE_EXCEPTION.equalsIgnoreCase(failMode)) {
					throw new ValidateFailException(data, definition, vd);
				} 
				else if (ParserDefinition.FAILMODE_RECORD.equalsIgnoreCase(failMode)) {
					validateFailExceptions.addException(new ValidateFailException(data, definition, vd));
				}
				
				return false;
			}
		}
		
		return true;
	}
	
	protected Object render(Object data, Definition definition, List<RendererDefinition> rds, String phase) {
		Object result = data;
		if (rds != null && rds.size() > 0) {
			for (RendererDefinition rd : rds) {
				if (phase.equals(rd.getPhase())) {
					IRenderer renderer = dataMappingFactory.getRenderer(rd);
					if (renderer != null) {
						result = renderer.render(result, definition, rd);
					}
				}
			}
		}
		
		return result;
	}
	
	protected void handle(Object data, Object dataHost, Definition definition, List<HandlerDefinition> hds, ValidateFailExceptions validateFailExceptions) {
		this.handle(data, dataHost, definition, hds, null, validateFailExceptions);
	}
	protected void handle(Object data, Object dataHost, Definition definition, List<HandlerDefinition> hds, Map<String, Object> attributes, ValidateFailExceptions validateFailExceptions) {
		if (hds != null && hds.size() > 0) {
			for (HandlerDefinition hd : hds) {
				IHandler handler = dataMappingFactory.getHandler(hd);
				handler.execute(
					new IHandler.HandlerContext(
						data, 
						dataHost, 
						definition, 
						hd, 
						attributes,
						validateFailExceptions
					)
				);
			}
		}
	}
	
	public void setDataMappingFactory(IDataMappingFactory dataMappingFactory) {
		this.dataMappingFactory = dataMappingFactory;
	}
}