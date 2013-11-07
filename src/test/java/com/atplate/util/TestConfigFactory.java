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

import com.atplate.util.ConfigFactory;
import com.atplate.util.LocalizedPropertiesConfiguration;

public class TestConfigFactory {
    private ConfigFactory factory;
    private LocalizedPropertiesConfiguration config;
    private static final String TEST_FILE = "src/test/resources/atplate.properties";

    @Before
    public void setUp() {
        factory = ConfigFactory.getInstance();
    }

    @Test
    public void emptyDefaults() {
        config = (LocalizedPropertiesConfiguration) factory.getConfigProperties();
        assertEquals(ConfigFactory.DEFAULT_ENV, config.getEnv());
    }

    @Test(expected=IllegalArgumentException.class)
    public void badEnv() {
        config = (LocalizedPropertiesConfiguration) factory.getConfigProperties("foo", "");
        assertEquals(ConfigFactory.DEFAULT_ENV, config.getEnv());
    }

    public void dev() {
        factory.getConfigProperties(Const.DEVELOPMENT, "src/test/resources/atplate.properties");
        assertEquals(Const.DEVELOPMENT, config.getEnv());
    }

    public void stage() {
        factory.getConfigProperties(Const.STAGING, "");
        assertEquals(Const.STAGING, config.getEnv());
    }

    public void prod() {
        factory.getConfigProperties(Const.PRODUCTION, "");
        assertEquals(Const.PRODUCTION, config.getEnv());
    }

    public void propertiesFile() {
        factory.getConfigProperties(ConfigFactory.DEFAULT_ENV, TEST_FILE);
        assertEquals(TEST_FILE, config.getFileName());
        assertEquals("driver", config.getString("jdbc.connection.driver"));
    }
}