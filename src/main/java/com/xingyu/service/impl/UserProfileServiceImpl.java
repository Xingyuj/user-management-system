package com.xingyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.xingyu.dao.UserProfileDao;
import com.xingyu.model.UserAccount;
import com.xingyu.model.UserProfile;
import com.xingyu.service.UserProfileService;

@Service
public class UserProfileServiceImpl implements UserProfileService {
    @Resource
    private UserProfileDao userProfileDao;
    @Override
    public UserProfile findByAccount(UserAccount account) {
        return userProfileDao.findByAccount(account);
    }
}