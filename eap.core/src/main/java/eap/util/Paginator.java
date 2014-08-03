package eap.util;

import eap.EapContext;
import eap.Env;

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
public class Paginator { // int
	
	public static final int PAGE_MAX_SIZE = 100;
	
//	private List<Object> items;
	
	private long totalCount = 0; // TODO long
	private int currPage = 1;
	private int pageSize = 10;
	
	private String sortField;
	private String sortDir = "ASC"; // ASC / DESC
	
	public Paginator(){
		Env env = EapContext.getEnv();
		this.setPageSize(env.getProperty("app.paging.pageSize", Integer.class, 10));
	}
	
	public Paginator(int pageSize){
		this.setPageSize(pageSize);
	}
	
	public int getOffset() {
		return (totalCount == 0 ? 0 : (((currPage - 1) * pageSize) + 1) - 1);
	}
	
	public int getLimit() {
//		return currPage < this.getPages() ? (currPage * pageSize) : totalCount;
//		return currPage < this.getPages() ? pageSize : (totalCount % pageSize);
		return (int) (currPage < this.getPages() ? pageSize : (totalCount - ((this.getPages() - 1) * pageSize))); // TODO
	}
	
	public int getPages() {
		return (int) (((totalCount - 1) / pageSize) + 1);
	}
	
	public int prev() {
		if (hasPrev()) {
			return this.getCurrPage() - 1;
		}
		return this.getCurrPage();
	}
	public int next() {
		if (hasNext()) {
			return this.getCurrPage() + 1;
		}
		return this.getCurrPage();
	}
	public boolean isFirst() {
		return this.getCurrPage() == 1;
	}
	public boolean hasNext() {
		return this.getCurrPage() < this.getPages();
	}
	public boolean isLast() {
		return this.getCurrPage() == this.getPages();
	}
	public boolean hasPrev() {
		return this.getCurrPage() > 1;
	}
	
	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}

	public int getCurrPage() {
		return currPage;
	}

	public void setCurrPage(int currPage) {
		this.currPage = currPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = (pageSize < PAGE_MAX_SIZE ? pageSize : PAGE_MAX_SIZE);
	}
	
	public String getSortField() {
		return sortField;
	}

	public void setSortField(String sortField) {
		this.sortField = sortField;
	}

	public String getSortDir() {
		return sortDir;
	}

	public void setSortDir(String sortDir) {
		this.sortDir = sortDir;
	}

	public static void main(String[] args) {
		System.out.println(5 % 2);
		System.out.println(3 % 2);
		
		new Paginator();
		
//		Paginator p = new Paginator();
//		
//		p.setTotalCount(19);
//		p.setCurrPage(1);
//		System.out.println(p.getOffset());
//		System.out.println(p.getLimit());
//		p.setCurrPage(2);
//		System.out.println(p.getOffset());
//		System.out.println(p.getLimit());
//		
//		p.setTotalCount(20);
//		p.setCurrPage(1);
//		System.out.println(p.getOffset());
//		System.out.println(p.getLimit());
//		p.setCurrPage(2);
//		System.out.println(p.getOffset());
//		System.out.println(p.getLimit());
//		
//		p.setTotalCount(21);
//		p.setCurrPage(1);
//		System.out.println(p.getOffset());
//		System.out.println(p.getLimit());
//		p.setCurrPage(2);
//		System.out.println(p.getOffset());
//		System.out.println(p.getLimit());
//		p.setCurrPage(3);
//		System.out.println(p.getOffset());
//		System.out.println(p.getLimit());
	}
}