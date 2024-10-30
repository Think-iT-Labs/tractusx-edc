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
import org.eclipse.edc.spi.system.ServiceExtension;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Represents one {@link ServiceExtension} with a description of all its auto-injectable fields, which in turn are
 * represented by {@link FieldInjectionPoint}s.
 */
public class InjectionContainer<T> {
    private final T injectionTarget;
    private final List<ServiceProvider> serviceProviders;
    private final Set<InjectionPoint<T>> injectionPoint;

    public InjectionContainer(T target, Set<InjectionPoint<T>> injectionPoint) {
        this(target, injectionPoint, Collections.emptyList());
    }

    public InjectionContainer(T target, Set<InjectionPoint<T>> injectionPoint, List<ServiceProvider> serviceProviders) {
        injectionTarget = target;
        this.serviceProviders = serviceProviders;
        this.injectionPoint = injectionPoint;
    }

    public T getInjectionTarget() {
        return injectionTarget;
    }

    public Set<InjectionPoint<T>> getInjectionPoints() {
        return injectionPoint;
    }

    public List<ServiceProvider> getServiceProviders() {
        return serviceProviders;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "injectionTarget=" + injectionTarget +
                '}';
    }

}
