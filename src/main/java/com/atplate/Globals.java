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

package com.atplate;

import java.sql.Connection;
import java.sql.DriverManager;

import com.atplate.api.PhoenixAPI;
import com.atplate.util.ConfigFactory;
import com.atplate.util.Const;

import org.apache.commons.configuration.PropertiesConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Globals {
    private static Globals instance = null;
    private PropertiesConfiguration config = null;
    private PhoenixAPI phoenixApi = new PhoenixAPI();
    private static final Logger LOGGER = LoggerFactory.getLogger(Globals.class);

    public static Globals getInstance() {
        ConfigFactory factory = ConfigFactory.getInstance();
        PropertiesConfiguration config = factory.getConfigProperties();
        return Globals.getInstance(config);
    }

    public static Globals getInstance(PropertiesConfiguration config) {
        if (instance == null) {
            instance = new Globals(config);
        }
        return instance;
    }

    private Globals(PropertiesConfiguration config) {
        this.config = config;
    }

    public void panic(Class<?> c, Exception e) {
        LOGGER.debug(c.getCanonicalName() + ": " + e);
        System.exit(0);
    }

    public PhoenixAPI getPhoenixAPI() {
        return phoenixApi;
    }

    public PropertiesConfiguration getConfig() {
        return config;
    }

    public Connection getConnection() {
        Connection con;
        try {
            // register driver
            Class.forName("com.salesforce.phoenix.jdbc.PhoenixDriver");
            con = DriverManager.getConnection(config.getString(Const.CONNECTION_URL));
            return con;
        } catch (Exception e) {
            LOGGER.info("atplate had a connection error [" + e + "]");
            return null;
        }
    }
}