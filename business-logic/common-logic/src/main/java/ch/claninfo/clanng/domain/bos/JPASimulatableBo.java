/* $Id: JPASimulatableBo.java 1260 2017-07-28 16:55:30Z lar $ */

package ch.claninfo.clanng.domain.bos;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import ch.claninfo.clanng.domain.entities.SplittableBo;
import ch.claninfo.common.dao.BoFactory;

@MappedSuperclass
public class JPASimulatableBo extends SplittableBo {

	@Column(name = "pPROCNR")
	long procnr = 0;

	/**
	 * 
	 */
	public JPASimulatableBo() {
		super();
	}

	/**
	 * @param pFactory
	 */
	public JPASimulatableBo(BoFactory pFactory) {
		super(pFactory);
	}

}