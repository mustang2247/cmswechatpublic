/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.open.coinnews.ws.cmsnews;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * 连接用户信息
 */
public class User {
    private final int id;
    private final WebSocketSession session;

    public User(int id, WebSocketSession session) {
        this.id = id;
        this.session = session;
    }


    private void kill() throws Exception {
//        synchronized (this.monitor) {
//            resetState();
//            sendMessage("{'type': 'dead'}");
//        }
    }

    /**
     * 推送消息
     * @param msg
     * @throws Exception
     */
    protected void sendMessage(String msg) throws Exception {
        this.session.sendMessage(new TextMessage(msg));
    }

    public void update() throws Exception {

    }

    public int getId() {
        return this.id;
    }

}
