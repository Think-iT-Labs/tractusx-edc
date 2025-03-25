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

plugins {
    `java-library`
    id("application")
}


dependencies {

    // use basic (all in-mem) control plane
    implementation(project(":edc-controlplane:edc-controlplane-postgresql-hashicorp-vault")) {
        exclude(module = "json-ld-core")
        exclude(module = "tx-dcp-sts-dim")
        exclude(group = "org.eclipse.edc", "vault-hashicorp")
        exclude(module = "tx-dcp")
        exclude("org.eclipse.edc", "identity-trust-issuers-configuration")
    }

    // use basic (all in-mem) data plane
    runtimeOnly(project(":edc-dataplane:edc-dataplane-hashicorp-vault")) {
        exclude("org.eclipse.edc", "api-observability")
        exclude("org.eclipse.edc", "data-plane-selector-client")
        exclude("org.eclipse.edc", "vault-hashicorp")
    }

    implementation(libs.postgres)

    implementation(libs.edc.core.controlplane)
    // for the controller
    implementation(libs.jakarta.rsApi)
    runtimeOnly(libs.edc.transaction.local)
}

application {
    mainClass.set("org.eclipse.edc.boot.system.runtime.BaseRuntime")
}

edcBuild {
    publish.set(false)
}
