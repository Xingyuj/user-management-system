package com.xingyu.service;

import java.util.ArrayList;

import com.xingyu.model.UserAccount;
import com.xingyu.model.UserProfile;

public interface UserProfileService {
    public UserProfile findByAccount(UserAccount account);
    public UserProfile findById(long id);
    public ArrayList<UserProfile> findAll();
    public boolean saveProfile(UserProfile account);
    public boolean deleteProfile(Long id);
}