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

import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;

/**
 * Sets all fields of a {@link ServiceExtension}, that are annotated with {@code @Inject} by attempting to get the
 * corresponding service from the {@link ServiceExtensionContext}.
 * <p>
 * Injectors must throw an {@link EdcInjectionException} should they fail to set any field's value.
 */
@FunctionalInterface
public interface Injector {
    /**
     * Attempts to set all fields (i.e. {@link InjectionContainer#getInjectionPoints()}) of a service extension (i.e. {@link InjectionContainer#getInjectionTarget()})
     * by attempting to resolve as service of the field's type from the context.
     */
    <T> T inject(InjectionContainer<T> container, ServiceExtensionContext context);
}
