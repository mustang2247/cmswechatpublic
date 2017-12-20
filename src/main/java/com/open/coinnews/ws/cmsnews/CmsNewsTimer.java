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
import com.open.coinnews.basic.tools.SortTools;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
        String jsonData = getMessage(messageType);
        Collection<User> users = new CopyOnWriteArrayList<>(CmsNewsTimer.getUsers());
        for (User user : users) {
            try {
                user.sendMessage(jsonData);
            } catch (Throwable ex) {
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
        DateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        Date d=new Date();
        String str=format.format(d);
        Date begin= null;
        try {
            begin = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int dayMis=1000*60*60*24;//一天的毫秒-1
        long curMillisecond=begin.getTime();//当天的毫秒
        long resultMis=curMillisecond+(dayMis-1); //当天最后一秒

        //得到我需要的时间    当天最后一秒
        Date resultDate=new Date(resultMis);

        Specification<Article> spe = searchBySubmitDate(begin, resultDate);

                JSONObject object = new JSONObject();
        object.put("type", messageType);

        if (articleService == null){
            articleService = applicationContext.getBean(IArticleService.class);
        }
        object.put("data", articleService.findAll(spe, SortTools.basicSort("desc", "createDate")));
        object.put("datetime", dateToStr(new Date()));

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

    public static String dateToStr(java.util.Date dateDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(dateDate);
        return dateString + "&middot;" + getWeek(dateDate);
    }

    /**
     * 根据一个日期，返回是星期几的字符串
     *
     * @param date
     * @return
     */
    public static String getWeek(Date date) {
        // 再转换为时间
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        // int hour=c.get(Calendar.DAY_OF_WEEK);
        // hour中存的就是星期几了，其范围 1~7
        // 1=星期日 7=星期六，其他类推
        return getWeekStr(c.get(Calendar.DAY_OF_WEEK));
    }

    public static String getWeekStr(int w){
        String str = "";
        if(w == 1){
            str = "星期日";
        }else if(w == 2){
            str = "星期一";
        }else if(w == 3){
            str = "星期二";
        }else if(w == 4){
            str = "星期三";
        }else if(w == 5){
            str = "星期四";
        }else if(w == 6){
            str = "星期五";
        }else if(w == 7){
            str = "星期六";
        }
        return str;
    }

}
