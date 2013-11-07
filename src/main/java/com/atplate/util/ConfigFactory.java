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

import com.atplate.util.Const;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ConfigFactory {

    public static final String DEFAULT_ENV = "development";

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigFactory.class);

    private static ConfigFactory instance = new ConfigFactory();

    public static ConfigFactory getInstance() {
        return instance;
    }

    private ConfigFactory() {
    }

    protected String getDefaultEnvProperty() {
        return System.getProperty(Const.ENV_PROPERTY, ConfigFactory.DEFAULT_ENV);
    }

    protected InputStream getDefaultStream() {
        return ConfigFactory.class.getClassLoader().getResourceAsStream(Const.PROPERTIES_FILE);
    }

    public synchronized PropertiesConfiguration getConfigProperties() {
        String env = getDefaultEnvProperty();
        InputStream stream = getDefaultStream();
        return getConfigProperties(env, stream);
    }

    /**
     * This is the factory method for producing config properties object
     * each path has a single instance of config properties
     * @param env environment we want the properties for (ex. local, prod)
     * @param filePath the class path to the config file
     * @return
     */
    public synchronized PropertiesConfiguration getConfigProperties(String env, String filePath) {
        InputStream stream = null;
        if (StringUtils.isEmpty(filePath)) {
            stream = getDefaultStream();
        } else {
            try {
                stream = new FileInputStream(filePath);
            } catch (FileNotFoundException e) {
                LOGGER.info("could not find configuration {}", e);
                System.exit(0);
            }
        }
        return getConfigProperties(env, stream);
    }

    public synchronized PropertiesConfiguration getConfigProperties(String env, InputStream stream) {
        if (StringUtils.isEmpty(env)) {
            env = getDefaultEnvProperty();
        }

        PropertiesConfiguration config = new LocalizedPropertiesConfiguration(env);
        try {
            config.load(stream);
        } catch (ConfigurationException ce) {
            LOGGER.info("caught configuration exception {}", ce);
            System.exit(0);
        }

        LOGGER.info("atplate is running with the following env {}", env);

        return config;
    }
}