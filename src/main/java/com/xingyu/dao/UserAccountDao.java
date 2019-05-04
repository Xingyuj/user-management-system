package com.xingyu.dao;

import org.springframework.data.repository.CrudRepository;

import com.xingyu.model.UserAccount;

public interface UserAccountDao extends CrudRepository<UserAccount,Long> {
    public UserAccount findByUsername(String username);
}