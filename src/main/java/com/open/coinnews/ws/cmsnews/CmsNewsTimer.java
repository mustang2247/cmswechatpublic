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

import com.alibaba.fastjson.JSONObject;
import com.open.coinnews.app.model.Article;
import com.open.coinnews.app.service.IArticleService;
import com.open.coinnews.basic.tools.BaseSpecification;
import com.open.coinnews.basic.tools.SearchCriteria;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.domain.Specifications;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 设置多玩家蛇游戏WebSocket例子的计时器。
 */
public class CmsNewsTimer {

    private static final long TICK_DELAY = 100;

    private static final Object MONITOR = new Object();
    private static final ConcurrentHashMap<Integer, User> users = new ConcurrentHashMap<>();

//    private static Timer gameTimer = null;

    private static final AtomicInteger userIds = new AtomicInteger(0);
    /**
     * 实时在线用户数
     */
    public static int userNum = 0;

    private static IArticleService articleService;
    private static ApplicationContext applicationContext;

    public static void applicationContext(ApplicationContext context){
        applicationContext = context;
        articleService = applicationContext.getBean(IArticleService.class);
    }

    /**
     * 新进用户
     * @param user
     */
    public static void addUser(User user) {
        synchronized (MONITOR) {
            users.put(Integer.valueOf(user.getId()), user);
            userNum = userIds.getAndIncrement();

            System.out.println("###########  " + userNum);
            try {
                user.sendMessage(getMessage(MessageType.MESSAGE_TYPE_INIT));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static Collection<User> getUsers() {
        return Collections.unmodifiableCollection(users.values());
    }

    /**
     * 用户退出
     * @param user
     */
    public static void removeUser(User user) {
        synchronized (MONITOR) {
            users.remove(Integer.valueOf(user.getId()));
            userNum = userIds.getAndDecrement();
        }
    }

    /**
     * 广播
     *
     * @param message
     * @throws Exception
     */
    public static void broadcast(Integer messageType, String message) {
        Collection<User> users = new CopyOnWriteArrayList<>(CmsNewsTimer.getUsers());
        for (User user : users) {
            try {
                user.sendMessage(getMessage(messageType));
            } catch (Throwable ex) {
                // if User#sendMessage fails the client is removed
                removeUser(user);
            }
        }
    }

    /**
     * 获取信息
     * @param messageType
     * @return
     */
    private static String getMessage(Integer messageType) {
        Specifications<Article> spe = Specifications.where(new BaseSpecification<>(new SearchCriteria("createDate", BaseSpecification.EQUAL, new Date())));

        JSONObject object = new JSONObject();
        object.put("type", messageType);

        if (articleService == null){
            articleService = applicationContext.getBean(IArticleService.class);
        }
        object.put("data", articleService.findAll());
//        object.put("data", articleService.findAll(spe));

        System.out.println("########### getMessage  " + object.toJSONString());
        return object.toJSONString();
    }
}
