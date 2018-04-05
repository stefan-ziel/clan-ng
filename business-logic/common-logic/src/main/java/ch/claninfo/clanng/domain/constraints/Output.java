/* $Id: Output.java 1234 2017-05-31 19:11:15Z lar $ */

package ch.claninfo.clanng.domain.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Output {
	// NOP
}
