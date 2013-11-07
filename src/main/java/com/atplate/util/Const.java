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

public class Const {
    public static final String PROPERTIES_FILE = "atplate.properties";
    public static final String LOCAL = "local";
    public static final String DEVELOPMENT = "development";
    public static final String STAGING = "staging";
    public static final String PRODUCTION = "production";
    public static final String CONNECTION_DRIVER = "jdbc.connection.driver";
    public static final String CONNECTION_URL = "jdbc.connection.url";
    public static final String SNOWFLAKE_HOST = "atplate.snowflake.host";
    public static final String SNOWFLAKE_PORT = "atplate.snowflake.port";
    public static final String REDIS_HOST = "atplate.redis.host";
    public static final String REDIS_PORT = "atplate.redis.port";
    public static final String ENV_PROPERTY = "atplate.env";
    public static final String DELIM_SPACE = " ";
    public static final String DELIM_COLON = ":";
}