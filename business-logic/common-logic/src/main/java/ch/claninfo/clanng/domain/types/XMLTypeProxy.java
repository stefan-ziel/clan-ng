/* $Id: XMLTypeProxy.java 1248 2017-07-11 19:28:25Z lar $ */

package ch.claninfo.clanng.domain.types;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.jdbc.OracleTypes;
import oracle.sql.OPAQUE;
import oracle.xdb.XMLType;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;
import org.springframework.jdbc.support.nativejdbc.NativeJdbcExtractor;
import org.springframework.jdbc.support.nativejdbc.OracleJdbc4NativeJdbcExtractor;
import org.xml.sax.SAXException;

/**
 * XMLType wrapper
 */
public class XMLTypeProxy implements UserType {

	@Override
	public Object assemble(Serializable pCached, Object pOwner) throws HibernateException {
		return pCached == null ? null : new XMLDocument((String) pCached);
	}

	@Override
	public Object deepCopy(Object pValue) throws HibernateException {
		return ((XMLDocument) pValue).clone();
	}

	@Override
	public Serializable disassemble(Object pValue) throws HibernateException {
		if (pValue == null) {
			return null;
		}

		try {
			return ((XMLDocument) pValue).getString();
		}
		catch (SAXException e) {
			throw new HibernateException(e);
		}
	}

	@Override
	public boolean equals(Object pX, Object pY) throws HibernateException {
		return (pX != null) && pX.equals(pY);
	}

	@Override
	public int hashCode(Object pX) throws HibernateException {
		return (pX != null) ? pX.hashCode() : 0;
	}

	@Override
	public boolean isMutable() {
		return true;
	}

	@Override
	public Object nullSafeGet(ResultSet pRs, String[] pNames, SharedSessionContractImplementor pSession, Object pOwner) throws HibernateException, SQLException {
		OPAQUE o = (OPAQUE) pRs.getObject(pNames[0]);
		if (o != null) {
			XMLType xmlType = XMLType.createXML(o);
			return new XMLDocument(xmlType.getDocument());
		}
		return null;
	}

	@Override
	public void nullSafeSet(PreparedStatement pSt, Object pValue, int pIndex, SharedSessionContractImplementor pSession) throws HibernateException, SQLException {
		if (pValue != null) {
			XMLDocument val = (XMLDocument) pValue;
			if (val.isEmpty()) {
				pSt.setNull(pIndex, OracleTypes.OPAQUE);
			} else {
				XMLType xmlType;
				NativeJdbcExtractor extractor = new OracleJdbc4NativeJdbcExtractor();
				Connection connection = extractor.getNativeConnection(pSt.getConnection());
				try {
					if (val.hasDocument()) {
						xmlType = XMLType.createXML(connection, val.getDocument());
					} else {
						xmlType = XMLType.createXML(connection, val.getString());
					}
					pSt.setObject(pIndex, xmlType, OracleTypes.OPAQUE);
				}
				catch (SQLException | SAXException e) {
					throw new HibernateException(e);
				}
			}
		} else {
			pSt.setNull(pIndex, OracleTypes.OPAQUE);
		}
	}

	@Override
	public Object replace(Object pOriginal, Object pTarget, Object pOwner) throws HibernateException {
		return pOriginal;
	}

	@Override
	public Class<?> returnedClass() {
		return XMLDocument.class;
	}

	@Override
	public int[] sqlTypes() {
		return new int[] {OracleTypes.OPAQUE};
	}
}
