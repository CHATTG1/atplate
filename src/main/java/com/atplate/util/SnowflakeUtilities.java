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

import com.atplate.model.Snowflake;

public class SnowflakeUtilities {

    public static final long TWEPOCH = 1288834974657L;
    private static final long WORKER_ID_BITS = 5L;
    private static final long DATA_CENTER_ID_BITS = 5L;
    private static final long MAX_WORKER_ID = 1 << WORKER_ID_BITS;
    private static final long MAX_DATA_CENTER_ID = 1 << DATA_CENTER_ID_BITS;
    private static final long SEQUENCE_BITS = 12L;
    private static final long MAX_SEQUENCE_ID = 1 << SEQUENCE_BITS;

    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    private static final long DATA_CENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS
            + DATA_CENTER_ID_BITS;
    private static final long SEQUENCE_MASK = -1L ^ (-1L << SEQUENCE_BITS);

    public static Snowflake melt(long snowflakeId) {
        Long sequenceId = snowflakeId & (MAX_SEQUENCE_ID - 1);
        Long workerId = (snowflakeId >> SEQUENCE_BITS) & (MAX_WORKER_ID - 1);
        Long datacenterId = (snowflakeId >> SEQUENCE_BITS >> WORKER_ID_BITS) & (MAX_DATA_CENTER_ID - 1);
        Long timestampMs = snowflakeId >> SEQUENCE_BITS >> WORKER_ID_BITS >> DATA_CENTER_ID_BITS;
        timestampMs += TWEPOCH;

        Snowflake snowflake = new Snowflake();
        snowflake.setId(snowflakeId);
        snowflake.setSequenceId(Integer.valueOf(sequenceId.intValue()));
        snowflake.setWorkerId(Integer.valueOf(workerId.intValue()));
        snowflake.setDatacenterId(Integer.valueOf(datacenterId.intValue()));
        snowflake.setTimestampMs(timestampMs);

        return snowflake;
    }

    /*
     * generate a twitter-snowflake id, based on 
     * https://github.com/twitter/snowflake/blob/master/src/main/scala/com/twitter/service/snowflake/IdWorker.scala
     * @param timestamp_ms time since UNIX epoch in milliseconds
     */
    public static Long snowflake(long timestamp, long datacenterId, long workerId, long sequenceId) {
        long curSequence = 0L;

        final long id = ((timestamp - TWEPOCH) << TIMESTAMP_LEFT_SHIFT)
                | (datacenterId << DATA_CENTER_ID_SHIFT)
                | (workerId << WORKER_ID_SHIFT) | curSequence;

        return id;
    }
}