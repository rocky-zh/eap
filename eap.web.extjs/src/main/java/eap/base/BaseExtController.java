package eap.base;

import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadRequest;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadResult;
import eap.util.BeanUtil;
import eap.util.Paginator;
import eap.util.PagingDataList;

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
public class BaseExtController extends BaseController {
	public Paginator getPaginator(ExtDirectStoreReadRequest request) { // request not null
		Paginator paginator = new Paginator();
		paginator.setCurrPage(request.getPage());
		paginator.setPageSize(request.getLimit());
		paginator.setSortField(request.getSort());
		paginator.setSortDir(request.getDir());
		
		return paginator;
	}
	
	public <T> T getParameter(ExtDirectStoreReadRequest request, Class<T> paramClass) {
		T paramObj = null;
		try {
			paramObj = paramClass.newInstance();
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
		BeanUtil.copyProperties(request.getParams(), paramObj);
		BeanUtil.setProperty(paramObj, "paginator", this.getPaginator(request));
		
		return paramObj;
	}
	
	public <T> ExtDirectStoreReadResult<T> toExtDirectStoreReadResult(PagingDataList dataList) {
		return new ExtDirectStoreReadResult<T>(dataList.getTotalCount(), dataList.getItems());
	}
}