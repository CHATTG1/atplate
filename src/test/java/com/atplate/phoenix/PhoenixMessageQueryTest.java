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
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import java.sql.*;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(DataProviderRunner.class)
public class PhoenixMessageQueryTest {

    private static class ManualPhoenixMessageQuery extends PhoenixMessageQuery {
        public AtPlateMessage[] testQuery(ResultSet set) throws SQLException {
            return processResultSet(set);
        }
    }

    private ManualPhoenixMessageQuery query = new ManualPhoenixMessageQuery();

    @DataProvider
    public static Object[][] messageProvider() {
        AtPlateMessage message1 = new AtPlateMessage();
        message1.setRowId(1234);
        message1.setSenderId(96);
        message1.setRecipientId(69);
        message1.setMUnixTimestamp(123456789000L);
        message1.setMText("It's your birthday!");
        message1.setVisibility(0);
        message1.setSenderIp(new String[]{"127.0.0.1", "127.0.0.2"});
        message1.setIsRead(1);
        message1.setIsDeleted(2);
        message1.setJson("{\"foo\":\"bar\"}");

        AtPlateMessage message2 = message1.clone();
        message2.setSenderIpString(null);

        return new Object[][] {
                { message1 }
        };
    }

    @Test
    @UseDataProvider("messageProvider")
    public void test_processResultSet(AtPlateMessage expect) throws Exception {
        ResultSet set = mock(ResultSet.class);
        when(set.next()).thenReturn(true).thenReturn(false);
        when(set.getLong(PhoenixMessageQuery.FIELD_ID)).thenReturn(expect.getRowId());
        when(set.getLong(PhoenixMessageQuery.FIELD_SENDER_ID)).thenReturn(expect.getSenderId());
        when(set.getLong(PhoenixMessageQuery.FIELD_RECIPIENT_ID)).thenReturn(expect.getRecipientId());
        when(set.getDate(PhoenixMessageQuery.FIELD_TIMESTAMP)).thenReturn(expect.getDate());
        when(set.getString(PhoenixMessageQuery.FIELD_TEXT)).thenReturn(expect.getMText());
        when(set.getInt(PhoenixMessageQuery.FIELD_VISIBILITY)).thenReturn(expect.getVisibility());
        when(set.getString(PhoenixMessageQuery.FIELD_SENDER_IP)).thenReturn(expect.getSenderIpString());
        when(set.getInt(PhoenixMessageQuery.FIELD_IS_READ)).thenReturn(expect.getIsRead());
        when(set.getInt(PhoenixMessageQuery.FIELD_IS_DELETED)).thenReturn(expect.getIsDeleted());
        when(set.getString(PhoenixMessageQuery.FIELD_JSON)).thenReturn(expect.getJson());

        AtPlateMessage[] messages = query.testQuery(set);
        assertEquals(1, messages.length);
        assertEquals(expect.getRowId(), messages[0].getRowId());
        assertEquals(expect.getSenderId(), messages[0].getSenderId());
        assertEquals(expect.getRecipientId(), messages[0].getRecipientId());
        assertEquals(expect.getMUnixTimestamp(), messages[0].getMUnixTimestamp());
        assertEquals(expect.getMText(), messages[0].getMText());
        assertEquals(expect.getVisibility(), messages[0].getVisibility());
        if (expect.getSenderIp() == null) {
            assertNull(messages[0].getSenderIp());
        } else {
            assertArrayEquals(expect.getSenderIp(), messages[0].getSenderIp());
        }
        assertEquals(expect.getIsRead(), messages[0].getIsRead());
        assertEquals(expect.getIsDeleted(), messages[0].getIsDeleted());
        assertEquals(expect.getJson(), messages[0].getJson());
    }
}
