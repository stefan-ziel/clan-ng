/* $Id: Code.java 1234 2017-05-31 19:11:15Z lar $ */

package ch.claninfo.clanng.domain.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

import ch.claninfo.clanng.domain.constraints.validators.CodeValidator;

/**
 * 
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CodeValidator.class)
@Documented
public @interface Code {

	Class<?>[] groups() default {};

	String message() default "Invalid code value.";

	String name() default "";

	Class<? extends Payload>[] payload() default { };
}
