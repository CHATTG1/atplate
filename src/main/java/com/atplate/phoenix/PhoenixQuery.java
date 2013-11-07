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

import com.atplate.phoenix.PhoenixConnection;

import java.sql.*;

public abstract class PhoenixQuery<T> {

    /**
     * Open a database connection,
     * execute the statement,
     * delegate processing of the result to `processResultSet`,
     * and close the connection.
     *
     * @param sql
     * @param args wildcard sql replacements, '?'
     * @return custom return type
     * @throws SQLException
     */
    public T executeQuery(final String sql, final Object... args) throws SQLException {
        PhoenixConnection<T> connection = new PhoenixConnection<T>() {
            @Override
            protected T withConnection(Connection connection) throws SQLException {
                PreparedStatement statement = connection.prepareStatement(sql);
                int i = 0;
                for (Object arg: args) {
                    i++;
                    statement.setObject(i, arg);
                }
                ResultSet set = statement.executeQuery();
                return processResultSet(set);
            }
        };
        return connection.execute();
    }

    /**
     * Once the query is run,
     * the result will be processed here in a subclass.
     *
     * @param set raw query result, with the connection open
     * @return custom result type
     * @throws SQLException
     */
    protected abstract T processResultSet(ResultSet set) throws SQLException;
}
