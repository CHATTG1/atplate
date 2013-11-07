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

import org.apache.commons.lang.WordUtils;

public class JavaNameConversion {

    public static String toJavaFieldName(String name) {
        // "MY_COLUMN" -> "MY COLUMN" -> "My Column" -> "MyColumn" -> "myColumn"
        String name0 = name.replace("_", " ");
        name0 = WordUtils.capitalizeFully(name0);
        name0 = name0.replace(" ", "");
        name0 = WordUtils.uncapitalize(name0);
        return name0;
    }

    public static String toJavaClassName(String name) {
        // "MY_TABLE" -> "MY TABLE" -> "My Table" -> "MyTable"
        String name0 = name.replace("_", " ");
        name0 = WordUtils.capitalizeFully(name0);
        name0 = name0.replace(" ", "");
        return name0;
    }
}