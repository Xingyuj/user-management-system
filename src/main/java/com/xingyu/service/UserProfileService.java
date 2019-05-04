package com.xingyu.service;

import com.xingyu.model.UserAccount;
import com.xingyu.model.UserProfile;

public interface UserProfileService {
    public UserProfile findByAccount(UserAccount account);
}