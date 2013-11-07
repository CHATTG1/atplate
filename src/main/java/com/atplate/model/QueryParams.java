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

import java.util.Date;
import java.util.*;
import java.math.*;

public class QueryParams {

    protected final String className;
    protected Map<String, Object> params;
    private static final int MS_CONVERSION = 1000;

    public QueryParams() {
        this.className = this.getClass().getSimpleName();
        this.params = new HashMap<String, Object>();
    }

    private Long objectToLongConversion(Object obj) {
        Long ret = 0L;
        if(obj instanceof Double) {
            ret = Math.round((Double)obj);
        } else if (obj instanceof Long) {
            ret = (Long)obj;
        } else if (obj instanceof String) {
            ret = Long.parseLong((String)obj);
        }
        return ret;
    }

    public QueryParams(Long since, Long until, String order, Integer limit) {
        this.className = this.getClass().getSimpleName();
        this.params = new HashMap<String, Object>();
        this.params.put("since", since);
        this.params.put("until", until);
        this.params.put("order", order);
        this.params.put("limit", limit);
    }

    public void setSince(Long since) {
        this.params.put("since", since);
    }

    public Long getSince() {
        return objectToLongConversion(this.params.get("since"));
    }

    public Date getSinceAsDate() {
        Long since = objectToLongConversion(this.params.get("since"));
        return new Date(since * MS_CONVERSION);
    }

    public void setUntil(Long until) {
        this.params.put("until", until);
    }

    public Long getUntil() {
        return objectToLongConversion(this.params.get("until"));
    }

    public Date getUntilAsDate() {
        Long until = objectToLongConversion(this.params.get("until"));
        return new Date(until * MS_CONVERSION);
    }

    public void setOrder(String order) {
        this.params.put("order", order);
    }

    public String getOrder() {
        return this.params.get("order").toString();
    }

    public void setLimit(Integer limit) {
        this.params.put("limit", limit);
    }

    public Integer getLimit() {
        return (Integer)this.params.get("limit");
    }

    public String getClassName() {
        return this.className;
    }
}