/*
 * Copyright (c) 2023, enix223@163.com
 * All rights reserved.
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * </p>
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * </p>
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * </p>
 */

package com.enixyu.djolar.mybatis.dialect;

import com.enixyu.djolar.mybatis.plugin.DjolarProperty;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class DjolarAutoDialect {

  private static final Map<String, Class<? extends Dialect>> dialects = new HashMap<>();

  static {
    dialects.put("mysql", MySQLDialect.class);
    dialects.put("postgresql", PostgreSQLDialect.class);
  }

  @SuppressWarnings("unchecked")
  public void initAutoDialect(Properties properties) {
    String dialectClassName = properties.getProperty(DjolarProperty.KEY_DIALECT);
    if (dialectClassName != null && !dialectClassName.isEmpty()) {
      String[] tokens = dialectClassName.split("=");
      if (tokens.length != 2) {
        throw new IllegalArgumentException(
          "'dialect' config invalid, dialect value should be sth. like: mysql=com.enixyu.MySQLDialect");
      }
      try {
        Class<? extends Dialect> dialectCls = (Class<? extends Dialect>) Class.forName(tokens[1]);
        dialects.put(tokens[0].toLowerCase(), dialectCls);
      } catch (ClassNotFoundException e) {
        throw new IllegalArgumentException(
          "dialect class not found, please check your plugin config");
      } catch (ClassCastException e) {
        throw new IllegalArgumentException(
          "dialect class does not implement 'Dialect' interface");
      }
    }
  }

  public Class<? extends Dialect> resolveDialect(String dialectAlias) {
    Class<? extends Dialect> dialectCls = dialects.get(dialectAlias.toLowerCase());
    if (dialectCls == null) {
      throw new IllegalArgumentException(
        String.format("dialect %s is not supported", dialectAlias));
    }
    return dialectCls;
  }
}
