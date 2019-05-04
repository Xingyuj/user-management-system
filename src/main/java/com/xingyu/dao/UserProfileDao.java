package com.xingyu.dao;


import org.springframework.data.repository.CrudRepository;

import com.xingyu.model.UserAccount;
import com.xingyu.model.UserProfile;

public interface UserProfileDao extends CrudRepository<UserProfile,Long> {
    public UserProfile findByAccount(UserAccount account);
}