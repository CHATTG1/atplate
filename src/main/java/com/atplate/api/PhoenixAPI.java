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

package com.atplate.api;

import com.atplate.model.AtPlateMessage;
import com.google.gson.*;
import java.sql.*;

import com.atplate.phoenix.PhoenixConnection;
import com.atplate.phoenix.PhoenixJsonQuery;
import com.atplate.phoenix.PhoenixMessageQuery;
import com.atplate.phoenix.PhoenixQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PhoenixAPI {

    private static final String MESSAGES_TABLE = "MESSAGES";
    private static final int IS_READ  = 1;
    private static final int NOT_READ = 0;
    private static final int NOT_DELETED = 0;
    private static final int SENDER_DELETED = 1;
    private static final int RECIPIENT_DELETED = 2;
    private static final int BOTH_DELETED = 3;

    private static final int SENDER_ID_COLUMN = 1;
    private static final int RECIPIENT_ID_COLUNN = 2;
    private static final int DATE_COLUMN = 3;
    private static final int ROW_COLUMN = 4;
    private static final int IS_READ_COLUMN = 5;
    private static final int IS_DELETED_COLUMN = 6;
    private static final int VISIBILITY_COLUMN = 7;
    private static final int SENDER_IP_COLUMN = 8;
    private static final int JSON_COLUMN = 9;
    private static final int M_TEXT = 10;

    private static final Logger LOGGER = LoggerFactory.getLogger(PhoenixAPI.class);

    private PhoenixJsonQuery phoenixJsonQuery = new PhoenixJsonQuery();

    private JsonObject query(String sql) throws SQLException {
        JsonArray messages = phoenixJsonQuery.executeQuery(sql);
        JsonObject json = new JsonObject();
        json.add("messages", messages);
        return json;
    }

    public AtPlateMessage getMessageById(long senderId, long recipientId, long msTimestamp) {
        try {
            String sql = String.format("SELECT * FROM " + MESSAGES_TABLE + " WHERE sender_id = ? AND recipient_id = ? AND m_timestamp = ?");
            PhoenixMessageQuery query = new PhoenixMessageQuery();
            AtPlateMessage[] messages = query.executeQuery(sql, senderId, recipientId, new Date(msTimestamp));
            return messages[0];
        } catch (IndexOutOfBoundsException e) {
            // nothing
        } catch (Exception e) {
            LOGGER.info("atplate had a error [" + e + "]");
        }
        return null;
    }

    public String getMessages(long userId) throws Exception {
        String qUserId = String.valueOf(userId);
        String sql = "SELECT * FROM " + MESSAGES_TABLE + " WHERE SENDER_ID = " + qUserId;
        return query(sql).toString();
    }

    public String getMessagesOutbox(long userId) throws Exception {
        String qUserId = String.valueOf(userId);
        String sql = "SELECT * FROM " + MESSAGES_TABLE + " WHERE SENDER_ID = " + qUserId;
        return query(sql).toString();
    }

    public String getMessageCount(long userId) throws Exception {
        String qUserId = String.valueOf(userId);
        String sql = "SELECT COUNT(*) AS MESSAGE_COUNT FROM " + MESSAGES_TABLE + " WHERE RECIPIENT_ID = " + qUserId;
        return query(sql).toString();
    }

    public String getUnreadMessages(long userId) throws Exception {
        String qUserId = String.valueOf(userId);
        String sql = "SELECT * FROM " + MESSAGES_TABLE + " WHERE RECIPIENT_ID = " + qUserId + " AND VISIBILITY = 1";
        return query(sql).toString();
    }

    public String getUnreadMessageCount(long userId) throws Exception {
        String qUserId = String.valueOf(userId);
        String sql = "SELECT COUNT(*) AS MESSAGE_COUNT FROM " + MESSAGES_TABLE + " WHERE RECIPIENT_ID = " + qUserId + " AND VISIBILITY = 1";
        return query(sql).toString();
    }

    public String getFriends(long userId) throws Exception {
        String qUserId = String.valueOf(userId);
        String sql = "SELECT DISTINCT(SENDER_ID) FROM " + MESSAGES_TABLE + " WHERE RECIPIENT_ID = " + qUserId;
        return query(sql).toString();
    }

    public String getFriendCount(long userId) throws Exception {
        String qUserId = String.valueOf(userId);
        String sql = "SELECT COUNT(DISTINCT(SENDER_ID)) AS FRIEND_COUNT FROM " + MESSAGES_TABLE + " WHERE RECIPIENT_ID = " + qUserId;
        return query(sql).toString();
    }

    public String setMessage(final AtPlateMessage atPlateMessage) throws Exception {
        PhoenixConnection<Void> upsert = new PhoenixConnection<Void>() {
            @Override
            protected Void withConnection(Connection conn) throws SQLException {
                conn.setAutoCommit(false);
                String upsertStmt = "UPSERT INTO " + MESSAGES_TABLE + " VALUES (?,?,?,?,?,?,?,?,?,?)";
                PreparedStatement stmt = conn.prepareStatement(upsertStmt);
                stmt.setLong(SENDER_ID_COLUMN, atPlateMessage.getSenderId());
                stmt.setLong(RECIPIENT_ID_COLUNN, atPlateMessage.getRecipientId());
                stmt.setDate(DATE_COLUMN, atPlateMessage.getDate());
                stmt.setLong(ROW_COLUMN, atPlateMessage.getRowId());
                stmt.setInt(IS_READ_COLUMN, atPlateMessage.getIsRead());
                stmt.setInt(IS_DELETED_COLUMN, atPlateMessage.getIsDeleted());
                stmt.setInt(VISIBILITY_COLUMN, atPlateMessage.getVisibility());
                stmt.setString(SENDER_IP_COLUMN, atPlateMessage.getSenderIpString());
                stmt.setString(JSON_COLUMN, atPlateMessage.getJson());
                stmt.setString(M_TEXT, atPlateMessage.getMText());
                stmt.execute();
                stmt.executeUpdate();
                conn.commit();

                LOGGER.info("atplate inserted record [" + atPlateMessage.getRowId() + "] into the table [" + MESSAGES_TABLE + "]");
                return null;
            }
        };
        try {
            upsert.execute();
        } catch(Exception e) {
            LOGGER.info("atplate had an error [" + e + "]");
        }
        return "";
    }

    /**
     * Mark messages recently read by the recipient and sent from the sender as read.
     * By default only messages up until the last read messages will be marked.
     * To override, use the optional `optionLastReadRowId`.
     *
     * @param senderId
     * @param recipientId reader of the message
     * @param msTimestamp timestamp of the message read
     * @param optionLastTimestamp override the lower bound of messages marked as read
     * @throws SQLException
     */
    public void markConversationAsRead(final long senderId, final long recipientId, final long msTimestamp, Long optionLastTimestamp) throws SQLException {
        final Date lastReadDate = optionLastTimestamp == null ? getLastReadDate(senderId, recipientId, msTimestamp) : new Date(optionLastTimestamp);
        PhoenixConnection<Void> update = new PhoenixConnection<Void>() {
            @Override
            protected Void withConnection(Connection connection) throws SQLException {
                connection.setAutoCommit(true);
                String sql = "UPSERT INTO " + MESSAGES_TABLE + "(sender_id, recipient_id, m_timestamp, row_id, is_read)"
                           + " SELECT sender_id, recipient_id, m_timestamp, row_id, ? FROM " + MESSAGES_TABLE
                           + " WHERE sender_id = ? and recipient_id = ? and m_timestamp <= ? and m_timestamp > ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setInt (1, IS_READ);
                statement.setLong(2, senderId);
                statement.setLong(3, recipientId);
                statement.setDate(4, new Date(msTimestamp));
                statement.setDate(5, lastReadDate);
                statement.executeUpdate();
                return null;
            }
        };
        update.execute();
    }

    /**
     * Searches for the last message a recipient has read from a particular sender.
     *
     * @param senderId
     * @param recipientId reader of the message
     * @param msTimestamp
     * @return rowId of the last read message
     * @throws SQLException
     */
    private Date getLastReadDate(long senderId, long recipientId, long msTimestamp) throws SQLException {
        PhoenixQuery<Date> query = new PhoenixQuery<Date>() {
            @Override
            protected Date processResultSet(ResultSet set) throws SQLException {
                if (set.next()) {
                    Date lastReadDate = set.getDate("m_timestamp");
                    if (lastReadDate != null) {
                        return lastReadDate;
                    }
                }
                return new Date(0);
            }
        };
        String sql = "SELECT MAX(m_timestamp) AS m_timestamp FROM " + MESSAGES_TABLE + " WHERE sender_id = ? AND recipient_id = ? AND m_timestamp < ? AND is_read = ?";
        return query.executeQuery(sql, senderId, recipientId, new Date(msTimestamp), IS_READ);
    }

    /**
     * Mark a specific message thread as deleted by the sender, recipient, or both.
     *
     * @param isSender whether the user deleting the thread is the sender of the message corresponding to the deletedRowId
     * @param deletedRowId message deleted
     * @throws SQLException
     */
    public void markThreadAsDeleted(final long senderId, final long recipientId, final long msTimestamp) throws SQLException {
        PhoenixConnection<Void> update = new PhoenixConnection<Void>() {

            @Override
            protected Void withConnection(Connection connection) throws SQLException {
                connection.setAutoCommit(true);

                String sql = "UPSERT INTO " + MESSAGES_TABLE + "(sender_id, recipient_id, m_timestamp, row_id, is_deleted)"
                        + " SELECT sender_id, recipient_id, m_timestamp, row_id,"
                        + " (CASE sender_id"
                            + " WHEN ? THEN (CASE is_deleted WHEN ? THEN ? WHEN ? THEN ? ELSE is_deleted END)"
                            +        " ELSE (CASE is_deleted WHEN ? THEN ? WHEN ? THEN ? ELSE is_deleted END)"
                            + " END)"
                        + " FROM " + MESSAGES_TABLE
                        + " WHERE sender_id = ? AND recipient_id = ? AND m_timestamp = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setLong(1, senderId);

                statement.setInt(2, NOT_DELETED);
                statement.setInt(3, SENDER_DELETED);
                statement.setInt(4, RECIPIENT_DELETED);
                statement.setInt(5, BOTH_DELETED);

                statement.setInt(6, NOT_DELETED);
                statement.setInt(7, RECIPIENT_DELETED);
                statement.setInt(8, SENDER_DELETED);
                statement.setInt(9, BOTH_DELETED);

                statement.setLong(10, senderId);
                statement.setLong(11, recipientId);
                statement.setDate(12, new Date(msTimestamp));
                statement.executeUpdate();
                return null;
            }
        };
        update.execute();
    }
}