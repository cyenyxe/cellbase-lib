package org.bioinfo.infrared.lib.impl.hibernate;

import java.util.List;

import org.bioinfo.infrared.lib.impl.DBAdaptor;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;

public class HibernateDBAdaptor extends DBAdaptor{

	private SessionFactory sessionFactory;
	private Session session;

	//	public HibernateDBAdaptor() {
	//
	//	}

	public HibernateDBAdaptor(SessionFactory sessionFactory) {
		this.setSessionFactory(sessionFactory);
	}

	protected Session openSession() {
		if(session == null) {
			logger.debug("HibernateDBAdaptor: Session is null");
			session = sessionFactory.openSession();
		}else {
			if(!session.isOpen()) {
				logger.debug("HibernateDBAdaptor: Session is closed");
				session = sessionFactory.openSession();
			}else {
				logger.debug("HibernateDBAdaptor: Session is already open");
			}
		}

		return session;
	}

	protected List<?> execute(Query query){
		List<?> result = query.list();
		return result;
	}


	protected List<?> executeAndClose(Criteria criteria){
		List<?> result = criteria.list();
		closeSession();
		return result;
	}

	protected List<?> executeAndClose(Query query){
		List<?> result = query.list();
		closeSession();
		return result;
	}

	protected void closeSession() {
		if(session != null && session.isOpen()) {
			session.close();
		}
	}

	/**
	 * @return the sessionFactory
	 */
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	/**
	 * @param sessionFactory the sessionFactory to set
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}
