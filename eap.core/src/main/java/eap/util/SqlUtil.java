package eap.util;

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
public class SqlUtil {
	
	public static String getLimitString(String dbType, String sql, int offset, int limit) {
		StringBuilder pagingSelect = null;
		
		if ("mysql".equalsIgnoreCase(dbType)) {
			pagingSelect = new StringBuilder(sql.length() + 20);
			pagingSelect.append(sql);
			pagingSelect.append(String.format(" limit %d,%d", offset, limit));
		}
		else if ("oracle".equalsIgnoreCase(dbType)) {
			sql = sql.trim();
			boolean isForUpdate = false;
			if (sql.toLowerCase().endsWith(" for update")) {
				sql = sql.substring( 0, sql.length() - 11);
				isForUpdate = true;
			}
			
			pagingSelect = new StringBuilder(sql.length() + 100);
			pagingSelect.append("select * from ( select row_.*, rownum rownum_ from ( ");
			pagingSelect.append(sql);
			pagingSelect.append(String.format(" ) row_ where rownum <= %d) where rownum_ > %d", offset+limit, offset));
			
			if (isForUpdate) {
				pagingSelect.append( " for update" );
			}
		}
		
		return pagingSelect.toString();
	}
}
