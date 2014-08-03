package eap.comps.datamapping.factory.object;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanWrapper;

import eap.comps.datamapping.definition.CellFieldDefinition;
import eap.comps.datamapping.definition.CellFieldDefinition.CellIndex;
import eap.comps.datamapping.definition.CellFieldGroupDefinition;
import eap.comps.datamapping.definition.CellFieldSetDefinition;
import eap.comps.datamapping.definition.DataFieldDefinition;
import eap.comps.datamapping.definition.DataMappingDefinition;
import eap.comps.datamapping.definition.RendererDefinition;
import eap.comps.datamapping.definition.SheetDefinition;
import eap.comps.datamapping.definition.parser.ExcelToPojoParserDefinition;
import eap.comps.datamapping.definition.parser.ParserDefinition;
import eap.comps.datamapping.exception.ExcelToPojoOutOfBoundsException;
import eap.comps.datamapping.exception.ValidateFailExceptions;
import eap.comps.datamapping.util.ExcelUtil;
import eap.comps.datamapping.util.ObjectUtil;

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
public class ExcelToPojoMapping extends AbstractDataMapping<InputStream, Object> {
	
	private static boolean closeInputStream = true;

	@Override
	protected Object mappingImpl(String name, InputStream source, Map<String, Object> params,
		DataMappingDefinition dmd, ParserDefinition pd) 
	{
		ExcelToPojoParserDefinition etppd = (ExcelToPojoParserDefinition) pd;
		List<SheetDefinition> sds = etppd.getSheets();
		
		List<Object> sheetData = new ArrayList<Object>(sds.size());
		ValidateFailExceptions allValidateFailExceptions =  new ValidateFailExceptions();

		Workbook workbook = this.createWorkbook(etppd, source, params);
		for (int i = 0; i < sds.size(); i++) {
			Sheet sheet = workbook.getSheetAt(i);
			Integer maxRowNum = etppd.getMaxRowNum();
			if (maxRowNum != null && maxRowNum >= 0) {
			    if (sheet.getLastRowNum() + 1 > maxRowNum) {
			        throw new ExcelToPojoOutOfBoundsException(sheet.getLastRowNum() + 1, maxRowNum);
			    }
			}
			
			this.sheetProcess(sheet, sds.get(i), etppd, sheetData, allValidateFailExceptions);
		}
		this.handle(sheetData, sheetData, dmd, dmd.getHandlers(), allValidateFailExceptions);
		
		if (sds != null && sds.size() == 1 
				&& sheetData != null && sheetData.size() == 1) {
			return sheetData.get(0);
		}
		
		return sheetData;
	}
	
	private void sheetProcess(Sheet sheet, SheetDefinition sd, ExcelToPojoParserDefinition pd, 
		List<Object> sheetData, ValidateFailExceptions allValidateFailExceptions) 
	{
		Object sheetObject = ObjectUtil.instance(sd.getMappingClass());
		BeanWrapper sheetBean = ObjectUtil.buildBeanWrapperImpl(sheetObject);
		ValidateFailExceptions validateFailExceptions = new ValidateFailExceptions();
		for (DataFieldDefinition dfd : sd.getCells()) {
			this.cellProcess(sheet, null, dfd, sd, pd, sheetBean, validateFailExceptions);
		}
		this.validate(sheetObject, sd, sd.getValidator(), pd.getFailMode(), validateFailExceptions);
		this.handle(sheetObject, sheetData, sd, sd.getHandlers(), validateFailExceptions);
		
		sheetData.add(sheetObject);
	}
	
