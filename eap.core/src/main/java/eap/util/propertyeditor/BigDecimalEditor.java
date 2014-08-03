package eap.util.propertyeditor;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import org.springframework.beans.propertyeditors.CustomNumberEditor;

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
public class BigDecimalEditor extends CustomNumberEditor {
	public BigDecimalEditor() {
		super(BigDecimal.class, new DecimalFormat("#,###.##########"), true);
	}
}