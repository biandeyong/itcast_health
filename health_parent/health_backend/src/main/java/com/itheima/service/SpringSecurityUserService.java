package com.itheima.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.pojo.Permission;
import com.itheima.pojo.Role;
import com.itheima.pojo.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class SpringSecurityUserService implements UserDetailsService {
    @Reference
    UserService userService;
    /**
     * 根据用户名查询用户信息，需要通过dubbo远程调用service
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User byName = userService.findByName(username);
        if (byName == null) {
            return null;
        }
        //动态授权
        //GrantedAuthority是springSecurity的一个接口
        List<GrantedAuthority> list = new ArrayList<>();
        //
        Set<Role> roles = byName.getRoles();
        for (Role role : roles) {
            //为用户授予角色
            list.add(new SimpleGrantedAuthority(role.getKeyword()));
            Set<Permission> permissions = role.getPermissions();
            //为用户授权
            for (Permission permission : permissions) {
                list.add(new SimpleGrantedAuthority(permission.getKeyword()));
            }
        }
        org.springframework.security.core.userdetails.User user = new org.springframework.security.core.userdetails.User(username, byName.getPassword(), list);
        return user;
        //为security返回一个user用户
    }
}
