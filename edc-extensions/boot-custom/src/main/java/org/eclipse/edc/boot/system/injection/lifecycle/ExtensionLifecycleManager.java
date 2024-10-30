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

import org.eclipse.edc.boot.system.injection.InjectionContainer;
import org.eclipse.edc.boot.system.injection.InjectionPointDefaultServiceSupplier;
import org.eclipse.edc.boot.system.injection.InjectorImpl;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Provider;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;

import java.util.List;

/**
 * {@link ServiceExtension} implementors should not be constructed by just invoking their constructors, instead they need to go through
 * a lifecycle, which is what this class aims at doing. There are three major phases for initialization:
 * <ol>
 *     <li>inject dependencies: all fields annotated with {@link Inject} are set</li>
 *     <li>initialize: invokes the {@link ServiceExtension#initialize(ServiceExtensionContext)} method</li>
 *     <li>provide: invokes all methods annotated with {@link Provider} to register more services into the context</li>
 * </ol>
 * <p>
 * The sequence of these phases is actually important.
 * <p>
 * It is advisable to put all {@link ServiceExtension} instances through their initialization lifecycle <em>before</em> invoking their
 * {@linkplain ServiceExtension#start()} method!
 */
public class ExtensionLifecycleManager {

    /**
     * Convenience method for loading service extensions.
     */
    public static void bootServiceExtensions(List<InjectionContainer<ServiceExtension>> containers, ServiceExtensionContext context) {
        var injector = new InjectorImpl(new InjectionPointDefaultServiceSupplier());

        for (var container : containers) {
            var target = container.getInjectionTarget();
            injector.inject(container, context);

            target.initialize(context);
            context.getMonitor().info("Initialized " + target.name());

            var serviceProviders = container.getServiceProviders();
            if (serviceProviders != null) {
                serviceProviders.forEach(serviceProvider -> serviceProvider.register(context));
            }
        }

        context.freeze();

        for (var container : containers) {
            var target = container.getInjectionTarget();
            target.prepare();
            context.getMonitor().info("Prepared " + target.name());
        }

        for (var container : containers) {
            var target = container.getInjectionTarget();
            target.start();
            context.getMonitor().info("Started " + target.name());
        }

    }

}
