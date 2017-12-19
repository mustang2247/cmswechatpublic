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
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

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
//        Specifications<Article> spe = Specifications.where(new BaseSpecification<>(
//                new SearchCriteria("createDate", BaseSpecification.EQUAL, new java.sql.Date(new Date().getTime()))));
        DateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        Date d=new Date();
        String str=format.format(d);
//        System.out.println(str);
        Date begin= null;
        try {
            begin = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        System.out.println(d2);
        /////////////////得到想要测试的时间整天

        int dayMis=1000*60*60*24;//一天的毫秒-1
//        System.out.println("一天的毫秒-1:"+dayMis);
        //返回自 1970 年 1 月 1 日 00:00:00 GMT 以来此 Date 对象表示的毫秒数。
        long curMillisecond=begin.getTime();//当天的毫秒
//        System.out.println("curMillisecond:"+new Date(curMillisecond));
        long resultMis=curMillisecond+(dayMis-1); //当天最后一秒
//        System.out.println("resultMis:"+resultMis);

        //得到我需要的时间    当天最后一秒
        Date resultDate=new Date(resultMis);

        Specification<Article> spe = searchBySubmitDate(begin, resultDate);

                JSONObject object = new JSONObject();
        object.put("type", messageType);

        if (articleService == null){
            articleService = applicationContext.getBean(IArticleService.class);
        }
//        object.put("data", articleService.findAll());
        object.put("data", articleService.findAll(spe));

//        System.out.println("########### getMessage  " + object.toJSONString());
        return object.toJSONString();
    }

    private static Specification<Article> searchBySubmitDate(final Date startDate,
                                                final Date endDate) {
        return new Specification<Article>() {
            @Override
            public Predicate toPredicate(Root<Article> transaction,
                                         CriteriaQuery<?> q, CriteriaBuilder cb) {
                Predicate between = cb.between(transaction.get("createDate"), startDate, endDate);

                return between;
            }
        };

    }
}
