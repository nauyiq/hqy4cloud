/**
 * Copyright (c) 2012-2019 Nikita Koksharov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hqy.socketio;

import com.hqy.base.common.base.lang.StringConstants;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * socket.io 握手数据.<br/>
 * 修改源码 修改握手参数
 */
public class HandshakeData implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(HandshakeData.class);

    private static final long serialVersionUID = 1196350300161819978L;

    //注释掉源码字段.
//    private HttpHeaders headers;
//    private InetSocketAddress local;
//    private InetSocketAddress address;

    private final Date time = new Date();
    private String url;
    private Map<String, List<String>> urlParams;
    private boolean xdomain;

    /**
     * 请求头 origin
     */
    private String origin;

    /**
     * 客户端userAgent
     */
    private String userAgent;

    /**
     * 客户端的ip
     */
    private String realIp;

    /**
     * 新增token 用于握手数据身份安全校验.
     */
    private String token;

    /**
     * 可认为是当前会话id, 通道id
     */
    private String bizId;

    // needed for correct deserialization
    public HandshakeData() {
    }

    public HandshakeData(HttpHeaders headers, Map<String, List<String>> urlParams, InetSocketAddress address, String url, boolean xdomain) {
        this(headers, urlParams, address, null, url, xdomain);
    }

    public HandshakeData(HttpHeaders headers, Map<String, List<String>> urlParams, InetSocketAddress address,
                         InetSocketAddress local, String url, boolean xdomain) {
        super();
        //获取请求头数据
        this.origin = headers.get(HttpHeaderNames.ORIGIN);
        this.userAgent = headers.get(HttpHeaderNames.USER_AGENT);
        this.urlParams = urlParams;
        this.url = url;
        this.xdomain = xdomain;

        //解析参数里的token
        if (urlParams.containsKey(StringConstants.Auth.SOCKET_AUTH_TOKEN)) {
            this.token = urlParams.get(StringConstants.Auth.SOCKET_AUTH_TOKEN).get(0);
        } else {
            log.warn("@@@ Socket.io handshakeData token is empty.");
        }
        //解析参数里的bizId
        if (urlParams.containsKey(StringConstants.Auth.BIZ_ID)) {
            List<String> bizIds = urlParams.get(StringConstants.Auth.BIZ_ID);
            if (CollectionUtils.isNotEmpty(bizIds)) {
                this.bizId = bizIds.get(0);
            }
        }
    }

    public String getOrigin() {
        return origin;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getRealIp() {
        return realIp;
    }

    public void setRealIp(String realIp) {
        this.realIp = realIp;
    }

    public String getAccessToken() {
        return token;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    /**
     * Client network address
     *
     * @return network address
     */
//    public InetSocketAddress getAddress() {
//        return address;
//    }

    /**
     * Connection local address
     *
     * @return local address
     */
//    public InetSocketAddress getLocal() {
//        return local;
//    }

    /**
     * Http headers sent during first client request
     *
     * @return headers
     */
//    public HttpHeaders getHttpHeaders() {
//        return headers;
//    }

    /**
     * Client connection date
     *
     * @return date
     */
    public Date getTime() {
        return time;
    }

    /**
     * Url used by client during first request
     *
     * @return url
     */
    public String getUrl() {
        return url;
    }

    public boolean isXdomain() {
        return xdomain;
    }

    /**
     * Url params stored in url used by client during first request
     *
     * @return map
     */
    public Map<String, List<String>> getUrlParams() {
        return urlParams;
    }

    public String getSingleUrlParam(String name) {
        List<String> values = urlParams.get(name);
        if (values != null && values.size() == 1) {
            return values.iterator().next();
        }
        return null;
    }

}
