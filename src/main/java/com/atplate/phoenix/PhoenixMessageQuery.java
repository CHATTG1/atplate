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

import com.atplate.model.AtPlateMessage;
import java.sql.*;
import java.util.ArrayList;

public class PhoenixMessageQuery extends PhoenixQuery<AtPlateMessage[]> {
    public static final String FIELD_ID           = "row_id";
    public static final String FIELD_SENDER_ID    = "sender_id";
    public static final String FIELD_RECIPIENT_ID = "recipient_id";
    public static final String FIELD_TIMESTAMP    = "m_timestamp";
    public static final String FIELD_TEXT         = "m_text";
    public static final String FIELD_VISIBILITY   = "visibility";
    public static final String FIELD_SENDER_IP    = "sender_ip";
    public static final String FIELD_IS_READ      = "is_read";
    public static final String FIELD_IS_DELETED   = "is_deleted";
    public static final String FIELD_JSON         = "json";

    /**
     * Convert the result set to an array of AtPlateMessage objects.
     *
     * @param set raw query result set
     * @return array of messages
     * @throws SQLException
     */
    @Override
    protected AtPlateMessage[] processResultSet(ResultSet set) throws SQLException {
        ArrayList<AtPlateMessage> messages = new ArrayList<AtPlateMessage>();
        while (set.next()) {
            AtPlateMessage message = new AtPlateMessage();
            message.setRowId(set.getLong(FIELD_ID));
            message.setSenderId(set.getLong(FIELD_SENDER_ID));
            message.setRecipientId(set.getLong(FIELD_RECIPIENT_ID));
            message.setMUnixTimestamp(set.getDate(FIELD_TIMESTAMP).getTime() * 1000L);
            message.setMText(set.getString(FIELD_TEXT));
            message.setVisibility(set.getInt(FIELD_VISIBILITY));
            message.setSenderIpString(set.getString(FIELD_SENDER_IP));
            message.setIsRead(set.getInt(FIELD_IS_READ));
            message.setIsDeleted(set.getInt(FIELD_IS_DELETED));
            message.setJson(set.getString(FIELD_JSON));
            messages.add(message);
        }
        return messages.toArray(new AtPlateMessage[messages.size()]);
    }
}
