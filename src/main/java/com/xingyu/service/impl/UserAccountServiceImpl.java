package com.xingyu.service.impl;

import java.util.ArrayList;
import java.util.Optional;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.xingyu.dao.UserAccountDao;
import com.xingyu.model.UserAccount;
import com.xingyu.service.UserAccountService;

@Service
public class UserAccountServiceImpl implements UserAccountService {
    @Resource
    private UserAccountDao userAccountDao;
    
    @Override
    public UserAccount findByUsername(String username) {
        return userAccountDao.findByUsername(username);
    }
    
    @Override
    public boolean saveAccount(UserAccount account) {
    	try {
    		userAccountDao.save(account);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
        return true;
    }
    
    @Override
    public boolean deleteAccount(Long id) {
    	try {
    		userAccountDao.deleteById(id);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
        return true;
    }
    
    @Override
    public ArrayList<UserAccount> findAll() {
    	ArrayList<UserAccount> arr = Lists.newArrayList(userAccountDao.findAll());
    	System.out.println(arr);
        return Lists.newArrayList(userAccountDao.findAll());
    }

	@Override
	public UserAccount findById(long id) {
        return userAccountDao.findById(id).get();
	}
    
}