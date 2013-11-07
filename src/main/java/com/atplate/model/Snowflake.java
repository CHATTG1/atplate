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

public class Snowflake {

    private long id;
    private Integer sequenceId;
    private Integer workerId;
    private Integer datacenterId;
    private long timestampMs;
    private Date timestamp;

    public Snowflake() {
    }

    public Snowflake(long id, Integer sequenceId, Integer workerId, Integer datacenterId, long timestampMs) {
        this.id = id;
        this.sequenceId = sequenceId;
        this.workerId = workerId;
        this.datacenterId = datacenterId;
        this.timestampMs = timestampMs;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Integer getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(Integer sequenceId) {
        this.sequenceId = sequenceId;
    }

    public Integer getWorkerId() {
        return workerId;
    }

    public void setWorkerId(Integer workerId) {
        this.workerId = workerId;
    }

    public Integer getDatacenterId() {
        return datacenterId;
    }

    public void setDatacenterId(Integer datacenterId) {
        this.datacenterId = datacenterId;
    }

    public long getTimestampMs() {
        return timestampMs;
    }

    public void setTimestampMs(long timestampMs) {
        this.timestampMs = timestampMs;
        this.timestamp = new Date(timestampMs);
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}