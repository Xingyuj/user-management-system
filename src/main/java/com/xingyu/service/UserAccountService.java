package com.xingyu.service;

import java.util.ArrayList;

import com.xingyu.model.UserAccount;

public interface UserAccountService {
    public UserAccount findByUsername(String username);
    public UserAccount findById(long id);
    public ArrayList<UserAccount> findAll();
    public boolean saveAccount(UserAccount account);
    public boolean deleteAccount(Long id);
}