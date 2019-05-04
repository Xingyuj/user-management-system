package com.xingyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.xingyu.dao.UserAccountDao;
import com.xingyu.model.UserAccount;
import com.xingyu.service.UserAccountService;

@Service
public class UserAccountServiceImpl implements UserAccountService {
    @Resource
    private UserAccountDao userDao;
    @Override
    public UserAccount findByUsername(String username) {
        System.out.println("UserAccountServiceImpl.findByUsername()");
        return userDao.findByUsername(username);
    }
}