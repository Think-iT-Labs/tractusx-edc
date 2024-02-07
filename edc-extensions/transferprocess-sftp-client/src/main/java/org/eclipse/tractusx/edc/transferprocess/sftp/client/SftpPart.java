/********************************************************************************
 * Copyright (c) 2022 Mercedes-Benz Tech Innovation GmbH
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

package org.eclipse.tractusx.edc.transferprocess.sftp.client;

import org.eclipse.edc.connector.dataplane.spi.pipeline.DataSource;
import org.eclipse.tractusx.edc.transferprocess.sftp.common.EdcSftpException;

import java.io.IOException;
import java.io.InputStream;

public class SftpPart implements DataSource.Part {
    private final SftpClientWrapper sftpClientWrapper;

    public SftpPart(SftpClientWrapper sftpClientWrapper) {
        this.sftpClientWrapper = sftpClientWrapper;
    }

    @Override
    public String name() {
        return ((SftpClientWrapperImpl) sftpClientWrapper).getConfig().getSftpLocation().getPath();
    }

    @Override
    public InputStream openStream() {
        try {
            return sftpClientWrapper.downloadFile();
        } catch (IOException e) {
            throw new EdcSftpException(e);
        }
    }
}
