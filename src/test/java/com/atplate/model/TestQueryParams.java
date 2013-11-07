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

package com.atplate.model;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import org.junit.*;

import java.text.*;
import java.util.*;

import com.atplate.model.QueryParams;

public class TestQueryParams {
    private QueryParams queryParams;

    @Before
    public void init() {
        queryParams = new QueryParams();
    }

    @Test
    public void validateSince() {
        Long unixtimestamp = 1381858814L;
        queryParams.setSince(unixtimestamp);
        Date date = null;
        try {
            String testDate = "Tue Oct 15 10:40:14 PDT 2013";
            date = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH).parse(testDate);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(queryParams.getSinceAsDate(), date);
    }

    @Test
    public void validateUntil() {
        Long unixtimestamp = 1381858814L;
        queryParams.setUntil(unixtimestamp);
        Date date = null;
        try {
            String testDate = "Tue Oct 15 10:40:14 PDT 2013";
            date = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH).parse(testDate);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(queryParams.getUntilAsDate(), date);
    }

    @Test
    public void validateOrder() {
        String order = "SENDER_ID";
        queryParams.setOrder(order);
        assertEquals(queryParams.getOrder(), order);
    }

    @Test
    public void validateLimit() {
        Integer limit = 1000;
        queryParams.setLimit(limit);
        assertEquals(queryParams.getLimit(), limit);
    }

    @Test
    public void validateClassName() {
        assertEquals(queryParams.getClassName(), "QueryParams");
    }
}