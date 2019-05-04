package com.xingyu.service;

import com.xingyu.model.UserAccount;

public interface UserAccountService {
    public UserAccount findByUsername(String username);
}