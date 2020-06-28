/*
 * Licensed to ObjectStyle LLC under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ObjectStyle LLC licenses
 * this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.bootique.junit5.handler;

import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * @since 2.0
 */
public class GlobalCallbacks implements ExtensionContext.Store.CloseableResource {

    private ExtensionContext closingContext;
    private final Map<Field, Callback> callbacks;

    public GlobalCallbacks(ExtensionContext closingContext) {
        callbacks = new ConcurrentHashMap<>();
        this.closingContext = Objects.requireNonNull(closingContext);
    }

    public Callback computeIfAbsent(Field f, Function<Field, Callback> callbackCalc) {
        return callbacks.computeIfAbsent(f, callbackCalc);
    }

    @Override
    public void close() throws Throwable {
        for (Callback c : callbacks.values()) {
            if (c.getAfterAll() != null) {
                c.getAfterAll().afterAll(closingContext);
            }
        }
    }
}
