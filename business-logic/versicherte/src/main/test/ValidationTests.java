import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import ch.claninfo.clanng.versicherte.entities.Vslohn;
import org.junit.Before;
import org.junit.Test;

public class ValidationTests {

	private Validator validator;

	@Before
	public void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@Test
	public void testValidation() {
		Vslohn vslohn = new Vslohn();

		vslohn.setVsnum(BigDecimal.ONE);
		vslohn.setGdat(LocalDate.now());

		vslohn.setLohnart(1234567L);

		Set<ConstraintViolation<Vslohn>> validationResult = validator.validate(vslohn);
		System.out.println(validationResult);
	}
}