package eap.comps.datamapping.exception;

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
public class ExcelToPojoOutOfBoundsException extends RuntimeException {
    
    private int lastRowNum;
    private int maxRowNum;
    
    public ExcelToPojoOutOfBoundsException(int lastRowNum, int maxRowNum) {
        super("row out of range: " + lastRowNum);
        this.lastRowNum = lastRowNum;
        this.maxRowNum = maxRowNum;
    }
    
    public int getLastRowNum() {
        return lastRowNum;
    }
    public void setLastRowNum(int lastRowNum) {
        this.lastRowNum = lastRowNum;
    }
    public int getMaxRowNum() {
        return maxRowNum;
    }
    public void setMaxRowNum(int maxRowNum) {
        this.maxRowNum = maxRowNum;
    }
}