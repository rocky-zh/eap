package eap.comps.datamapping.factory.object;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import eap.comps.datamapping.definition.CellFieldDefinition;
import eap.comps.datamapping.definition.CellFieldDefinition.CellIndex;
import eap.comps.datamapping.definition.CellFieldGroupDefinition;
import eap.comps.datamapping.definition.CellFieldSetDefinition;
import eap.comps.datamapping.definition.DataFieldDefinition;
import eap.comps.datamapping.definition.DataMappingDefinition;
import eap.comps.datamapping.definition.RendererDefinition;
import eap.comps.datamapping.definition.SheetDefinition;
import eap.comps.datamapping.definition.parser.ParserDefinition;
import eap.comps.datamapping.definition.parser.PojoToExcelParserDefinition;
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
public class PojoToExcelMapping extends AbstractDataMapping<Object, byte[]> {
	
	public static final String PROPERTY_EXCEL_TEMPLATE = "template";
	
	@Override
	protected byte[] mappingImpl(String name, Object source, Map<String, Object> params,
		DataMappingDefinition dmd, ParserDefinition pd) 
	{
		PojoToExcelParserDefinition ptepd = (PojoToExcelParserDefinition) pd;
		List<SheetDefinition> sds = ptepd.getSheets();
		
		ValidateFailExceptions allValidateFailExceptions =  new ValidateFailExceptions();
		
		String excelTemplate = pd.getProps().getProperty(PROPERTY_EXCEL_TEMPLATE);
		Workbook workBook = ExcelUtil.getWorkbook(excelTemplate, ptepd.getVersion());
		if (source instanceof Collection<?>) { // OTHER Map, Array
			Collection<?> sourceCollection = (Collection<?>) source;
			int sheetIndex = 0;
			for (Object sheetObj : sourceCollection) {
				if (sheetIndex >= sds.size()) {
					continue;
				}
				
				SheetDefinition sd = sds.get(sheetIndex);
				Sheet sheet = this.getSheet(workBook, sheetIndex, sd, sheetObj);
				this.sheetProcess(workBook, sheet, sd, ptepd, sheetObj, allValidateFailExceptions);
				
				sheetIndex++;
			}
		} else {
			int sheetIndex = 0;
			SheetDefinition sd = sds.get(sheetIndex);
			Sheet sheet = this.getSheet(workBook, sheetIndex, sd, source);
			this.sheetProcess(workBook, sheet, sd, ptepd, source, allValidateFailExceptions);
		}
		this.handle(workBook, source, pd, dmd.getHandlers(), allValidateFailExceptions);
		
		OutputStream outputStream = (OutputStream) params.get("outputStream");
		if (outputStream != null) {
			try {
				workBook.write(outputStream);
				return null;
			} catch (IOException e) {
				throw new IllegalStateException(e.getMessage(), e);
			}
		} else {
			ByteArrayOutputStream baos = null;
			try {
				baos = new ByteArrayOutputStream();
				workBook.write(baos);
				
				return baos.toByteArray();
			} catch (IOException e) {
				return null;
			} finally {
				if (baos != null) {
					try {
						baos.close();
					} catch (IOException e1) {
					}
				}
			}
		}
	}
	
	private void sheetProcess(Workbook workBook, Sheet sheet, 
			SheetDefinition sd, PojoToExcelParserDefinition pd,
			Object sheetObject, ValidateFailExceptions allValidateFailExceptions) 
	{
		ValidateFailExceptions validateFailExceptions = new ValidateFailExceptions();
		
		boolean validatePassed = this.validate(sheetObject, sd, sd.getValidator(), pd.getFailMode(), validateFailExceptions);
		if (validatePassed) {
			this.handle(sheetObject, sheetObject, sd, sd.getHandlers(), null);
			
			List<DataFieldDefinition> cfds = sd.getCells();
			for (DataFieldDefinition cfd : cfds) {
				this.cellProcess(workBook, sheet, null, cfd, sd, pd, sheetObject, validateFailExceptions);
			}
			
			this.handle(sheet, workBook, sd, sd.getHandlers(), validateFailExceptions);
		} else {
			this.handle(null, null, sd, sd.getHandlers(), validateFailExceptions);
		}
		allValidateFailExceptions.addException(validateFailExceptions);
	}
	
