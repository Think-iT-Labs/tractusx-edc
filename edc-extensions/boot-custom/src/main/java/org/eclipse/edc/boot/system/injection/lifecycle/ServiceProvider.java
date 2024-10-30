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

package org.eclipse.edc.boot.system.injection.lifecycle;

import org.eclipse.edc.boot.system.injection.ProviderMethod;
import org.eclipse.edc.runtime.metamodel.annotation.Provider;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;

/**
 * Represent a service provider, that's a method annotated with the {@link Provider}
 *
 * @param method the provider method.
 * @param extension the extension in which the method is contained.
 */
public record ServiceProvider(ProviderMethod method, ServiceExtension extension) {

    /**
     * Call the method and register the service.
     *
     * @param context the service context.
     * @return the instantiated service.
     */
    public Object register(ServiceExtensionContext context) {
        var type = method.getReturnType();
        var service = method.invoke(extension, context);
        context.registerService(type, service);
        return service;
    }

}
