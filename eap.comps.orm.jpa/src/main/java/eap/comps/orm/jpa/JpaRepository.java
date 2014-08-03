package eap.comps.orm.jpa;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

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
public class JpaRepository<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> {
	
	public JpaRepository(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
		super(entityInformation, entityManager);
	}

	public JpaRepository(Class<T> domainClass, EntityManager em) {
		super(domainClass, em);
	}
	
	public PagingDataList findAll(Paginator paginator) {
		PageRequest pageable = new PageRequest(paginator.getCurrPage() - 1, paginator.getPageSize()); 

		Page page = findAll(pageable);
		
		return new PagingDataList(page.getTotalElements(), page.getContent(), paginator);
	}
	
	public PagingDataList findAll(Specification<T> spec, Paginator paginator) {
		PageRequest pageable = new PageRequest(paginator.getCurrPage() - 1, paginator.getPageSize()); 
		
		Page page = findAll(spec, pageable);

		return new PagingDataList(page.getTotalElements(), page.getContent(), paginator);
	}
}