
package ch.claninfo.clanng.domain.services;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Table;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;

import ch.claninfo.clanng.domain.entities.SplittableBo;
import ch.claninfo.common.connect.CommException;
import ch.claninfo.common.dao.BaseBo;
import ch.claninfo.common.dao.BaseQuery;
import ch.claninfo.common.dao.BoFactory;

public class JpaBoFactory extends BoFactory {

	private static final Pattern IS_DIRTY_METHOD_NAME = Pattern.compile("is.*Dirty");
	private EntityManager em;

	public JpaBoFactory(EntityManager pEm) {
		em = pEm;
	}

	public static List<PropertyDescriptor> getPropertyDescriptors(Class<? extends BaseBo> boClass) {
		PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(boClass);

		ArrayList<PropertyDescriptor> descriptorList = new ArrayList<>();
		for (PropertyDescriptor descriptor : descriptors) {
			java.lang.reflect.Method readMethod = descriptor.getReadMethod();
			if (readMethod == null || readMethod.getDeclaringClass().isAssignableFrom(SplittableBo.class) || IS_DIRTY_METHOD_NAME.matcher(readMethod.getName()).matches()) {
				continue;
			}
			descriptorList.add(descriptor);
		}
		return descriptorList;
	}

	@Override
	public <T extends BaseBo> List<T> asList(Class<T> pDTOClass, List<BaseQuery.FilterOperator> pFilter, List<BaseQuery.OrderOperator> pOrder) throws CommException {
		return asList(pDTOClass, JPQLSyntax.toWhere(pFilter), JPQLSyntax.toOrder(pOrder));
	}

	@SuppressWarnings("unchecked")
	public <T extends BaseBo> List<T> asList(Class<T> pDTOClass, String pWhere, String pOrder) throws CommException {
		return getQuery(pDTOClass, pWhere, pOrder).getResultList();
	}

	@Override
	public <T extends BaseBo> void delete(T pBo) throws CommException {
		em.remove(pBo);
	}

	public void fillProperties(SplittableBo bo, Map<String, Object> parameterMap) throws CommException {
		try {
			for (Map.Entry<String, Object> entry : parameterMap.entrySet()) {
				BeanUtils.setProperty(bo, entry.getKey(), entry.getValue());
			}
		}
		catch (IllegalAccessException | InvocationTargetException e) {
			throw new CommException(e);
		}
	}

	public <T extends SplittableBo> T find(Class<T> pDTOClass, Object pPk) {
		return em.find(pDTOClass, pPk);
	}

	@SuppressWarnings("unchecked")
	public Class<? extends SplittableBo> getBoClass(String pModul, String pBo) throws CommException {
		try {
			return (Class<? extends SplittableBo>) getClass(pModul, pBo, ClassType.BO);
		}
		catch (ClassNotFoundException e) {
			throw new CommException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends SplittableBo> List<T> nativeQuery(Class<T> pDTOClass, String pWhere, String pOrder) throws CommException {
		Table table = pDTOClass.getAnnotation(Table.class);
		Query query = em.createNativeQuery("select * from " + table.schema() + '.' + table.name() + (pWhere == null ? "" : " where " + pWhere) + (pOrder == null ? "" : " order by " + pOrder), pDTOClass);

		return query.getResultList();
	}

	public Object newPk(String pModul, String pBo) throws CommException {
		try {
			return getClass(pModul, pBo, ClassType.PK).newInstance();
		}
		catch (ClassNotFoundException e) {
			return null;
		}
		catch (InstantiationException | IllegalAccessException e) {
			throw new CommException(e);
		}
	}

	@Override
	public <T extends BaseBo> T singleResult(Class<T> pDTOClass, List<BaseQuery.FilterOperator> pFilter, List<BaseQuery.OrderOperator> pOrder) throws CommException {
		return singleResult(pDTOClass, JPQLSyntax.toWhere(pFilter), JPQLSyntax.toOrder(pOrder));
	}

	@SuppressWarnings("unchecked")
	public <T extends BaseBo> T singleResult(Class<T> pDTOClass, String pWhere, String pOrderBy) throws CommException {
		return (T) getQuery(pDTOClass, pWhere, pOrderBy).getSingleResult();
	}

	@Override
	public <T extends BaseBo> void store(T pBo) throws CommException {
		em.persist(pBo);
	}

	private <T extends BaseBo> Query getQuery(Class<T> pDTOClass, String pWhere, String pOrder) {
		return em.createQuery("select a from " + pDTOClass.getSimpleName() + " a" + (pWhere == null ? "" : " where " + pWhere) + (pOrder == null ? "" : " order by " + pOrder)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
	}
}
