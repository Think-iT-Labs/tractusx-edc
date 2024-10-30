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

import java.util.List;

/**
 * Locates implementors of a given class.
 */
public interface ServiceLocator {
    /**
     * Locates all implementors/inheritors of a given abstract class or interface <code>type</code>. A EdcException is
     * thrown if implementors are required, but none are found
     *
     * @param type     The abstract class or interface whos implementors to find
     * @param required Whether implementors of <code>type</code> MUST exist. A EdcException is thrown if required is true, but none are found
     * @return A list of implementors or an empty list if none found (and required=false)
     */
    <T> List<T> loadImplementors(Class<T> type, boolean required);

    /**
     * Locates a single implementor/inheritor of a given abstract class or interface. A EdcException is thrown if either
     * none are found and <code>required=true</code>, or if &gt; 1 implementors/inheritors are found.
     *
     * @param type     The abstract class or interface whos implementors to find
     * @param required Whether implementors of <code>type</code> MUST exist. A EdcException is thrown if required is true, but none are found
     * @return An implementor/inheritor of <code>type</code>
     */
    <T> T loadSingletonImplementor(Class<T> type, boolean required);
}
