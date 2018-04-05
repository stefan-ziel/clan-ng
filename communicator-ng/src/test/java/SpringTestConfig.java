import java.lang.reflect.Field;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import ch.claninfo.clanng.web.config.SpringConfig;
import ch.claninfo.clanng.web.metadata.BoDef;

import org.hibernate.ScrollMode;
import org.hibernate.internal.AbstractScrollableResults;
import org.hibernate.query.Query;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;

@Configuration
@EnableSpringConfigured
@ComponentScan({"ch.claninfo.clanng.bologic", "ch.claninfo.clanng.domain"})
public class SpringTestConfig {

	@Bean(name = SpringConfig.NG_PERSISTENCE_UNIT)
	public EntityManagerFactory pekaEntityManagerFactory() throws IllegalAccessException, NoSuchFieldException {
		EntityManagerFactory emf = Mockito.mock(EntityManagerFactory.class);
		EntityManager em = Mockito.mock(EntityManager.class);
		Mockito.when(emf.createEntityManager()).thenReturn(em);

		Query query = Mockito.mock(Query.class);
		Mockito.when(em.createQuery(Mockito.anyString(), Mockito.any())).thenReturn(query);

		AbstractScrollableResults scrollableResults = Mockito.mock(AbstractScrollableResults.class);
		Mockito.when(query.scroll(ScrollMode.FORWARD_ONLY)).thenReturn(scrollableResults);

		Field closed = AbstractScrollableResults.class.getDeclaredField("closed");
		closed.setAccessible(true);
		closed.set(scrollableResults, true);
		return emf;
	}

	@Bean
	public KieServices kieServices() {
		return KieServices.Factory.get();
	}

	@Bean(destroyMethod = "dispose")
	public KieContainer kieContainer(KieServices kieServices) {
		return kieServices.getKieClasspathContainer();
	}

	@Bean
	public DataSource dataSource() {
		return Mockito.mock(DataSource.class);
	}


	@Bean
	public BoDef boDefQuery() {
		return Mockito.mock(BoDef.class);
	}
}