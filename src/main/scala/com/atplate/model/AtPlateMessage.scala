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

package com.atplate.model

import java.sql.Date

case class TaggedMessage (
  @reflect.BeanProperty var rowId : Long,
  @reflect.BeanProperty var senderId : Long,
  @reflect.BeanProperty var recipientId : Long,
  @reflect.BeanProperty var mUnixTimestamp : Long,
  @reflect.BeanProperty var mTimestamp : Date,
  @reflect.BeanProperty var mText : String,
  @reflect.BeanProperty var visibility : Integer,
  @reflect.BeanProperty var senderIp : Array[String],
  @reflect.BeanProperty var isRead : Integer,
  @reflect.BeanProperty var isDeleted : Integer,
  @reflect.BeanProperty var mType : Integer,
  @reflect.BeanProperty var json : String)
