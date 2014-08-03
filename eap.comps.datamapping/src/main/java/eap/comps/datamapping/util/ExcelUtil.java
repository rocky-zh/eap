package eap.comps.datamapping.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;

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
public class ExcelUtil {
	
	public static Workbook getWorkbook(String excelTemplate, String excelVersion) {
		InputStream inputStream = null;
		try {
			if (StringUtils.isNotBlank(excelTemplate)) {
				inputStream = new ClassPathResource(excelTemplate).getInputStream();
			}
			
			return getWorkbook(inputStream, excelVersion);
		} catch (IOException e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
				}
			}
		}
	}
	public static Workbook getWorkbook(InputStream inputStream, String excelVersion) {
	    int rowAccessWindowSize = 100;
	    
		if (inputStream != null) {
			try {
				return "2003".equals(excelVersion) ? new HSSFWorkbook(inputStream) : new SXSSFWorkbook(new XSSFWorkbook(inputStream), rowAccessWindowSize);
			} catch (IOException e) {
				throw new IllegalArgumentException(e.getMessage(), e);
			}
		} else {
			return "2003".equals(excelVersion) ? new HSSFWorkbook() : new SXSSFWorkbook(new XSSFWorkbook(), rowAccessWindowSize);
		}
	}
	
	public static Object getCellValue(Cell cell) {
		return getCellValue(cell, cell.getCellType());
	}
	public static Object getCellValue(Cell cell, int cellType) {
		if (cell == null) { 
			return null;
		}
		
		Object valueObj = null;
		switch (cellType) {
			case Cell.CELL_TYPE_BLANK: valueObj = null; break;
			case Cell.CELL_TYPE_BOOLEAN: valueObj = cell.getBooleanCellValue(); break;
			case Cell.CELL_TYPE_ERROR: valueObj = cell.getErrorCellValue(); break;
			case Cell.CELL_TYPE_FORMULA: 
				valueObj = cell.getCellFormula(); // getCellValue(cell, cell.getCachedFormulaResultType()); 
			break;
			case Cell.CELL_TYPE_NUMERIC: 
				if (HSSFDateUtil.isCellDateFormatted(cell)) {
					valueObj = cell.getDateCellValue();
				} else {
				    valueObj = ObjectUtil.removeEndsWithAsString(cell.getNumericCellValue());
				}
				break;
			case Cell.CELL_TYPE_STRING: valueObj = cell.getRichStringCellValue().getString(); break;
		}
		
		return valueObj;
	}
	
	public static Row getRow(Sheet sheet, int rowIndex) {
		Row row = sheet.getRow(rowIndex);
		if (row == null) {
			row = sheet.createRow(rowIndex);
		}
		
		return row;
	}
	
	public static Cell getCell(Row row, int cellIndex) {
		Cell cell = row.getCell(cellIndex);
		if (cell == null) {
			cell = row.createCell((short) cellIndex);
		}
		
		return cell;
	}
	
	public static void setCellValue(Cell cell, Object cellValue) {
		if (cellValue == null) {
			return;
		}
		
		if (cellValue instanceof String) {
			cell.setCellValue(getRichTextString(cell, cellValue));
		} else if (cellValue instanceof Double) {
			cell.setCellValue((Double) cellValue);
		} else if (cellValue instanceof Boolean) {
			cell.setCellValue((Boolean) cellValue);
		} else if (cellValue instanceof Date) {
			cell.setCellValue((Date) cellValue);
		} else if (cellValue instanceof Calendar) {
			cell.setCellValue((Calendar) cellValue);
		} else {
			cell.setCellValue(getRichTextString(cell, cellValue));
		}
	}
	private static RichTextString getRichTextString(Cell cell, Object cellValue) {
	    if (cellValue instanceof RichTextString) {
	        return (RichTextString) cellValue;
	    }
	    
	    String cellValuleStr = cellValue.toString();
	    if (cell instanceof HSSFCell) {
	        return new HSSFRichTextString(cellValuleStr);
	    } else if (cell instanceof XSSFCell || cell instanceof SXSSFCell) {
	        return new XSSFRichTextString(cellValuleStr);
	    }
	    
	    return null;
	}
}