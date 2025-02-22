package com.enixyu.djolar.mybatis.dialect;

import com.enixyu.djolar.mybatis.exceptions.DjolarParserException;
import com.enixyu.djolar.mybatis.parser.Clause;
import com.enixyu.djolar.mybatis.parser.Op;
import com.enixyu.djolar.mybatis.parser.QueryMapping;
import com.enixyu.djolar.mybatis.parser.QueryMapping.Item;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;

public abstract class BaseDialect implements Dialect {

  protected abstract String getFieldQuoteSymbol();

  protected String getColumnName(Clause clause) {
    String quote = clause.isNeedEscape() ? getFieldQuoteSymbol() : "";
    return clause.getTableName() == null
      ? String.format("%s%s%s", quote, clause.getColumnName(), quote)
      : String.format("%s%s%s.%s%s%s", quote, clause.getTableName(), quote, quote,
        clause.getColumnName(), quote);
  }

  /**
   * Convert string value into target field type
   *
   * @param field    query mapping field
   * @param operator djolar operator
   * @param value    source string value
   * @return converted target value for given field type
   */
  protected Object parseValue(QueryMapping.Item field, Op operator, String value)
    throws NumberFormatException, DjolarParserException {
    if (field.getFieldType().isPrimitive()) {
      switch (field.getFieldType().getName()) {
        case "int":
          return Integer.parseInt(value);
        case "boolean":
          return Boolean.parseBoolean(value);
        case "long":
          return Long.parseLong(value);
        case "float":
          return Float.parseFloat(value);
        case "double":
          return Double.parseDouble(value);
        case "short":
          return Short.parseShort(value);
        default:
          // unsupported primitive type
          throw new DjolarParserException("unsupported primitive type");
      }
    } else if (field.getFieldType().equals(String.class)) {
      if (operator == Op.CONTAIN || operator == Op.IGNORE_CASE_CONTAIN) {
        return String.format("%%%s%%", value);
      } else if (operator == Op.STARTS_WITH) {
        return String.format("%s%%", value);
      } else if (operator == Op.ENDS_WITH) {
        return String.format("%%%s", value);
      } else {
        return value;
      }
    } else if (field.getFieldType().equals(Integer.class)) {
      return Integer.parseInt(value);
    } else if (field.getFieldType().equals(Boolean.class)) {
      return Boolean.parseBoolean(value);
    } else if (field.getFieldType().equals(Long.class)) {
      return Long.parseLong(value);
    } else if (field.getFieldType().equals(Float.class)) {
      return Float.parseFloat(value);
    } else if (field.getFieldType().equals(Double.class)) {
      return Double.parseDouble(value);
    } else if (field.getFieldType().equals(Short.class)) {
      return Short.parseShort(value);
    } else {
      return null;
    }
  }

  // Single value case
  protected Object parseSingleValueField(MappedStatement ms, List<ParameterMapping> parameterMappings,
    Map<String, Object> parameterObject, int fieldIndex, Op op, Item field, String value) {
    Object parsedValue = parseValue(field, op, value);
    String property = String.format("%s_%s_%d", field.getTableName(), field.getFieldName(),
      fieldIndex);
    ParameterMapping parameterMapping = new ParameterMapping.Builder(
      ms.getConfiguration(),
      property,
      field.getFieldType()).build();
    parameterMappings.add(parameterMapping);
    parameterObject.put(property, parsedValue);
    return parsedValue;
  }

  protected Object parseListValueField(MappedStatement ms, List<ParameterMapping> parameterMappings,
    Map<String, Object> parameterObject, Map<String, Object> additionalParameters,
    int fieldIndex, Op op, Item field, String value) {
    // For IN or NOT IN operator
    // We need to split the value into tokens and parse to target field type
    String[] tokens = value.split(",");
    List<Object> parsedValue = new ArrayList<>(tokens.length);
    for (int i = 0; i < tokens.length; i++) {
      String token = tokens[i];
      Object itemParsedValue = parseValue(field, op, token);
      parsedValue.add(itemParsedValue);
      String property = String.format("%s_%s_%d_%d", field.getTableName(), field.getFieldName(),
        fieldIndex, i);
      ParameterMapping parameterMapping = new ParameterMapping.Builder(
        ms.getConfiguration(),
        property,
        field.getFieldType()).build();
      parameterMappings.add(parameterMapping);
      additionalParameters.put(property, itemParsedValue);
    }
    String property = String.format("%s_%s_%d", field.getTableName(), field.getFieldName(),
      fieldIndex);
    parameterObject.put(property, parsedValue);
    return parsedValue;
  }

  protected boolean isBlank(String value) {
    if (value == null) {
      return true;
    }
    return value.isEmpty();
  }
}
