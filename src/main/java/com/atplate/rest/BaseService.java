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

package com.atplate.rest;

import com.atplate.Globals;
import org.apache.commons.configuration.PropertiesConfiguration;
import com.atplate.util.Const;
import com.atplate.api.PhoenixAPI;
import redis.clients.jedis.Jedis;
import com.google.gson.Gson;
import com.atplate.model.RegisteredClient;
import com.atplate.model.QueryParams;

public abstract class BaseService {
    // JAX-RS injected fields
    protected Long since;
    protected Long until;
    protected String order;
    protected Integer limit;
    protected String accessToken;
    protected Globals globals;
    protected PropertiesConfiguration config;

    protected PhoenixAPI phoenixApi;
    protected Jedis jedis;
    protected Gson gson;
    protected QueryParams queryParams;

    public BaseService (Long since, Long until, String order, Integer limit, String accessToken) {
        this.since = since;
        this.until = until;
        this.order = order;
        this.limit = limit;
        this.accessToken = accessToken;
        this.globals = Globals.getInstance();
        this.config = globals.getConfig();
        this.jedis = new Jedis(config.getString(Const.REDIS_HOST));
        this.phoenixApi = globals.getPhoenixAPI();
        this.gson = new Gson();
        this.queryParams = new QueryParams(since, until, order, limit);
    }

    public boolean isValidClientId(Long clientId, String accessToken) {
        String cacheKey = "register_client_" + clientId;
        String json = jedis.get(cacheKey);
        if (json != null && !json.isEmpty()) {
            RegisteredClient registeredClient = gson.fromJson(json, RegisteredClient.class);
            if (registeredClient.getAccessToken().equals(accessToken)) {
                return true;
            }
        }
        return false;
    }
}