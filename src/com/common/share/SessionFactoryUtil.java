/**
 * 
 * @author Sebastian Hennebrueder
 * created Feb 22, 2006
 * copyright 2006 by http://www.laliluna.de
 */
package com.common.share;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;

/**
 * @author hennebrueder This class garanties that only one single SessionFactory
 *         is instanciated and that the configuration is done thread safe as
 *         singleton. Actually it only wraps the Hibernate SessionFactory.
 *         You are free to use any kind of JTA or Thread transactionFactories.
 */
public class SessionFactoryUtil {

  /** The single instance of hibernate SessionFactory */
  private static org.hibernate.SessionFactory sessionFactory;

	/**
	 * disable contructor to guaranty a single instance
	 */
	private SessionFactoryUtil() 
	{
		
	}

	static
	{
		
		/******************Client PC******************/
	
		/*Configuration configuration=new Configuration();
		configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect");
		configuration.setProperty("hibernate.connection.driver_class", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
		configuration.setProperty("hibernate.connection.url", "jdbc:sqlserver://localhost;");
		configuration.setProperty("hibernate.connection.databaseName", "astecherp");
		configuration.setProperty("hibernate.connection.username", "sa");
		configuration.setProperty("hibernate.connection.password", "dh78>apg");
		configuration.setProperty("hibernate.bytecode.use_reflection_optimizer", "false");
		configuration.setProperty("hibernate.current_session_context_class", "thread");
		configuration.setProperty("hibernate.search.autoregister_listeners", "false");
		configuration.setProperty("hibernate.show_sql", "true");
		
		sessionFactory=configuration.configure().buildSessionFactory();*/
		
		
		/******************Client PC******************/
		
		
		
		//******************ESL User PC******************//*
		
		Configuration configuration=new Configuration();
		configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect");
		configuration.setProperty("hibernate.connection.driver_class", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
		configuration.setProperty("hibernate.connection.url", "jdbc:sqlserver://esl4;");
		configuration.setProperty("hibernate.connection.databaseName", "astecherp03-02-2021");
		configuration.setProperty("hibernate.connection.username", "sa");
		configuration.setProperty("hibernate.connection.password", "123");
		configuration.setProperty("hibernate.bytecode.use_reflection_optimizer", "false");
		configuration.setProperty("hibernate.current_session_context_class", "thread");
		configuration.setProperty("hibernate.search.autoregister_listeners", "false");
		configuration.setProperty("hibernate.show_sql", "true");
		
		sessionFactory=configuration.configure().buildSessionFactory();
	
		/******************ESL User PC******************/
  }
	
	                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   
	public static SessionFactory getInstance() 
	{
		return sessionFactory;
	}

  /**
   * Opens a session and will not bind it to a session context
   * @return the session
   */
	public Session openSession() 
	{
		return sessionFactory.openSession();
	}

	/**
   * Returns a session from the session context. If there is no session in the context it opens a session,
   * stores it in the context and returns it.
	 * This factory is intended to be used with a hibernate.cfg.xml
	 * including the following property <property
	 * name="current_session_context_class">thread</property> This would return
	 * the current open session or if this does not exist, will create a new
	 * session
	 * 
	 * @return the session
	 */
	public Session getCurrentSession()
	{
		return sessionFactory.getCurrentSession();
	}

  /**
   * closes the session factory
   */
	public static void close(){
		if (sessionFactory != null)
			sessionFactory.close();
		sessionFactory = null;
	
	}
}