	private void cellProcess(Workbook workBook, Sheet sheet, CellIndex cellIndex,
		DataFieldDefinition cfd, SheetDefinition sd, PojoToExcelParserDefinition pd, 
		Object data, ValidateFailExceptions validateFailExceptions) 
	{
		if (cfd instanceof CellFieldSetDefinition) {
			List<DataFieldDefinition> rdfds = dataMappingFactory.getDataFieldSet((CellFieldSetDefinition) cfd);
			for (DataFieldDefinition rdfd : rdfds) {
				this.cellProcess(workBook, sheet, ((CellFieldDefinition)rdfd).getCellIndex(), rdfd, sd, pd, data, validateFailExceptions);
			}
		}
		else if (cfd instanceof CellFieldGroupDefinition) {
			CellFieldGroupDefinition cfgd = (CellFieldGroupDefinition) cfd;
			
			int rowIndex = cfgd.getRow();
			Object groupValue = ObjectUtil.getProperty(data, cfgd.getName(), Object.class);
			if (groupValue != null) {
				if (groupValue instanceof Collection<?>) {
					Collection<?> gvs = (Collection<?>) groupValue;
					for (Object gv : gvs) {
						for (CellFieldDefinition gcfd : cfgd.getCells()) {
							this.cellProcess(workBook, sheet, new CellIndex(rowIndex, gcfd.getColumn()), gcfd, sd, pd, gv, validateFailExceptions);
						}
						rowIndex++;
					}
				} else if (groupValue instanceof Object[]) {
					Object[] gvs = (Object[]) groupValue;
					for (Object gv : gvs) {
						for (CellFieldDefinition gcfd : cfgd.getCells()) {
							this.cellProcess(workBook, sheet, new CellIndex(rowIndex, gcfd.getColumn()), gcfd, sd, pd, gv, validateFailExceptions);
						}
						rowIndex++;
					}
				} else {
					for (CellFieldDefinition gcfd : cfgd.getCells()) {
						this.cellProcess(workBook, sheet, new CellIndex(rowIndex, gcfd.getColumn()), gcfd, sd, pd, groupValue, validateFailExceptions);
					}
					rowIndex++;
				}
			}
		}
		else if (cfd instanceof CellFieldDefinition) {
			Object cellValue = null;
			if (StringUtils.isNotBlank(cfd.getName())) {
				cellValue = ObjectUtil.getProperty(data, cfd.getName(), Object.class);
			}
			if (cellValue == null) {
				cellValue = cfd.getValue();
			}
			
			cellValue = this.render(cellValue, cfd, cfd.getRenderers(), RendererDefinition.PHASE_BEFORE);
			
			boolean validatePassed = this.validate(cellValue, cfd, cfd.getValidator(), pd.getFailMode(), validateFailExceptions);
			if (validatePassed) {
				cellValue = this.render(cellValue, cfd, cfd.getRenderers(), RendererDefinition.PHASE_AFTER);
				
				if (cellIndex == null) {
					cellIndex = ((CellFieldDefinition) cfd).getCellIndex();
				}
				
				Row row = ExcelUtil.getRow(sheet, cellIndex.getRow());
				Cell cell = ExcelUtil.getCell(row, cellIndex.getColumn());
				ExcelUtil.setCellValue(cell, cellValue);
				
				this.handle(cell, row, cfd, cfd.getHandlers(), null);
			} else {
				this.handle(null, data, cfd, cfd.getHandlers(), validateFailExceptions);
			}
		}
	}

	private Sheet getSheet(Workbook workBook, int sheetIndex, SheetDefinition sd, Object bean) {
		Sheet sheet = null;
		try {
			sheet = workBook.getSheetAt(sheetIndex);
		} catch (IllegalArgumentException e) { // IndexOutOfBoundsException
			String sheetName = null;
			String propName = sd.getName();
			if (StringUtils.isNotEmpty(propName)) {
				sheetName = ObjectUtil.getProperty(bean, propName);
			}
			if (StringUtils.isEmpty(sheetName)) {
				sheetName = sd.getSheetName();
			}
			
			if (StringUtils.isEmpty(sheetName)) {
				sheet = workBook.createSheet();
			} else {
				sheet = workBook.createSheet(sheetName);
			}
		}
		
		return sheet;
	}
}