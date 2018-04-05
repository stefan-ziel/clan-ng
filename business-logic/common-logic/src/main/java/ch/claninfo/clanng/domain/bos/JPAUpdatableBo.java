/* $Id: JPAUpdatableBo.java 1260 2017-07-28 16:55:30Z lar $ */

package ch.claninfo.clanng.domain.bos;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;

import ch.claninfo.clanng.domain.entities.SplittableBo;
import ch.claninfo.common.dao.BoFactory;

@MappedSuperclass
public class JPAUpdatableBo extends SplittableBo {

	@Column(name = "pUPDFLAG")
	String updflag;

	/**
	 * 
	 */
	public JPAUpdatableBo() {
		super();
	}

	/**
	 * @param pFactory
	 */
	public JPAUpdatableBo(BoFactory pFactory) {
		super(pFactory);
	}

	@PrePersist
	public void buildUpdateFlag() {
		StringBuilder flag = new StringBuilder();
		for (String changed : getChangedProperties()) {
			if (flag.length() > 0) {
				flag.append(';');
			}
			flag.append(changed);
		}
		updflag = flag.toString();
	}
}