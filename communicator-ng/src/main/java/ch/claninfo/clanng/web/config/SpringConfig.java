
package ch.claninfo.clanng.web.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.runtime.KieContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

import ch.claninfo.clanng.session.services.ClanSessionRegistry;
import ch.claninfo.clanng.web.metadata.DbMetadataLoader;
import ch.claninfo.clanng.web.metadata.MetadataLoaderInterface;
import ch.claninfo.common.xml.TextFactory;

@Configuration
@ComponentScan(SpringConfig.PACKAGES_TO_SCAN)
@EnableSpringConfigured
@EnableScheduling
public class SpringConfig {

	public static final String PACKAGES_TO_SCAN = "ch.claninfo.clanng";
	public static final String NG_PERSISTENCE_UNIT = "ng-persistence-unit";
	public static final Logger LOGGER = LogManager.getLogger();

	@Bean
	public DataSource dataSource() throws NamingException {
		return (DataSource) ClanSessionRegistry.getJni("jdbc/clanNGDB", null);
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource ds) throws IOException {
		LocalContainerEntityManagerFactoryBean emfFactory = new LocalContainerEntityManagerFactoryBean();

		emfFactory.setPersistenceProviderClass(HibernatePersistenceProvider.class);
		emfFactory.setPackagesToScan("ch.claninfo");
		emfFactory.setDataSource(ds);
		emfFactory.setPersistenceUnitName(NG_PERSISTENCE_UNIT);

		try (InputStream is = SpringConfig.class.getResourceAsStream("/META-INF/persistence.properties")) {
			Properties properties = new Properties();
			properties.load(is);
			emfFactory.setJpaProperties(properties);
		}

		return emfFactory;
	}

	@Bean(destroyMethod = "dispose")
	public KieContainer kieContainer(KieServices kieServices, ResourcePatternResolver resourceResolver) throws IOException {
		KieFileSystem kieFileSystem = kieServices.newKieFileSystem();

		String vendor = getClass().getPackage().getImplementationVendor();
		if (vendor == null) {
			vendor = "webapp-clan";
		}

		String version = getClass().getPackage().getImplementationVersion();
		if (version == null) {
			version = "1.0";
		}
		ReleaseId releaseId = kieServices.newReleaseId(vendor, "drl-rules", version);
		kieFileSystem.generateAndWritePomXML(releaseId);

		for (Resource resource : resourceResolver.getResources("classpath*:/drls/*.drl")) {
			kieFileSystem.write(kieServices.getResources().newUrlResource(resource.getURL()));
		}

		KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
		Results results = kieBuilder.getResults();
		if (results.hasMessages(Message.Level.ERROR)) {
			throw new RuntimeException("Exception while loading drools.\n" + results.toString());
		}

		return kieServices.newKieContainer(releaseId);
	}

	@Bean
	public KieServices kieServices() {
		return KieServices.Factory.get();
	}

	@Bean
	public MetadataLoaderInterface metadataLoader() {
		MetadataLoaderInterface metadataLoader = null;
		String className = (String) ClanSessionRegistry.getJni("ch.claninfo.ias.metadata.loader", null);
		if (className != null) {
			try {
				metadataLoader = (MetadataLoaderInterface) Class.forName(className).newInstance();
			}
			catch (Throwable ex) {
				LOGGER.warn("Failed to instantiate the metadata loader of type '" + className + "'. Fall back to RULESET.", ex); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		if (metadataLoader == null) {
			metadataLoader = new DbMetadataLoader();
		}
		return metadataLoader;
	}

	@Bean
	public TaskScheduler taskScheduler() {
		return new ConcurrentTaskScheduler(); // single threaded by default
	}

	@Bean
	public EntityManager technicalEntityManager(EntityManagerFactory emf) {
		return emf.createEntityManager();
	}

	@Bean
	public TextFactory textFactory() {
		return new TextFactory();
	}
}