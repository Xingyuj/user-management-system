package com.xingyu.service.impl;

import java.util.ArrayList;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
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

    @Override
    public boolean saveProfile(UserProfile profile) {
    	try {
    		userProfileDao.save(profile);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
        return true;
    }
    
    @Override
    public boolean deleteProfile(Long id) {
    	try {
    		userProfileDao.deleteById(id);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
        return true;
    }
    
    @Override
    public ArrayList<UserProfile> findAll() {
    	ArrayList<UserProfile> arr = Lists.newArrayList(userProfileDao.findAll());
    	System.out.println(arr);
        return Lists.newArrayList(userProfileDao.findAll());
    }

	@Override
	public UserProfile findById(long id) {
        return userProfileDao.findById(id).get();
	}
}