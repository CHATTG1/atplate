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

package com.atplate.util;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import org.junit.*;

import com.atplate.util.SnowflakeUtilities;
import com.atplate.model.Snowflake;

public class TestSnowflakeUtilities {
    private SnowflakeUtilities snowflakeUtilities;
    private Snowflake snowflake;
    private long snowflakeId = 388378712812425216L;
    private long timestamp = 1381431675519L;

    @Before
    public void init() throws Exception {
        snowflakeUtilities = new SnowflakeUtilities();
    }

    @Test
    public void melt() {
        snowflake = snowflakeUtilities.melt(snowflakeId);
        assertEquals(snowflake.getTimestampMs(), timestamp);
    }

    @Test
    public void snowflake() {
        long snowflakeIdTest = snowflakeUtilities.snowflake(timestamp, 1, 1, 0);
        assertEquals(snowflakeIdTest, snowflakeId);
    }
}