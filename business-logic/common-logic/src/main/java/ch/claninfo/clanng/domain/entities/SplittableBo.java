/* $Id: SplittableBo.java 1262 2017-08-09 19:05:33Z lar $ */

package ch.claninfo.clanng.domain.entities;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.MappedSuperclass;

import ch.claninfo.common.connect.Method;
import ch.claninfo.common.dao.BaseBo;
import ch.claninfo.common.dao.BoFactory;

@MappedSuperclass
public class SplittableBo extends BaseBo {

	private transient boolean deleted = false;
	private transient boolean split;
	private transient Map<String, List<PropertyChangeListener>> fieldChangeListeners = new HashMap<>();
	private final transient List<String> changedProperties = new ArrayList<>();

	public SplittableBo() {
		super();
	}

	/**
	 * @param pFactory
	 */
	public SplittableBo(BoFactory pFactory) {
		super(pFactory);
	}

	/**
	 * @return get the actual State as a Method value
	 */
	public Method getMethod() {
		if (deleted) {
			return Method.DEL;
		}
		if (!exists()) {
			return Method.INS;
		}
		if (isDirty()) {
			return Method.UPD;
		}
		return Method.SEL;
	}

	/**
	 * @return the deleted
	 */
	public boolean isDeleted() {
		return deleted;
	}

	/**
	 * @return true if split
	 */
	public boolean isSplit() {
		return split;
	}

	public void markSplit() {
		split = true;
	}

	/**
	 * @param pDeleted the deleted to set
	 */
	public void setDeleted(boolean pDeleted) {
		deleted = pDeleted;
	}

	public void addChangedListener(String property, PropertyChangeListener callback) {
		List<PropertyChangeListener> listeners = fieldChangeListeners.computeIfAbsent(property, (p) -> new ArrayList<>());
		listeners.add(callback);
	}

	void callChangeListeners(PropertyChangeEvent e) {
		for (PropertyChangeListener propertyChangeListener : fieldChangeListeners.getOrDefault(e.getPropertyName(), Collections.emptyList())) {
			propertyChangeListener.propertyChange(e);
		}
	}
}