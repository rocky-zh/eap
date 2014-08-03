package org.apache.ibatis.executor.parameter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.apache.ibatis.builder.xml.dynamic.ForEachSqlNode;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

public class DefaultParameterHandler implements ParameterHandler {

  private final TypeHandlerRegistry typeHandlerRegistry;

  private final MappedStatement mappedStatement;
  private final Object parameterObject;
  private BoundSql boundSql;
  private Configuration configuration;


  public DefaultParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
    this.mappedStatement = mappedStatement;
    this.configuration = mappedStatement.getConfiguration();
    this.typeHandlerRegistry = mappedStatement.getConfiguration().getTypeHandlerRegistry();
    this.parameterObject = parameterObject;
    this.boundSql = boundSql;
  }

  public Object getParameterObject() {
    return parameterObject;
  }

  public void setParameters(PreparedStatement ps)
      throws SQLException {
    ErrorContext.instance().activity("setting parameters").object(mappedStatement.getParameterMap().getId());
    List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
    if (parameterMappings != null) {
      MetaObject metaObject = parameterObject == null ? null : configuration.newMetaObject(parameterObject);
      for (int i = 0; i < parameterMappings.size(); i++) {
        ParameterMapping parameterMapping = parameterMappings.get(i);
        if (parameterMapping.getMode() != ParameterMode.OUT) {
          Object value;
          String propertyName = parameterMapping.getProperty();
          PropertyTokenizer prop = new PropertyTokenizer(propertyName);
          if (parameterObject == null) {
            value = null;
          } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
            value = parameterObject;
          } else if (boundSql.hasAdditionalParameter(propertyName)) {
            value = boundSql.getAdditionalParameter(propertyName);
          } else if (propertyName.startsWith(ForEachSqlNode.ITEM_PREFIX)
              && boundSql.hasAdditionalParameter(prop.getName())) {
            value = boundSql.getAdditionalParameter(prop.getName());
            if (value != null) {
              value = configuration.newMetaObject(value).getValue(propertyName.substring(prop.getName().length()));
            }
          } else {
            value = metaObject == null ? null : metaObject.getValue(propertyName);
          }
          TypeHandler typeHandler = parameterMapping.getTypeHandler();
          if (typeHandler == null) {
            throw new ExecutorException("There was no TypeHandler found for parameter " + propertyName + " of statement " + mappedStatement.getId());
          }
//          typeHandler.setParameter(ps, i + 1, value, parameterMapping.getJdbcType()); // remove by chiknin
          
        // START: add by chiknin
          boolean isOracle = ps.getConnection().getMetaData().getDatabaseProductName().toLowerCase().indexOf("oracle") != -1 ; // TODO 分析PS获取数据源类型
          if (isOracle) {
        	  JdbcType jdbcType = parameterMapping.getJdbcType();
        	  if (value == null) {
        	      if (jdbcType == null) {
        	    	  Class javaType = parameterMapping.getJavaType();
        	    	  if (String.class.isAssignableFrom(javaType)) {
        	    		  jdbcType = JdbcType.VARCHAR;
        	    	  } else if (Number.class.isAssignableFrom(javaType)) {
        	    		  jdbcType = JdbcType.NUMERIC;
        	    	  } else if (java.util.Date.class.isAssignableFrom(javaType) || java.sql.Date.class.isAssignableFrom(javaType)) {
        	    		  jdbcType = JdbcType.DATE;
        	    	  } else if (Timestamp.class.isAssignableFrom(javaType)) {
        	    		  jdbcType = JdbcType.TIMESTAMP;
        	    	  } else {
        	    	  }
        	      }
        	  }
        	  typeHandler.setParameter(ps, i + 1, value, jdbcType);
          } else {
        	  typeHandler.setParameter(ps, i + 1, value, parameterMapping.getJdbcType());
          }
       // END: add by chiknin
        }
      }
    }
  }

}