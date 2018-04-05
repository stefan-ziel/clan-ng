/* $Id: JPABaseBo.java 1260 2017-07-28 16:55:30Z lar $ */

package ch.claninfo.clanng.domain.bos;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import ch.claninfo.common.dao.BoFactory;

@MappedSuperclass
public class JPABaseBo extends JPAUpdatableBo {

	@Column(name = "pPROCNR")
	long procnr = 0;

	public JPABaseBo() {
		super();
	}

	/**
	 * @param pFactory
	 */
	public JPABaseBo(BoFactory pFactory) {
		super(pFactory);
	}

	public long getProcnr() {
		return procnr;
	}

	public void setProcnr(long procnr) {
		this.procnr = procnr;
	}
}
