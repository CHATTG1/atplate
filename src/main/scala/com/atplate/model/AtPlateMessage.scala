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

import com.google.gson.GsonBuilder
import java.sql.Date

object AtPlateMessage {
  val gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create()

  def fromJson(json: String): AtPlateMessage = {
    gson.fromJson(json, classOf[AtPlateMessage])
  }
}

class AtPlateMessage extends Cloneable {
  @reflect.BeanProperty var rowId : Long = 0
  @reflect.BeanProperty var senderId : Long = 0
  @reflect.BeanProperty var recipientId : Long = 0
  @reflect.BeanProperty var mUnixTimestamp : Long = 0
  @reflect.BeanProperty var mText : String = ""
  @reflect.BeanProperty var visibility : Int = 0
  @reflect.BeanProperty var senderIp : Array[String] = Array()
  @reflect.BeanProperty var isRead : Int = 0
  @reflect.BeanProperty var isDeleted : Int = 0
  @reflect.BeanProperty var json : String = ""

  override def clone = this

  def getDate: Date = new Date(mUnixTimestamp / 1000L)

  def setSenderIpString(ip: String) = {
    senderIp = ip match {
      case _: String => ip.split(",")
      case _ => null
    }
  }

  def getSenderIpString: String = senderIp match {
    case _: Array[String] => senderIp.mkString(",")
    case _ => null
  }

  def toJson: String = AtPlateMessage.gson.toJson(this)
}