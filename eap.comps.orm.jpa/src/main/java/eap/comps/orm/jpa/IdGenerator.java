package eap.comps.orm.jpa;

import java.io.Serializable;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.Assigned;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;

import eap.EapContext;
import eap.base.BaseBO;

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
public class IdGenerator extends Assigned implements IdentifierGenerator, Configurable {

	@Override
	public Serializable generate(SessionImplementor session, Object object) throws HibernateException {
		if (object instanceof BaseBO) {
			return EapContext.getEnv().nextId();
		}
		
		return super.generate(session, object);
	}
}