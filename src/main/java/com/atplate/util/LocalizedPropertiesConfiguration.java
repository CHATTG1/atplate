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
 * This is a PropertiesConfiguration that supports the environment appended
 * to the end of a key.
 *
 * For example, with a "local" env, getProperty("a.key") will match:
 *  - a.key
 *  - a.key.local
 * 
 * If the PropertiesConfiguration has keys for both possible matches,
 * precedence is given to the localized key.
 */

package com.atplate.util;

import java.util.*;
import org.apache.commons.configuration.PropertiesConfiguration;

public class LocalizedPropertiesConfiguration extends PropertiesConfiguration {
    private static final List<String> envs = Arrays.asList(Const.LOCAL,Const.DEVELOPMENT,Const.STAGING,Const.PRODUCTION); 
    private String env;

    public LocalizedPropertiesConfiguration(String env) {
        super();

        if(!envs.contains(env)) {
            throw new IllegalArgumentException("unknown environment: " + env);
        }
        this.env = env;
    }

    @Override
    public Object getProperty(String key) {
        Object val = super.getProperty(localize(key));
        if(val == null) {
           val = super.getProperty(key); 
        }
        return val;
    }

    @Override
    public String getString(String key) {
        String val = super.getString(localize(key));
        if(val == null) {
            val = super.getString(key);
        }
        return val;
    }

    @Override
    public int getInt(String key) {
        int val = 0;
        try {
            val = super.getInt(localize(key));
        } catch (NoSuchElementException e) {
            val = super.getInt(key);
        }
        return val;
    }

    protected String localize(String key) {
        return key + "." + env;
    }

    public String getEnv() {
        return env;
    }
}