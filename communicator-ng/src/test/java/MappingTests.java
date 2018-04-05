import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import javax.persistence.EntityManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ch.claninfo.clanng.business.logic.conversion.DtoConverter;
import ch.claninfo.clanng.domain.entities.ClanEntity;
import ch.claninfo.clanng.domain.services.JpaBoFactory;
import ch.claninfo.clanng.versicherte.entities.Vslohn;
import ch.claninfo.common.connect.CommException;
import ch.claninfo.pvclan.bo.VersLohnBo;

@RunWith(MockitoJUnitRunner.class)
public class MappingTests {

	@Mock
	private EntityManager em;

	private JpaBoFactory jpaBoFactory;


	@Before
	public void init() {
		jpaBoFactory = new JpaBoFactory(em);
	}

	@Test
	public void linkingTest() throws CommException {
		VersLohnBo versLohnBo = jpaBoFactory.newTransferObject(VersLohnBo.class);
		versLohnBo.setUpfcd(3);

		Collection<ClanEntity> clanEntities = DtoConverter.toEntity(versLohnBo);
		Vslohn vslohn = null;
		for (ClanEntity clanEntity : clanEntities) {
			vslohn = (Vslohn) clanEntity;
			break;
		}

		vslohn.setGdat(LocalDate.now());
		versLohnBo.setVslohn(3.5);
		vslohn.setFil1cd((byte)4);
		versLohnBo.setVslpendenz(37);

		Assert.assertEquals(vslohn.getGdat(), versLohnBo.getVslgdat());
		Assert.assertEquals(vslohn.getVslohn(), new BigDecimal(versLohnBo.getVslohn().toString()));
		Assert.assertEquals(Integer.valueOf(vslohn.getFil1cd()), versLohnBo.getFil1cd());
		Assert.assertEquals(Integer.valueOf(vslohn.getPendenz()), versLohnBo.getVslpendenz());
		Assert.assertEquals(Integer.valueOf(vslohn.getUpfcd()), versLohnBo.getUpfcd());
	}
}