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

package org.eclipse.edc.boot.config;

import org.eclipse.edc.boot.system.ServiceLocator;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.ConfigurationExtension;
import org.eclipse.edc.spi.system.configuration.Config;
import org.eclipse.edc.spi.system.configuration.ConfigFactory;

import java.util.Objects;

/**
 * Load configuration from configuration extensions, environment variables and system properties.
 */
public class ConfigurationLoader {
    private final ServiceLocator serviceLocator;
    private final EnvironmentVariables environmentVariables;
    private final SystemProperties systemProperties;

    public ConfigurationLoader(ServiceLocator serviceLocator, EnvironmentVariables environmentVariables, SystemProperties systemProperties) {
        this.serviceLocator = serviceLocator;
        this.environmentVariables = environmentVariables;
        this.systemProperties = systemProperties;
    }

    /**
     * Load configuration.
     * Please note that Environment variables keys will be converted from the ENVIRONMENT_NOTATION to the dot.notation.
     *
     * @param monitor the monitor.
     * @return the Config instance.
     */
    public Config loadConfiguration(Monitor monitor) {
        var config = serviceLocator.loadImplementors(ConfigurationExtension.class, false)
                .stream().peek(extension -> {
                    extension.initialize(monitor);
                    monitor.info("Initialized " + extension.name());
                })
                .map(ConfigurationExtension::getConfig)
                .filter(Objects::nonNull)
                .reduce(Config::merge)
                .orElse(ConfigFactory.empty());

        var environmentConfig = ConfigFactory.fromEnvironment(environmentVariables.get());
        var systemPropertyConfig = ConfigFactory.fromProperties(systemProperties.get());

        return config.merge(environmentConfig).merge(systemPropertyConfig);
    }

}
