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

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.atomic.AtomicInteger;

public class CmsNewsWebSocketHandler extends TextWebSocketHandler {

    /**
     * 累加器
     */
    private static final AtomicInteger userIds = new AtomicInteger(0);

    private final int id;
    private User user;

    public CmsNewsWebSocketHandler() {
        this.id = userIds.getAndIncrement();
    }

    /**
     * 连接成功
     * @param session
     * @throws Exception
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        this.user = new User(this.id, session);
        CmsNewsTimer.addUser(this.user);
//        CmsNewsTimer.broadcast(MessageType.MESSAGE_TYPE_INIT, "init");
    }

    /**
     * 接收消息
     * @param session
     * @param message
     * @throws Exception
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message)
            throws Exception {
        String payload = message.getPayload();
//        if ("west".equals(payload)) {
//            this.user.setDirection(Direction.WEST);
//        } else if ("north".equals(payload)) {
//            this.user.setDirection(Direction.NORTH);
//        } else if ("east".equals(payload)) {
//            this.user.setDirection(Direction.EAST);
//        } else if ("south".equals(payload)) {
//            this.user.setDirection(Direction.SOUTH);
//        }
    }

    /**
     * 关闭连接
     * @param session
     * @param status
     * @throws Exception
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status)
            throws Exception {
        CmsNewsTimer.removeUser(this.user);
//        CmsNewsTimer.broadcast(MessageType.MESSAGE_TYPE_INIT, String.format("{'type': 'leave', 'id': %d}", Integer.valueOf(this.id)));
    }
}
