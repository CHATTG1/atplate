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

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.QueryParam;
import javax.ws.rs.DefaultValue;

@Path("/api/healthcheck")
@Produces(MediaType.APPLICATION_JSON)
public class HealthCheckService extends BaseService {

    public HealthCheckService(
        @DefaultValue("0") @QueryParam("since") Long since,
        @DefaultValue("0") @QueryParam("until") Long until,
        @DefaultValue("0") @QueryParam("order") String order,
        @DefaultValue("0") @QueryParam("limit") Integer limit,
        @DefaultValue("0") @QueryParam("access_token") String accessToken
    ) {
        super(since, until, order, limit, accessToken);
    }

    @Path("")
    @GET
    public Response getHealthCheck() {
        return Response.ok("<status>success<status>").build();
    }
}
