package com.open.coinnews.basic.auth.service;

import com.open.coinnews.basic.auth.iservice.IMenuService;
import com.open.coinnews.basic.auth.iservice.IRoleService;
import com.open.coinnews.basic.auth.iservice.IUserService;
import com.open.coinnews.basic.auth.model.Menu;
import com.open.coinnews.basic.auth.model.Role;
import com.open.coinnews.basic.auth.model.User;
import com.open.coinnews.basic.auth.tools.AuthTools;
import com.open.coinnews.basic.auth.tools.SecurityUtil;
import com.open.coinnews.basic.iservice.IAppConfigService;
import com.open.coinnews.basic.model.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public class UserServiceImpl {

    @Autowired
    private MenuServiceImpl menuServiceImpl;

    @Autowired
    private IMenuService menuService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IRoleService roleService;

    @Autowired
    private RoleMenuServiceImpl roleMenuServiceImpl;

    @Autowired
    private UserRoleServiceImpl userRoleServiceImpl;

    @Autowired
    private IAppConfigService appConfigService;

    /**
     * 初始化基础用户数据
     * - 1、初始化菜单
     * - 2、初始化角色
     * - 3、为角色分配所有菜单
     * - 4、添加用户
     * - 5、为用户分配角色
     * @param user
     */
    public void initBaseUser(User user) {
        try {
            AuthTools.getInstance().buildSystemMenu(menuServiceImpl);
            Role role = new Role();
            role.setName("超级管理员角色"); role.setSn("ROLE_SUPER_ADMIN");
            roleService.save(role);
            List<Menu> menuList = menuService.listAllUrlMenu();
            for(Menu m : menuList) {
                roleMenuServiceImpl.addOrDelete(role.getId(), m.getId());
            }

            user.setPassword(SecurityUtil.md5(user.getUsername(), user.getPassword()));
            user.setStatus(1);
            user.setIsAdmin(role.getId());
            userService.save(user);
            userRoleServiceImpl.addOrDelete(user.getId(), role.getId());

            AppConfig ac = appConfigService.loadOne();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if(ac==null) {
                ac = new AppConfig();
                ac.setCreateDate(sdf.format(new Date()));
                ac.setInitFlag("1"); appConfigService.save(ac);
            } else {
                ac.setCreateDate(sdf.format(new Date()));
                ac.setInitFlag("1"); appConfigService.save(ac);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
