package ch.claninfo.clanng.domain.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@Documented
@NotNull
@Min(1)
@Max(2)
public @interface LohnArtConstraint {
	String message() default "Invalid LohnArt";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}