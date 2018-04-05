package ch.claninfo.clanng.web.config;

import java.util.EnumSet;
import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebListener;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.BeanUtilsBean2;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import ch.claninfo.clanng.web.rerouter.RequestCachingFilter;
import ch.claninfo.clanng.web.servlets.CommunicatorNg;
import ch.claninfo.common.logging.LogCategory;
import ch.claninfo.common.logging.LogConfigurator;
import ch.claninfo.common.logging.PossibleLogCategory;

@WebListener
public class ApplicationBootstrapper implements ServletContextListener {

	private static final String COMMUNICATOR_NG_CONTEXT_NAME = "/CommunicatorNG";

	private static final String COMMUNICATOR_CONTEXT_NAME = "/Communicator";
	private static final String COMMUNICATOR_NG_SERVLET_NAME = "CommunicatorServlet";

	static {
		// Disable oracle xmlparser DocumentBuilderFactory and instead use the
		// standard rt.jar implementation.
		System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
		System.setProperty("javax.xml.parsers.SAXParserFactory", "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");

		//Initialize commons-converter
		BeanUtilsBean.setInstance(new BeanUtilsBean2());
	}

	private LogCategory log;

	private AnnotationConfigApplicationContext applicationContext;

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		log.info("Shutting down spring application.");
		applicationContext.close();
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		try {
			ServletContext servletContext = servletContextEvent.getServletContext();
			initLogging(servletContext);
			log.debug("Configuring Spring root application context"); //$NON-NLS-1$
			applicationContext = initSpringContext();
			initServlets(servletContext);
			initFilters(servletContext);
		}
		catch (Throwable t) {
			log.error("Exception while initializing the application.", t);
			throw t;
		}
	}

	private void initFilters(ServletContext servletContext) {
		servletContext.addFilter("CachingFilter", RequestCachingFilter.class).addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, COMMUNICATOR_CONTEXT_NAME);
	}

	private void initLogging(ServletContext servletContext) {
		if (log == null) {
			if (!LogConfigurator.isConfigured()) {
				String log4jPropsName = servletContext.getInitParameter("ch.claninfo.ias.log4jconfig"); //$NON-NLS-1$
				if (log4jPropsName == null) {
					LogConfigurator.configurebasic();
				} else {
					LogConfigurator.configure(log4jPropsName);
				}
			}
			log = new LogCategory(PossibleLogCategory.DEFAULTCAT, getClass().getName());
		}
	}

	private void initServlets(ServletContext servletContext) {
		ServletRegistration.Dynamic dispatcher = servletContext.addServlet(COMMUNICATOR_NG_SERVLET_NAME, CommunicatorNg.class);
		dispatcher.setLoadOnStartup(1);
		dispatcher.addMapping(COMMUNICATOR_NG_CONTEXT_NAME);
	}

	private AnnotationConfigApplicationContext initSpringContext() {
		return new AnnotationConfigApplicationContext(SpringConfig.class);
	}
}