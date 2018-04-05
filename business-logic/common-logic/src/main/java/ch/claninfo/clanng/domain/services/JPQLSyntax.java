/* $Id: JPQLSyntax.java 1249 2017-07-13 20:38:41Z lar $ */

package ch.claninfo.clanng.domain.services;

import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.List;

import ch.claninfo.clanng.converters.ClanDateConverter;
import ch.claninfo.common.dao.BaseQuery;
import ch.claninfo.common.dao.BaseQuery.FilterOperator;
import ch.claninfo.common.dao.BaseQuery.OrderOperator;
import ch.claninfo.common.util.CSubString;
import ch.claninfo.common.util.TypeMap;

/**
 * JPQL syntax
 */
public class JPQLSyntax {

	/** Utility constant */
	private static final String AND = " AND "; //$NON-NLS-1$

	/**
	 * Condert a memeberName to a DB name and append it to a given Buffer
	 * 
	 * @param pBuffer the buffer
	 * @param pName membername in java style
	 * @return oracle name
	 */
	public static StringBuilder appendName(StringBuilder pBuffer, String pName) {
		pBuffer.append("a."); //$NON-NLS-1$
		pBuffer.append(pName);
		return pBuffer;
	}

	/**
	 * @param pOrder a list of order definitions
	 * @return an order by clause
	 */
	public static String toOrder(List<OrderOperator> pOrder) {
		if (pOrder.size() == 0) {
			return null;
		}
		StringBuilder orderBy = new StringBuilder();
		for (OrderOperator op : pOrder) {
			appendOrder(orderBy, op.getName(), op.getDirection());
		}
		return orderBy.toString();
	}

	/**
	 * @param pFilter a list of filter definitions
	 * @return a where clause
	 */
	public static String toWhere(List<FilterOperator> pFilter) {
		if (pFilter.size() == 0) {
			return null;
		}
		StringBuilder where = new StringBuilder();
		where.append('(');
		boolean initial = true;
		for (FilterOperator op : pFilter) {
			if (BaseQuery.Operator.OR.equals(op.getOp())) {
				if (initial) {
					throw new IllegalArgumentException("OR operator not allowed in this place"); //$NON-NLS-1$
				}
				where.append(") OR ("); //$NON-NLS-1$
				initial = true;
			} else if (BaseQuery.Operator.AND.equals(op.getOp())) {
				if (initial) {
					throw new IllegalArgumentException("AND operator not allowed in this place"); //$NON-NLS-1$
				}
				where.append(") AND ("); //$NON-NLS-1$
				initial = true;
			} else {
				if (initial) {
					initial = false;
				} else {
					where.append(AND);
				}
				appendOp(where, op.getName(), op.getOp(), op.getValues());
			}
		}
		where.append(')');
		return where.toString();
	}

	private static StringBuilder appendOp(StringBuilder pWhere, String pName, BaseQuery.Operator pOp, Object... pValue) {
		BaseQuery.Operator op;

		if (BaseQuery.Operator.EQ.equals(pOp) && pValue[0] == null) {
			op = BaseQuery.Operator.NULL;
		} else if (BaseQuery.Operator.NE.equals(pOp) && pValue[0] == null) {
			op = BaseQuery.Operator.NOT_NULL;
		} else {
			op = pOp;
		}

		switch (op) {
			case EQ:
				appendName(pWhere, pName).append('=');
				formatText(pWhere, pValue[0]);
				break;
			case BETWEEN:
				appendName(pWhere, pName).append(" BETWEEN "); //$NON-NLS-1$
				formatText(pWhere, pValue[0]);
				pWhere.append(AND);
				formatText(pWhere, pValue[1]);
				break;
			case GE:
				appendName(pWhere, pName).append(">="); //$NON-NLS-1$
				formatText(pWhere, pValue[0]);
				break;
			case GT:
				appendName(pWhere, pName).append('>');
				formatText(pWhere, pValue[0]);
				break;
			case IN:
				appendName(pWhere, pName).append(" IN ("); //$NON-NLS-1$
				formatText(pWhere, pValue[0]);
				for (int i = 1; i < pValue.length; i++) {
					pWhere.append(',');
					formatText(pWhere, pValue[i]);
				}
				pWhere.append(')');
				break;
			case LE:
				appendName(pWhere, pName).append("<="); //$NON-NLS-1$
				formatText(pWhere, pValue[0]);
				break;
			case LT:
				appendName(pWhere, pName).append('<');
				formatText(pWhere, pValue[0]);
				break;
			case NE:
				appendName(pWhere, pName).append("!="); //$NON-NLS-1$
				formatText(pWhere, pValue[0]);
				break;
			case NOT_NULL:
				appendName(pWhere, pName).append(" IS NULL"); //$NON-NLS-1$
				break;
			case NULL:
				appendName(pWhere, pName).append(" IS NOT NULL"); //$NON-NLS-1$
				break;
			default:
				throw new IllegalArgumentException("Not allowed: " + op); //$NON-NLS-1$
		}
		return pWhere;
	}

	private static StringBuilder appendOrder(StringBuilder pOrderBy, String pName, BaseQuery.Direction pOp) {
		return appendName(pOrderBy, pName).append(' ').append(pOp);
	}

	/**
	 * Wandelt ein Objekt in einen String um, der der Datenbank übergeben werden
	 * kann.
	 */
	private static void formatText(StringBuilder pWhere, Object pValue) {
		if (pValue instanceof TemporalAccessor) { // Date
			pWhere.append("{ts '"); //$NON-NLS-1$
			pWhere.append(ClanDateConverter.clanDateFormat((Temporal) pValue));
			pWhere.append("'}"); //$NON-NLS-1$
		} else if (pValue instanceof Date) { // Date
			pWhere.append("{ts '"); //$NON-NLS-1$
			pWhere.append(ClanDateConverter.clanDateFormat(((Date) pValue).toInstant()));
			pWhere.append("'}"); //$NON-NLS-1$
		} else if (pValue instanceof Number) {
			pWhere.append(pValue);
		} else {
			pWhere.append("'"); //$NON-NLS-1$
			pWhere.append(CSubString.replace(TypeMap.objToDbString(pValue.toString()), "'", "''")); //$NON-NLS-1$//$NON-NLS-2$
			pWhere.append("'"); //$NON-NLS-1$
		}
	}
}
