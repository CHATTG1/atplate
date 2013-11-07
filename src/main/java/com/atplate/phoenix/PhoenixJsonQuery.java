/*
 * Author: Patrick Reilly <preilly@php.net>
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.atplate.phoenix;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.atplate.util.JavaNameConversion;
import java.sql.*;

public class PhoenixJsonQuery extends PhoenixQuery<JsonArray> {

    /**
     * Convert the result set to a json array of row objects.
     *
     * @param set raw query result set
     * @return json array of row objects
     * @throws SQLException
     */
    @Override
    protected JsonArray processResultSet(ResultSet set) throws SQLException {
        ResultSetMetaData meta = set.getMetaData();
        int count = meta.getColumnCount();
        JsonArray rows = new JsonArray();
        while (set.next()) {
            JsonObject row = new JsonObject();
            for (int i = 0; i < count; i++) {
                String field = meta.getColumnName(i + 1);
                String value = set.getString(field);
                row.addProperty(JavaNameConversion.toJavaFieldName(field), value);
            }
            rows.add(row);
        }
        return rows;
    }
}
