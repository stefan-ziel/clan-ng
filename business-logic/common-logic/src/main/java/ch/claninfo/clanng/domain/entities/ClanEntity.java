
package ch.claninfo.clanng.domain.entities;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.MappedSuperclass;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;

import org.kie.api.runtime.KieSession;

import ch.claninfo.common.connect.Method;

/**
 * Entity with change tracking
 */
@MappedSuperclass
public abstract class ClanEntity implements Serializable {

	private static ThreadLocal<KieSession> kieSession = new ThreadLocal<>();

	//	private transient final List<PropertyChangeListener> propertyChangeListeners = new ArrayList<>();
	private transient Map<String, List<PropertyChangeListener>> fieldChangeListeners = new HashMap<>();
	private transient boolean dirtyProperties = false;
	private transient boolean exists = false;
	private transient boolean deleted = false;

	protected ClanEntity() {
		//		addChangedListener((property, oldValue, newValue) -> {
		//			KieSession kieSession = ClanEntity.kieSession.get();
		//			if (kieSession != null) {
		////				kieSession.insert(new PropertyChangedEvent(property, oldValue, newValue));
		//			}
		//		});
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

	public static void setThreadDrools(KieSession kieSession) {
		ClanEntity.kieSession.set(kieSession);
	}

	/**
	 * set to unmodified state
	 */
	@PostPersist
	public void clearDirty() {
		dirtyProperties = false;
	}

	/**
	 * mark as deleted
	 */
	public void delete() {
		deleted = true;
	}

	/**
	 * @return whether the bo exists in the storage or not
	 */
	public boolean exists() {
		return exists;
	}

	/**
	 * @return get the actual State as a Method value
	 */
	public Method getMethod() {
		if (deleted) {
			return Method.DEL;
		}
		if (!exists) {
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
	 * @return something was modified in this entity
	 */
	public boolean isDirty() {
		return dirtyProperties;
	}

	/**
	 * mark as loaded
	 */
	@PostLoad
	public void loaded() {
		exists = true;
	}
}