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

import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.jetbrains.annotations.Nullable;

/**
 * Provides a way to retrieve the default service implementation, to be used when an extended implementation cannot be
 * found.
 */
@FunctionalInterface
public interface DefaultServiceSupplier {

    /**
     * Provides a default service for the passed type
     *
     * @param type the class object of the needed type
     * @return a default service, null if not found
     */
    @Nullable
    Object provideFor(InjectionPoint<?> type, ServiceExtensionContext context);
}
