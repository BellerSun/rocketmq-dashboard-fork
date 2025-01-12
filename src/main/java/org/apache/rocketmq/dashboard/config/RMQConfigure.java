/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.rocketmq.dashboard.config;

import java.util.ArrayList;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.common.MixAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import java.io.File;
import java.util.List;

import static org.apache.rocketmq.client.ClientConfig.SEND_MESSAGE_WITH_VIP_CHANNEL_PROPERTY;

@Configuration
@ConfigurationProperties(prefix = "rocketmq.config")
public class RMQConfigure {
    private static final String ROCKET_MQ_AK_PROPERTY = "rocketmq.config.accessKey";
    private static final String ROCKET_MQ_SK_PROPERTY = "rocketmq.config.secretKey";

    private Logger logger = LoggerFactory.getLogger(RMQConfigure.class);
    //use rocketmq.namesrv.addr first,if it is empty,than use system proerty or system env
    private volatile String namesrvAddr = System.getProperty(MixAll.NAMESRV_ADDR_PROPERTY, System.getenv(MixAll.NAMESRV_ADDR_ENV));

    private volatile String isVIPChannel = System.getProperty(SEND_MESSAGE_WITH_VIP_CHANNEL_PROPERTY, "true");


    private String dataPath = "/tmp/rocketmq-console/data";

    private boolean enableDashBoardCollect;

    private boolean loginRequired = false;

    private String accessKey = System.getProperty(ROCKET_MQ_AK_PROPERTY, System.getenv("ROCKET_MQ_AK"));

    private String secretKey = System.getProperty(ROCKET_MQ_SK_PROPERTY, System.getenv("ROCKET_MQ_SK"));

    private boolean useTLS = false;

    private Long timeoutMillis;

    private List<String> namesrvAddrs = new ArrayList<>();

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        if (StringUtils.isNotBlank(accessKey)) {
            this.accessKey = accessKey;
            System.setProperty(ROCKET_MQ_AK_PROPERTY, accessKey);
            logger.info("setAccessKey accessKey={}", accessKey);
        }
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        if (StringUtils.isNotBlank(secretKey)) {
            this.secretKey = secretKey;
            System.setProperty(ROCKET_MQ_SK_PROPERTY, secretKey);
            logger.info("setAccessKey accessKey={}", secretKey);
        }
    }

    public String getNamesrvAddr() {
        return namesrvAddr;
    }

    public List<String> getNamesrvAddrs() {
        return namesrvAddrs;
    }

    public void setNamesrvAddrs(List<String> namesrvAddrs) {
        this.namesrvAddrs = namesrvAddrs;
        if (CollectionUtils.isNotEmpty(namesrvAddrs)) {
            this.setNamesrvAddr(namesrvAddrs.get(0));
        }
    }

    public void setNamesrvAddr(String namesrvAddr) {
        if (StringUtils.isNotBlank(namesrvAddr)) {
            this.namesrvAddr = namesrvAddr;
            System.setProperty(MixAll.NAMESRV_ADDR_PROPERTY, namesrvAddr);
            logger.info("setNameSrvAddrByProperty nameSrvAddr={}", namesrvAddr);
        }
    }
    public boolean isACLEnabled() {
        return !(StringUtils.isAnyBlank(this.accessKey, this.secretKey) ||
                 StringUtils.isAnyEmpty(this.accessKey, this.secretKey));
    }
    public String getRocketMqDashboardDataPath() {
        return dataPath;
    }

    public String getDashboardCollectData() {
        return dataPath + File.separator + "dashboard";
    }

    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }

    public String getIsVIPChannel() {
        return isVIPChannel;
    }

    public void setIsVIPChannel(String isVIPChannel) {
        if (StringUtils.isNotBlank(isVIPChannel)) {
            this.isVIPChannel = isVIPChannel;
            System.setProperty(SEND_MESSAGE_WITH_VIP_CHANNEL_PROPERTY, isVIPChannel);
            logger.info("setIsVIPChannel isVIPChannel={}", isVIPChannel);
        }
    }

    public boolean isEnableDashBoardCollect() {
        return enableDashBoardCollect;
    }

    public void setEnableDashBoardCollect(String enableDashBoardCollect) {
        this.enableDashBoardCollect = Boolean.valueOf(enableDashBoardCollect);
    }

    public boolean isLoginRequired() {
        return loginRequired;
    }

    public void setLoginRequired(boolean loginRequired) {
        this.loginRequired = loginRequired;
    }

    public boolean isUseTLS() {
        return useTLS;
    }

    public void setUseTLS(boolean useTLS) {
        this.useTLS = useTLS;
    }

    public Long getTimeoutMillis() {
        return timeoutMillis;
    }

    public void setTimeoutMillis(Long timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
    }

    // Error Page process logic, move to a central configure later
    @Bean
    public ErrorPageRegistrar errorPageRegistrar() {
        return new MyErrorPageRegistrar();
    }

    private static class MyErrorPageRegistrar implements ErrorPageRegistrar {

        @Override
        public void registerErrorPages(ErrorPageRegistry registry) {
            registry.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/404"));
        }

    }
}
