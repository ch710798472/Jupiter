/*
 * Copyright (c) 2015 The Jupiter Project
 *
 * Licensed under the Apache License, version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jupiter.spring.support;

import org.jupiter.common.util.Lists;
import org.jupiter.common.util.Strings;
import org.jupiter.rpc.JClient;
import org.jupiter.transport.UnresolvedAddress;
import org.springframework.beans.factory.InitializingBean;

import java.util.Collections;
import java.util.List;

/**
 * jupiter client wrapper, 负责初始化并启动客户端.
 *
 * jupiter
 * org.jupiter.spring.support
 *
 * @author jiachun.fjc
 */
public class JupiterSpringClient implements InitializingBean {

    private JClient client;

    private String registryServerAddresses;                             // 注册中心地址 [host1:port1,host2:port2....]
    private String providerServerAddresses;                             // IP直连到providers [host1:port1,host2:port2....]
    private List<UnresolvedAddress> providerServerUnresolvedAddresses;  // IP直连的地址列表
    private boolean hasRegistryServer;                                  // true: 需要连接注册中心; false: IP直连方式

    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }

    private void init() {
        // 注册中心
        if (Strings.isNotBlank(registryServerAddresses)) {
            client.connectToRegistryServer(registryServerAddresses);
            hasRegistryServer = true;
        }

        if (!hasRegistryServer) {
            // IP直连方式
            if (Strings.isNotBlank(providerServerAddresses)) {
                String[] array = Strings.split(providerServerAddresses, ',');
                providerServerUnresolvedAddresses = Lists.newArrayList();
                for (String s : array) {
                    String[] addressStr = Strings.split(s, ':');
                    String host = addressStr[0];
                    int port = Integer.parseInt(addressStr[1]);
                    UnresolvedAddress address = new UnresolvedAddress(host, port);
                    providerServerUnresolvedAddresses.add(address);

                    client.connector().connect(address, true); // 异步建立连接
                }
            }
        }
    }

    public JClient getClient() {
        return client;
    }

    public void setClient(JClient client) {
        this.client = client;
    }

    public String getRegistryServerAddresses() {
        return registryServerAddresses;
    }

    public void setRegistryServerAddresses(String registryServerAddresses) {
        this.registryServerAddresses = registryServerAddresses;
    }

    public String getProviderServerAddresses() {
        return providerServerAddresses;
    }

    public void setProviderServerAddresses(String providerServerAddresses) {
        this.providerServerAddresses = providerServerAddresses;
    }

    public List<UnresolvedAddress> getProviderServerUnresolvedAddresses() {
        return providerServerUnresolvedAddresses == null
                ?
                Collections.<UnresolvedAddress>emptyList()
                :
                providerServerUnresolvedAddresses;
    }

    public boolean isHasRegistryServer() {
        return hasRegistryServer;
    }
}
