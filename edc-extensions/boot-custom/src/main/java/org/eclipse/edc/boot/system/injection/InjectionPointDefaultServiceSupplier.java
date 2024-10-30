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

package org.eclipse.edc.boot.system.injection;

import org.eclipse.edc.boot.system.injection.lifecycle.ServiceProvider;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.jetbrains.annotations.Nullable;

/**
 * Supplies the default {@link ServiceProvider} that has been stored in
 * the {@link InjectionPoint}
 */
public class InjectionPointDefaultServiceSupplier implements DefaultServiceSupplier {

    @Override
    public @Nullable Object provideFor(InjectionPoint<?> injectionPoint, ServiceExtensionContext context) {
        var defaultService = injectionPoint.getDefaultServiceProvider();
        if (injectionPoint.isRequired() && defaultService == null) {
            throw new EdcInjectionException("No default provider for required service " + injectionPoint.getType());
        }
        return defaultService == null ? null : defaultService.register(context);
    }

}
