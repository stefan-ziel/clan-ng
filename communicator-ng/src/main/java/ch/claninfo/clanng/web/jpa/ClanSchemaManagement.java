package ch.claninfo.clanng.web.jpa;

import java.sql.Types;
import java.util.Map;

import org.hibernate.boot.Metadata;
import org.hibernate.dialect.Dialect;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Table;
import org.hibernate.tool.schema.extract.spi.ColumnInformation;
import org.hibernate.tool.schema.internal.DefaultSchemaFilter;
import org.hibernate.tool.schema.internal.GroupedSchemaValidatorImpl;
import org.hibernate.tool.schema.internal.HibernateSchemaManagementTool;
import org.hibernate.tool.schema.spi.ExecutionOptions;
import org.hibernate.tool.schema.spi.SchemaValidator;

/**
 * Hack to consider Database Char type equal to Varchar type
 */
public class ClanSchemaManagement extends HibernateSchemaManagementTool {

	@Override
	public SchemaValidator getSchemaValidator(Map options) {
		return new GroupedSchemaValidatorImpl(this, DefaultSchemaFilter.INSTANCE) {

			@Override
			protected void validateColumnType(Table table, Column column, ColumnInformation columnInformation, Metadata metadata, ExecutionOptions options, Dialect dialect) {
				if (Types.CHAR == columnInformation.getTypeCode() && column.getSqlTypeCode(metadata) == Types.VARCHAR) {
					//Assume that char type is equal to varchar and accept String when database type is CHAR.
					return;
				}

				super.validateColumnType(table, column, columnInformation, metadata, options, dialect);
			}
		};
	}
}