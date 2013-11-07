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

import java.util.*;
import org.apache.commons.configuration.PropertiesConfiguration;

public class TestLocalizedPropertiesConfiguration {
    private LocalizedPropertiesConfiguration config;

    @Test
    public void localEnv() throws Exception {
        loadConfig(Const.LOCAL);
        assertEquals("localjdbc", (String)config.getProperty("jdbc.connection.url"));
        assertEquals("localjdbc", config.getString("jdbc.connection.url"));
    }

    @Test
    public void devEnv() throws Exception {
        loadConfig(Const.DEVELOPMENT);
        assertEquals("developmentjdbc", (String)config.getProperty("jdbc.connection.url"));
        assertEquals("developmentjdbc", config.getString("jdbc.connection.url"));
    }

    @Test
    public void stageEnv() throws Exception {
        loadConfig("staging");
        assertEquals("stagingjdbc", (String)config.getProperty("jdbc.connection.url"));
        assertEquals("stagingjdbc", config.getString("jdbc.connection.url"));
    }

    @Test
    public void prodEnv() throws Exception {
        loadConfig(Const.PRODUCTION);
        assertEquals("productionjdbc", (String)config.getProperty("jdbc.connection.url"));
        assertEquals("productionjdbc", config.getString("jdbc.connection.url"));
    }

    @Test
    public void hasProdKeyInDev() throws Exception {
        loadConfig(Const.DEVELOPMENT);
        assertEquals("snow", (String)config.getProperty("atplate.snowflake.host"));
        assertEquals("snow", config.getString("atplate.snowflake.host"));
    }

    @Test
    public void hasNoEnv() throws Exception {
        loadConfig(Const.LOCAL);
        assertEquals("driver", (String)config.getProperty("jdbc.connection.driver"));
        assertEquals("driver", config.getString("jdbc.connection.driver"));
    }

    @Test
    public void badKey() throws Exception {
        loadConfig(Const.LOCAL);
        assertEquals(null, config.getProperty("no"));
        assertEquals(null, config.getString("no"));
    }

    @Test
    public void getIntDev() throws Exception {
        loadConfig(Const.DEVELOPMENT);
        assertEquals("6379", (String)config.getProperty("atplate.redis.port"));
        assertEquals("6379", config.getString("atplate.redis.port"));
        assertEquals(6379, config.getInt("atplate.redis.port"));
    }

    @Test
    public void getIntProd() throws Exception {
        loadConfig(Const.PRODUCTION);
        assertEquals("9999", (String)config.getProperty("atplate.redis.port"));
        assertEquals("9999", config.getString("atplate.redis.port"));
        assertEquals(9999, config.getInt("atplate.redis.port"));
    }

    @Test(expected=NoSuchElementException.class)
    public void getIntNoKey() throws Exception {
        loadConfig(Const.STAGING);
        config.getInt("goats");
    }

    @Test(expected=IllegalArgumentException.class)
    public void badEnv() throws Exception {
        config = new LocalizedPropertiesConfiguration("bbb");
    }

    private void loadConfig(String env) throws Exception {
        config = new LocalizedPropertiesConfiguration(env);
        config.load("src/test/resources/atplate.properties");
    }
}