/********************************************************************************
 * Copyright (c) 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
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
 ********************************************************************************/

package org.eclipse.tractusx.edc.dataplane.tokenrefresh.e2e;

import io.restassured.specification.RequestSpecification;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.eclipse.edc.junit.testfixtures.TestUtils.getFreePort;

/**
 * Configuration baseline for Data-Plane e2e tests
 */
public class RuntimeConfig {

    private final Endpoint publicApi = new Endpoint(URI.create("http://localhost:%d/public".formatted(getFreePort())));
    private final Endpoint signalingApi = new Endpoint(URI.create("http://localhost:%d/signaling".formatted(getFreePort())));
    private final Endpoint refreshApi = publicApi;
    private final Endpoint defaultApi = new Endpoint(URI.create("http://localhost:%d/api".formatted(getFreePort())));

    /**
     * Configures the data plane token endpoint, and all relevant HTTP contexts
     */
    public Map<String, String> baseConfig() {
        return new HashMap<>() {
            {
                put("edc.dataplane.token.validation.endpoint", "http://token-validation.com");
                put("web.http.path", defaultApi.url().getPath());
                put("web.http.port", String.valueOf(defaultApi.url().getPort()));
                put("web.http.public.path", publicApi.url().getPath());
                put("web.http.public.port", String.valueOf(publicApi.url().getPort()));
                put("web.http.signaling.path", signalingApi.url().getPath());
                put("web.http.signaling.port", String.valueOf(signalingApi.url().getPort()));
            }
        };
    }

    public Endpoint getPublicApi() {
        return publicApi;
    }

    public Endpoint getSignalingApi() {
        return signalingApi;
    }

    public Endpoint getRefreshApi() {
        return refreshApi;
    }

    public Endpoint getDefaultApi() {
        return defaultApi;
    }

    public record Endpoint(URI url, Map<String, String> headers) {
        public Endpoint(URI url) {
            this(url, Map.of());
        }

        public RequestSpecification baseRequest() {
            return given().baseUri(url.toString()).headers(headers);
        }

    }

}

