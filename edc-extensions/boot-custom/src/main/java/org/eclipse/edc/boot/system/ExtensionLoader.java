/*
 * Copyright (c) 2024 Cofinity-X
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.eclipse.edc.boot.system;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import org.eclipse.edc.boot.monitor.MultiplexingMonitor;
import org.eclipse.edc.boot.system.injection.InjectionContainer;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.monitor.ConsoleMonitor;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.result.Result;
import org.eclipse.edc.spi.system.MonitorExtension;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.telemetry.Telemetry;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExtensionLoader {

    private final ServiceLocator serviceLocator;

    public ExtensionLoader(ServiceLocator serviceLocator) {
        this.serviceLocator = serviceLocator;
    }

    public static @NotNull Monitor loadMonitor(String... programArgs) {
        var loader = ServiceLoader.load(MonitorExtension.class);
        return loadMonitor(loader.stream().map(ServiceLoader.Provider::get).collect(Collectors.toList()), programArgs);
    }

    static @NotNull Monitor loadMonitor(List<MonitorExtension> availableMonitors, String... programArgs) {
        if (availableMonitors.isEmpty()) {
            var parseResult = parseLogLevel(programArgs);
            if (parseResult.failed()) {
                throw new EdcException(parseResult.getFailureDetail());
            }
            return new ConsoleMonitor((ConsoleMonitor.Level) parseResult.getContent(), !Set.of(programArgs).contains(ConsoleMonitor.COLOR_PROG_ARG));
        }

        if (availableMonitors.size() > 1) {
            return new MultiplexingMonitor(availableMonitors.stream().map(MonitorExtension::getMonitor).collect(Collectors.toList()));
        }

        return availableMonitors.get(0).getMonitor();
    }

    public static @NotNull Telemetry loadTelemetry() {
        var loader = ServiceLoader.load(OpenTelemetry.class);
        var openTelemetries = loader.stream().map(ServiceLoader.Provider::get).collect(Collectors.toList());
        return new Telemetry(selectOpenTelemetryImpl(openTelemetries));
    }

    static @NotNull OpenTelemetry selectOpenTelemetryImpl(List<OpenTelemetry> openTelemetries) {
        if (openTelemetries.size() > 1) {
            throw new IllegalStateException(String.format("Found %s OpenTelemetry implementations. Please provide only one OpenTelemetry service provider.", openTelemetries.size()));
        }
        return openTelemetries.isEmpty() ? GlobalOpenTelemetry.get() : openTelemetries.get(0);
    }

    /**
     * Loads and orders the service extensions.
     */
    public List<InjectionContainer<ServiceExtension>> loadServiceExtensions(ServiceExtensionContext context) {
        var serviceExtensions = loadExtensions(ServiceExtension.class, true);
        var runtime = Runtime.getRuntime();
        var memoryUsed = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("USED MEMORY BEFORE: " + memoryUsed);
        var l = System.currentTimeMillis();
        System.out.println("CREATE DEPENDENCY GRAPH STARTED AT " + l);
        var injectionContainers = new DependencyGraph(context).of(serviceExtensions);
        System.out.println("DEPENDENCY GRAPH CREATED IN " + (System.currentTimeMillis() - l));
        System.out.println("USED MEMORY BY DEP GRAPH: " + ((runtime.totalMemory() - runtime.freeMemory()) - memoryUsed));
        return injectionContainers;
    }

    /**
     * Loads multiple extensions, raising an exception if at least one is not found.
     */
    public <T> List<T> loadExtensions(Class<T> type, boolean required) {
        return serviceLocator.loadImplementors(type, required);
    }

    /**
     * Parses the ConsoleMonitor log level from the program args. If no log level is provided, defaults to Level default.
     */
    private static Result<?> parseLogLevel(String[] programArgs) {
        return Stream.of(programArgs)
                .filter(arg -> arg.startsWith(ConsoleMonitor.LEVEL_PROG_ARG))
                .map(arg -> {
                    var validValueMessage = String.format("Valid values for the console level are %s", Stream.of(ConsoleMonitor.Level.values()).toList());
                    var splitArgs = arg.split("=");
                    if (splitArgs.length != 2) {
                        return Result.failure(String.format("Value missing for the --log-level argument. %s", validValueMessage));
                    }
                    try {
                        return Result.success(ConsoleMonitor.Level.valueOf(splitArgs[1].toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        return Result.failure(String.format("Invalid value \"%s\" for the --log-level argument. %s", splitArgs[1], validValueMessage));
                    }
                })
                .findFirst()
                .orElse(Result.success(ConsoleMonitor.Level.getDefaultLevel()));
    }

}