	private void cellProcess(Sheet sheet, CellIndex cellIndex, DataFieldDefinition cfd, SheetDefinition sd, ExcelToPojoParserDefinition pd, 
		BeanWrapper sheetBean, ValidateFailExceptions validateFailExceptions) 
	{
		if (cfd instanceof CellFieldSetDefinition) {
			List<DataFieldDefinition> rdfds = dataMappingFactory.getDataFieldSet((CellFieldSetDefinition) cfd);
			for (DataFieldDefinition rdfd : rdfds) {
				this.cellProcess(sheet, ((CellFieldDefinition)rdfd).getCellIndex(), rdfd, sd, pd, sheetBean, validateFailExceptions);
			}
		} else if (cfd instanceof CellFieldGroupDefinition) {
			CellFieldGroupDefinition cfgd = (CellFieldGroupDefinition) cfd;
			List<CellFieldDefinition> gcfds = cfgd.getCells();
			Object groupData = ObjectUtil.instance(cfgd.getMappingClass());
			BeanWrapper groupBean = ObjectUtil.buildBeanWrapperImpl(groupData);
			
			Integer maxRowIndex = (cfgd.getCount() == null) ? sheet.getLastRowNum() : Math.min(((Math.max((cfgd.getCount() - 1), 0)) + cfgd.getRow()), sheet.getLastRowNum());
			for (int rowIndex = cfgd.getRow(), i = 0; rowIndex <= maxRowIndex; rowIndex++, i++) {
				Object itemData = ObjectUtil.instance(cfgd.getItemMappingClass());
				BeanWrapper itemBean = ObjectUtil.buildBeanWrapperImpl(itemData);
				for (CellFieldDefinition gcfd : gcfds) {
					this.cellProcess(sheet, new CellIndex(rowIndex, gcfd.getColumn()), gcfd, sd, pd, itemBean, validateFailExceptions);
				}
				
				if (pd.getEmptyRowAsNull() &&  (Boolean) ObjectUtil.invokeMethod(itemBean, null, "isEmpty")) { // TODO
					if (!pd.getFilterEmptyRow()) {
						groupBean.setPropertyValue(String.valueOf(i), null);
					}
				} else {
					groupBean.setPropertyValue(String.valueOf(i), itemBean.getWrappedInstance());
				}
			}
			
			sheetBean.setPropertyValue(cfgd.getName(), groupBean.getWrappedInstance());
		} else if (cfd instanceof CellFieldDefinition) {
			if (cellIndex == null) {
				cellIndex = ((CellFieldDefinition)cfd).getCellIndex();
			}
			
			Row row = sheet.getRow(cellIndex.getRow());
			if (row == null) return;
			Cell cell = row.getCell(cellIndex.getColumn());
			if (cell == null) return;
			
			Object cellValue = ExcelUtil.getCellValue(cell);
			if (cellValue == null) {
				cellValue = cfd.getValue();
			}
			
			cellValue = this.render(cellValue, cfd, cfd.getRenderers(), RendererDefinition.PHASE_BEFORE);
			
			boolean validatePassed = this.validate(cellValue, cfd, cfd.getValidator(), pd.getFailMode(), validateFailExceptions);
			if (validatePassed) {
				cellValue = this.render(cellValue, cfd, cfd.getRenderers(), RendererDefinition.PHASE_AFTER);
				sheetBean.setPropertyValue(cfd.getName(), cellValue);
				
				this.handle(cellValue, sheetBean.getWrappedInstance(), cfd, cfd.getHandlers(), null);
			} else {
				this.handle(null, sheetBean.getWrappedInstance(), cfd, cfd.getHandlers(), validateFailExceptions);
			}
		}
	}
	
	private Workbook createWorkbook(ExcelToPojoParserDefinition etppd, InputStream inputStream, Map<String, Object> params) {
		try {
			if (params != null && params.containsKey("filePath") && params.get("filePath").toString().toLowerCase().endsWith(".xlsx")) {
			    return new XSSFWorkbook(inputStream); // excel 2007, 2010 ...
			} else {
			    return new HSSFWorkbook(inputStream); // excel 2003
			}
		}catch (IOException e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		} finally {
			if (closeInputStream) {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException e) {
					}
				}
			}
		}
	}
}