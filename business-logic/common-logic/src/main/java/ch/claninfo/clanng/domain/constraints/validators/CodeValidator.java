/* $Id: CodeValidator.java 1280 2017-10-25 19:53:32Z zis $ */

package ch.claninfo.clanng.domain.constraints.validators;

import javax.inject.Inject;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Configurable;

import ch.claninfo.clanng.domain.constraints.Code;
import ch.claninfo.clanng.domain.services.CodeService;
import ch.claninfo.clanng.session.services.SessionUtils;

@Configurable
public class CodeValidator implements ConstraintValidator<Code, Object> {

	@Inject
	private CodeService codeService;

	private String codeName;

	@Override
	public void initialize(Code pConstraintAnnotation) {
		codeName = pConstraintAnnotation.name();
	}

	@Override
	public boolean isValid(Object pValue, ConstraintValidatorContext pContext) {
		return codeService.isValid(SessionUtils.getSession().getModul(), codeName, pValue);
	}

}
